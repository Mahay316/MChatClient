package com.mahay.mchat.im;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Duplex Thread Pool for executing repeated reconnecting and heartbeat sending tasks
 */
public class ExecutorFactory {
    // boss thread pool, taking care of reconnecting
    private ExecutorService bossPool;
    // worker thread pool, taking care of sending heartbeat packet
    private ExecutorService workerPool;

    public void initBossPool() {
        initBossPool(1);
    }

    public void initBossPool(int size) {
        destoryBossPool();
        bossPool = Executors.newFixedThreadPool(size);
    }

    public void initWorkerPool() {
        initWorkerPool(1);
    }

    public void initWorkerPool(int size) {
        destoryWorkerPool();
        workerPool = Executors.newFixedThreadPool(size);
    }

    public void executeBossTask(Runnable task) {
        if (bossPool == null) {
            initBossPool();
        }
        bossPool.execute(task);
    }

    public void executeWorkerTask(Runnable task) {
        if (workerPool == null) {
            initWorkerPool();
        }
        workerPool.execute(task);
    }

    public void destoryBossPool() {
        if (bossPool != null) {
            try {
                bossPool.shutdownNow();
            } finally {
                bossPool = null;
            }
        }
    }

    public void destoryWorkerPool() {
        if (workerPool != null) {
            try {
                workerPool.shutdownNow();
            } finally {
                workerPool = null;
            }
        }
    }
}

