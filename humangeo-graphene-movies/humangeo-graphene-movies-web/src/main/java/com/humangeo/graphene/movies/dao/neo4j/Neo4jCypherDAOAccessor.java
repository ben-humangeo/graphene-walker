package com.humangeo.graphene.movies.dao.neo4j;

import com.humangeo.graphene.movies.dao.GenericDAOAccessor;
import graphene.dao.neo4j.Neo4JEmbeddedService;
import graphene.dao.neo4j.annotations.DataGraph;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.kernel.impl.util.StringLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.Iterator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is utilized to make calls to a Neo4j instance using Cypher queries.
 *
 * Created by bparrish on 12/24/14.
 */
public class Neo4jCypherDAOAccessor implements GenericDAOAccessor<Node, String> {

    private Logger logger = LoggerFactory.getLogger(Neo4jCypherDAOAccessor.class);

    private GraphDatabaseService _graphDbService;

    private ExecutionEngine _executionEngine;

    //<editor-fold desc="constructors">
    // force use of other constructor
    private Neo4jCypherDAOAccessor() { }

    /***
     * Default constructor
     *
     * @param embeddedService
     */
    @Inject
    public Neo4jCypherDAOAccessor(@DataGraph Neo4JEmbeddedService embeddedService) {
        // get the Graph Database Service that we can use to query the Neo4j database
        _graphDbService = embeddedService.getGraphDb();

        // create a log file
        File file = new File("/tmp/graph_db_cypher_logger.log");

        // setup the cypher execution engine
        _executionEngine = new ExecutionEngine(_graphDbService, StringLogger.logger(file));
    }
    //</editor-fold>

    //<editor-fold desc="interface methods">
    @Override
    public Map<String, List<Node>> get(String query, String... setIdentifiers) {
        Map<String, List<Node>> results = new HashMap<>();

        for (String setIdentifier : setIdentifiers) {
            results.put(setIdentifier, new ArrayList<Node>());
        }

        // execute the cypher query
        ExecutionResult executionResult = _executionEngine.execute(query);

        // get all of the objects -- note the n that was in the cypher query text and the columnAs n here
        ResourceIterator<Map<String, Object>> foundNodes = executionResult.javaIterator();

        // iterate through each node found
        while (foundNodes.hasNext()) {
            // get the next node
            Map<String, Object> rowMap = (Map<String, Object>) foundNodes.next();

            for (String setIdentifier : setIdentifiers) {
                List<Node> identifierNodes = results.get(setIdentifier);

                Node node = (Node) rowMap.get(setIdentifier);

                identifierNodes.add(node);

                results.put(setIdentifier, identifierNodes);
            }
        }

        // return the results
        return results;
    }

    @Override
    public boolean create(String query) {
        return false;
    }

    @Override
    public boolean update(String query) {
        return false;
    }

    @Override
    public boolean delete(String query) {
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="public methods">
    public GraphDatabaseService getGraphDbService() {
        return _graphDbService;
    }
    //</editor-fold>

}
