package com.humangeo.graphene.movies.model.graph;

import com.humangeo.graphene.movies.annotations.Neo4jCypherAccessor;
import com.humangeo.graphene.movies.dao.neo4j.Neo4jCypherDAOAccessor;
import com.humangeo.graphene.movies.dao.neo4j.Neo4jCypherQuery;
import com.humangeo.graphene.movies.model.Movie;
import com.humangeo.graphene.movies.model.Person;
import graphene.dao.EntityRefDAO;
import graphene.dao.GenericDAO;
import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_IdType;
import graphene.model.query.EntityQuery;
import graphene.services.HyperGraphBuilder;
import graphene.services.PropertyGraphBuilder;
import graphene.util.validator.ValidationUtils;
import mil.darpa.vande.generic.*;
import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by bparrish on 12/23/14.
 */
public class PersonGraphBuilderImpl extends PropertyGraphBuilder<Person> implements HyperGraphBuilder<Person> {

    private Logger logger = LoggerFactory.getLogger(PersonGraphBuilderImpl.class);

    private Neo4jCypherDAOAccessor _neo4jCypherDAOAccessor;

    //<editor-fold desc="constructors">
    // force to use the constructor that has the EntityRefDAO parameter
    private PersonGraphBuilderImpl() { }

    /***
     * Default Constructor
     *
     * @see com.humangeo.graphene.movies.dao.MoviesDAOModule - sets up the EntityRefDAO injection
     * @param dao
     */
    public PersonGraphBuilderImpl(@Neo4jCypherAccessor Neo4jCypherDAOAccessor neo4jCypherDAOAccessor, @Inject EntityRefDAO dao) {
        _neo4jCypherDAOAccessor = neo4jCypherDAOAccessor;

        this.dao = dao;
    }
    //</editor-fold>

    @Override
    public V_GenericGraph makeGraphResponse(final V_GraphQuery graphQuery) throws Exception {
        if (graphQuery.getMaxHops() <= 0) {
            return new V_GenericGraph();
        } else {
            logger.debug("Attempting a graph for query " + graphQuery.toString());
        }

        edgeList = new V_EdgeList(null);

        for (String searchId : graphQuery.getSearchIds()) {
            String cypherQuery = new Neo4jCypherQuery()
                    .start("p=node(" + searchId + ")")
                    .match("p-[]->(m)")
                    .ret("p,m").build();

            Map<String, List<Node>> nodes = _neo4jCypherDAOAccessor.get(cypherQuery, "p", "m");

            List<Node> personNodes = nodes.get("p");
            List<Node> movieNodes = nodes.get("m");

            Transaction tx = _neo4jCypherDAOAccessor.getGraphDbService().beginTx();

            try {
                for (int i = 0; i < personNodes.size(); i++) {
                    Node personNode = personNodes.get(i);
                    V_GenericNode personGenericNode = nodeList.getNode("" + personNode.getId());
                    if (personGenericNode == null) {
                        Person person = new Person(personNode);
                        personGenericNode = person.getAsGenericNode();
                        nodeList.addNode(personGenericNode);
                    }

                    Node movieNode = movieNodes.get(i);
                    V_GenericNode movieGenericNode = nodeList.getNode("" + movieNode.getId());
                    if (movieGenericNode == null) {
                        Movie movie = new Movie(movieNode);
                        movieGenericNode = movie.getAsGenericNode();
                        nodeList.addNode(movieGenericNode);
                    }

                    V_GenericEdge edge = new V_GenericEdge(personGenericNode, movieGenericNode);
                    edgeList.addEdge(edge);
                }

                tx.success();
            } finally {
                tx.close();
            }
        }

        performPostProcess(graphQuery);

        V_GenericGraph g = new V_GenericGraph(nodeList.getNodes(), edgeList.getEdges());

        return g;
    }

    //<editor-fold desc="HyperGraphBuilder overrides">
    @Override
    public boolean callBack(Person person) {
        if (ValidationUtils.isValid(person.getName())) {
            String personName = person.getName();

            V_GenericNode personNode = nodeList.getNode(personName);

            if (personNode == null) {
                G_IdType name;

                try {
                    name = nodeTypeAccess.getNodeType(G_CanonicalPropertyType.NAME.name());

                    personNode = new V_GenericNode(personName);
                    personNode.setIdType("name");
                    personNode.setNodeType(name.getName());
                    personNode.setIdVal(personName);
                    personNode.setValue(personName);
                    personNode.setLabel("Name");
                    personNode.setColor(person.getColor());
                } catch (AvroRemoteException e) {
                    e.printStackTrace();
                }

				/*
				 * This is kind of business logic-like. The customer node also
				 * gets any id properties baked in.
				 */
//                if (ValidationUtils.isValid(person.getId())) {
//                    int idTypeId = p.getIdtypeId();
//                    custNode.addData("ShortName", idTypeDAO.getShortName(idTypeId));
//                    custNode.addData("Value", "" + person.getId());
//                    custNode.addData("Family", idTypeDAO.getNodeType(idTypeId));
//
//                }
                unscannedNodeList.add(personNode);
                nodeList.addNode(personNode);
            }

        }

//        if (ValidationUtils.isValid(acno)) {
//            acnoNode = nodeList.getNode(acno);
//            if (acnoNode == null) {
//                G_IdType account;
//                try {
//                    account = nodeTypeAccess.getNodeType(G_CanonicalPropertyType.ACCOUNT.name());
//
//                    acnoNode = new V_GenericNode(acno);
//                    acnoNode.setIdType(idTypeDAO.getNodeType(p.getIdtypeId()));
//                    acnoNode.setNodeType(account.getName());
//                    acnoNode.setIdVal(acno);
//                    acnoNode.setValue(acno);
//                    acnoNode.setLabel(acno);
//                    acnoNode.setColor("#00FF00");
//                    unscannedNodeList.add(acnoNode);
//                    nodeList.addNode(acnoNode);
//                } catch (AvroRemoteException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        if (ValidationUtils.isValid(identifier, p.getIdtypeId())) {
//            String nodeId = identifier + p.getIdtypeId();
//            idNode = nodeList.getNode(nodeId);
//            String nodeType = idTypeDAO.getNodeType(p.getIdtypeId());
//
//            if (idNode == null) {
//                idNode = new V_GenericNode(nodeId);
//                // logger.debug("Adding identifier node with value " + key);
//                acnoNode.setNodeType(nodeType);
//                idNode.setIdType(nodeType);
//                idNode.setIdVal(identifier);
//                idNode.setValue(identifier);
//                idNode.setLabel(identifier);
//                //idNode.addProperty(idFamily, identifier);
//                if (custNode != null) {
//                    // also add it to the customer, this is a legacy thing to
//                    // embed more data in the important nodes. --djue
//                    custNode.addProperty(nodeType, identifier);
//                }
//
//                //TODO: adjust these properties, as they may not be relevant/correct
//
//                if (nodeType.equals(G_CanonicalPropertyType.PHONE.name())) {
//                    //idNode.addProperty("color", "green");
//                    idNode.setColor("#00FF00");
//                }
//                if (nodeType.equals(G_CanonicalPropertyType.EMAIL_ADDRESS.name())) {
//                    //idNode.addProperty("color", "aqua");
//                    idNode.setColor("#0088FF");
//                }
//                if (nodeType.equals(G_CanonicalPropertyType.ADDRESS.name())) {
//                    //idNode.addProperty("color", "gray");
//                    idNode.setColor("#888888");
//                }
//                unscannedNodeList.add(idNode);
//                nodeList.addNode(idNode);
//            }
//
//            if (custNode != null && idNode != null) {
//                String key = generateEdgeId(custNode.getId(), idNode.getId());
//                if (key != null && !edgeMap.containsKey(key)) {
//                    V_GenericEdge v = new V_GenericEdge(custNode, idNode);
//                    G_CanonicalRelationshipType rel = G_CanonicalRelationshipType.HAS_ID;
//                    if (nodeType.equals(G_CanonicalPropertyType.PHONE.name())) {
//                        rel = G_CanonicalRelationshipType.COMMERCIAL_ID_OF;
//                    }
//                    if (nodeType.equals(G_CanonicalPropertyType.EMAIL_ADDRESS.name())) {
//                        rel = G_CanonicalRelationshipType.COMMERCIAL_ID_OF;
//                    }
//                    if (nodeType.equals(G_CanonicalPropertyType.ADDRESS.name())) {
//                        rel = G_CanonicalRelationshipType.ADDRESS_OF;
//                    }
//                    v.setIdType(rel.name());
//                    v.setLabel(null);
//                    v.setIdVal(rel.name());
//                    v.addData("Relationship type", G_CanonicalRelationshipType.OWNER_OF.name());
//                    v.addData("Source Column", p.getIdentifiercolumnsource());
//                    v.addData("Source Table", p.getIdentifiertablesource());
//                    edgeMap.put(key, v);
//                }
//
//            }
//        }
//
//        if (custNode != null && acnoNode != null) {
//            String key = generateEdgeId(custNode.getId(), acnoNode.getId());
//            if (!edgeMap.containsKey(key)) {
//                V_GenericEdge v = new V_GenericEdge(custNode, acnoNode);
//                v.setIdType(G_CanonicalRelationshipType.OWNER_OF.name());
//                v.setLabel(null);
//                v.setIdVal(G_CanonicalRelationshipType.OWNER_OF.name());
//                v.addData("Relationship type", G_CanonicalRelationshipType.OWNER_OF.name());
//                v.addData("Source Column", p.getIdentifiercolumnsource());
//                v.addData("Source Table", p.getIdentifiertablesource());
//
//                edgeMap.put(key, v);
//            }
//        }

        return true;
    }

    @Override
    public void performPostProcess(V_GraphQuery graphQuery) {
        Iterator<String> iteratorIds = graphQuery.getSearchIds().iterator();

        logger.debug("Performing post process");

        String color = "red";

        while (iteratorIds.hasNext()) {
            String id = iteratorIds.next();

            try {
                V_GenericNode searched = nodeList.getNode(id);

                searched.setColor(color);

                logger.debug("Node with id(" + id + ") is now " + color);
            } catch (Exception e) {
                logger.error("Could not find node with id = " + id);
            }
        }
    }

    @Override
    public GenericDAO<Person, EntityQuery> getDAO() {
        return null;
    }

    @Override
    public void buildQueryForNextIteration(V_GenericNode... nodes) {

    }

    @Override
    public V_GenericNode createOrUpdateNode(String id, String idType, String nodeType, V_GenericNode attachTo, String relationType, String relationValue) {
        return null;
    }

    @Override
    public V_GenericNode createOrUpdateNode(String id, String idType, String nodeType, V_GenericNode attachTo, String relationType, String relationValue, String forceColor) {
        return null;
    }

    @Override
    public boolean determineTraversability(V_GenericNode n) {
        return false;
    }
    //</editor-fold>
}
