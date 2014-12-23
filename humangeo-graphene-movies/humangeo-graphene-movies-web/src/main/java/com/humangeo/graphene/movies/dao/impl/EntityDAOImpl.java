package com.humangeo.graphene.movies.dao.impl;

import com.humangeo.graphene.movies.model.Person;
import com.humangeo.graphene.movies.model.Movie;
import graphene.dao.EntityDAO;
import graphene.dao.annotations.EntityLightFunnelMarker;
import graphene.dao.neo4j.Neo4JEmbeddedService;
import graphene.dao.neo4j.annotations.DataGraph;
import graphene.model.Funnel;
import graphene.model.idl.*;
import graphene.model.idlhelper.EntityHelper;
import graphene.model.query.AdvancedSearch;
import graphene.model.query.EventQuery;
import graphene.model.query.SearchFilter;
import graphene.model.view.entities.EntityLight;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.graphdb.*;
import org.neo4j.kernel.impl.util.StringLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.Iterator;

import java.io.File;
import java.util.*;

/**
 * This class helps gather entities to return to the UI.  A EntityDAO should normally only be implemented once
 * because of how Graphene is using the EntityDAO class.  Therefore, logic regarding multiple entities in the
 * database need to be condensed to this one class.
 *
 * EXAMPLE: In this class you will see references to Actor and Movie classes to represent the data in the
 * movie database.
 *
 * Created by bparrish on 12/23/14.
 */
public class EntityDAOImpl implements EntityDAO {

    private Logger logger = LoggerFactory.getLogger(EntityDAOImpl.class);

    private GraphDatabaseService _graphDbService;

    private ExecutionEngine _executionEngine;

    private Funnel<EntityLight, G_Entity> _funnel;

    /***
     * Default constructor
     *
     * @see graphene.dao.neo4j.DAONeo4JEModule - sets up the Neo4JEmbeddedService injection
     * @see com.humangeo.graphene.movies.dao.MoviesDAOModule - sets up funnel injection
     * @see com.humangeo.graphene.movies.model.funnels.MoviesEntityLightFunnel
     * @param embeddedService
     * @param funnel
     */
    @Inject
    public EntityDAOImpl(@DataGraph Neo4JEmbeddedService embeddedService, @EntityLightFunnelMarker Funnel funnel) {
        // get the Graph Database Service that we can use to query the Neo4j database
        _graphDbService = embeddedService.getGraphDb();

        // create a log file
        File file = new File("/tmp/graph_db_cypher_logger.log");

        // setup the cypher execution engine
        _executionEngine = new ExecutionEngine(_graphDbService, StringLogger.logger(file));

        // set the funnel from the injected funnel -- this converts G_Entity to EntityLight
        _funnel = funnel;
    }

    /***
     * This method will get all entities based on the advanced search object.
     *
     * @see graphene.model.idl.G_Entity
     * @see graphene.model.query.AdvancedSearch
     * @param search
     * @return
     */
    @Override
    public List<G_Entity> getEntitiesByAdvancedSearch(AdvancedSearch search) {
        ArrayList<G_Entity> results = new ArrayList<G_Entity>();

        String cypherQuery = "";

        // iterate through each filter...we should only have 1 filter right now
        for (SearchFilter filter : search.getFilters()) {
            // retrieve the field name that is being searched on
            String fieldName = filter.getFieldName();

            // retrieve the value that is being searched for
            String fieldValue = filter.getValue();

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

            // execute the cypher query
            ExecutionResult executionResult = _executionEngine.execute(cypherQuery);

            // get all of the objects -- note the n that was in the cypher query text and the columnAs n here
            Iterator<Object> nodes = executionResult.columnAs("n");

            // iterate through each node found
            while (nodes.hasNext()) {
                // get the next node
                Node node = (Node) nodes.next();

                // start the database transaction so that we can get the properties from the node
                Transaction tx = _graphDbService.beginTx();

                try {
                    // create the G_Entity values
                    List<G_EntityTag> tagList = Collections.singletonList(G_EntityTag.FILE);
                    G_Provenance provenance = new G_Provenance(search.getSource());
                    G_Uncertainty uncertainty = new G_Uncertainty(1.0d);

                    // create our G_Entity object
                    G_Entity entity = null;

                    // check to see if we were looking for a Person or Movie
                    if (fieldName.equals("name")) {
                        // convert our node to a Person object
                        Person actor = new Person(node);

                        // create the G_Entity object
                        entity = new EntityHelper(
                                "" + node.getId(),
                                tagList,
                                provenance,
                                uncertainty,
                                actor.getProperties(provenance, uncertainty));
                    } else if (fieldName.equals("title")) {
                        // convert our node to a Movie object
                        Movie movie = new Movie(node);

                        // create the G_Entity object
                        entity = new EntityHelper(
                                "" + node.getId(),
                                tagList,
                                provenance,
                                uncertainty,
                                movie.getProperties(provenance, uncertainty));
                    }

                    // add the new G_Entity to the list of results
                    results.add(entity);

                    // complete the transaction
                    tx.success();
                } finally {
                    // close out the transaction
                    tx.close();
                }
            }
        }

        // return the results
        return results;
    }

    @Override
    public EntityLight getById(String id) {
        return null;
    }

    @Override
    public long count(EventQuery q) {
        return 0;
    }

    /***
     * This method will get a lightweight representation of the G_Entity objects that we found
     *
     * @see graphene.model.view.entities.EntityLight
     * @see graphene.model.query.AdvancedSearch
     * @param search
     * @return
     */
    @Override
    public List<EntityLight> getLightEntitiesByAdvancedSearch(AdvancedSearch search) {
        // create our list for the return value
        List<EntityLight> list = new ArrayList<EntityLight>();

        // find all of the G_Entities in the database
        List<G_Entity> entities = getEntitiesByAdvancedSearch(search);

        // iterate through each G_Entity
        for (G_Entity g : entities) {
            // utilize our injected funnel to convert a G_Entity to a EntityLight
            EntityLight entityLight = _funnel.to(g);

            // add the lightweight entity to the returned results
            list.add(entityLight);
        }

        // return all of the lightweight results
        return list;
    }

}
