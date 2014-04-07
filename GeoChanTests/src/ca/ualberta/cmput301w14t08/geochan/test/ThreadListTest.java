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

package ca.ualberta.cmput301w14t08.geochan.test;

import android.app.Activity;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;

public class ThreadListTest extends ActivityInstrumentationTestCase2<MainActivity> {
    
    
    public ThreadListTest(){
        super(MainActivity.class);
    }
    
    protected void setUp() throws Exception{
        super.setUp();
        MainActivity a = (MainActivity) waitForActivity(5000);
        assertNotNull("fragment not initialized",a);
    }
    
    /**
     * http://stackoverflow.com/a/17789933
     * Sometimes the emulator is too slow.
     */
    protected Activity waitForActivity(int timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() <= endTime) {

            Activity a = getActivity();
            if (a != null) {
                return a;
            }
        }
        return null;
    }
    

    public void testAddThread() {
        Comment comment = new Comment();
        ThreadList.addThread(comment, "Test title");
        assertTrue(ThreadList.getThreads().size() == 1);
    }
    
    public void testClearThreads(){
        ThreadComment t1 = new ThreadComment();
        ThreadComment t2 = new ThreadComment();
        ThreadComment t3 = new ThreadComment();
        
        ThreadList.addThread(t1);
        ThreadList.addThread(t2);
        ThreadList.addThread(t3);
        assertTrue("Our initialization passed.", 
                    ThreadList.getThreads().size() != 0);
        
        ThreadList.clearThreads();
        
        assertTrue("Clearing threads succeeded.",
                    ThreadList.getThreads().size() == 0);
    }
    
//    @SuppressWarnings("static-access")
//    public void testSortThreadsByDateNewest(){
//        /*
//         * Tests ThreadList.sortThreads("DATE_NEWEST")
//         */
//        ThreadList.clearThreads();
//        long extraTime = 1320000;
//        ThreadList tm = new ThreadList();
//        ThreadComment t1 = new ThreadComment();
//        ThreadComment t2 = new ThreadComment();
//        ThreadComment t3 = new ThreadComment();
//        ThreadComment t4 = new ThreadComment();
//        ThreadComment t5 = new ThreadComment();
//        
//        Date currentDate = new Date();
//        
//        t1.setThreadDate(new Date(currentDate.getTime() + 1*extraTime));
//        t2.setThreadDate(new Date(currentDate.getTime() + 2*extraTime));
//        t3.setThreadDate(new Date(currentDate.getTime() + 3*extraTime));
//        t4.setThreadDate(new Date(currentDate.getTime() + 4*extraTime));
//        t5.setThreadDate(new Date(currentDate.getTime() + 5*extraTime));
//        
//        tm.setThreads(new ArrayList<ThreadComment>());
//        tm.addThread(t1);
//        tm.addThread(t2);
//        tm.addThread(t5);
//        tm.addThread(t3);
//        tm.addThread(t4);
//        
//        tm.sortThreads(SortUtil.SORT_DATE_NEWEST);
//        
//        assertTrue("t5 is at index 0", tm.getThreads().get(0) == t5);
//        assertTrue("t4 is at index 1", tm.getThreads().get(1) == t4);
//        assertTrue("t3 is at index 2", tm.getThreads().get(2) == t3);
//        assertTrue("t2 is at index 3", tm.getThreads().get(3) == t2);
//        assertTrue("t1 is at index 4", tm.getThreads().get(4) == t1);
//    }
//    
//    @SuppressWarnings("static-access")
//    public void testSortThreadsByDateOldest(){
//        /*
//         * Tests ThreadList.sortThreads("DATE_OLDEST")
//         */
//        ThreadList.clearThreads();
//        long extraTime = 1320000;
//        ThreadList tm = new ThreadList();
//        ThreadComment t1 = new ThreadComment();
//        ThreadComment t2 = new ThreadComment();
//        ThreadComment t3 = new ThreadComment();
//        ThreadComment t4 = new ThreadComment();
//        ThreadComment t5 = new ThreadComment();
//        
//        Date currentDate = new Date();
//        
//        t1.setThreadDate(new Date(currentDate.getTime() + 1*extraTime));
//        t2.setThreadDate(new Date(currentDate.getTime() + 2*extraTime));
//        t3.setThreadDate(new Date(currentDate.getTime() + 3*extraTime));
//        t4.setThreadDate(new Date(currentDate.getTime() + 4*extraTime));
//        t5.setThreadDate(new Date(currentDate.getTime() + 5*extraTime));
//        
//        tm.setThreads(new ArrayList<ThreadComment>());
//        tm.addThread(t1);
//        tm.addThread(t2);
//        tm.addThread(t5);
//        tm.addThread(t3);
//        tm.addThread(t4);
//        
//        tm.sortThreads(SortUtil.SORT_DATE_OLDEST);
//        
//        assertTrue("t1 is at index 0", tm.getThreads().get(0) == t1);
//        assertTrue("t2 is at index 1", tm.getThreads().get(1) == t2);
//        assertTrue("t3 is at index 2", tm.getThreads().get(2) == t3);
//        assertTrue("t4 is at index 3", tm.getThreads().get(3) == t4);
//        assertTrue("t5 is at index 4", tm.getThreads().get(4) == t5);
//    }
//    
//    /**
//     * Tests the sorting of comments in a thread by the score relative to the user.
//     */
//    @SuppressWarnings("static-access")
//    public void testSortByUserScoreHighest(){
//        LocationListenerService llc = new LocationListenerService(getActivity());
//        llc.startListening();
//        
//        ThreadList.clearThreads();
//        long extraTime = 1320000;
//        Date currentDate = new Date();
//        ThreadList T = new ThreadList();
//        
//        ThreadComment t1 = new ThreadComment();
//        ThreadComment t2 = new ThreadComment();
//        ThreadComment t3 = new ThreadComment();
//        ThreadComment t4 = new ThreadComment();
//        ThreadComment t5 = new ThreadComment();
//        
//        Comment c1 = new Comment();
//        Comment c2 = new Comment();
//        Comment c3 = new Comment();
//        Comment c4 = new Comment();
//        Comment c5 = new Comment();
//        
//        Location loc1 = new Location(LocationManager.GPS_PROVIDER);
//        Location loc2 = new Location(LocationManager.GPS_PROVIDER);
//        Location loc3 = new Location(LocationManager.GPS_PROVIDER);
//        Location loc4 = new Location(LocationManager.GPS_PROVIDER);
//        Location loc5 = new Location(LocationManager.GPS_PROVIDER);
//        Location locT = new Location(LocationManager.GPS_PROVIDER);
//        
//        GeoLocation g1 = new GeoLocation(llc);
//        GeoLocation g2 = new GeoLocation(llc);
//        GeoLocation g3 = new GeoLocation(llc);
//        GeoLocation g4 = new GeoLocation(llc);
//        GeoLocation g5 = new GeoLocation(llc);
//        GeoLocation gT = new GeoLocation(llc);
//        
//        g1.setLocation(loc1);
//        g2.setLocation(loc2);
//        g3.setLocation(loc3);
//        g4.setLocation(loc4);
//        g5.setLocation(loc5);
//        gT.setLocation(locT);
//        
//        c1.setLocation(g1);
//        c2.setLocation(g2);
//        c3.setLocation(g3);
//        c4.setLocation(g4);
//        c5.setLocation(g5);
//        T.setSortLoc(gT);
//        
//        c1.setTextPost("c1");
//        c2.setTextPost("c2");
//        c3.setTextPost("c3");
//        c4.setTextPost("c4");
//        c5.setTextPost("c5");
//        /*
//        t1.setTopComment(c1);
//        t2.setTopComment(c2);
//        t3.setTopComment(c3);
//        t4.setTopComment(c4);
//        t5.setTopComment(c5);
//        */
//        t1.getBodyComment().getLocation().setCoordinates(1,1);
//        t2.getBodyComment().getLocation().setCoordinates(2,2);
//        t3.getBodyComment().getLocation().setCoordinates(3,3);
//        t4.getBodyComment().getLocation().setCoordinates(4,4);
//        t5.getBodyComment().getLocation().setCoordinates(5,5);
//        
//        t1.setThreadDate(new Date(currentDate.getTime() + extraTime * 1));
//        t2.setThreadDate(new Date(currentDate.getTime() + extraTime * 2));
//        t3.setThreadDate(new Date(currentDate.getTime() + extraTime * 3));
//        t4.setThreadDate(new Date(currentDate.getTime() + extraTime * 4));
//        t5.setThreadDate(new Date(currentDate.getTime() + extraTime * 5));
//        
//        T.addThread(t2);
//        T.addThread(t3);
//        T.addThread(t1);
//        T.addThread(t5);
//        T.addThread(t4);
//        
//        T.getSortLoc().setCoordinates(0,0);
//        
//        T.sortThreads(SortUtil.SORT_USER_SCORE_HIGHEST);
//        
//        assertEquals("t1 is at index 0:", t1, T.getThreads().get(0));
//        assertEquals("t2 is at index 1:", t2, T.getThreads().get(1));
//        assertEquals("t3 is at index 2:", t3, T.getThreads().get(2));
//        assertEquals("t4 is at index 3:", t4, T.getThreads().get(3));
//        assertEquals("t5 is at index 4:", t5, T.getThreads().get(4));
//    }
//    
//    /**
//     * Tests the sorting of comments in a thread by the score relative to the user.
//     */
//    @SuppressWarnings("static-access")
//    public void testSortByUserScoreLowest(){
//        LocationListenerService llc = new LocationListenerService(getActivity());
//        llc.startListening();
//        
//        ThreadList.clearThreads();
//        long extraTime = 1320000;
//        Date currentDate = new Date();
//        ThreadList T = new ThreadList();
//        
//        ThreadComment t1 = new ThreadComment();
//        ThreadComment t2 = new ThreadComment();
//        ThreadComment t3 = new ThreadComment();
//        ThreadComment t4 = new ThreadComment();
//        ThreadComment t5 = new ThreadComment();
//        
//        Comment c1 = new Comment();
//        Comment c2 = new Comment();
//        Comment c3 = new Comment();
//        Comment c4 = new Comment();
//        Comment c5 = new Comment();
//        
//        Location loc1 = new Location(LocationManager.GPS_PROVIDER);
//        Location loc2 = new Location(LocationManager.GPS_PROVIDER);
//        Location loc3 = new Location(LocationManager.GPS_PROVIDER);
//        Location loc4 = new Location(LocationManager.GPS_PROVIDER);
//        Location loc5 = new Location(LocationManager.GPS_PROVIDER);
//        Location locT = new Location(LocationManager.GPS_PROVIDER);
//        
//        GeoLocation g1 = new GeoLocation(llc);
//        GeoLocation g2 = new GeoLocation(llc);
//        GeoLocation g3 = new GeoLocation(llc);
//        GeoLocation g4 = new GeoLocation(llc);
//        GeoLocation g5 = new GeoLocation(llc);
//        GeoLocation gT = new GeoLocation(llc);
//        
//        g1.setLocation(loc1);
//        g2.setLocation(loc2);
//        g3.setLocation(loc3);
//        g4.setLocation(loc4);
//        g5.setLocation(loc5);
//        gT.setLocation(locT);
//        
//        c1.setLocation(g1);
//        c2.setLocation(g2);
//        c3.setLocation(g3);
//        c4.setLocation(g4);
//        c5.setLocation(g5);
//        T.setSortLoc(gT);
//        
//        c1.setTextPost("c1");
//        c2.setTextPost("c2");
//        c3.setTextPost("c3");
//        c4.setTextPost("c4");
//        c5.setTextPost("c5");
//        /*
//        t1.setTopComment(c1);
//        t2.setTopComment(c2);
//        t3.setTopComment(c3);
//        t4.setTopComment(c4);
//        t5.setTopComment(c5);
//        */
//        t1.getBodyComment().getLocation().setCoordinates(1,1);
//        t2.getBodyComment().getLocation().setCoordinates(2,2);
//        t3.getBodyComment().getLocation().setCoordinates(3,3);
//        t4.getBodyComment().getLocation().setCoordinates(4,4);
//        t5.getBodyComment().getLocation().setCoordinates(5,5);
//        
//        t1.setThreadDate(new Date(currentDate.getTime() + extraTime * 1));
//        t2.setThreadDate(new Date(currentDate.getTime() + extraTime * 2));
//        t3.setThreadDate(new Date(currentDate.getTime() + extraTime * 3));
//        t4.setThreadDate(new Date(currentDate.getTime() + extraTime * 4));
//        t5.setThreadDate(new Date(currentDate.getTime() + extraTime * 5));
//        
//        T.addThread(t2);
//        T.addThread(t3);
//        T.addThread(t1);
//        T.addThread(t5);
//        T.addThread(t4);
//        
//        T.getSortLoc().setCoordinates(0,0);
//        
//        T.sortThreads(SortUtil.SORT_USER_SCORE_LOWEST);
//        
//        assertEquals("t5 is at index 0:", t5, T.getThreads().get(0));
//        assertEquals("t4 is at index 1:", t4, T.getThreads().get(1));
//        assertEquals("t3 is at index 2:", t3, T.getThreads().get(2));
//        assertEquals("t2 is at index 3:", t2, T.getThreads().get(3));
//        assertEquals("t1 is at index 4:", t1, T.getThreads().get(4));
//    }
//    
//    /**
//     * Tests the sorting of comments in a thread by the score relative to the user.
//     */
//    @SuppressWarnings("static-access")
//    public void testSortByLocation(){
//        LocationListenerService llc = new LocationListenerService(getActivity());
//        llc.startListening();
//        
//        ThreadList.clearThreads();
//        long extraTime = 1320000;
//        Date currentDate = new Date();
//        ThreadList T = new ThreadList();
//        
//        ThreadComment t1 = new ThreadComment();
//        ThreadComment t2 = new ThreadComment();
//        ThreadComment t3 = new ThreadComment();
//        ThreadComment t4 = new ThreadComment();
//        ThreadComment t5 = new ThreadComment();
//        
//        Comment c1 = new Comment();
//        Comment c2 = new Comment();
//        Comment c3 = new Comment();
//        Comment c4 = new Comment();
//        Comment c5 = new Comment();
//        
//        Location loc1 = new Location(LocationManager.GPS_PROVIDER);
//        Location loc2 = new Location(LocationManager.GPS_PROVIDER);
//        Location loc3 = new Location(LocationManager.GPS_PROVIDER);
//        Location loc4 = new Location(LocationManager.GPS_PROVIDER);
//        Location loc5 = new Location(LocationManager.GPS_PROVIDER);
//        Location locT = new Location(LocationManager.GPS_PROVIDER);
//        
//        GeoLocation g1 = new GeoLocation(llc);
//        GeoLocation g2 = new GeoLocation(llc);
//        GeoLocation g3 = new GeoLocation(llc);
//        GeoLocation g4 = new GeoLocation(llc);
//        GeoLocation g5 = new GeoLocation(llc);
//        GeoLocation gT = new GeoLocation(llc);
//        
//        g1.setLocation(loc1);
//        g2.setLocation(loc2);
//        g3.setLocation(loc3);
//        g4.setLocation(loc4);
//        g5.setLocation(loc5);
//        gT.setLocation(locT);
//        
//        c1.setLocation(g1);
//        c2.setLocation(g2);
//        c3.setLocation(g3);
//        c4.setLocation(g4);
//        c5.setLocation(g5);
//        T.setSortLoc(gT);
//        
//        c1.setTextPost("c1");
//        c2.setTextPost("c2");
//        c3.setTextPost("c3");
//        c4.setTextPost("c4");
//        c5.setTextPost("c5");
//        /*
//        t1.setTopComment(c1);
//        t2.setTopComment(c2);
//        t3.setTopComment(c3);
//        t4.setTopComment(c4);
//        t5.setTopComment(c5);
//        */
//        t1.getBodyComment().getLocation().setCoordinates(1,1);
//        t2.getBodyComment().getLocation().setCoordinates(2,2);
//        t3.getBodyComment().getLocation().setCoordinates(3,3);
//        t4.getBodyComment().getLocation().setCoordinates(4,4);
//        t5.getBodyComment().getLocation().setCoordinates(5,5);
//        
//        t1.setThreadDate(new Date(currentDate.getTime() + extraTime * 1));
//        t2.setThreadDate(new Date(currentDate.getTime() + extraTime * 2));
//        t3.setThreadDate(new Date(currentDate.getTime() + extraTime * 3));
//        t4.setThreadDate(new Date(currentDate.getTime() + extraTime * 4));
//        t5.setThreadDate(new Date(currentDate.getTime() + extraTime * 5));
//        
//        T.addThread(t2);
//        T.addThread(t3);
//        T.addThread(t1);
//        T.addThread(t5);
//        T.addThread(t4);
//        
//        T.getSortLoc().setCoordinates(0,0);
//        
//        T.sortThreads(SortUtil.SORT_LOCATION);
//        
//        assertEquals("t1 is at index 0:", t1, T.getThreads().get(0));
//        assertEquals("t2 is at index 1:", t2, T.getThreads().get(1));
//        assertEquals("t3 is at index 2:", t3, T.getThreads().get(2));
//        assertEquals("t4 is at index 3:", t4, T.getThreads().get(3));
//        assertEquals("t5 is at index 4:", t5, T.getThreads().get(4));
//    }
}
