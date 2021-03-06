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
package com.viadeo.kasper.platform.bundle.sample;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.annotation.XKasperDomain;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.component.event.DomainEvent;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.annotation.XKasperCommandHandler;
import com.viadeo.kasper.core.component.annotation.XKasperEventListener;
import com.viadeo.kasper.core.component.annotation.XKasperRepository;
import com.viadeo.kasper.core.component.command.AutowiredCommandHandler;
import com.viadeo.kasper.core.component.command.aggregate.Concept;
import com.viadeo.kasper.core.component.command.aggregate.annotation.XKasperConcept;
import com.viadeo.kasper.core.component.command.repository.AutowiredRepository;
import com.viadeo.kasper.core.component.event.listener.AutowiredEventListener;
import com.viadeo.kasper.core.component.query.AutowiredQueryHandler;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.platform.bundle.DomainBundle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class MyCustomDomainBox {

    public static final String MY_CUSTOM_DOMAIN_LABEL = "MyCustomDomain";

    @XKasperDomain(label = MY_CUSTOM_DOMAIN_LABEL, prefix = "test", description = "A domain definition used only for the test")
    public static class MyCustomDomain implements Domain { }

    @XKasperCommandHandler(domain = MyCustomDomain.class)
    public static class MyCustomCommandHandler extends AutowiredCommandHandler<MyCustomCommand> {
        @Override
        public CommandResponse handle(MyCustomCommand command) {
            if ( ! command.isSuccessful()) {
                throw new RuntimeException("I must failed!");
            }
            return CommandResponse.ok();
        }
    }

    @XKasperQueryHandler(domain = MyCustomDomain.class)
    public static class MyCustomQueryHandler extends AutowiredQueryHandler<MyCustomQuery, MyCustomQueryResult> {
        @Override
        public QueryResponse<MyCustomQueryResult> handle(MyCustomQuery query) {
            if ( ! query.isSuccessful()) {
                throw new RuntimeException("I must failed!");
            }
            return QueryResponse.of(new MyCustomQueryResult());
        }
    }

    @XKasperEventListener(domain = MyCustomDomain.class)
    public static class MyCustomEventListener extends AutowiredEventListener<MyCustomEvent> {
        @Override
        public EventResponse handle(Context context, MyCustomEvent event) {
            return EventResponse.success();
        }
    }

    @XKasperRepository()
    public static class MyCustomRepository extends AutowiredRepository<KasperID,MyCustomEntity> {

        @Override
        protected Optional<MyCustomEntity> doLoad(final KasperID aggregateIdentifier, final Long expectedVersion) {
            return null;
        }

        @Override
        protected void doSave(MyCustomEntity aggregate) {
            // nothing
        }

        @Override
        protected void doDelete(MyCustomEntity aggregate) {
            // nothing
        }
    }

    @XKasperConcept(domain = MyCustomDomain.class, label = "MyCustomEntity")
    public static class MyCustomEntity extends Concept { }

    public static class MyCustomCommand implements Command {
        private static final long serialVersionUID = -8986821788476319364L;

        private final boolean successful;

        public MyCustomCommand() {
            this(true);
        }

        public MyCustomCommand(boolean successful) {
            this.successful = successful;
        }

        public boolean isSuccessful() {
            return successful;
        }
    }

    public static class MyCustomQuery implements Query {
        private static final long serialVersionUID = -6151236712593564163L;

        private final boolean successful;

        public MyCustomQuery() {
            this(true);
        }

        public MyCustomQuery(boolean successful) {
            this.successful = successful;
        }

        public boolean isSuccessful() {
            return successful;
        }
    }

    public static class MyCustomQueryResult implements QueryResult { }

    public static class MyCustomEvent implements Event { }

    public static abstract class AbstractMyCustomEvent implements Event { }

    public static class MyCustomDomainEvent implements DomainEvent<MyCustomDomain> { }

    public static class MyCustomMalformedDomainEvent implements DomainEvent { }

    @Configuration
    public static class MyCustomDomainSpringConfiguration {

        @Bean
        public MyCustomCommandHandler myCustomCommandHandler() {
            return new MyCustomCommandHandler();
        }

        @Bean
        public MyCustomQueryHandler myCustomQueryHandler() {
            return new MyCustomQueryHandler();
        }

        @Bean
        public MyCustomEventListener myCustomEventListener() {
            return new MyCustomEventListener();
        }

        @Bean
        public MyCustomRepository myCustomRepository() {
            return new MyCustomRepository();
        }
    }

    public static DomainBundle getBundle() {
        return new DomainBundle.Builder(new MyCustomDomain())
                .with(new MyCustomCommandHandler())
                .with(new MyCustomQueryHandler())
                .with(new MyCustomEventListener())
                .with(new MyCustomRepository())
                .build();
    }

}
