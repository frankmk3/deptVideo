package com.dept.video.server.parser;

import com.dept.video.server.exception.QueryException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Query;

public class QueryParserTest {

    private QueryParser queryParser;

    @Before
    public void init() {
        queryParser = new QueryParser();
    }

    @Test
    public void whenQueryIsSimpleValueNameTheQueryUseRegexToMatch() throws QueryException {
        String queryElasticsearch = "name: test-name";

        Query query = queryParser.buildQuery(queryElasticsearch);

        Assert.assertEquals("{ \"name\" : { \"$regex\" : \"^test-name$\", \"$options\" : \"im\" } }", query.getQueryObject().toJson());
    }

    @Test
    public void whenQueryIsEmptyReturnBasicQuery() throws QueryException {
        String queryElasticsearch = "";

        Query query = queryParser.buildQuery(queryElasticsearch);

        Assert.assertEquals("{ }", query.getQueryObject().toJson());
    }

    @Test
    public void whenComplexQueryIsProvidedReturnProperMongoQuery() throws QueryException {
        String queryElasticsearch = "id:test-id AND entity.name:some-name AND age :> 25 OR (name:null AND NOT entity.id:*) AND age:<= 4 AND genre: *";

        Query query = queryParser.buildQuery(queryElasticsearch);

        Assert.assertNotNull(query.getQueryObject().toJson());
    }

    @Test(expected = QueryException.class)
    public void whenQueryIsWrongThenThrowsQueryException() throws QueryException {
        String queryElasticsearch = "entity.name!: some-name AND";

       queryParser.buildQuery(queryElasticsearch);
    }
}