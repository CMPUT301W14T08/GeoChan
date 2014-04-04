package ca.ualberta.cmput301w14t08.geochan.elasticsearch.runnables;

import ca.ualberta.cmput301w14t08.geochan.elasticsearch.tasks.GetPOITask;

public class GetPOIRunnable implements Runnable {

    private GetPOITask task;
    public static final int STATE_GET_POI_FAILED = -1;
    public static final int STATE_GET_POI_RUNNING = 0;
    public static final int STATE_GET_POI_COMPLETE = 1;

    public GetPOIRunnable(GetPOITask task) {
        this.task = task;
    }
    @Override
    public void run() {
        task.setGetPOIThread(Thread.currentThread());
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT);
    }
}