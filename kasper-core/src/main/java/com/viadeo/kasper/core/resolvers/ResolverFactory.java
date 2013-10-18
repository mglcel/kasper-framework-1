// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryAnswer;
import com.viadeo.kasper.cqrs.query.QueryService;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.Entity;
import com.viadeo.kasper.ddd.impl.Repository;
import com.viadeo.kasper.er.Concept;
import com.viadeo.kasper.er.Relation;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.EventListener;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Tool resolver for domain components
 */
public class ResolverFactory {

    private static ConcurrentMap<Class, Optional<Resolver>> cache = Maps.newConcurrentMap();

    private DomainResolver domainResolver;
    private CommandResolver commandResolver;
    private CommandHandlerResolver commandHandlerResolver;
    private EventListenerResolver eventListenerResolver;
    private QueryResolver queryResolver;
    private QueryAnswerResolver queryAnswerResolver;
    private QueryServiceResolver queryServiceResolver;
    private RepositoryResolver repositoryResolver;
    private EntityResolver entityResolver;
    private ConceptResolver conceptResolver;
    private RelationResolver relationResolver;
    private EventResolver eventResolver;

    private Map<Class, Resolver> resolvers = new LinkedHashMap<Class, Resolver>() {
        {
            put(Domain.class, domainResolver);
            put(Command.class, commandResolver);
            put(CommandHandler.class, commandHandlerResolver);
            put(EventListener.class, eventListenerResolver);
            put(Query.class, queryResolver);
            put(QueryAnswer.class, queryAnswerResolver);
            put(QueryService.class, queryServiceResolver);
            put(Repository.class, repositoryResolver);
            put(Event.class, eventResolver);

            /* Order is important here (Concept/Relation before Entity) */
            put(Concept.class, conceptResolver);
            put(Relation.class, relationResolver);
            put(Entity.class, entityResolver);
        }
    };

    // ------------------------------------------------------------------------

    public Optional<Resolver> getResolverFromClass(final Class clazz) {

        if (cache.containsKey(clazz)) {
            return cache.get(clazz);
        }

        Resolver resolver = null;

        for (final Map.Entry<Class, Resolver> resolverEntry : resolvers.entrySet()) {
            if (resolverEntry.getKey().isAssignableFrom(clazz)) {
                resolver = resolverEntry.getValue();
                break;
            }
        }

        final Optional<Resolver> optResolver = Optional.fromNullable(resolver);
        cache.put(clazz, optResolver);
        return optResolver;
    }

    // ------------------------------------------------------------------------

    public DomainResolver getDomainResolver() {
        return domainResolver;
    }

    public void setDomainResolver(final DomainResolver domainResolver) {
        this.domainResolver = checkNotNull(domainResolver);
    }

    //-------------------------------------------------------------------------

    public CommandResolver getCommandResolver() {
        return commandResolver;
    }

    public void setCommandResolver(final CommandResolver commandResolver) {
        this.commandResolver = checkNotNull(commandResolver);
    }

    //-------------------------------------------------------------------------

    public EventListenerResolver getEventListenerResolver() {
        return eventListenerResolver;
    }

    public void setEventListenerResolver(final EventListenerResolver eventListenerResolver) {
        this.eventListenerResolver = checkNotNull(eventListenerResolver);
    }

    //-------------------------------------------------------------------------

    public QueryResolver getQueryResolver() {
        return queryResolver;
    }

    public void setQueryResolver(final QueryResolver queryResolver) {
        this.queryResolver = checkNotNull(queryResolver);
    }

    //-------------------------------------------------------------------------

    public QueryAnswerResolver getQueryAnswerResolver() {
        return queryAnswerResolver;
    }

    public void setQueryAnswerResolver(final QueryAnswerResolver queryAnswerResolver) {
        this.queryAnswerResolver = checkNotNull(queryAnswerResolver);
    }

    //-------------------------------------------------------------------------

    public QueryServiceResolver getQueryServiceResolver() {
        return queryServiceResolver;
    }

    public void setQueryServiceResolver(final QueryServiceResolver queryServiceResolver) {
        this.queryServiceResolver = checkNotNull(queryServiceResolver);
    }

    //-------------------------------------------------------------------------

    public RepositoryResolver getRepositoryResolver() {
        return repositoryResolver;
    }

    public void setRepositoryResolver(final RepositoryResolver repositoryResolver) {
        this.repositoryResolver = checkNotNull(repositoryResolver);
    }

    //-------------------------------------------------------------------------

    public EntityResolver getEntityResolver() {
        return entityResolver;
    }

    public void setEntityResolver(final EntityResolver entityResolver) {
        this.entityResolver = checkNotNull(entityResolver);
    }

    //-------------------------------------------------------------------------

    public ConceptResolver getConceptResolver() {
        return conceptResolver;
    }

    public void setConceptResolver(final ConceptResolver conceptResolver) {
        this.conceptResolver = checkNotNull(conceptResolver);
    }

    //-------------------------------------------------------------------------

    public RelationResolver getRelationResolver() {
        return relationResolver;
    }

    public void setRelationResolver(final RelationResolver relationResolver) {
        this.relationResolver = checkNotNull(relationResolver);
    }

    //-------------------------------------------------------------------------

    public EventResolver getEventResolver() {
        return eventResolver;
    }

    public void setEventResolver(final EventResolver eventResolver) {
        this.eventResolver = checkNotNull(eventResolver);
    }

    // ------------------------------------------------------------------------

    public CommandHandlerResolver getCommandHandlerResolver() {
        return commandHandlerResolver;
    }

    public void setCommandHandlerResolver(final CommandHandlerResolver commandHandlerResolver) {
        this.commandHandlerResolver = checkNotNull(commandHandlerResolver);
    }

    // ------------------------------------------------------------------------

    public void clearCaches() {
        cache.clear();
        for (final Resolver resolver : resolvers.values()) {
            resolver.clearCache();
        }
    }

}