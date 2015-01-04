/* GLOBALS */
var searchContent = '#searchContent';
var queryInput = "#queryInput";
//var toolbar = '#toolbar';
//var backButton = "#backButton";
//var gridLayoutButton = '#circleLayoutButton';
//var gridLayoutButton = '#gridLayoutButton';
//var centerButton = "#centerButton";
var searchResults = "#searchResults";
var graphResults = "#graphResults";

// nulls
var graphView;

/* MODELS */

// create model object
MovieSearch = Backbone.Model.extend({
	defaults: {
        /*
        id: "71",
        datasource_id: "Movies",
        effectiveName: "Tom Hanks",
        allNames: "Tom Hanks",
        attributes: [
            {
                family: "Name",
                name: "",
                value: "Tom Hanks"
            }
        ],
        accountList: []
        */
	},
    parse: function (response) {
        console.log(response);

        return response;
    }
});

/* COLLECTIONS */

// create collection object
MovieSearches = Backbone.Collection.extend({
	model: MovieSearch,
	url: '/humangeo-graphene-movies-web/rest/EntitySearch/advancedSearch',
	defaults: {
		dataSet: "Entities",
		source: "Movies"
	},
	parse: function(response, xhr) {
	    // return the list of entities to be parsed by Model.parse function
		return response.entity;
	}
});

// initialize the collection model
var searchCollection = new MovieSearches();

/* VIEWS */

// create search view object
SearchView = Backbone.View.extend({
    events: {
		// function to perform when user clicks on `?` button
        "click #querySearch": 'querySearch'
    },
    template: Handlebars.compile($("#search-template").html()),
    el: "#content",
    initialize: function() {
        this.render();
    },
    render: function() {
        var self = this;

        var output = self.template();

        self.$el.append(output);

        return self;
    },
    querySearch: function() {
		// get the value to search on
		var searchText = $(queryInput).val();

		// clear previous search results
		$(searchResults).html('');

		var jsonSearch = '{' +
		    '"dataSet":"' + searchCollection.defaults.dataSet + '",' +
		    '"source":"' + searchCollection.defaults.source + '",' +
		    '"filters":[' +
		        '{' +
                    '"fieldName":"name",' +
                    '"operator":"COMPARE_INCLUDE",' +
                    '"value":"' + searchText + '",' +
                    '"caseSensitive":false' +
                '}' +
            ']' +
        '}';

		// send a search to the backend
		searchCollection.fetch({
			// setup the query param data
			data: 'jsonSearch=' + encodeURIComponent(jsonSearch),
			success: function(e) {
				searchResultsTableView = new SearchResultsTableView({collection: searchCollection});

				searchResultsTableView.render();
			},
			error: function(e) {
				console.log(e);
			}
		});
	}
});

// create table view object
SearchResultsTableView = Backbone.View.extend({
	template: Handlebars.compile($("#search-results-table-template").html()),
    el: searchResults,
    render: function() {
        var self = this;

        var templateVars = {'query': $(queryInput).val()}

        // create HTML output from template
        var output = self.template(templateVars);

        // append HTML to self's el property
        self.$el.prepend(output);

        // append table row HTML for each object in the collection
        self.collection.each(function(element, index) {
            self.renderRow(element);
        });

        return self;
    },
    renderRow: function(model) {
        var self = this;

        // create the row view
        var rowView = new SearchResultsRowView({model: model});

        rowView.$el.append(rowView.render().$el);

        return self;
    }
});

SearchResultsRowView = Backbone.View.extend({
    events: {
        "click .search-result-name": 'showResult'
    },
	template: Handlebars.compile($("#search-results-row-template").html()),
	el: "#searchResultsTable",
    render: function() {
        var self = this;

        // create HTML output from template with model data
        var output = self.template(self.model.toJSON());

        self.setElement($(output));

        return self;
    },
    showResult: function() {
        console.log(this.model);

        var graphBuilder = new GraphBuilder({id: this.model.id});

        graphBuilder.fetch({
            success: function(model, response, options) {
                console.log('Fetch success');

                $(graphResults).html('');

                toggleResults();

                graphView = new GraphView({model: model});
            },
            error: function(model, response, options) {
                console.log(response);
            }
        });
    }
});

// initialize search view
var searchView = new SearchView();

// initialize view without collection object
var searchResultsTableView = new SearchResultsTableView({collection: searchCollection});

//$(backButton).click(function() {
//    toggleResults();
//});
//
//$(centerButton).click(function() {
//    if (graphView) {
//        graphView.fit();
//    }
//});
//
//$(gridLayoutButton).click(function() {
//    if (graphView) {
//        graphView.layout('grid');
//    }
//});
//
//$(circleLayoutButton).click(function() {
//    if (graphView) {
//        graphView.layout('circle');
//    }
//});

function toggleResults() {
    if ($(graphResults).is(':visible')) {
        $(searchContent).show();
        $(searchResults).show();
//        $(toolbar).hide();
        $(graphResults).hide();
    } else {
        $(searchContent).hide();
        $(searchResults).hide();
//        $(toolbar).show();
        $(graphResults).show();
    }
}