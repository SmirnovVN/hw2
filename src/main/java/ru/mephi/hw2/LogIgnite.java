package ru.mephi.hw2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.*;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.integration.CacheLoaderException;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.cache.store.CacheLoadOnlyStoreAdapter;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.internal.util.IgniteUtils;
import org.apache.ignite.internal.util.typedef.T2;
import org.apache.ignite.lang.IgniteBiTuple;
import org.jetbrains.annotations.Nullable;

import static ru.mephi.hw2.Server.CACHE_NAME;

public class LogIgnite {

    /**
     * Ingest log to Ignite.
     *
     * @param args Command line arguments, 0 - ignite config.xml path, 1 - csv file path.
     * @throws IgniteException If example execution failed.
     */
    public static void main(String[] args) throws IgniteException {
        Ignition.setClientMode(true);
        try (Ignite ignite = Ignition.start(args[0])) {
            ignite.active(true);

            try (IgniteCache<Long, LogRecord> cache = ignite.getOrCreateCache(cacheConfiguration(args[1]))) {
                // load data.
                cache.loadCache(null);

                System.out.println(">>> Loaded number of items: " + cache.size(CachePeekMode.PRIMARY));
            }
        }
    }

    /**
     * Creates cache configurations for the loader.
     *
     * @return {@link CacheConfiguration}.
     */
    static CacheConfiguration<Long, LogRecord> cacheConfiguration(String path) {
        CacheConfiguration<Long, LogRecord> cacheCfg = new CacheConfiguration<>();

        cacheCfg.setCacheMode(CacheMode.PARTITIONED);
        cacheCfg.setName(CACHE_NAME);
        cacheCfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
        cacheCfg.setBackups(1);
        cacheCfg.setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_SYNC);
        cacheCfg.setIndexedTypes(Long.class, LogRecord.class);
        // provide the loader.
        if (path != null) {
            ProductLoader productLoader = new ProductLoader(path);

            productLoader.setThreadsCount(2);
            productLoader.setBatchSize(10);
            productLoader.setBatchQueueSize(1);
            cacheCfg.setCacheStoreFactory(new FactoryBuilder.SingletonFactory(productLoader));
        }

        return cacheCfg;
    }

    /**
     * Csv data loader for product data.
     */
    private static class ProductLoader extends CacheLoadOnlyStoreAdapter<Long, LogRecord, String> implements Serializable {
        /**
         * Csv file name.
         */
        final String csvFileName;

        /**
         * Constructor.
         */
        ProductLoader(String csvFileName) {
            this.csvFileName = csvFileName;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Iterator<String> inputIterator(@Nullable Object... args) throws CacheLoaderException {
            final Scanner scanner;

            try {
                File path = IgniteUtils.resolveIgnitePath(csvFileName);

                if (path == null)
                    throw new CacheLoaderException("Failed to open the source file: " + csvFileName);

                scanner = new Scanner(path);

                scanner.useDelimiter("\\n");
            } catch (FileNotFoundException e) {
                throw new CacheLoaderException("Failed to open the source file " + csvFileName, e);
            }

            /*
              Iterator for text input. The scanner is implicitly closed when there's nothing to scan.
             */
            return new Iterator<String>() {
                /** {@inheritDoc} */
                @Override
                public boolean hasNext() {
                    if (!scanner.hasNext()) {
                        scanner.close();

                        return false;
                    }

                    return true;
                }

                /** {@inheritDoc} */
                @Override
                public String next() {
                    if (!hasNext())
                        throw new NoSuchElementException();

                    return scanner.next();
                }

                @Override
                public void remove() {

                }
            };
        }

        /**
         * {@inheritDoc}
         */
        @Nullable
        @Override
        protected IgniteBiTuple<Long, LogRecord> parse(String rec, @Nullable Object... args) {
            String[] p = rec.split("\\s*,\\s*");
            LogRecord record = new LogRecord(Long.valueOf(p[0]),
                    Long.valueOf(p[1]), Long.valueOf(p[2]));
            return new T2<>(record.recordId, record);
        }
    }
}
