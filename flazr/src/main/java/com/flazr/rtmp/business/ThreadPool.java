package com.flazr.rtmp.business;

import java.util.LinkedList;

public class ThreadPool extends ThreadGroup {

    /**
     *  线程池是否关闭
     */
    private boolean isClosed = false;

    /**
     * 表示工作队列
     */
    private LinkedList<Runnable> workQueue;

    /**
     * 表示工作队列
     */
    private static int threadPoolID;

    /**
     * 表示工作线程 ID
     */
    private int threadID;

    /**
     * 构造函数
     * @param poolSize 线程池中的工作线程数目
     */
    public ThreadPool(int poolSize) {
        super("ThreadPool-" + (threadPoolID++));
        setDaemon(true);
        // 创建工作队列
        workQueue = new LinkedList<Runnable>();
        for (int i = 0; i < poolSize; i++)
            new WorkThread().start();	// 创建并启动工作线程
    }

    /**
     * 向工作队列中加入一个新任务,由工作线程去执行该任务
     * @param task 新任务
     */
    public synchronized void execute(Runnable task) {
        // 线程池被关则抛出 IllegalStateException 异常
        if (isClosed) {
            throw new IllegalStateException();
        }
        if (task != null) {
            workQueue.add(task);
            // 唤醒正在 getTask()方法中等待任务的工作线程
            notify();
        }
    }

    /**
     * 从工作队列中取出一个任务,工作线程会调用此方法
     * @return 取出的任务
     * @throws InterruptedException
     */
    protected synchronized Runnable getTask() throws InterruptedException {
        while (workQueue.size() == 0) {
            if (isClosed)
                return null;
            // 如果工作队列中没有任务,就等待任务
            wait();
        }
        return workQueue.removeFirst();
    }

    /**
     * 关闭线程池
     */
    public synchronized void close() {
        if (!isClosed) {
            isClosed = true;
            // 清空工作队列
            workQueue.clear();
            // 中断所有的工作线程,该方法继承自 ThreadGroup 类
            interrupt();
        }
    }

    /**
     * 等待工作线程把所有任务执行完
     */
    public void join() {
        synchronized (this) {
            isClosed = true;
            // 唤醒还在 getTask()方法中等待任务的工作线程
            notifyAll();
        }
        Thread[] threads = new Thread[activeCount()];
        // enumerate()方法继承自 ThreadGroup 类,获得线程组中当前所有活着的工作线程
        int count = enumerate(threads);
        // 等待所有工作线程运行结束
        for (int i = 0; i < count; i++) {
            try {
                // 等待工作线程运行结束
                threads[i].join();
            } catch (InterruptedException ex) {
            }
        }
    }

    /**
     * 内部类:工作线程
     * @author robins
     */
    private class WorkThread extends Thread {
        public WorkThread() {
            // 加入到当前ThreadPool线程组中
            super(ThreadPool.this, "WorkThread-" + (threadID++));
        }

        public void run() {
            // isInterrupted()方法继承自Thread类,判断线程是否被中断
            while (!isInterrupted()) {
                Runnable task = null;
                try {
                    // 取出任务
                    task = getTask();
                } catch (InterruptedException ex) {
                }
                // 如果 getTask()返回 null 或者线程执行 getTask()时被中断,则结束此线程
                if (task == null)
                    return;
                try {
                    // 运行任务,异常在 catch 代码块中捕获
                    task.run();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }// #while
        }// #run()
    }// #WorkThread 类
}
