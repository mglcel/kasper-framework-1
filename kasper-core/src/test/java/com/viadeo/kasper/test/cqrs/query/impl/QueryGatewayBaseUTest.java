package com.viadeo.kasper.test.cqrs.query.impl;

import com.google.common.base.Optional;
import com.viadeo.kasper.context.IContext;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.cqrs.query.IQuery;
import com.viadeo.kasper.cqrs.query.IQueryDTO;
import com.viadeo.kasper.cqrs.query.IQueryMessage;
import com.viadeo.kasper.cqrs.query.IQueryService;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.cqrs.query.impl.QueryGatewayBase;
import com.viadeo.kasper.locators.IQueryServicesLocator;
import org.junit.Test;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QueryGatewayBaseUTest {

    private class ServiceWhichRaiseExceptionQuery implements IQuery {}
    private class TestDTO implements IQueryDTO {}

    public class ServiceWhichRaiseException implements IQueryService<ServiceWhichRaiseExceptionQuery, TestDTO> {
        @Override
        public TestDTO retrieve(final IQueryMessage<ServiceWhichRaiseExceptionQuery> message) throws KasperQueryException {
            throw new KasperQueryException("Exception in the service implementation");
        }
    }

    private IContext defaultContext() {
        return new DefaultContextBuilder().buildDefault();
    }

    private QueryGatewayBase getQueryGatewayForQueryAndService(IQuery query, IQueryService service) {

        // Associates Query and Service
        IQueryServicesLocator locator = mock(IQueryServicesLocator.class);
        when(locator.getServiceFromQueryClass(query.getClass())).thenReturn(Optional.of((IQueryService)service));

        // Create the queryGateway with mocked locator
        QueryGatewayBase queryGateway = new QueryGatewayBase();
        queryGateway.setQueryServicesLocator(locator);

        return queryGateway;
    }

    @Test
    public void retrieve_shouldWrapAnyException_InKasperException() throws KasperQueryException {

        // Given - a_service_which_raise_exception;
        IQuery query = new ServiceWhichRaiseExceptionQuery();
        ServiceWhichRaiseException service = new ServiceWhichRaiseException();
        QueryGatewayBase queryGateway = getQueryGatewayForQueryAndService(query, service);

        // When
        try {
            queryGateway.retrieve(defaultContext(), query);
            fail("Should raise a KasperQueryException");
        }
        // Then
        catch (KasperQueryException e) {
            // OK. Expected KasperQueryException
        }

    }
}


//  TestService service = Mockito.spy(new TestService());
//  doThrow(new KasperQueryException("OK L exception !!")).when(service).retrieve(Matchers.<IQueryMessage<TestQuery>>any());

