// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga;

public class SagaInstantitationException extends RuntimeException {

    public SagaInstantitationException(String message, Throwable cause) {
        super(message, cause);
    }

}
