package com.restbucks.ordering.client;

import java.util.HashMap;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;

public class TestHelper {
    public static final String BASE_URI = "http://localhost:9998/";
    private static TestHelper testHelper;
    
    private String serviceUri;
    private SelectorThread threadSelector;

    public static synchronized TestHelper getInstance() {
        if(testHelper != null) { 
            return testHelper;
        } else {
            return getInstance(BASE_URI);
        }
    }

    public static synchronized TestHelper getInstance(String baseUri) {
        if(testHelper == null) {
            testHelper = new TestHelper(baseUri);
        }
        return testHelper;
    }

    private TestHelper(String uri) {
        serviceUri = uri;
    }

    public void startServer() throws Exception {
        final HashMap<String, String> initParams = new HashMap<String, String>();
        initParams.put("com.sun.jersey.config.property.packages", "com.restbucks.ordering.resources");
        threadSelector = GrizzlyWebContainerFactory.create(serviceUri, initParams);
    }

    public void stopServer() {
        threadSelector.stopEndpoint();
    }
}
