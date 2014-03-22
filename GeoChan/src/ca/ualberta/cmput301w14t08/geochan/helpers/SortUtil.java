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

package ca.ualberta.cmput301w14t08.geochan.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

/**
 * Helper class. Manages Comparators for sorting across multiple classes.
 * 
 */
public class SortUtil {

    public static final int SORT_DATE_NEWEST = 0;
    public static final int SORT_DATE_OLDEST = 1;
    public static final int SORT_LOCATION_USER = 2;
    public static final int SORT_SCORE_HIGHEST = 3;
    public static final int SORT_SCORE_LOWEST = 4;
    public static final int SORT_USER_SCORE_HIGHEST = 5;
    public static final int SORT_USER_SCORE_LOWEST = 6;
    public static final int SORT_LOCATION = 7;
    public static final int SORT_IMAGE = 8;
    
    private static GeoLocation sortGeo;
    
    public static GeoLocation getSortGeo(){
        if(sortGeo == null){
            sortGeo = new GeoLocation(0,0);
        }
        return sortGeo;
    }
    
    public static void setSortGeo(GeoLocation g){
        sortGeo = g;
    }
    
    
    /**
     * Sorts the ArrayList of ThreadComments passed to it according to
     * the tag passed. The passed GeoLocation is used in sorting by
     * location or score.
     * @param tag Tag specifying the sorting method to be used.
     * @param threads The ArrayList of ThreadComments to be sorted
     * @param sortLoc The GeoLocation used in sorting of ThreadComments.
     */
    public static void sortThreads(int tag, 
                                   ArrayList<ThreadComment> threads){
        switch (tag){
        case(SORT_DATE_OLDEST):
            Collections.sort(threads, sortThreadsByDateOldest());
            break;
        case(SORT_DATE_NEWEST):
            Collections.sort(threads, sortThreadsByDateNewest());
            break;
        case(SORT_USER_SCORE_HIGHEST):
            Collections.sort(threads, 
                      sortThreadsByUserScoreHighest());
            break;
        case(SORT_USER_SCORE_LOWEST):
            Collections.sort(threads,
                    sortThreadsByUserScoreLowest());
            break;
        case(SORT_LOCATION):
            Collections.sort(threads,
                    sortThreadsByLocation());
            break;
        }      
    }
    
    /**
     * Sorts the ArrayList of ThreadComments according to the tag
     * passed to it. Only used for sorting methods
     * that do not require a GeoLocation.
     * @param tag Tag specifying the type of sorting to be done.
     * @param threads The ArrayList of ThreadComments to be sorted.
     */
    
    public static void sortComments(int tag,
                                    ArrayList<Comment> coms){
        switch(tag){
        case(SORT_DATE_OLDEST):
            Collections.sort(coms, sortCommentsByDateOldest());
            for(Comment c: coms){
                sortComments(tag, c.getChildren());
            }
            break;
        case(SORT_DATE_NEWEST):
            Collections.sort(coms, sortCommentsByDateNewest());
            for(Comment c: coms){
                sortComments(tag, c.getChildren());
            }
            break;
        case(SORT_LOCATION):
            Collections.sort(coms, sortCommentsByLocation());
            for(Comment c: coms){
                sortComments(tag, c.getChildren());
            }
            break;
        case(SORT_USER_SCORE_HIGHEST):
            Collections.sort(coms, sortCommentsByUserScoreHighest());
            for(Comment c: coms){
                sortComments(tag, c.getChildren());
            }
            break;
        case(SORT_USER_SCORE_LOWEST):
            Collections.sort(coms, sortCommentsByUserScoreLowest());
            for(Comment c: coms){
                sortComments(tag, c.getChildren());
            }
            break;
        case(SORT_IMAGE):
            Collections.sort(coms, sortCommentsByImage());
            for(Comment c: coms){
                sortComments(tag, c.getChildren());
            }
            break;
        }
    }
    
    
    /**
     * Comparator for pushing comments with images to the top.
     * Uses comment date to break ties if both comments have images,
     * or do not have images.
     * @return
     */
    private static Comparator<Comment> sortCommentsByImage(){
        return new Comparator<Comment>(){
            public int compare(Comment c1, Comment c2){
                if(c1.hasImage() && !(c2.hasImage())){
                    return -1;
                } else if (!(c1.hasImage()) && c2.hasImage()){
                    return 1;
                } else if(c1.hasImage() == c2.hasImage()){
                    int val = c1.getCommentDate().compareTo(c2.getCommentDate());
                    if (val < 0){
                        return -1;
                    } else if (val > 0) {
                        return 1;
                    } else {
                        return 0;
                    }
                } else {
                    return 0;
                }
            }
        };
    }

    /**
     * Comparator for pushing old comments in a thread to the top.
     */
    private static Comparator<Comment> sortCommentsByDateOldest() {
        return new Comparator<Comment>() {
            public int compare(Comment c1, Comment c2) {
                int val = c1.getCommentDate().compareTo(c2.getCommentDate());
                if (val < 0) {
                    return -1;
                } else if (val > 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
    }

    /**
     * Comparator for pushing new comments in a thread to the top.
     */
    private static Comparator<Comment> sortCommentsByDateNewest() {
        return new Comparator<Comment>() {
            public int compare(Comment c1, Comment c2) {
                int val = c1.getCommentDate().compareTo(c2.getCommentDate());
                if (val < 0) {
                    return 1;
                } else if (val > 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };
    }

    /**
     * Comparator for pushing child comments closest to their parent to the top.
     */
    @Deprecated
    private static Comparator<Comment> sortCommentsByParentDistance() {
        return new Comparator<Comment>() {
            public int compare(Comment c1, Comment c2) {
                double val1 = c1.getDistanceFrom(c1.getParent().getLocation());
                double val2 = c2.getDistanceFrom(c2.getParent().getLocation());
                if (val1 > val2) {
                    return 1;
                } else if (val1 < val2) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };
    }

    /**
     * Comparator for pushing higher scored child comments to the top.
     */
    @Deprecated
    private static Comparator<Comment> sortCommentsByParentScoreHighest() {
        return new Comparator<Comment>() {
            public int compare(Comment c1, Comment c2) {
                double val1 = c1.getScoreFromParent();
                double val2 = c2.getScoreFromParent();
                if (val1 > val2) {
                    return -1;
                } else if (val1 < val2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
    }

    /**
     * Comparator for pushing higher scored comments (relative to user provided
     * location) to the top.
     * 
     * @param g
     *            The passed GeoLocation.
     * @return A comparator for sorting comments by score relative to user
     *         provided location.
     */
   /* private static Comparator<Comment> sortCommentsByUserScoreHighest(final GeoLocation g) {
        return new Comparator<Comment>() {
            public int compare(Comment c1, Comment c2) {
                double val1 = c1.getScoreFromUser(g);
                double val2 = c2.getScoreFromUser(g);
                if (val1 > val2) {
                    return -1;
                } else if (val1 < val2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
    }*/
    private static Comparator<Comment> sortCommentsByUserScoreHighest() {
        return new Comparator<Comment>() {
            public int compare(Comment c1, Comment c2) {
                double val1 = c1.getScoreFromUser(SortUtil.getSortGeo());
                double val2 = c2.getScoreFromUser(SortUtil.getSortGeo());
                if (val1 > val2) {
                    return -1;
                } else if (val1 < val2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
    }

    /**
     * Comparator for pushing lower scored comments (relative to user provided
     * location) to the top.
     * 
     * @param g
     *            The passed GeoLocation.
     * @return A comparator for sorting comments by score relative to user
     *         provided location.
     */
    private static Comparator<Comment> sortCommentsByUserScoreLowest() {
        return new Comparator<Comment>() {
            public int compare(Comment c1, Comment c2) {
                double val1 = c1.getScoreFromUser(SortUtil.getSortGeo());
                double val2 = c2.getScoreFromUser(SortUtil.getSortGeo());
                if (val1 > val2) {
                    return 1;
                } else if (val1 < val2) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };
    }

    /**
     * Comparator for pushing lower scored child comments to the top.
     */
    @Deprecated
    private static Comparator<Comment> sortCommentsByParentScoreLowest() {
        return new Comparator<Comment>() {
            public int compare(Comment c1, Comment c2) {
                double val1 = c1.getScoreFromParent();
                double val2 = c2.getScoreFromParent();
                if (val1 > val2) {
                    return 1;
                } else if (val1 < val2) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };
    }

    /**
     * Comparator for sorting comments in a thread based on the current
     * GeoLocation of the Thread's sortLoc member.
     * 
     * @param g the GeoLocation to sort by
     * @return the comparator
     */
    private static Comparator<Comment> sortCommentsByLocation() {
        return new Comparator<Comment>() {
            public int compare(Comment c1, Comment c2) {
                double val1 = c1.getDistanceFrom(SortUtil.getSortGeo());
                double val2 = c2.getDistanceFrom(SortUtil.getSortGeo());
                if (val1 > val2) {
                    return 1;
                } else if (val1 < val2) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };
    }

    /**
     * Comparator for pushing old threads to the top.
     */
    private static Comparator<ThreadComment> sortThreadsByDateOldest() {
        return new Comparator<ThreadComment>() {
            public int compare(ThreadComment t1, ThreadComment t2) {
                int val = t1.getThreadDate().compareTo(t2.getThreadDate());
                if (val < 0) {
                    return -1;
                } else if (val > 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
    }

    /**
     * Comparator for pushing new threads to the top.
     */
    private static Comparator<ThreadComment> sortThreadsByDateNewest() {
        return new Comparator<ThreadComment>() {
            public int compare(ThreadComment t1, ThreadComment t2) {
                int val = t1.getThreadDate().compareTo(t2.getThreadDate());
                if (val < 0) {
                    return 1;
                } else if (val > 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };
    }

    /**
     * Comparator for passing higher scored Threads to the top.
     * 
     * @param g
     *            The current ThreadList.sortLoc
     * @return A Comparator used to sort Threads according to highest score.
     */
    private static Comparator<ThreadComment> sortThreadsByUserScoreHighest() {
        return new Comparator<ThreadComment>() {
            public int compare(ThreadComment t1, ThreadComment t2) {
                double val1 = t1.getScoreFromUser(SortUtil.getSortGeo());
                double val2 = t2.getScoreFromUser(SortUtil.getSortGeo());
                if (val1 > val2) {
                    return -1;
                } else if (val1 < val2) {// mixed these 2 around
                    return 1;
                } else {
                    return 0;
                }
            }
        };
    }

    /**
     * Comparator for pushing lower scored Threads to the top.
     * 
     * @param g
     *            The current ThreadList.sortLoc
     * @return A Comparator used to sort Threads according to lowest score.
     */
    private static Comparator<ThreadComment> sortThreadsByUserScoreLowest() {
        return new Comparator<ThreadComment>() {
            public int compare(ThreadComment t1, ThreadComment t2) {
                double val1 = t1.getScoreFromUser(SortUtil.getSortGeo());
                double val2 = t2.getScoreFromUser(SortUtil.getSortGeo());
                if (val1 > val2) {
                    return 1;
                } else if (val1 < val2) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };
    }

    /**
     * Comparator for pushing ThreadComments closer to the provided location to
     * the top.
     * 
     * @param g
     *            The GeoLocation to be compared against.
     * @return A Comparator used to sort ThreadComments according to location.
     */
    private static Comparator<ThreadComment> sortThreadsByLocation() {
        return new Comparator<ThreadComment>() {
            public int compare(ThreadComment t1, ThreadComment t2) {
                double val1 = t1.getDistanceFrom(SortUtil.getSortGeo());
                double val2 = t2.getDistanceFrom(SortUtil.getSortGeo());
                if (val1 > val2) {
                    return 1;
                } else if (val1 < val2) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };
    }

}
