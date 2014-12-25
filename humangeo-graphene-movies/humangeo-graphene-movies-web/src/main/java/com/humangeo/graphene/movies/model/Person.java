package com.humangeo.graphene.movies.model;

import com.humangeo.graphene.movies.utils.RandomUtils;
import graphene.model.idl.*;
import graphene.model.idlhelper.PropertyHelper;
import mil.darpa.vande.generic.V_GenericNode;
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
public class Person {

    //<editor-fold desc="private properties">
    private Logger _logger = LoggerFactory.getLogger(Person.class);

    private long _id;
    private String _name;
    private String _born;
    //</editor-fold>

    //<editor-fold desc="constants">
    public final String NAME = "name";
    public final String BORN = "born";
    //</editor-fold>

    //<editor-fold desc="constructors">
    // force someone to build these entities with a graph Node
    private Person() { }

    public Person(Node node) {
        try {
            _id = node.getId();
            _name = node.getProperty(NAME).toString();
//            _born = node.getProperty(BORN).toString();
        } catch (NotFoundException nfe) {
            _logger.error("Error converting Node to Actor", nfe);
        }
    }
    //</editor-fold>

    //<editor-fold desc="public getters">
    public long getId() { return _id; }

    public String getName() {
        return _name;
    }

    public String getBirthYear() {
        return _born;
    }
    //</editor-fold>

    //<editor-fold desc="public methods">
    public V_GenericNode getAsGenericNode() {
        V_GenericNode node = new V_GenericNode();

        node.setId("" + _id);
        node.addData(NAME, _name);
        node.addData(BORN, _born);

        return node;
    }

    public List<G_Property> getProperties(G_Provenance provenance, G_Uncertainty uncertainty) {
        List<G_Property> properties = new ArrayList<>();

        G_Property idProperty = new PropertyHelper(
                "ID",
                "Actor Graph ID",
                _id,
                G_PropertyType.LONG,
                provenance,
                uncertainty,
                Collections.singletonList(G_PropertyTag.ID));

        properties.add(idProperty);

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

    public String getColor() {
        return "#00FF00";
    }
    //</editor-fold>

    //<editor-fold desc="Object overrides">
    @Override
    public String toString() {
        return String.format(
                "{\"id\":%d,\"name\":\"%s\",\"birthYear\":%s",
                _id,
                _name,
                (_born != null) ? _born : ""
        );
    }

    @Override
    public int hashCode() {
        int first = RandomUtils.getRandomNonZeroOddNumber(1, Integer.MAX_VALUE);

        int second = RandomUtils.getRandomNonZeroOddNumber(1,Integer.MAX_VALUE);

        return HashCodeBuilder.reflectionHashCode(first, second, this);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Person)) {
            return false;
        }

        Person person = (Person) obj;

        if (person.getId() == 0) {
            return false;
        }

        if (this.getId() == person.getId() &&
                this.getName().equals(person.getName()) &&
                this.getBirthYear().equals(person.getBirthYear())) {
            return true;
        }

        return false;
    }
    //</editor-fold>
}
