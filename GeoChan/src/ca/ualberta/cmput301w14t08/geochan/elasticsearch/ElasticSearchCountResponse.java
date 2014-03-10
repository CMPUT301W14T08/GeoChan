package ca.ualberta.cmput301w14t08.geochan.elasticsearch;

import java.util.ArrayList;
import java.util.Collection;

public class ElasticSearchCountResponse {
    int count;
    transient Object _shards;
    
    public int getCount() {
        return count;
    }
}
