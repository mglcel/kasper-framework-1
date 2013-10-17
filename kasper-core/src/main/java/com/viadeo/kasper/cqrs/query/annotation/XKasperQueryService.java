// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.annotation;

import com.viadeo.kasper.cqrs.query.ServiceFilter;
import com.viadeo.kasper.ddd.Domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Query service marker
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XKasperQueryService {

    /**
     * @return the name of the service
     */
    String name() default "";

    /**
     * @return the description of the service
     */
    String description() default "";

    /**
     * @return the associated domain
     */
    Class<? extends Domain> domain();

    /**
     * @return the associated query service filters
     */
    Class<? extends ServiceFilter>[] filters() default {};

    /**
     * Whether the response of this query service should be cached.
     * False by default.
     */
    XKasperQueryCache cache() default @XKasperQueryCache(enabled = false);

}
