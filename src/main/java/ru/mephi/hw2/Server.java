package ru.mephi.hw2;

import org.apache.ignite.Ignition;

public class Server {

    /**
     * Cache name.
     */
    static final String CACHE_NAME = "LOG";

    /**
     * Start server node
     * @param args
     */
    public static void main(String[] args) {
        Ignition.start(args[0]);
    }
}
