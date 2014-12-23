package com.humangeo.graphene.movies.model.funnels;

import com.humangeo.graphene.movies.model.Person;
import graphene.model.Funnel;
import graphene.model.idl.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by bparrish on 12/23/14.
 */
public class PersonGEntityFunnel implements Funnel<Person, G_Entity> {
    @Override
    public G_Entity from(Person actor) {
        G_Entity entity = new G_Entity();

        // TODO: implement Person -> G_Entity

        return entity;
    }

    @Override
    public Person to(G_Entity entity) {
        throw new NotImplementedException();
    }
}
