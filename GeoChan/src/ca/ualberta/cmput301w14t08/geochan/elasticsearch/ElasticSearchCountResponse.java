package ca.ualberta.cmput301w14t08.geochan.elasticsearch;

public class ElasticSearchCountResponse {
    int count;
    transient Object _shards;

    public int getCount() {
        return count;
    }
}
