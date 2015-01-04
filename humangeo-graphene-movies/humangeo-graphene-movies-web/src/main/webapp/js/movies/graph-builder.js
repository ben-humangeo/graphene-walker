/* GLOBALS */
var cytoscapeResults = 'cytoscapeResults'

// nulls
var cytoscapeGraph;
var linkSrc;

/* MODELS */

// create model object
GraphBuilder = Backbone.Model.extend({
	url: function() {
	    return this.instanceUrl + this.id;
	},
    parse: function (response) {
        console.log(response);

        response.edges = new EdgeList(response.edges);
        response.nodes = new NodeList(response.nodes);

        return response;
    },
	instanceUrl: '/humangeo-graphene-movies-web/rest/csgraph/customer/'
});

Edge = Backbone.Model.extend({});

EdgeList = Backbone.Collection.extend({
    model: Edge,
    initialize: function() {
        console.log('Initializing edge list');
    }
});

Node = Backbone.Model.extend({});

NodeList = Backbone.Collection.extend({
    model: Node,
    initialize: function() {
        console.log('Initializing node list');
    }
});

GraphView = Backbone.View.extend({
    model: GraphBuilder,
    el: '#graphResults',
    template: Handlebars.compile($("#graph-results-cytoscape").html()),
    initialize: function() {
        this.render();
    },
    render: function() {
        var self = this;

        var output = self.template();

        self.$el.append(output);

        var nodes = [];

        $.each(this.model.get('nodes').models, function (index, element) {
            var data = element.get('data');

            $.each(data.attrs, function(index, element) {
                if (element.key == 'title' || element.key == 'name') {
                    data.name = element.val;
                }
            });

            nodes.push({data: data});
        });

        var edges = [];

        $.each(this.model.get('edges').models, function (index, element) {
            var data = element.get('data');

            edges.push({data: data});
        });

        var nodesAndEdges = {
            nodes: nodes,
            edges: edges
        }

        // setup default style for our cytoscape graph
        var cytoscapeStyle = [
            {
                selector: 'node',
                css: {
                    'background-color': 'data([color])',
                    'content': 'data(name)'
                }
            },
            {
                selector: 'edge',
                css: {
                    'line-color': 'data([color])',
                    'target-arrow-color': 'data([color])',
                    'target-arrow-shape': 'triangle',
                    'opacity': 1
                }
            },
            {
                selector: ':selected',
                css: {
                    'border-color': '#f00',
                    'border-width': '2px',
//                    'background-color': 'black',
//                    'line-color': 'black',
                    'target-arrow-color': 'black',
                    'source-arrow-color': 'black',
                    'opacity': 1
                }
            }
        ]

        // create cytoscape graph and add it to the ui
        cytoscapeGraph = window.cy = cytoscape({
            container: document.getElementById(cytoscapeResults),

            ready: function () {
                window.cy = this;
            },

            style: cytoscapeStyle,

            elements: nodesAndEdges,

//            style: [
//                {
//                    selector: 'node',
//                    css: {
//                        'content': 'data(name)',
//                        'text-halign': 'center',
//                        'text-valign': 'center',
//                        'background-opacity': '0'
//                    }
//                },
//
//                {
//                    selector: 'edge',
//                    css: {
//                        'target-arrow-shape': 'triangle'
//                    }
//                },
//
//                // custom classes
//                {
//                    selector: '.selected-node',
//                    css: {
//                        'border-color': '#f00',
//                        'border-width': '2px'
//                    }
//                },
//                {
//                    selector: '.tool-node',
//                    css: {
//                        'font-family': 'FontAwesome',
//                        'font-size': '3em',
//                    }
//                },
//                {
//                    selector: '.node-person',
//                    css: {
//                        'content': '\uf183'
//                    }
//                },
//                {
//                    selector: '.node-home',
//                    css: {
//                        'content': '\uf015'
//                    }
//                },
//                {
//                    selector: '.node-business',
//                    css: {
//                        'content': '\uf0f7'
//                    }
//                },
//                {
//                    selector: '.node-automobile',
//                    css: {
//                        'content': '\uf0d1'
//                    }
//                }
//            ]
        });

        cytoscapeGraph.toolbar({
            toolbarClass: "cy-toolbar",
            tools: [
                [
                    {
                        icon: 'fa fa-search-plus',
                        event: ['tap'],
                        selector: 'cy',
                        options: {
                            cy: {
                                zoom: 0.1,
                                minZoom: 0.1,
                                maxZoom: 10,
                                zoomDelay: 45
                            }
                        },
                        bubbleToCore: false,
                        tooltip: 'Zoom In',
                        action: [performZoomIn]
                    },
                    {
                        icon: 'fa fa-search-minus',
                        event: ['tap'],
                        selector: 'cy',
                        options: {
                            cy: {
                                zoom: -0.1,
                                minZoom: 0.1,
                                maxZoom: 10,
                                zoomDelay: 45
                            }
                        },
                        bubbleToCore: false,
                        tooltip: 'Zoom Out',
                        action: [performZoomOut]
                    }
                ],
                [
                    {
                        icon: 'fa fa-arrow-right',
                        event: ['tap'],
                        selector: 'cy',
                        options: {
                            cy: {
                                distance: -80,
                            }
                        },
                        bubbleToCore: true,
                        tooltip: 'Pan Right',
                        action: [performPanRight]
                    },
                    {
                        icon: 'fa fa-arrow-down',
                        event: ['tap'],
                        selector: 'cy',
                        options: {
                            cy: {
                                distance: -80,
                            }
                        },
                        bubbleToCore: true,
                        tooltip: 'Pan Down',
                        action: [performPanDown]
                    },
                    {
                        icon: 'fa fa-arrow-left',
                        event: ['tap'],
                        selector: 'cy',
                        options: {
                            cy: {
                                distance: 80,
                            }
                        },
                        bubbleToCore: true,
                        tooltip: 'Pan Left',
                        action: [performPanLeft]
                    },
                    {
                        icon: 'fa fa-arrow-up',
                        event: ['tap'],
                        selector: 'cy',
                        options: {
                            cy: {
                                distance: 80,
                            }
                        },
                        bubbleToCore: true,
                        tooltip: 'Pan Up',
                        action: [performPanUp]
                    }
                ],
                [
                    {
                        icon: 'fa fa-dot-circle-o',
                        event: ['tap'],
                        selector: 'node',
                        bubbleToCore: false,
                        tooltip: 'Fit Graph',
                        action: [self.fit]
                    }
                ],
                [
                    {
                        icon: 'fa fa-th',
                        event: ['tap'],
                        selector: 'cy',
                        options: {
                            layout: 'grid'
                        },
                        bubbleToCore: false,
                        tooltip: 'Grid Layout',
                        action: [self.layout]
                    },
                    {
                        icon: 'fa fa-circle-o',
                        event: ['tap'],
                        selector: 'cy',
                        options: {
                            layout: 'circle'
                        },
                        bubbleToCore: false,
                        tooltip: 'Circle Layout',
                        action: [self.layout]
                    }
                ],
                [
                    {
                        icon: 'fa fa-link',
                        event: ['tap'],
                        selector: 'node',
                        bubbleToCore: false,
                        tooltip: 'Link',
                        action: [self.performLink]
                    }
                ],
                [
                    {
                        icon: 'fa fa-trash-o',
                        event: ['tap'],
                        selector: 'edge,node',
                        bubbleToCore: false,
                        tooltip: 'Remove Node/Edge',
                        action: [self.performRemove]
                    }
                ]

            ],
            appendTools: false
        });

        return self;
    },
    fit: function(e) {
        if (!e.data.canPerform(e, graphView.fit)) {
            return;
        }

        if (cytoscapeGraph) {
            cy.fit();
        }
    },
    layout: function(e) {
        if (!e.data.canPerform(e, graphView.layout)) {
            return;
        }

        var selectedTool = e.data.data.selectedTool;
        var options = e.data.getToolOptions(selectedTool);

        if (cytoscapeGraph) {
            cy.layout({ name: options.layout });
        }
    },
    performLink: function(e) {
        if (!e.data.canPerform(e, graphView.performLink)) {
            return;
        }

        if (linkSrc) {
            tgt = e.cyTarget;

            e.cy.add({
                group: "edges",
                data: {
                    source: linkSrc.id(),
                    target: tgt.id()
                }
            });

            linkSrc.removeClass('selected-node');
            linkSrc = undefined;
        } else {
            linkSrc = e.cyTarget;
            linkSrc.addClass('selected-node');
        }
    },
    performRemove: function(e) {
        if (!e.data.canPerform(e, graphView.performRemove)) {
            return;
        }

        cy.remove(e.cyTarget);
    }
});