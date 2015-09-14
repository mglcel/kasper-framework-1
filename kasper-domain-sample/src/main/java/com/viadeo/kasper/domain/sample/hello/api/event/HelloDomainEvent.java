// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.hello.api.event;

import com.viadeo.kasper.api.component.event.DomainEvent;
import com.viadeo.kasper.domain.sample.hello.api.HelloDomain;

/**
 * The base event for hello domain
 */
public interface HelloDomainEvent extends DomainEvent<HelloDomain> { }