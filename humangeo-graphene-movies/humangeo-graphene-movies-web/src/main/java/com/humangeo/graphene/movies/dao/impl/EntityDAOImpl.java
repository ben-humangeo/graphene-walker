package com.humangeo.graphene.movies.dao.impl;

import com.humangeo.graphene.movies.model.Actor;
import com.humangeo.graphene.movies.model.Movie;
import graphene.dao.EntityDAO;
import graphene.dao.annotations.EntityLightFunnelMarker;
import graphene.dao.neo4j.Neo4JEmbeddedService;
import graphene.dao.neo4j.annotations.DataGraph;
import graphene.model.Funnel;
import graphene.model.idl.*;
import graphene.model.idlhelper.EntityHelper;
import graphene.model.idlhelper.PropertyHelper;
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
import java.util.Map;

/**
 * Created by bparrish on 12/23/14.
 */
public class EntityDAOImpl implements EntityDAO {

    private Logger logger = LoggerFactory.getLogger(EntityDAOImpl.class);

    private GraphDatabaseService _graphDbService;

    private ExecutionEngine _executionEngine;

    private Funnel<EntityLight, G_Entity> _funnel;

    @Inject
    public EntityDAOImpl(@DataGraph Neo4JEmbeddedService embeddedService, @EntityLightFunnelMarker Funnel funnel) {
        _graphDbService = embeddedService.getGraphDb();

        File file = new File("/tmp/graph_db_cypher_logger.log");

        _executionEngine = new ExecutionEngine(_graphDbService, StringLogger.logger(file));

        _funnel = funnel;
    }

    @Override
    public List<G_Entity> getEntitiesByAdvancedSearch(AdvancedSearch search) {
        ArrayList<G_Entity> results = new ArrayList<G_Entity>();

        String cypherQuery = "";

        for (SearchFilter filter : search.getFilters()) {
            String fieldName = filter.getFieldName();

            if (fieldName.equals("name")) {
                cypherQuery = "MATCH (n:`Person`) WHERE has(n.`name`) RETURN (n)";
            } else if (fieldName.equals("title")) {
                cypherQuery = "MATCH (n:`Movie`) WHERE has(n.`title`) RETURN (n)";
            } else {
                return null;
            }

            ExecutionResult executionResult = _executionEngine.execute(cypherQuery);

            Iterator<Object> nodes = executionResult.columnAs("n");

            while (nodes.hasNext()) {
                Node node = (Node) nodes.next();

                Transaction tx = _graphDbService.beginTx();

                try {
                    List<G_EntityTag> tagList = new ArrayList<G_EntityTag>(1);
                    tagList.add(G_EntityTag.FILE);
                    G_Provenance prov = new G_Provenance(search.getSource());
                    G_Uncertainty uncertainty = new G_Uncertainty(1.0d);

                    G_Entity entity = null;

                    if (fieldName.equals("name")) {
                        Actor actor = new Actor(node);

                        G_Property property = new PropertyHelper(G_PropertyTag.LABEL, actor.getName());
                        entity = new EntityHelper(actor.getName(), tagList, prov, uncertainty, Collections.singletonList(property));
                        entity.setUid(actor.getName());
                    } else if (fieldName.equals("title")) {
                        Movie movie = new Movie(node);

                        G_Property property = new PropertyHelper(G_PropertyTag.LABEL, movie.getTitle());
                        entity = new EntityHelper(movie.getTitle(), tagList, prov, uncertainty, Collections.singletonList(property));
                        entity.setUid(movie.getTitle());
                    }

                    results.add(entity);

                    tx.success();
                } finally {
                    tx.close();
                }
            }
        }

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

    @Override
    public List<EntityLight> getLightEntitiesByAdvancedSearch(AdvancedSearch search) {
        List<EntityLight> list = new ArrayList<EntityLight>();

        List<G_Entity> entities = getEntitiesByAdvancedSearch(search);

        for (G_Entity g : entities) {
            list.add(_funnel.to(g));
        }

        return list;
    }

}
