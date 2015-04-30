// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.viadeo.kasper.client.platform.domain.DefaultDomainBundle;
import com.viadeo.kasper.client.platform.domain.DomainBundle;
import com.viadeo.kasper.core.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.interceptor.EventInterceptorFactory;
import com.viadeo.kasper.core.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.KasperCommandMessage;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.exposition.http.HttpCommandExposerPlugin;
import org.junit.Test;

import java.util.Locale;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HttpCommandExposerContextTest extends BaseHttpExposerTest {

    public static final String RETURNED_SECURITY_TOKEN = UUID.randomUUID().toString();

    public HttpCommandExposerContextTest() {
        Locale.setDefault(Locale.US);
    }

    @Override
    protected HttpCommandExposerPlugin createExposerPlugin() {
        return new HttpCommandExposerPlugin();
    }

    @Override
    protected DomainBundle getDomainBundle(){
        return new DefaultDomainBundle(
                Lists.<CommandHandler>newArrayList(new ContextCheckCommandHandler())
                , Lists.<QueryHandler>newArrayList()
                , Lists.<Repository>newArrayList()
                , Lists.<EventListener>newArrayList()
                , Lists.<QueryInterceptorFactory>newArrayList()
                , Lists.<CommandInterceptorFactory>newArrayList()
                , Lists.<EventInterceptorFactory>newArrayList()
                , new TestDomain()
                , "TestDomain"
        );
    }

    // ------------------------------------------------------------------------

    @Test
    public void testCommandNotFound() throws Exception {
        // Given
        final Command command = new ContextCheckCommand(getContextName());

        // When
        final CommandResponse response = client().send(getFullContext(), command);

        // Then
        assertTrue(response.isOK());
        assertTrue(response.getSecurityToken().isPresent());
        assertEquals(RETURNED_SECURITY_TOKEN, response.getSecurityToken().get());
    }

    // ------------------------------------------------------------------------

    public class TestDomain implements Domain { }

    public static class ContextCheckCommand implements Command {
        private static final long serialVersionUID = 674842094842929150L;

        private String contextName;

        @JsonCreator
        public ContextCheckCommand(@JsonProperty("contextName") final String contextName) {
            this.contextName = contextName;
        }

        public String getContextName() {
            return this.contextName;
        }

    }

    @XKasperCommandHandler(domain = TestDomain.class)
    public static class ContextCheckCommandHandler extends CommandHandler<ContextCheckCommand> {
        @Override
        public CommandResponse handle(final KasperCommandMessage<ContextCheckCommand> message) throws Exception {
            return CommandResponse.ok().withSecurityToken(RETURNED_SECURITY_TOKEN);
        }
    }

}

