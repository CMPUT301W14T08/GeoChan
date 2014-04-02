package ca.ualberta.cmput301w14t08.geochan.runnables;

import ca.ualberta.cmput301w14t08.geochan.elasticsearch.ElasticSearchGetTask;

public class ElasticSearchGetCommentRunnable implements Runnable {

    private ElasticSearchGetTask task;
    
    public ElasticSearchGetCommentRunnable(ElasticSearchGetTask task) {
        this.task = task;
    }
    
    @Override
    public void run() {
        // TODO Auto-generated method stub

    }

}
