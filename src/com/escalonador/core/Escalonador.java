package com.escalonador.core;

import com.escalonador.model.BCP;
import com.escalonador.util.EstadoProcesso;
import com.escalonador.model.TabelaProcessos;
import com.escalonador.queue.BlockedQueue;
import com.escalonador.queue.ReadyQueue;
import com.escalonador.core.ProgramLoader;
import java.util.List;

public class Escalonador {

    private TabelaProcessos tabelaProcessos;
    private ReadyQueue readyQueue;
    private BlockedQueue blockedQueue;
    private int quantum;

    public Escalonador() {
        ProgramLoader loader = new ProgramLoader();
        loader.carregarTudo();

        this.quantum = loader.getQuantum();
        List<BCP> processosCarregados = loader.getProcessos();

        this.tabelaProcessos = new TabelaProcessos();
        this.readyQueue = new ReadyQueue();
        this.blockedQueue = new BlockedQueue();

        for (BCP processo : processosCarregados) {
            tabelaProcessos.addProcess(processo);
            readyQueue.add(processo);
            System.out.println("Arquivo " + processo.getProgramName() + " carregado.");
        }
    }

    public static void main(String[] args) {
        Escalonador escalonador = new Escalonador();
        escalonador.execute();
    }

    public void execute() {
        while (!readyQueue.isEmpty() || !blockedQueue.isEmpty()) {

            if (readyQueue.isEmpty()) {
                blockedQueue.updateReadyQueue(readyQueue);
                continue;
            }

            BCP currentProcess = readyQueue.poll();
            boolean processEnded = false;

            if (currentProcess != null) {
                currentProcess.setState(EstadoProcesso.EXECUTANDO);

                for (int i = 0; i < quantum; i++) {
                    if (currentProcess.getProgramCounter() >= currentProcess.getInstructions().length) {
                        processEnded = true;
                        break;
                    }

                    String currentInstruction = currentProcess.getInstructions()[currentProcess.getProgramCounter()];

                    if (currentInstruction.startsWith("X=")) {
                        int registerValue = Integer.parseInt(currentInstruction.substring(2));
                        currentProcess.setRegisterX(registerValue);
                        currentProcess.increaseProgramCounter();
                    } else if (currentInstruction.startsWith("Y=")) {
                        int registerValue = Integer.parseInt(currentInstruction.substring(2));
                        currentProcess.setRegisterY(registerValue);
                        currentProcess.increaseProgramCounter();
                    } else if (currentInstruction.equals("COM")) {
                        currentProcess.increaseProgramCounter();
                    } else if (currentInstruction.equals("E/S")) {
                        currentProcess.setState(EstadoProcesso.BLOQUEADO);
                        currentProcess.setWaitTime(2);
                        blockedQueue.add(currentProcess);
                        currentProcess.increaseProgramCounter();
                        break;
                    } else if (currentInstruction.equals("SAIDA")) {
                        tabelaProcessos.removeProcess(currentProcess);
                        processEnded = true;
                        break;
                    }
                }

                if (!processEnded && currentProcess.getState() == EstadoProcesso.EXECUTANDO) {
                    currentProcess.setState(EstadoProcesso.PRONTO);
                    readyQueue.add(currentProcess);
                }
            }

            blockedQueue.updateReadyQueue(readyQueue);
        }
        System.out.println("Execução finalizada.");
    }
}
