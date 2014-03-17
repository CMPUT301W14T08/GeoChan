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
 
 /*
  * Reused from https://github.com/rayzhangcl/ESDemo/
  */

package ca.ualberta.cmput301w14t08.geochan.elasticsearch;

import java.util.Collection;

/**
 * Represents a hit within a response served by ElasticSearch
 *
 */
public class ElasticSearchHits<T> {
    int total;
    double max_score;
    Collection<ElasticSearchResponse<T>> hits;

    public Collection<ElasticSearchResponse<T>> getHits() {
        return hits;
    }

    public String toString() {
        return (super.toString() + "," + total + "," + max_score + "," + hits);
    }
}
