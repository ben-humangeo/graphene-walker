package com.humangeo.graphene.movies.model.graph;

import graphene.dao.FederatedPropertyGraphServer;
import graphene.services.HyperGraphBuilder;
import graphene.services.PropertyGraphBuilder;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.InjectService;

/**
 * Created by bparrish on 12/23/14.
 */
public class GraphServerModule {
    public static void bind(ServiceBinder binder) {
        binder.bind(PropertyGraphBuilder.class, PersonGraphBuilderImpl.class)
                .withId("Property");

        binder.bind(HyperGraphBuilder.class, PersonGraphBuilderImpl.class)
                .withId("HyperProperty").eagerLoad()
                .scope(ScopeConstants.PERTHREAD);
    }

    @Contribute(FederatedPropertyGraphServer.class)
    public static void contributeApplication2(
            Configuration<PropertyGraphBuilder> singletons,
            @InjectService("Property") PropertyGraphBuilder egb) {
        singletons.add(egb);
    }
}
