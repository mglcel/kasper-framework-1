// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.annotation.Immutable;
import org.joda.time.DateTime;

import java.io.Serializable;

public interface QueryEntityPayload extends QueryPayload {

    KasperID getId();

    String getType();

    DateTime getLastModificationTime();

}
