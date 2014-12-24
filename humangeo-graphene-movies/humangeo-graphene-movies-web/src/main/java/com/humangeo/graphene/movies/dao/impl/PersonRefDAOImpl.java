package com.humangeo.graphene.movies.dao.impl;

import com.humangeo.graphene.movies.annotations.Neo4jCypherAccessor;
import com.humangeo.graphene.movies.dao.neo4j.Neo4jCypherDAOAccessor;
import com.humangeo.graphene.movies.model.Person;
import graphene.dao.EntityRefDAO;
import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_SearchTuple;
import graphene.model.query.AdvancedSearch;
import graphene.model.query.EntityQuery;
import graphene.model.query.SearchFilter;
import graphene.model.view.entities.BasicEntityRef;
import graphene.util.G_CallBack;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by bparrish on 12/24/14.
 */
public class PersonRefDAOImpl implements EntityRefDAO<Person, EntityQuery> {

    private Logger logger = LoggerFactory.getLogger(PersonRefDAOImpl.class);

    private Neo4jCypherDAOAccessor _neo4jCypherDAOAccessor;

    //<editor-fold desc="constructors">
    // force use of other constructor
    private PersonRefDAOImpl() { }

    /***
     * Default constructor
     *
     * @see com.humangeo.graphene.movies.dao.MoviesDAOModule - sets up the Neo4jCypherDAOAccessor injection
     * @param neo4jCypherDAOAccessor
     */
    @Inject
    public PersonRefDAOImpl(@Neo4jCypherAccessor Neo4jCypherDAOAccessor neo4jCypherDAOAccessor) {
        _neo4jCypherDAOAccessor = neo4jCypherDAOAccessor;
    }
    //</editor-fold>

    //<editor-fold desc="interface methods">
    @Override
    public long countEdges(String id) throws Exception {
        return 0;
    }

    @Override
    public Set<String> entityIDsByAdvancedSearch(AdvancedSearch search) {
        Set<String> resultIds = new HashSet<>();

        // iterate through each filter...we should only have 1 filter right now
        for (SearchFilter filter : search.getFilters()) {
            // retrieve the field name that is being searched on
            String fieldName = filter.getFieldName();

            // retrieve the value that is being searched for
            String fieldValue = filter.getValue();

            String cypherQuery = "";

            // check to see if we are search for an Actor or a Movie
            if (fieldName.equals("name")) {
                // create a cypher query for Neo4j that uses a regex expression to find a case-insensitive name on an Person label
                cypherQuery = "MATCH (n:`Person`) WHERE has(n.`name`) AND n.`name` =~ '(?i).*" + fieldValue + ".*' RETURN (n)";
            } else if (fieldName.equals("title")) {
                // create a cypher query for Neo4j that uses a regex expression to find a case-insensitive title on  a Movie label
                cypherQuery = "MATCH (n:`Movie`) WHERE has(n.`title`) AND n.`title` =~ '(?i).*" + fieldValue + ".*' RETURN (n)";
            } else {
                return null;
            }

            // get all of the nodes
            List<Node> nodes = _neo4jCypherDAOAccessor.get(cypherQuery);

            // iterate through each node found
            for (Node node : nodes) {
                // start the database transaction so that we can get the properties from the node
                Transaction tx = _neo4jCypherDAOAccessor.getGraphDbService().beginTx();

                try {

                    // add the new string representation of the node id to the list of results
                    resultIds.add("" + node.getId());

                    // complete the transaction
                    tx.success();
                } finally {
                    // close out the transaction
                    tx.close();
                }
            }
        }

        return resultIds;
    }

    @Override
    public Set<String> getAccountsForCustomer(String cust) throws Exception {
        return null;
    }

    @Override
    public Set<Person> getRowsForCustomer(String cust) throws Exception {
        return null;
    }

    @Override
    public Set<String> regexSearch(String name, String family, boolean caseSensitive) {
        return null;
    }

    @Override
    public List<Person> rowSearch(EntityQuery q) throws Exception {
        return null;
    }

    @Override
    public Set<String> soundsLikeSearch(String src, String family) {
        return null;
    }

    @Override
    public Set<String> valueSearch(EntityQuery entityQuery) throws Exception {
        return null;
    }

    @Override
    public Set<BasicEntityRef> getBasicRowsForCustomer(String id) {
        return null;
    }

    @Override
    public List<Person> findByQuery(EntityQuery pq) throws Exception {
        return null;
    }

    @Override
    public List<Person> getAll(long offset, long maxResults) throws Exception {
        return null;
    }

    @Override
    public long count(EntityQuery q) throws Exception {
        return 0;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReady(boolean b) {

    }

    @Override
    public double getReadiness() {
        return 0;
    }

    @Override
    public boolean performCallback(long offset, long maxResults, G_CallBack<Person> cb, EntityQuery q) {
        List<G_SearchTuple<String>> values = q.getAttributeList();

        for (G_SearchTuple<String> searchTuple : values) {
            String family = searchTuple.getNodeType().getName();
            String value = searchTuple.getValue();

            String cypherQuery = "";

            if (family.equals(G_CanonicalPropertyType.NAME.name())) {
                cypherQuery = "MATCH (n:`Person`) WHERE HAS(n.`name`) AND n.`name` =~ '(?i).*" + value + ".*' RETURN n";
            } else {
                cypherQuery = "START n=node(" + value + ") RETURN n";
            }

            if (maxResults != 0) {
                cypherQuery = cypherQuery + " LIMIT " + maxResults;
            }

            List<Node> nodes = _neo4jCypherDAOAccessor.get(cypherQuery);

            for (Node node : nodes) {
                Transaction tx = _neo4jCypherDAOAccessor.getGraphDbService().beginTx();

                try {
                    Person person = new Person(node);

                    cb.callBack(person);

                    tx.success();
                } finally {
                    tx.close();
                }
            }
        }

        return true;
    }
    //</editor-fold>
}
