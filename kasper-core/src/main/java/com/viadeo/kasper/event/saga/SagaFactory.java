// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga;

public interface  SagaFactory {
    public <SAGA extends Saga> SAGA create(Object identifier, Class<SAGA> sagaClass) throws SagaInstantitationException;
}
