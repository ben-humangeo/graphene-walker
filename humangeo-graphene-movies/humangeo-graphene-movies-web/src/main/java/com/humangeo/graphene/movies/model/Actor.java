package com.humangeo.graphene.movies.model;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

}
