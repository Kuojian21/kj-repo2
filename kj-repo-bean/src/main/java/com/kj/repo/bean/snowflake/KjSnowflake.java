package com.kj.repo.bean.snowflake;

/**
 * @author bjzhangkuojian
 */
public class KjSnowflake {

    private static final int WORKER_BIT = 10;
    private static final int WORKER_MASK = -1 ^ (-1 << WORKER_BIT);
    private static final int SEQ_BIT = 12;
    private static final int SEQ_MASK = -1 ^ (-1 << SEQ_BIT);
    private static final int WORK_SEQ_BIT = WORKER_BIT + SEQ_BIT;
    private static final long IDEPOCH = 1344322705519L;

    private int workerId;
    private int sequence = 0;
    private long lastTimestamp = -1L;

    public KjSnowflake(int workerId) {
        this.workerId = workerId & WORKER_MASK;
    }

    public long nextId() {
        long timestamp = System.currentTimeMillis();
        int seq = 0;
        synchronized (this) {
            if (timestamp < this.lastTimestamp) {
                throw new RuntimeException("snowflake");
            } else if (timestamp == this.lastTimestamp) {
                seq = ++this.sequence & SEQ_MASK;
                if (seq == 0) {
                    do {
                        timestamp = System.currentTimeMillis();
                    } while (timestamp == this.lastTimestamp);
                    this.sequence = 0;
                    this.lastTimestamp = timestamp;
                }
            } else {
                this.lastTimestamp = timestamp;
                this.sequence = 0;
            }
        }

        return ((timestamp - IDEPOCH) << WORK_SEQ_BIT) | (this.workerId << SEQ_BIT) | seq;
    }

}
