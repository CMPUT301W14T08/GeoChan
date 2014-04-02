package ca.ualberta.cmput301w14t08.geochan.test;

import android.provider.Settings.Secure;
import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.helpers.SortUtil;
import ca.ualberta.cmput301w14t08.geochan.managers.PreferencesManager;

public class PreferencesManagerTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity activity;
    private PreferencesManager manager;
    
    public PreferencesManagerTest(Class<MainActivity> activityClass) {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.activity = getActivity();
        PreferencesManager.generateInstance(activity);
        this.manager = PreferencesManager.getInstance();
    }
    
    public void testThreadSort() {
        manager.setThreadSort(SortUtil.SORT_DATE_NEWEST);
        assertTrue("should be date newest", manager.getThreadSort() == SortUtil.SORT_DATE_NEWEST);
        manager.setThreadSort(SortUtil.SORT_DATE_OLDEST);
        assertTrue("should be date newest", manager.getThreadSort() == SortUtil.SORT_DATE_OLDEST);
        manager.setThreadSort(SortUtil.SORT_IMAGE);
        assertTrue("should be date newest", manager.getThreadSort() == SortUtil.SORT_IMAGE);
    }
    
    public void testCommentSort() {
        manager.setCommentSort(SortUtil.SORT_DATE_NEWEST);
        assertTrue("should be date newest", manager.getCommentSort() == SortUtil.SORT_DATE_NEWEST);
        manager.setCommentSort(SortUtil.SORT_DATE_OLDEST);
        assertTrue("should be date newest", manager.getCommentSort() == SortUtil.SORT_DATE_OLDEST);
        manager.setCommentSort(SortUtil.SORT_IMAGE);
        assertTrue("should be date newest", manager.getCommentSort() == SortUtil.SORT_IMAGE);
    }
    
    public void testID() {
        String id = Secure.getString(activity.getContentResolver(), Secure.ANDROID_ID);
        assertTrue("id must match", id == manager.getId());
    }
}
