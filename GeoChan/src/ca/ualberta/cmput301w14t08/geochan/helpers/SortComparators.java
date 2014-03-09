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

import java.util.Comparator;

import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;

public class SortComparators {

    public static final int SORT_DATE_NEWEST = 0;
    public static final int SORT_DATE_OLDEST = 1;
    public static final int SORT_LOCATION_OP = 2;
    public static final int SORT_SCORE_HIGHEST = 3;
    public static final int SORT_SCORE_LOWEST = 4;
    public static final int SORT_USER_SCORE_HIGHEST = 5;
    public static final int SORT_USER_SCORE_LOWEST = 6;
    public static final int SORT_LOCATION_MISC = 7;

    /**
     * Comparator for pushing old comments in a thread to the top.
     */
    public static Comparator<Comment> sortCommentsByDateOldest() {
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
    public static Comparator<Comment> sortCommentsByDateNewest() {
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
    public static Comparator<Comment> sortCommentsByParentDistance() {
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
    public static Comparator<Comment> sortCommentsByParentScoreHighest() {
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
     * Comparator for pushing higher scored comments (relative to user provided location)
     * to the top.
     * @param g The passed GeoLocation.
     * @return A comparator for sorting comments by score relative to user provided location.
     */
    public static Comparator<Comment> sortCommentsByUserScoreHighest(final GeoLocation g){
        return new Comparator<Comment>(){
          public int compare(Comment c1,Comment c2){
              double val1 = c1.getScoreFromUser(g);
              double val2 = c2.getScoreFromUser(g);
              if (val1 > val2){
                  return -1;
              } else if (val1 < val2){
                  return 1;
              } else {
                  return 0;
              }
          }
        };
    }
    
    /**
     * Comparator for pushing lower scored comments (relative to user provided location)
     * to the top.
     * @param g The passed GeoLocation.
     * @return A comparator for sorting comments by score relative to user provided location.
     */
    public static Comparator<Comment> sortCommentsByUserScoreLowest(final GeoLocation g){
        return new Comparator<Comment>(){
          public int compare(Comment c1,Comment c2){
              double val1 = c1.getScoreFromUser(g);
              double val2 = c2.getScoreFromUser(g);
              if (val1 > val2){
                  return 1;
              } else if (val1 < val2){
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
    public static Comparator<Comment> sortCommentsByParentScoreLowest() {
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
     * Comparator for sorting comments in a thread based on the current GeoLocation of
     * the Thread's sortLoc member.
     * @param g
     * @return
     */
    public static Comparator<Comment> sortCommentsByLocationDistance(final GeoLocation g){
        return new Comparator<Comment>(){
            public int compare(Comment c1,Comment c2){
                double val1 = c1.getDistanceFrom(g);
                double val2 = c2.getDistanceFrom(g);
                if (val1 > val2){
                    return 1;
                } else if (val1 < val2){
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
    public static Comparator<ThreadComment> sortThreadsByDateOldest() {
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
    public static Comparator<ThreadComment> sortThreadsByDateNewest() {
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
     * @param g The current ThreadList.sortLoc
     * @return A Comparator used to sort Threads according to highest score.
     */
    public static Comparator<ThreadComment> sortThreadsByUserScoreHighest(final GeoLocation g){
        return new Comparator<ThreadComment>(){
            public int compare(ThreadComment t1, ThreadComment t2){
                double val1 = t1.getScoreFromUser(g);
                double val2 = t2.getScoreFromUser(g);
                if (val1 > val2) {
                    return -1;
                } else if (val1 < val2) {//mixed these 2 around
                    return 1;
                } else {
                    return 0;
                }
            }
        };
    }
    
    /**
     * Comparator for pushing lower scored Threads to the top.
     * @param g The current ThreadList.sortLoc
     * @return A Comparator used to sort Threads according to lowest score.
     */
    public static Comparator<ThreadComment> sortThreadsByUserScoreLowest(final GeoLocation g){
        return new Comparator<ThreadComment>(){
            public int compare(ThreadComment t1, ThreadComment t2){
                double val1 = t1.getScoreFromUser(g);
                double val2 = t2.getScoreFromUser(g);
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
    
    public static Comparator<ThreadComment> sortThreadsByLocation(final GeoLocation g){
        return new Comparator<ThreadComment>(){
            public int compare(ThreadComment t1, ThreadComment t2){
                double val1 = t1.getDistanceFrom(g);
                double val2 = t2.getDistanceFrom(g);
                if (val1 > val2){
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
