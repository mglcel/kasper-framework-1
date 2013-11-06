// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContext;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.CommandResponse.Status;
import com.viadeo.kasper.cqrs.command.KasperCommandMessage;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.cqrs.command.impl.AbstractCommandHandler;
import com.viadeo.kasper.ddd.Domain;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import java.util.Locale;

import static com.viadeo.kasper.exposition.TestContexts.CONTEXT_FULL;
import static com.viadeo.kasper.exposition.TestContexts.context_full;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HttpCommandExposerContextTest extends BaseHttpExposerTest<HttpCommandExposer> {

    public HttpCommandExposerContextTest() {
        Locale.setDefault(Locale.US);
    }

    @Override
    protected HttpCommandExposer createExposer(final ApplicationContext ctx) {
        return new HttpCommandExposer(ctx.getBean(CommandGateway.class), ctx.getBean(DomainLocator.class));
    }

    // ------------------------------------------------------------------------

    @Test
    public void testCommandNotFound() throws Exception {
        // Given
        final Command command = new ContextCheckCommand(CONTEXT_FULL);

        // When
        final CommandResponse response = client().send(context_full, command);

        // Then
        assertEquals(Status.OK, response.getStatus());
    }

    // ------------------------------------------------------------------------

    public class TestDomain implements Domain { }

    public static class ContextCheckCommand implements Command {
        private static final long serialVersionUID = 674842094842929150L;

        private String contextName;

        ContextCheckCommand() { }

        ContextCheckCommand(final String contextName) {
            this.contextName = contextName;
        }

        public String getContextName() {
            return this.contextName;
        }

    }

    @XKasperCommandHandler(domain = TestDomain.class)
    public static class ContextCheckCommandHandler extends AbstractCommandHandler<ContextCheckCommand> {
        @Override
        public CommandResponse handle(final KasperCommandMessage<ContextCheckCommand> message) throws Exception {
            if (message.getCommand().getContextName().contentEquals(CONTEXT_FULL)) {

                /* Kasper correlation id is set by the gateway or auto-expo layer */
                final DefaultContext clonedContext = ((DefaultContext) message.getContext()).child();
                clonedContext.setKasperCorrelationId(context_full.getKasperCorrelationId());

                assertTrue(clonedContext.equals(context_full));
            }
            return CommandResponse.ok();
        }
    }

}
