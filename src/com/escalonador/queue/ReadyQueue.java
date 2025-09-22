package com.escalonador.queue;

import java.util.LinkedList;
import com.escalonador.model.BCP;

public class ReadyQueue {

    private LinkedList<BCP> queue;

    public ReadyQueue() {
        this.queue = new LinkedList<>();
    }

    public void add(BCP process) {
        queue.add(process);
    }

    public BCP poll() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
