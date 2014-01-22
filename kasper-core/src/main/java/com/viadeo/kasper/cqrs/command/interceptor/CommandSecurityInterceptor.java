// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.interceptor;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.security.KasperSecurityException;
import com.viadeo.kasper.security.SecurityStrategy;

public class CommandSecurityInterceptor<C extends Command> implements Interceptor<C, CommandResponse> {
    private SecurityStrategy securityStrategy;

    public CommandSecurityInterceptor(SecurityStrategy securityStrategy) {
        this.securityStrategy = securityStrategy;
    }

    // ------------------------------------------------------------------------

    @Override
    public CommandResponse process(final C input,
                                   final Context context,
                                   final InterceptorChain<C, CommandResponse> chain) throws Exception {
        try {
            securityStrategy.beforeRequest(context);
        } catch (final KasperSecurityException e) {
            return CommandResponse.error(e.getKasperReason());
        }
        CommandResponse commandResponse = chain.next(input, context);
        securityStrategy.afterRequest();
        return commandResponse;
    }

}
