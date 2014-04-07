package ca.ualberta.cmput301w14t08.geochan.test;

import io.searchbox.client.JestClient;
import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.helpers.ElasticSearchClient;


public class ElasticSearchClientTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private ElasticSearchClient client;
    
    public ElasticSearchClientTest() {
        super(MainActivity.class);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    public void testConstruction() {
        client = ElasticSearchClient.getInstance();
        assertNotNull(client);
    }
    
    public void testGetJest() {
        JestClient jest = client.getClient();
        assertNotNull(jest);
    }
    
}
