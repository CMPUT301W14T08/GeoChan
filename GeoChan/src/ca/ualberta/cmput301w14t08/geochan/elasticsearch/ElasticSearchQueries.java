/*
 * Copyright 2014 Artem Chikin
 * Copyright 2014 Artem Herasymchuk
 * Copyright 2014 Tom Krywitsky
 * Copyright 2014 Henry Pabst
 * Copyright 2014 Bradley Simons
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.ualberta.cmput301w14t08.geochan.elasticsearch;

/**
 * A utility class for ElasticSearch. Contains various queries and builders for
 * queries to be used when interacting with ElasticSearch.
 * 
 * @author Artem Herasymchuk
 */
public class ElasticSearchQueries {
    /**
     * A query string to search for all results in a type.
     */
    public static final String SEARCH_MATCH_ALL =           "{\n" +
                                                            "   \"size\" : 9999,  \n" +
                                                            "   \"query\": {\n" +
                                                            "       \"match_all\" : { } \n" +
                                                            "   }\n" +
                                                            "}";

    /**
     * A query string to search for all results based on a parent ID.
     */
    private static final String SEARCH_MATCH_PARENT_BEGIN = "{\n" + 
                                                            "   \"query\": {\n" +
                                                            "       \"match\" : {\n" +
                                                            "           \"parent\" : \"";
    private static final String SEARCH_MATCH_PARENT_END =   "\" \n" +
                                                            "       }\n" +
                                                            "   }\n" +
                                                            "}";

    /**
     * A query string to update the list of comments when a new comment is posted.
     */
    private static final String UPDATE_COMMENT_LIST_BEGIN = "{\n" +
                                                            "   \"doc\" : \n";
    private static final String UPDATE_COMMENT_LIST_END =   "   ,\n" +
                                                            "   \"doc_as_upsert\" : true\n" +
                                                            "}";

    /**
     * Returns a query string to search by parent ID
     * 
     * @param id
     *            the parent ID
     * @return the JSON query string
     * 
     */
    public static String getMatchParent(String id) {
        return SEARCH_MATCH_PARENT_BEGIN + id + SEARCH_MATCH_PARENT_END;
    }

    /**
     * Returns a query string to replace the list of comments
     * 
     * @param json
     *            the list of comments in JSON format
     * @return the JSON query string
     * 
     */
    public static String commentListScript(String json) {
        return UPDATE_COMMENT_LIST_BEGIN + json + UPDATE_COMMENT_LIST_END;
    }
}