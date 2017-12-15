package ru.mephi.hw2;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.ignite.cache.affinity.AffinityKey;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

/**
 * LogRecord class.
 */
public class LogRecord implements Serializable {
    private static final AtomicLong ID_GEN = new AtomicLong();
    /** Record ID (indexed). */
    @QuerySqlField(index = true)
    public Long recordId;

    /** ID. */
    @QuerySqlField()
    public Long id;

    /** Timestamp. */
    @QuerySqlField
    public Long timestamp;

    /** Size. */
    @QuerySqlField
    public Long size;

    /**
     * Default constructor.
     */
    public LogRecord() {
        // No-op.
    }

    /**
     * Constructs LogRecord record.
     *
     * @param id        Id.
     * @param timestamp Timestamp.
     * @param size      Size.
     */
    public LogRecord(Long id, Long timestamp, Long size) {
        this.recordId = ID_GEN.incrementAndGet();
        this.id = id;
        this.timestamp = timestamp;
        this.size = size;
    }

    /**
     * Constructs LogRecord record.
     *
     * @param recordId LogRecord ID.
     * @param id        Id.
     * @param timestamp Timestamp.
     * @param size      Size.
     */
    public LogRecord(Long recordId, Long id, Long timestamp, Long size) {
        this.recordId = recordId;
        this.id = id;
        this.timestamp = timestamp;
        this.size = size;
    }


    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "LogRecord{" +
                "recordId=" + recordId +
                ", id=" + id +
                ", timestamp=" + timestamp +
                ", size=" + size +
                '}';
    }
}
