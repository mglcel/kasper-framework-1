// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.domain.impl;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.Entity;
import com.viadeo.kasper.event.domain.EntityEvent;
import org.joda.time.DateTime;

/**
 *
 * Base implementation for entity events
 *
 * @see com.viadeo.kasper.event.domain.EntityEvent
 */
public abstract class AbstractEntityEvent<D extends Domain, E extends Entity>
        extends AbstractDomainEvent<D> implements EntityEvent<D, E> {

	private static final long serialVersionUID = -1948165707419476512L;

	private KasperID entityId;
	private DateTime lastEntityModificationDate;

	// ------------------------------------------------------------------------
    protected AbstractEntityEvent() { /* For serialization */ }

	protected AbstractEntityEvent(final KasperID id, final DateTime lastModificationDate) {
		this.entityId = Preconditions.checkNotNull(id);
		this.lastEntityModificationDate = Preconditions.checkNotNull(lastModificationDate);
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.event.domain.EntityEvent#getEntityId()
	 */
	@Override
	public KasperID getEntityId() {
		return this.entityId;
	}

	// ------------------------------------------------------------------------

	public DateTime getEntityLastModificationDate() {
		return this.lastEntityModificationDate;
	}

}
