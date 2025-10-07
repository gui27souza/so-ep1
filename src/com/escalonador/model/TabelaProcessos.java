package com.escalonador.model;

import java.util.ArrayList;
import java.util.List;

public class TabelaProcessos {
    
    private List<BCP> processList;

    public TabelaProcessos() {
        this.processList = new ArrayList<>();
    }

    public void addProcess(BCP processo) {
        this.processList.add(processo);
    }

    public void removeProcess(BCP processo) {
        this.processList.remove(processo);
    }

    public int getProcessCount() {
        return this.processList.size();
    }
}