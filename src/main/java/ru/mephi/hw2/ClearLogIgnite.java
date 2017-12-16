package ru.mephi.hw2;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;

import static ru.mephi.hw2.Server.CACHE_NAME;

public class ClearLogIgnite {

    /**
     * Clear Ignite cache.
     *
     * @param args Command line arguments, 0 - ignite config.xml path.
     * @throws IgniteException If example execution failed.
     */
    public static void main(String[] args) throws IgniteException {
        Ignition.setClientMode(true);
        try (Ignite ignite = Ignition.start(args[0])) {
            ignite.active(true);
            ignite.destroyCache(CACHE_NAME);
        }
    }
}
