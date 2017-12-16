package ru.mephi.hw2;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static ru.mephi.hw2.Main.process;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.count;

public class SparkTest {

    /**
     * Check dataset aggregation
     */
    @Test
    public void test() {
        SparkConf sparkConf = new SparkConf()
                .setAppName("HW2")
                .setMaster("local")
                .set("spark.executor.instances", "1");
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
        SQLContext sqlContext = new SQLContext(sparkContext);

        List<LogRecord> records = new ArrayList<>();

        records.add(new LogRecord(1L,1234123L,23L));
        records.add(new LogRecord(1L,1234123L,23L));
        records.add(new LogRecord(2L,1234223L,23L));

        Dataset<Row> ds = process(records, sqlContext);

        assertEquals(2, ds.select(count(col("id"))).first().getLong(0));

        assertEquals(46, ds.select(col("sumSize")).first().getLong(0));
    }
}
