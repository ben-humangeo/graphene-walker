package com.humangeo.graphene.movies.dao.impl;

import graphene.dao.GenericDAO;
import graphene.model.query.BasicQuery;

/**
 * Created by bparrish on 12/24/14.
 */
public abstract class GenericDAONeo4jImpl<T, Q extends BasicQuery> implements GenericDAO<T, Q> {
}
