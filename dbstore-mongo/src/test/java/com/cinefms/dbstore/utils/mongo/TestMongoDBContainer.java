package com.cinefms.dbstore.utils.mongo;

import org.testcontainers.containers.GenericContainer;

public class TestMongoDBContainer<SELF extends TestMongoDBContainer<SELF>> extends GenericContainer<SELF> {

    public static final String IMAGE = "mongo";
    public static final String DEFAULT_TAG = "3.6.18";
    public static final int MONGO_PORT = 27017;

    public static TestMongoDBContainer<?> container;

    public TestMongoDBContainer() {
        this(IMAGE + ":" + DEFAULT_TAG);
    }

    public TestMongoDBContainer(String dockerImageName) {
        super(dockerImageName);
        withExposedPorts(MONGO_PORT);
    }

    public static TestMongoDBContainer<?> getInstance() {
        if (container == null) {
            container = new TestMongoDBContainer<>();
        }

        return container;
    }

    public Integer getPort() {
        return getMappedPort(MONGO_PORT);
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }

}
