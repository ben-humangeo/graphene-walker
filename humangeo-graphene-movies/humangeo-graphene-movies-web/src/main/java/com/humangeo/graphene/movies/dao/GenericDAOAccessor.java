package com.humangeo.graphene.movies.dao;

import java.util.List;

/**
 * This interface defines how one should implement a generic DAO set of data calls.
 *
 * T = type returned in a get call
 * Q = type for your queries, (i.e., String for a cypher query to be executed in Neo4j).  Allows you to define classes
 *     that you may have already setup to handle your queries.
 *
 * Created by bparrish on 12/24/14.
 */
public interface GenericDAOAccessor<T, Q> {

    /***
     * This method will get all of the results found from a data query to the dataset.  It can return >= 0 results.
     *
     * @param query
     * @return
     */
    public List<T> get(Q query);

    /***
     * This method will send a create call to the dataset.  It will return true on success and false on failure.
     * @param query
     * @return true on success, false on failure
     */
    public boolean create(Q query);

    /***
     * This method will send an update call to the dataset.  It will return true on success and false on failure.
     * @param query
     * @return true on success, false on failure
     */
    public boolean update(Q query);

    /***
     * This method will send a delete call to the dataset.  It will return true on success and false on failure.
     * @param query
     * @return true on success, false on failure
     */
    public boolean delete(Q query);

}
