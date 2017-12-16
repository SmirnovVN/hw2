package ru.mephi.hw2;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CachePeekMode;
import org.junit.Test;

import java.util.Objects;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static ru.mephi.hw2.LogIgnite.cacheConfiguration;
import static ru.mephi.hw2.Server.CACHE_NAME;

public class IgniteTest {

    /**
     * Ignite node started
     * Log ingested
     * Cache size assertion
     */
    @Test
    public void test(){
        try (Ignite ignite = Ignition.start(Objects.requireNonNull(ClassLoader.getSystemClassLoader().
                getResource("config.xml")).getFile())) {
            ignite.active(true);

            try (IgniteCache<Long, LogRecord> cache = ignite.getOrCreateCache(cacheConfiguration(Objects.requireNonNull(ClassLoader.getSystemClassLoader().
                    getResource("test.log")).getFile()))) {
                // load data.
                cache.loadCache(null);

                assertEquals(100, cache.size(CachePeekMode.PRIMARY));
            } catch (Exception e) {
                fail();
            } finally {
                ignite.active(true);
                ignite.destroyCache(CACHE_NAME);
            }
        }

    }
}
