package ru.mephi.hw2;

import org.apache.ignite.Ignition;

import java.util.Objects;

public class Server {

    /**
     * Cache name.
     */
    static final String CACHE_NAME = "LOG";

    public static void main(String[] args) throws Exception {
        Ignition.start(args[0]);
    }
}
