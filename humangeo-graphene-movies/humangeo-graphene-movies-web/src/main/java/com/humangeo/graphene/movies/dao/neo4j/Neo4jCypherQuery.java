package com.humangeo.graphene.movies.dao.neo4j;

/**
 * This class helps build a cypher query through static methods.
 *
 * Created by bparrish on 12/24/14.
 */
public class Neo4jCypherQuery {

    private StringBuilder _finalCypherQuery;

    //<editor-fold desc="constructors">
    public Neo4jCypherQuery() {
        _finalCypherQuery = new StringBuilder();
    }
    //</editor-fold>

    //<editor-fold desc="public methods">
    /***
     * This method will return a String in order to use in a ExecutionEngine.
     *
     * Will do some minor checking, and will throw an error if it is an invalid statement.
     *
     * @see org.neo4j.cypher.ExecutionEngine
     * @return
     */
    public String build() throws UnsupportedOperationException {
        String cypherQuery = _finalCypherQuery.toString();

        validateQuery(cypherQuery);

        return cypherQuery;
    }

    /***
     * This method will start a MATCH statement.
     *
     * EXAMPLE: MATCH n
     *
     * @param clause - user supplied clause
     * @return
     */
    public Neo4jCypherQuery match(String clause) {
        Neo4jCypherQuery query = new Neo4jCypherQuery();

        String formattedClause = String.format("MATCH %s ", clause);

        _finalCypherQuery.append(formattedClause);

        return this;
    }

    /***
     * This method will start a START statement.
     *
     * @param clause - user supplied clause
     * @return
     */
    public Neo4jCypherQuery start(String clause) {
        String formattedClause = String.format("START %s ", clause);

        _finalCypherQuery.append(formattedClause);

        return this;
    }

    /***
     * This method will start a WHERE statement.
     *
     * @return
     */
    public Neo4jCypherQuery where() {
        _finalCypherQuery.append("WHERE ");

        return this;
    }

    /***
     * This method will start a WHERE statement.
     *
     * @param clause - user supplied clause
     * @return
     */
    public Neo4jCypherQuery where(String clause) {
        String formattedClause = String.format("WHERE %s ", clause);

        _finalCypherQuery.append(formattedClause);

        return this;
    }

    /***
     * This method will start a HAS statement. This statement will provide parentheses - {@code "HAS(clause)"}
     *
     * EXAMPLE: HAS(n.`name`)
     *
     * @param clause - user supplied clause
     * @return
     */
    public Neo4jCypherQuery has(String clause) {
        String formattedClause = String.format("HAS(%s) ", clause);

        _finalCypherQuery.append(formattedClause);

        return this;
    }

    /***
     * This method will start a AND statement.
     *
     * @return
     */
    public Neo4jCypherQuery and() {
        _finalCypherQuery.append("AND ");

        return this;
    }

    /***
     * This method will start a AND statement.
     *
     * @param clause - user supplied clause
     * @return
     */
    public Neo4jCypherQuery and(String clause) {
        String formattedClause = String.format("AND %s ", clause);

        _finalCypherQuery.append(formattedClause);

        return this;
    }

    /***
     * This method will start a regex statement.
     *
     * @param key - the property key {@code n.`name`}
     * @param regex - the regex pattern
     * @return
     */
    public Neo4jCypherQuery regex(String key, String regex) {
        return regex(key, regex, false);
    }

    /***
     * This method will start a regex statement.
     *
     * @param key - the property key {@code n.`name`}
     * @param regex - the regex pattern
     * @param caseInsensitive - add case sensitivity to the regex clause {@code '(?i)'}
     * @return
     */
    public Neo4jCypherQuery regex(String key, String regex, boolean caseInsensitive) {
        String formattedClause = String.format(
                "%s =~ '%s%s' ",
                key,
                (caseInsensitive) ? "(?i)" : "",
                regex
        );

        _finalCypherQuery.append(formattedClause);

        return this;
    }

    /***
     * This method will start a RETURN statement.
     *
     * @param clause - user supplied clause
     * @return
     */
    public Neo4jCypherQuery ret(String clause) {
        String formattedClause = String.format("RETURN %s ", clause);

        _finalCypherQuery.append(formattedClause);

        return this;
    }

    /***
     * This method will start a LIMIT statement.
     *
     * @param count - limit count
     * @return
     */
    public Neo4jCypherQuery limit(int count) {
        String formattedClause = String.format("LIMIT %d ", count);

        _finalCypherQuery.append(formattedClause);

        return this;
    }
    //</editor-fold>

    //<editor-fold desc="private methods">
    /***
     * This method will check the query for valid parsing.
     *
     * @param cypherQuery
     */
    private void validateQuery(String cypherQuery) throws UnsupportedOperationException {
        if (!cypherQuery.contains("RETURN")) {
            throw new UnsupportedOperationException("MATCH does not have a RETURN clause");
        }

        if (!cypherQuery.startsWith("MATCH") && !cypherQuery.startsWith("START")) {
            throw new UnsupportedOperationException("Cypher does not start with MATCH or START clause");
        }
    }
    //</editor-fold>
}
