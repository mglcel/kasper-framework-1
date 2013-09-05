// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.specification.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.viadeo.kasper.ddd.specification.ISpecification;
import com.viadeo.kasper.ddd.specification.SpecificationErrorMessage;
import com.viadeo.kasper.ddd.specification.annotation.XSpecification;

import java.util.Map;

/**
 *
 * @param <T> The object class
 */
public abstract class Specification<T> implements ISpecification<T> {

	/**
	 * Cache for all specifications annotation (if present)
	 */
	@SuppressWarnings("rawtypes")
	private static final Map<Class<? extends Specification>, XSpecification> ANNOTATIONS =
            Maps.newConcurrentMap();
	
	// ----------------------------------------------------------------------
	
	/**
	 * @see EntitySpecification#isSatisfiedBy(Object)
	 */
	@Override
	public abstract boolean isSatisfiedBy(T entity);

	public boolean isSatisfiedBy(final T entity, final SpecificationErrorMessage errorMessage) {
		Preconditions.checkNotNull(errorMessage);
		final boolean isSatisfied = this.isSatisfiedBy(Preconditions.checkNotNull(entity));
		
		if (!isSatisfied) {
			final String message = getErrorMessage(entity);
			errorMessage.setMessage(message);
		}
				
		return isSatisfied;
	}
	
	// ----------------------------------------------------------------------
	
	protected String getDefaultErrorMessage(final T entity) {
		return this.getClass().getSimpleName() + " specification was not met";
	}
	
	protected String getErrorMessage(final T entity) {
		final XSpecification annotation;
		String errorMessage;
		
		if (!Specification.ANNOTATIONS.containsKey(this.getClass())) {
			annotation = this.getClass().getAnnotation(XSpecification.class);
            if (null != annotation) {
			    Specification.ANNOTATIONS.put(this.getClass(), annotation);
            }
		} else {
			annotation = Specification.ANNOTATIONS.get(this.getClass());
		}
		
		if (null != annotation) {			 
			if (!annotation.errorMessage().isEmpty()) {
				errorMessage = annotation.errorMessage();				
			} else {
				if (!annotation.description().isEmpty()) {
					errorMessage = "Specification not met : " + annotation.description();
				} else {
					errorMessage = getDefaultErrorMessage(entity);
				}
			}
		} else {
			errorMessage = getDefaultErrorMessage(entity);
		}

        errorMessage += " for value " + entity.toString();
		
		return errorMessage;
	}
	
	// ----------------------------------------------------------------------

	@Override
	public ISpecification<T> and(final ISpecification<T> specification) {
		return new AndSpecification<>(this, Preconditions.checkNotNull(specification));
	}

	@Override
	public ISpecification<T> or(final ISpecification<T> specification) {
		return new OrSpecification<>(this, Preconditions.checkNotNull(specification));
	}

	@Override
	public ISpecification<T> not() {
		return new NotSpecification<>(this);
	}

}