// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform.bundle.descriptor;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.annotation.XKasperCommandHandler;
import com.viadeo.kasper.core.component.annotation.XKasperEventListener;
import com.viadeo.kasper.core.component.command.AutowiredCommandHandler;
import com.viadeo.kasper.core.component.command.aggregate.Concept;
import com.viadeo.kasper.core.component.command.aggregate.Relation;
import com.viadeo.kasper.core.component.command.repository.AutowiredRepository;
import com.viadeo.kasper.core.component.event.listener.AutowiredEventListener;
import com.viadeo.kasper.core.component.query.AutowiredQueryHandler;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.platform.bundle.sample.MyCustomDomainBox;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class DomainDescriptorFactoryUTest {

    public static class TestCommand implements Command { }

    @XKasperCommandHandler(domain = TestDomain.class)
    public static class TestCommandHandler extends AutowiredCommandHandler<TestCommand> { }

    public static class TestQuery implements Query { }

    public static class TestQueryResult implements QueryResult { }

    @XKasperQueryHandler(domain = TestDomain.class)
    public static class TestQueryHandler extends AutowiredQueryHandler<TestQuery, TestQueryResult> { }

    public static class TestEvent implements Event { }

    @XKasperEventListener(domain = TestDomain.class)
    public static class TestEventListener extends AutowiredEventListener<TestEvent> {
        @Override
        public EventResponse handle(Context context, TestEvent event) {
            return EventResponse.success();
        }
    }

    public static class TestConcept extends Concept {
        @EventHandler
        public void doStuff(final TestEvent event) {
        }
    }

    public static class TestRelation extends Relation<TestConcept, TestConcept> {
        @EventHandler
        public void doStuff(final TestEvent event) {
        }
    }

    public static class TestRepository extends AutowiredRepository<KasperID,TestConcept> {
        @Override
        protected Optional<TestConcept> doLoad(final KasperID aggregateIdentifier, final Long expectedVersion) {
            return Optional.absent();
        }

        @Override
        protected void doSave(final TestConcept aggregate) {
        }

        @Override
        protected void doDelete(final TestConcept aggregate) {
        }
    }

    private static class TestDomain implements Domain {}

    // ------------------------------------------------------------------------

    @Test
    public void toCommandHandlerDescriptor_fromCommandHandler_shouldBeOk() {
        // Given

        // When
        final CommandHandlerDescriptor descriptor =
                DomainDescriptorFactory.toCommandHandlerDescriptor(
                        new TestCommandHandler()
                );

        // Then
        assertNotNull(descriptor);
        assertEquals(TestCommandHandler.class, descriptor.getReferenceClass());
        assertEquals(TestCommand.class, descriptor.getCommandClass());
    }

    @Test
    public void toQueryHandlerDescriptor_fromQueryHandler_shouldBeOk() {
        // Given

        // When
        final QueryHandlerDescriptor descriptor =
                DomainDescriptorFactory.toQueryHandlerDescriptor(
                    new TestQueryHandler()
                );

        // Then
        assertNotNull(descriptor);
        assertEquals(TestQueryHandler.class, descriptor.getReferenceClass());
        assertEquals(TestQuery.class, descriptor.getQueryClass());
        assertEquals(TestQueryResult.class, descriptor.getQueryResultClass());
    }

    @Test
    public void toEventListenerDescriptor_fromEventListener_shouldBeOk() {
        // Given

        // When
        final EventListenerDescriptor descriptor =
                DomainDescriptorFactory.toEventListenerDescriptor(
                    new TestEventListener()
                );

        // Then
        assertNotNull(descriptor);
        assertEquals(TestEventListener.class, descriptor.getReferenceClass());
        assertTrue(descriptor.getEventClasses().contains(TestEvent.class));
    }

    @Test
    public void toAggregateDescriptor_fromConcept_shouldBeOk() {
        // Given

        // When
        final AggregateDescriptor descriptor =
                DomainDescriptorFactory.toAggregateDescriptor(
                    TestConcept.class
                );

        // Then
        assertNotNull(descriptor);
        assertEquals(TestConcept.class, descriptor.getReferenceClass());
        assertNull(descriptor.getSourceClass());
        assertNull(descriptor.getTargetClass());
        assertNotNull(descriptor.getSourceEventClasses());
        assertEquals(1, descriptor.getSourceEventClasses().size());
        assertTrue(descriptor.getSourceEventClasses().contains(TestEvent.class));
    }

    @Test
    public void toAggregateDescriptor_fromRelation_shouldBeOk() {
        // When
        final AggregateDescriptor descriptor =
                DomainDescriptorFactory.toAggregateDescriptor(
                    TestRelation.class
                );

        // Then
        assertNotNull(descriptor);
        assertEquals(TestRelation.class, descriptor.getReferenceClass());
        assertEquals(TestConcept.class, descriptor.getSourceClass());
        assertEquals(TestConcept.class, descriptor.getTargetClass());
        assertNotNull(descriptor.getSourceEventClasses());
        assertEquals(1, descriptor.getSourceEventClasses().size());
        assertTrue(descriptor.getSourceEventClasses().contains(TestEvent.class));
    }

    @Test
    public void toRepositoryDescriptor_fromRepository_shouldBeOk() {
        // When
        final RepositoryDescriptor descriptor =
                DomainDescriptorFactory.toRepositoryDescriptor(
                    new TestRepository()
                );

        // Then
        assertNotNull(descriptor);
        assertEquals(TestRepository.class, descriptor.getReferenceClass());
        final AggregateDescriptor aggregateDescriptor = descriptor.getAggregateDescriptor();
        assertNotNull(aggregateDescriptor);
        assertEquals(TestConcept.class, aggregateDescriptor.getReferenceClass());
    }

    @Test
    public void retrieveEventsFrom_shouldNeOk() {
        Collection<Class<? extends Event>> classes = DomainDescriptorFactory.retrieveEventsFrom(MyCustomDomainBox.MyCustomDomain.class);
        assertNotNull(classes);
        assertTrue(classes.contains(MyCustomDomainBox.MyCustomEvent.class));
        assertTrue(classes.contains(MyCustomDomainBox.MyCustomDomainEvent.class));
        assertTrue(classes.contains(MyCustomDomainBox.MyCustomMalformedDomainEvent.class));
        assertFalse(classes.contains(MyCustomDomainBox.AbstractMyCustomEvent.class));
    }
}
