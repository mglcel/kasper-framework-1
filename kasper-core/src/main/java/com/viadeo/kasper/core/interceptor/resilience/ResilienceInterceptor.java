// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor.resilience;

import com.codahale.metrics.MetricRegistry;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.netflix.hystrix.*;
import com.netflix.hystrix.contrib.codahalemetricspublisher.HystrixCodaHaleMetricsPublisher;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.response.KasperResponse;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import org.axonframework.unitofwork.CurrentUnitOfWork;
import org.axonframework.unitofwork.UnitOfWork;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class ResilienceInterceptor<INPUT, OUTPUT extends KasperResponse> implements Interceptor<INPUT, OUTPUT> {

    private static final String UNKNOWN_GROUP_NAME = "unknown";

    private final ResiliencePolicy policy;
    private final ResilienceConfigurator configurer;

    // ------------------------------------------------------------------------

    public ResilienceInterceptor(
            final MetricRegistry metricRegistry,
            final ResiliencePolicy policy,
            final ResilienceConfigurator configurer
    ) {
        this.policy = checkNotNull(policy);
        this.configurer = checkNotNull(configurer);
        registerMetricsPublisherOnHystrix(checkNotNull(metricRegistry), HystrixPlugins.getInstance(), configurer);
    }

    // ------------------------------------------------------------------------

    @Override
    public OUTPUT process(
            final INPUT input,
            final Context context,
            final InterceptorChain<INPUT, OUTPUT> chain
    ) throws Exception {
        try {
            final Optional<UnitOfWork> currentUnitOfWork;

            if (!CurrentUnitOfWork.isStarted()) {
                currentUnitOfWork = Optional.absent();
            } else {
                currentUnitOfWork = Optional.of(CurrentUnitOfWork.get());
            }

            return new HystrixCommand<OUTPUT>(from(input)) {

                private Exception exception;

                @Override
                protected OUTPUT run() throws Exception {
                    if (currentUnitOfWork.isPresent()) {
                        CurrentUnitOfWork.set(currentUnitOfWork.get());
                    }
                    try {
                        return chain.next(input, context);
                    } catch (final Exception e) {
                        policy.manage(e);
                        exception = e;
                        throw e;
                    }
                }

                @Override
                protected OUTPUT getFallback() {
                    return fallback(this, exception);
                }

            }.execute();

        } catch (final HystrixBadRequestException e) {
            throw (Exception) e.getCause();
        }
    }

    // ------------------------------------------------------------------------

    public abstract OUTPUT fallback(final HystrixCommand<OUTPUT> command, final Exception exception);
    public abstract String getGroupName();

    // ------------------------------------------------------------------------

    protected HystrixCommand.Setter from(final INPUT input) {
        checkNotNull(input);

        final String inputName = input.getClass().getName();
        final String groupName = Objects.firstNonNull(getGroupName(), UNKNOWN_GROUP_NAME);

        final ResilienceConfigurator.InputConfig config = configurer.configure(input);

        return HystrixCommand.Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupName))
                .andCommandKey(HystrixCommandKey.Factory.asKey(inputName))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(input.getClass().getName()))

                //TODO review values for the following thread pool properties in order to avoid to get a RejectedExecutionException
                .andThreadPoolPropertiesDefaults(
                        HystrixThreadPoolProperties.Setter()
                                .withCoreSize(config.threadPoolCoreSize)
                                .withQueueSizeRejectionThreshold(config.threadPoolQueueSizeRejectionThreshold)
                )

                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter()
                                .withCircuitBreakerEnabled(config.circuitBreakerEnable)
                                .withCircuitBreakerSleepWindowInMilliseconds(config.circuitBreakerSleepWindowInMillis)
                                .withCircuitBreakerErrorThresholdPercentage(config.circuitBreakerThresholdInPercent)
                                .withCircuitBreakerRequestVolumeThreshold(config.circuitBreakerRequestVolumeThreshold)

                                .withExecutionIsolationThreadTimeoutInMilliseconds(config.executionTimeoutInMillis)

                                .withMetricsRollingStatisticalWindowInMilliseconds(60000) // default to 10000 ms
                                .withMetricsRollingStatisticalWindowBuckets(60) // default to 10
                );
    }

    @VisibleForTesting
    protected static boolean metricInitialized;

    @VisibleForTesting
    protected static void registerMetricsPublisherOnHystrix(
            final MetricRegistry metricRegistry,
            final HystrixPlugins hystrixPlugins,
            final ResilienceConfigurator configurer
    ) {
        if ( ! metricInitialized && null != metricRegistry && configurer.isHystrixMetricEnable() ) {
            hystrixPlugins.registerMetricsPublisher(new HystrixCodaHaleMetricsPublisher(metricRegistry));
            metricInitialized = true;
        }
    }

}