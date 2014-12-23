package com.humangeo.graphene.movies.dao.impl;

import graphene.dao.DataSourceListDAO;
import graphene.model.datasourcedescriptors.DataSet;
import graphene.model.datasourcedescriptors.DataSetField;
import graphene.model.datasourcedescriptors.DataSource;
import graphene.model.datasourcedescriptors.DataSourceList;

/**
 * A DAO to return a list of available Data Sets. In some environments this is
 * done via a server call, so it should be an injected implementation.
 * 
 * @author PWG for DARPA
 * 
 */
public class DataSourceListDAOImpl implements DataSourceListDAO {
	private static DataSourceList dataSourceList = null;

	public DataSourceListDAOImpl() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Return a list of datasets for use by the rest service. These lists are
	 * used by the gui to allow users to choose a list, and to configure the
	 * appropriate screens.
	 * 
	 * @author PWG for DARPA
	 * 
	 */
	public DataSourceList getList() {
		if (dataSourceList == null) {
			loadList();
		}

		return dataSourceList;
	}

	private void loadList() {
		DataSourceList list = new DataSourceList();

		list.addSource(buildMovies());

		// add more data sources here if you want

		dataSourceList = list;
	}

	private DataSource buildMovies() {
		DataSource dataSource = new DataSource();
		DataSet dataSet = new DataSet();

		dataSource.setId("Movies");
		dataSource.setName("Movies");
		dataSource.setFriendlyName("Movie List");

		dataSource.addProperty("Country", "USA");

		dataSet.setName("Entities");
		dataSet.setEntity(true);
		dataSet.setTransaction(false);

		//TODO expand with more fields
		dataSet.addField(new DataSetField("name", "Actor Name", "string", false, true, true));
		dataSet.addField(new DataSetField("title", "Movie Title", "string", false, true, true));

		dataSource.addDataSet(dataSet);

		return dataSource;
	}

}
