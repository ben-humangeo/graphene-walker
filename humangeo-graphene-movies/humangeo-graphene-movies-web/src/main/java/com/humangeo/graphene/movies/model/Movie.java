package com.humangeo.graphene.movies.model;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

}
