// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.impl;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.interceptor.InterceptorChainRepository;
import com.viadeo.kasper.cqrs.command.Command;
import org.axonframework.commandhandling.CommandHandlerInterceptor;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.commandhandling.InterceptorChain;
import org.axonframework.unitofwork.UnitOfWork;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class KasperCommandInterceptor implements CommandHandlerInterceptor {

    private final InterceptorChainRepository<Command, Command> interceptorChainRepository;

    public KasperCommandInterceptor(InterceptorChainRepository<Command, Command> interceptorChainRepository) {
        this.interceptorChainRepository = checkNotNull(interceptorChainRepository);
    }

    @Override
    public Object handle(CommandMessage<?> commandMessage, UnitOfWork unitOfWork, InterceptorChain interceptorChain) throws Throwable {
        final Optional<com.viadeo.kasper.core.interceptor.InterceptorChain<Command, Command>> interceptorChainOptional = interceptorChainRepository.get(commandMessage.getPayloadType());
        final CommandMessage newCommandMessage;

        if (interceptorChainOptional.isPresent()) {
            final Context context = (Context) commandMessage.getMetaData().get(Context.METANAME);

            final Map<String, Object> metaData = Maps.newHashMap();
            metaData.put(Context.METANAME, context);

            final Command newCommand = interceptorChainOptional.get().next(
                    (Command) commandMessage.getPayload(),
                    context
            );

            newCommandMessage = new GenericCommandMessage<>(newCommand).withMetaData(metaData);
        } else {
            newCommandMessage = commandMessage;
        }

        return interceptorChain.proceed(newCommandMessage);
    }
}
