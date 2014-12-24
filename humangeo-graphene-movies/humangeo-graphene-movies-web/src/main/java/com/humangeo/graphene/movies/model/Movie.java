package com.humangeo.graphene.movies.model;

import com.humangeo.graphene.movies.utils.RandomUtils;
import graphene.model.idl.*;
import graphene.model.idlhelper.PropertyHelper;
import graphene.model.idlhelper.SingletonRangeHelper;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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

    //<editor-fold desc="private properties">
    private Logger _logger = LoggerFactory.getLogger(Movie.class);

    private long _id;
    private String _title;
    //</editor-fold>

    //<editor-fold desc="constants">
    private final String TITLE = "title";
    //</editor-fold>

    //<editor-fold desc="constructors">
    // force someone to build these entities with a graph Node
    private Movie() { }

    public Movie(Node node) {
        try {
            _id = node.getId();
            _title = node.getProperty(TITLE).toString();
        } catch (NotFoundException nfe) {
            _logger.error("Error converting Node to Movie", nfe);
        }
    }
    //</editor-fold>

    //<editor-fold desc="public getters">
    public long getId() { return _id; }

    public String getTitle() {
        return _title;
    }
    //</editor-fold>

    //<editor-fold desc="public methods">
    public List<G_Property> getProperties(G_Provenance provenance, G_Uncertainty uncertainty) {
        List<G_Property> properties = new ArrayList<>();

        G_Property idProperty = new PropertyHelper(
                "ID",
                "Movie Graph ID",
                _id,
                G_PropertyType.LONG,
                provenance,
                uncertainty,
                Collections.singletonList(G_PropertyTag.ID));

        properties.add(idProperty);

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
    //</editor-fold>

    //<editor-fold desc="Object overrides">
    @Override
    public String toString() {
        return String.format(
                "{\"id\":%s,\"title\":\"%s\"",
                _id,
                _title
        );
    }

    @Override
    public int hashCode() {
        int first = RandomUtils.getRandomNonZeroOddNumber(1,Integer.MAX_VALUE);

        int second = RandomUtils.getRandomNonZeroOddNumber(1,Integer.MAX_VALUE);

        return HashCodeBuilder.reflectionHashCode(first, second, this);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Movie)) {
            return false;
        }

        Movie movie = (Movie) obj;

        if (movie.getId() == 0) {
            return false;
        }

        if (this.getId() == movie.getId() &&
                this.getTitle().equals(movie.getTitle())) {
            return true;
        }

        return false;
    }
    //</editor-fold>
}
