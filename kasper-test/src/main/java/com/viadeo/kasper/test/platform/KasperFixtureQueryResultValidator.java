// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;

public interface KasperFixtureQueryResultValidator extends KasperFixtureResultValidator {

    KasperFixtureQueryResultValidator expectReturnResponse(QueryResponse commandResponse);

    KasperFixtureQueryResultValidator expectReturnError(KasperReason commandResponse);

    KasperFixtureQueryResultValidator expectReturnRefused(KasperReason commandResponse);

    KasperFixtureQueryResultValidator expectReturnResponse(QueryResult commandResponse);

}
