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
 * Helper class. Manages sorting of ArrayLists of different ThreadComments and
 * Comments by different measures.
 * 
 * @author Henry Pabst
 * 
 */
public class SortUtil {

    public static final int SORT_DATE_NEWEST = 0;
    public static final int SORT_DATE_OLDEST = 1;
    @Deprecated
    public static final int SORT_SCORE_HIGHEST = 3;
    @Deprecated
    public static final int SORT_SCORE_LOWEST = 4;
    public static final int SORT_USER_SCORE_HIGHEST = 5;
    public static final int SORT_USER_SCORE_LOWEST = 6;
    public static final int SORT_LOCATION = 7;
    public static final int SORT_IMAGE = 8;

    private static GeoLocation commentSortGeo = null;
    private static GeoLocation threadSortGeo = null;

    public static GeoLocation getCommentSortGeo() {
        if (commentSortGeo == null) {
            return new GeoLocation(0, 0);
        }
        return commentSortGeo;
    }

    public static GeoLocation getThreadSortGeo() {
        if (threadSortGeo == null) {
            return new GeoLocation(0, 0);
        }
        return threadSortGeo;
    }

    public static void setCommentSortGeo(GeoLocation g) {
        commentSortGeo = g;
    }

    public static void setThreadSortGeo(GeoLocation g) {
        threadSortGeo = g;
    }

    /**
     * Sorts the ArrayList of ThreadComments passed to it according to the tag
     * passed. The passed GeoLocation is used in sorting by location or score.
     * 
     * @param tag
     *            Tag specifying the sorting method to be used.
     * @param threads
     *            The ArrayList of ThreadComments to be sorted.
     */
    public static void sortThreads(int tag, ArrayList<ThreadComment> threads) {
        switch (tag) {
        case (SORT_DATE_OLDEST):
            Collections.sort(threads, sortThreadsByDateOldest());
            break;
        case (SORT_DATE_NEWEST):
            Collections.sort(threads, sortThreadsByDateNewest());
            break;
        case (SORT_USER_SCORE_HIGHEST):
            Collections.sort(threads, sortThreadsByUserScoreHighest());
            break;
        case (SORT_USER_SCORE_LOWEST):
            Collections.sort(threads, sortThreadsByUserScoreLowest());
            break;
        case (SORT_LOCATION):
            Collections.sort(threads, sortThreadsByLocation());
            break;
        }
    }

    /**
     * Sorts the ArrayList of Comments according to the tag passed to it.
     * Recursively sorts all the children of these comments according to the
     * same measure.
     * 
     * @param tag
     *            Tag specifying the type of sorting to be done.
     * @param threads
     *            The ArrayList of Comments to be sorted.
     */

    public static void sortComments(int tag, ArrayList<Comment> coms) {
        switch (tag) {
        case (SORT_DATE_OLDEST):
            Collections.sort(coms, sortCommentsByDateOldest());
            for (Comment c : coms) {
                sortComments(tag, c.getChildren());
            }
            break;
        case (SORT_DATE_NEWEST):
            Collections.sort(coms, sortCommentsByDateNewest());
            for (Comment c : coms) {
                sortComments(tag, c.getChildren());
            }
            break;
        case (SORT_LOCATION):
            Collections.sort(coms, sortCommentsByLocation());
            for (Comment c : coms) {
                sortComments(tag, c.getChildren());
            }
            break;
        case (SORT_USER_SCORE_HIGHEST):
            Collections.sort(coms, sortCommentsByUserScoreHighest());
            for (Comment c : coms) {
                sortComments(tag, c.getChildren());
            }
            break;
        case (SORT_USER_SCORE_LOWEST):
            Collections.sort(coms, sortCommentsByUserScoreLowest());
            for (Comment c : coms) {
                sortComments(tag, c.getChildren());
            }
            break;
        case (SORT_IMAGE):
            Collections.sort(coms, sortCommentsByImage());
            for (Comment c : coms) {
                sortComments(tag, c.getChildren());
            }
            break;
        }
    }

    /**
     * Comparator for pushing comments with images to the top. Uses comment date
     * to break ties if both comments have images, or do not have images.
     * 
     * @return A comparator used to sort comments by image.
     */
    private static Comparator<Comment> sortCommentsByImage() {
        return new Comparator<Comment>() {
            public int compare(Comment c1, Comment c2) {
                if (c1.hasImage() && !(c2.hasImage())) {
                    return -1;
                } else if (!(c1.hasImage()) && c2.hasImage()) {
                    return 1;
                } else if (c1.hasImage() == c2.hasImage()) {
                    int val = c1.getCommentDate().compareTo(c2.getCommentDate());
                    if (val < 0) {
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
     * 
     * @return a Comparator for sorting Comments by oldest date.
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
     * 
     * @return A Comparator for sorting Comments by newest Date.
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
     * Returns a comparator for pushing higher scored comments to the top.
     * 
     * @return A Comparator for pushing higher scored comments to the top.
     */
    private static Comparator<Comment> sortCommentsByUserScoreHighest() {
        return new Comparator<Comment>() {
            public int compare(Comment c1, Comment c2) {
                double val1 = c1.getScoreFromUser(SortUtil.getCommentSortGeo());
                double val2 = c2.getScoreFromUser(SortUtil.getCommentSortGeo());
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
     * Comparator for pushing lower scored comments (relative to user set
     * location) to the top.
     * 
     * @return A comparator for sorting comments by score relative to user set
     *         location.
     */
    private static Comparator<Comment> sortCommentsByUserScoreLowest() {
        return new Comparator<Comment>() {
            public int compare(Comment c1, Comment c2) {
                double val1 = c1.getScoreFromUser(SortUtil.getCommentSortGeo());
                double val2 = c2.getScoreFromUser(SortUtil.getCommentSortGeo());
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
     * SortUtil.commentSortGeo.
     * 
     * @return the comparator
     */
    private static Comparator<Comment> sortCommentsByLocation() {
        return new Comparator<Comment>() {
            public int compare(Comment c1, Comment c2) {
                double val1 = c1.getDistanceFrom(SortUtil.getCommentSortGeo());
                double val2 = c2.getDistanceFrom(SortUtil.getCommentSortGeo());
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
     * 
     * @return a Comparator for sorting threads by oldest date.
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
     * 
     * @return a Comparator for sorting Comments by newest Date.
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
     * @return A Comparator used to sort ThreadComments according to highest
     *         score.
     */
    private static Comparator<ThreadComment> sortThreadsByUserScoreHighest() {
        return new Comparator<ThreadComment>() {
            public int compare(ThreadComment t1, ThreadComment t2) {
                double val1 = t1.getScoreFromUser(SortUtil.getThreadSortGeo());
                double val2 = t2.getScoreFromUser(SortUtil.getThreadSortGeo());
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
     * Comparator for pushing lower scored Threads to the top.
     * 
     * @return A Comparator used to sort ThreadComments according to lowest
     *         score.
     */
    private static Comparator<ThreadComment> sortThreadsByUserScoreLowest() {
        return new Comparator<ThreadComment>() {
            public int compare(ThreadComment t1, ThreadComment t2) {
                double val1 = t1.getScoreFromUser(SortUtil.getThreadSortGeo());
                double val2 = t2.getScoreFromUser(SortUtil.getThreadSortGeo());
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
     * Comparator for pushing ThreadComments closer to SortUtil's threadSortGeo
     * to the top.
     * 
     * @return A Comparator used to sort ThreadComments according to location.
     */
    private static Comparator<ThreadComment> sortThreadsByLocation() {
        return new Comparator<ThreadComment>() {
            public int compare(ThreadComment t1, ThreadComment t2) {
                double val1 = t1.getDistanceFrom(SortUtil.getThreadSortGeo());
                double val2 = t2.getDistanceFrom(SortUtil.getThreadSortGeo());
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
