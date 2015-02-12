// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.event.EventResponse;
import com.viadeo.kasper.event.annotation.XKasperEventListener;
import com.viadeo.kasper.exception.KasperException;
import org.junit.Test;

import static org.junit.Assert.*;

public class EventListenerResolverTest {

    @XKasperUnregistered
    private static class TestDomain implements Domain {}

    @XKasperUnregistered
    @XKasperEventListener( domain = TestDomain.class )
    private static class TestEventListener extends EventListener {
        @Override
        public EventResponse handle(Context context, Event event) {
            return EventResponse.ok();
        }
    }

    @XKasperUnregistered
    private static class TestEvent implements Event { }

    @XKasperUnregistered
    private static class TestEventListener2 extends EventListener<TestEvent> {
        @Override
        public EventResponse handle(Context context, TestEvent event) {
            return EventResponse.ok();
        }
    }

    // ------------------------------------------------------------------------

    @Test
    public void testGetDomainFromEventListener() {
        // Given
        final EventListenerResolver resolver = new EventListenerResolver();

        // When
        final Optional<Class<? extends Domain>> domain =
                resolver.getDomainClass(TestEventListener.class);

        // Then
        assertTrue(domain.isPresent());
        assertEquals(TestDomain.class, domain.get());
    }

    @Test
    public void testGetEventFromValidHandler() {
        // Given
        final EventListenerResolver resolver = new EventListenerResolver();

        // When
        final Class<? extends Event> command =
                resolver.getEventClass(TestEventListener2.class);

        // Then
        assertEquals(TestEvent.class, command);
    }

    @Test
    public void testGetEventFromInvalidHandler() {
        // Given
        final EventListenerResolver resolver = new EventListenerResolver();

        // When
        try {
            resolver.getEventClass(TestEventListener.class);
            fail();
        } catch (final KasperException e) {
            // Then exception is raised
        }
    }

}
