package ca.ualberta.cmput301w14t08.geochan.elasticsearch;

public class ElasticSearchQueries {
	public static final String QUERY_SEARCH_MATCH_ALL = "{\n" + 
            "   \"query\": {\n" +
            "       \"match_all\" : { } \n" +
            "   }\n" +
            "}";
}
