package com.humangeo.graphene.movies.model;

import graphene.model.idl.*;
import graphene.model.idlhelper.PropertyHelper;
import graphene.model.idlhelper.SingletonRangeHelper;
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
public class Movie {

    private Logger _logger = LoggerFactory.getLogger(Movie.class);

    private String _title;
    private String _birthYear;

    private final String TITLE = "title";
    private final String BORN = "born";

    public Movie(Node node) {
        try {
            _title = node.getProperty(TITLE).toString();
//            _birthYear = node.getProperty(BORN).toString();
        } catch (NotFoundException nfe) {
            _logger.error("Error converting Node to Movie", nfe);
        }
    }

    public String getTitle() {
        return _title;
    }

    public String getBirthYear() {
        return _birthYear;
    }

    public List<G_Property> getProperties(G_Provenance provenance, G_Uncertainty uncertainty) {
        List<G_Property> properties = new ArrayList<>();

        G_Property titleProperty = new PropertyHelper(
                TITLE,
                "Movie Title",
                _title,
                G_PropertyType.STRING,
                provenance,
                uncertainty,
                Collections.singletonList(G_PropertyTag.NAME));

        properties.add(titleProperty);

        return properties;
    }
}
