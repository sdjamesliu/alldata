package com.alibaba.tesla.gateway.server.gateway;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.gateway.filter.factory.StripPrefixGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteLocator;
import reactor.core.publisher.Flux;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.event.FilterArgsEvent;
import org.springframework.cloud.gateway.event.PredicateArgsEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.cloud.gateway.handler.AsyncPredicate;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.handler.predicate.RoutePredicateFactory;
import org.springframework.cloud.gateway.support.ConfigurationUtils;
import org.springframework.cloud.gateway.support.HasRouteId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.convert.ConversionService;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.validation.Validator;
import org.springframework.web.server.ServerWebExchange;

/**
 * {@link RouteLocator} that loads routes from a {@link RouteDefinitionLocator}.
 *
 * @author Spencer Gibb
 */
public class TeslaRouteDefinitionRouteLocator
    implements RouteLocator, BeanFactoryAware, ApplicationEventPublisherAware {

    /**
     * Default filters name.
     */
    public static final String DEFAULT_FILTERS = "defaultFilters";

    protected final Log logger = LogFactory.getLog(getClass());

    private final RouteDefinitionLocator routeDefinitionLocator;

    private final ConversionService conversionService;

    private final Map<String, RoutePredicateFactory> predicates = new LinkedHashMap<>();

    private final Map<String, GatewayFilterFactory> gatewayFilterFactories = new HashMap<>();

    private final GatewayProperties gatewayProperties;

    private final SpelExpressionParser parser = new SpelExpressionParser();

    private BeanFactory beanFactory;

    private ApplicationEventPublisher publisher;

    @Autowired
    private Validator validator;

    public TeslaRouteDefinitionRouteLocator(RouteDefinitionLocator routeDefinitionLocator,
                                            List<RoutePredicateFactory> predicates,
                                            List<GatewayFilterFactory> gatewayFilterFactories,
                                            GatewayProperties gatewayProperties, ConversionService conversionService) {
        this.routeDefinitionLocator = routeDefinitionLocator;
        this.conversionService = conversionService;
        initFactories(predicates);
        gatewayFilterFactories.forEach(
            factory -> this.gatewayFilterFactories.put(factory.name(), factory));
        this.gatewayProperties = gatewayProperties;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    private void initFactories(List<RoutePredicateFactory> predicates) {
        predicates.forEach(factory -> {
            String key = factory.name();
            if (this.predicates.containsKey(key)) {
                this.logger.warn("A RoutePredicateFactory named " + key
                    + " already exists, class: " + this.predicates.get(key)
                    + ". It will be overwritten.");
            }
            this.predicates.put(key, factory);
            if (logger.isInfoEnabled()) {
                logger.info("Loaded RoutePredicateFactory [" + key + "]");
            }
        });
    }

    @Override
    public Flux<Route> getRoutes() {
        return this.routeDefinitionLocator.getRouteDefinitions().map(this::convertToRoute)
            // TODO: error handling
            .map(route -> {
                if (logger.isDebugEnabled()) {
                    logger.debug("RouteDefinition matched: " + route.getId());
                }
                return route;
            });

        /*
         * TODO: trace logging if (logger.isTraceEnabled()) {
         * logger.trace("RouteDefinition did not match: " + routeDefinition.getId()); }
         */
    }

    private Route convertToRoute(RouteDefinition routeDefinition) {
        AsyncPredicate<ServerWebExchange> predicate = combinePredicates(routeDefinition);
        List<GatewayFilter> gatewayFilters = getFilters(routeDefinition);

        return Route.async(routeDefinition).asyncPredicate(predicate)
            .replaceFilters(gatewayFilters).build();
    }

    @SuppressWarnings("unchecked")
    List<GatewayFilter> loadGatewayFilters(String id,
                                           List<FilterDefinition> filterDefinitions) {
        ArrayList<GatewayFilter> ordered = new ArrayList<>(filterDefinitions.size());
        for (int i = 0; i < filterDefinitions.size(); i++) {
            FilterDefinition definition = filterDefinitions.get(i);
            GatewayFilterFactory factory = this.gatewayFilterFactories
                .get(definition.getName());
            if (factory == null) {
                throw new IllegalArgumentException(
                    "Unable to find GatewayFilterFactory with name "
                        + definition.getName());
            }
            Map<String, String> args = definition.getArgs();
            if (logger.isDebugEnabled()) {
                logger.debug("RouteDefinition " + id + " applying filter " + args + " to "
                    + definition.getName());
            }

            Map<String, Object> properties = factory.shortcutType().normalize(args,
                factory, this.parser, this.beanFactory);

            Object configuration = factory.newConfig();

            ConfigurationUtils.bind(configuration, properties,
                factory.shortcutFieldPrefix(), definition.getName(), validator);

            // some filters require routeId
            // TODO: is there a better place to apply this?
            if (configuration instanceof HasRouteId) {
                HasRouteId hasRouteId = (HasRouteId) configuration;
                hasRouteId.setRouteId(id);
            }
            if(configuration instanceof StripPrefixGatewayFilterFactory.Config){
                int parts = ((StripPrefixGatewayFilterFactory.Config)configuration).getParts();
                String genKey = definition.getArgs().get("_genkey_0");
                if(StringUtils.isNotBlank(genKey) && StringUtils.isNumeric(genKey)){
                    int value = Integer.parseInt(genKey);
                    if(parts != value){
                        logger.warn("genkey to parts failed, genkey_0=" + genKey + "parts=" + parts);
                        ((StripPrefixGatewayFilterFactory.Config)configuration).setParts(value);
                    }
                }else {
                    logger.error("genkey_0 failed, routId=" + id + ", filterDefine=" + definition.toString());
                }
            }

            GatewayFilter gatewayFilter = factory.apply(configuration);
            if (this.publisher != null) {
                this.publisher.publishEvent(new FilterArgsEvent(this, id, properties));
            }
            if (gatewayFilter instanceof Ordered) {
                ordered.add(gatewayFilter);
            }
            else {
                ordered.add(new OrderedGatewayFilter(gatewayFilter, i + 1));
            }
        }

        return ordered;
    }

    private List<GatewayFilter> getFilters(RouteDefinition routeDefinition) {
        List<GatewayFilter> filters = new ArrayList<>();

        // TODO: support option to apply defaults after route specific filters?
        if (!this.gatewayProperties.getDefaultFilters().isEmpty()) {
            filters.addAll(loadGatewayFilters(DEFAULT_FILTERS,
                this.gatewayProperties.getDefaultFilters()));
        }

        if (!routeDefinition.getFilters().isEmpty()) {
            filters.addAll(loadGatewayFilters(routeDefinition.getId(),
                routeDefinition.getFilters()));
        }

        AnnotationAwareOrderComparator.sort(filters);
        return filters;
    }

    private AsyncPredicate<ServerWebExchange> combinePredicates(
        RouteDefinition routeDefinition) {
        List<PredicateDefinition> predicates = routeDefinition.getPredicates();
        AsyncPredicate<ServerWebExchange> predicate = lookup(routeDefinition,
            predicates.get(0));

        for (PredicateDefinition andPredicate : predicates.subList(1,
            predicates.size())) {
            AsyncPredicate<ServerWebExchange> found = lookup(routeDefinition,
                andPredicate);
            predicate = predicate.and(found);
        }

        return predicate;
    }

    @SuppressWarnings("unchecked")
    private AsyncPredicate<ServerWebExchange> lookup(RouteDefinition route,
                                                     PredicateDefinition predicate) {
        RoutePredicateFactory<Object> factory = this.predicates.get(predicate.getName());
        if (factory == null) {
            throw new IllegalArgumentException(
                "Unable to find RoutePredicateFactory with name "
                    + predicate.getName());
        }
        Map<String, String> args = predicate.getArgs();
        if (logger.isDebugEnabled()) {
            logger.debug("RouteDefinition " + route.getId() + " applying " + args + " to "
                + predicate.getName());
        }

        Map<String, Object> properties = factory.shortcutType().normalize(args, factory,
            this.parser, this.beanFactory);
        Object config = factory.newConfig();
        ConfigurationUtils.bind(config, properties, factory.shortcutFieldPrefix(),
            predicate.getName(), validator, conversionService);
        if (this.publisher != null) {
            this.publisher.publishEvent(
                new PredicateArgsEvent(this, route.getId(), properties));
        }
        return factory.applyAsync(config);
    }

}