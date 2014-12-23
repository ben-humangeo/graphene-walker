package com.humangeo.graphene.movies.model.funnels;

import com.humangeo.graphene.movies.model.Actor;
import graphene.model.Funnel;
import graphene.model.idl.*;
import graphene.model.idlhelper.EntityHelper;
import graphene.model.idlhelper.PropertyHelper;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bparrish on 12/23/14.
 */
public class ActorGEntityFunnel implements Funnel<Actor, G_Entity> {
    @Override
    public G_Entity from(Actor actor) {
        G_Entity entity = new G_Entity();

        // TODO: implement Actor -> G_Entity

        return entity;
    }

    @Override
    public Actor to(G_Entity entity) {
        throw new NotImplementedException();
    }
}
