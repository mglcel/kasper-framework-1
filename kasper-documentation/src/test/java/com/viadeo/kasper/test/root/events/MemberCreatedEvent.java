// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.root.events;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.domain.EntityCreatedEvent;
import com.viadeo.kasper.test.root.Facebook;

@XKasperEvent(action = "created")
public class MemberCreatedEvent extends EntityCreatedEvent<Facebook> {
	private static final long serialVersionUID = -3530058587014151484L;

    protected MemberCreatedEvent(KasperID id) {
        super(id);
    }
}
