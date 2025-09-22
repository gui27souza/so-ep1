package com.escalonador.queue;

import java.util.Iterator;
import java.util.LinkedList;
import com.escalonador.model.BCP;
import com.escalonador.util.EstadoProcesso;

public class BlockedQueue {

    private LinkedList<BCP> queue;

    public BlockedQueue() {
        this.queue = new LinkedList<>();
    }

    public void add(BCP process) {
        queue.add(process);
    }

    public void updateReadyQueue(ReadyQueue readyQueue) {
        Iterator<BCP> iterator = queue.iterator();
        while (iterator.hasNext()) {
            BCP blockedProcess = iterator.next();
            blockedProcess.decreaseWaitTime();
            if (blockedProcess.getWaitTime() <= 0) {
                blockedProcess.setState(EstadoProcesso.PRONTO);
                readyQueue.add(blockedProcess);
                iterator.remove();
            }
        }
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
