package com.dlink.daemon.entity;

import javafx.concurrent.Task;

import java.util.LinkedList;

public class TaskQueue<T> {

    private final LinkedList<T> tasks = new LinkedList<>();

    private final Object lock = new Object();


    public void enqueue(T task) {
        synchronized (lock) {
            lock.notifyAll();
            tasks.addLast( task );
        }
    }

    public T dequeue() {
        synchronized (lock) {
            while (tasks.isEmpty()) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            T task = tasks.removeFirst();
            return task;
        }
    }

    public int getTaskSize() {
        synchronized (lock) {
            return tasks.size();
        }
    }
}
