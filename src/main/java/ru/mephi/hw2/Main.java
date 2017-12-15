package ru.mephi.hw2;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.*;

import javax.cache.Cache;
import java.util.ArrayList;
import java.util.List;

import static ru.mephi.hw2.LogIgnite.cacheConfiguration;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.round;
import static org.apache.spark.sql.functions.sum;
import static org.apache.spark.sql.functions.lit;
import static org.apache.spark.sql.functions.format_number;


public class Main {

    static long scale = 300000L;
    static String SCALE = "20s";
    /**
     * Main method.
     * @param args Command line arguments, none required.
     */
    public static void main(String args[]) {
        SparkConf sparkConf = new SparkConf()
                .setAppName("HW2")
                .setMaster("local")
                .set("spark.executor.instances", "2");
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
        SQLContext sqlContext = new SQLContext(sparkContext);

        // Get all records from ignite
        List<Cache.Entry<Long, LogRecord>> res;
        Ignition.setClientMode(true);
        try (Ignite ignite = Ignition.start(args[0])) {
            ignite.active(true);
            try (IgniteCache<Long, LogRecord> cache = ignite.getOrCreateCache(cacheConfiguration(null))) {
                res = cache.query(new SqlQuery<Long, LogRecord>(LogRecord.class, "id > 0")).getAll();
            }
        }
        List<LogRecord> records = new ArrayList<>(res.size());
        for (Cache.Entry<Long, LogRecord> entry : res) {
            records.add(entry.getValue());
        }

        System.out.println("Create Dataset by records");
        Dataset<LogRecord> dsRecords = sqlContext.createDataset(records, Encoders.bean(LogRecord.class));

        System.out.println("Evaluate task");
        Dataset<Row> ds = dsRecords.withColumn("scaledTime", round(col("timestamp").divide(scale)));

        ds = ds.groupBy(col("id"), col("scaledTime")).agg(sum("size").as("sumSize"))
                .select(col("id"), lit(SCALE), format_number(col("scaledTime").$times(scale), 0), col("sumSize"));


        // Show the result of the execution.
        ds.coalesce(1).write().csv(args[1]);

        System.exit(0);
    }
}
