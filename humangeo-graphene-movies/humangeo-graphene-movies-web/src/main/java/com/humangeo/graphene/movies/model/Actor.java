package com.humangeo.graphene.movies.model;

import graphene.model.idl.*;
import graphene.model.idlhelper.PropertyHelper;
import org.apache.commons.collections.ListUtils;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bparrish on 12/23/14.
 */
public class Actor {

    private Logger _logger = LoggerFactory.getLogger(Actor.class);

    private String _name;
    private String _birthYear;

    private final String NAME = "name";
    private final String BORN = "born";

    public Actor(Node node) {
        try {
            _name = node.getProperty(NAME).toString();
//            _birthYear = node.getProperty(BORN).toString();
        } catch (NotFoundException nfe) {
            _logger.error("Error converting Node to Actor", nfe);
        }
    }

    public String getName() {
        return _name;
    }

    public String getBirthYear() {
        return _birthYear;
    }

    public List<G_Property> getProperties(G_Provenance provenance, G_Uncertainty uncertainty) {
        List<G_Property> properties = new ArrayList<>();

        G_Property nameProperty = new PropertyHelper(
                NAME,
                "Actor Name",
                _name,
                G_PropertyType.STRING,
                provenance,
                uncertainty,
                Collections.singletonList(G_PropertyTag.NAME));

        properties.add(nameProperty);

        return properties;
    }

}
