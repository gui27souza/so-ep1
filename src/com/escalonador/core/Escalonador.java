package com.escalonador.core;

import com.escalonador.model.BCP;
import com.escalonador.model.TabelaProcessos;
import com.escalonador.queue.BlockedQueue;
import com.escalonador.queue.ReadyQueue;
import com.escalonador.util.EstadoProcesso;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Escalonador {

    private TabelaProcessos tabelaProcessos;
    private ReadyQueue readyQueue;
    private BlockedQueue blockedQueue;
    private int quantum;
    private Logger logger;
    private int numProcessosIniciais;
    private List<BCP> processosFinalizados;
    
    private int totalTrocas;
    private int totalInstrucoes;
    private int totalQuantuns;
    
    public Escalonador(String[] args) {
        ProgramLoader loader = new ProgramLoader();
        String quantumArg = (args.length > 0) ? args[0] : null;
        loader.carregarTudo(quantumArg);

        this.quantum = loader.getQuantum();
        List<BCP> processosCarregados = loader.getProcessos();
        this.processosFinalizados = new ArrayList<>();

        this.logger = new Logger(this.quantum);
        this.tabelaProcessos = new TabelaProcessos();
        this.readyQueue = new ReadyQueue();
        this.blockedQueue = new BlockedQueue();
        this.numProcessosIniciais = processosCarregados.size();

        logger.log("Quantum = " + this.quantum);
        logger.log("------------------------------------------");

        for (BCP processo : processosCarregados) {
            tabelaProcessos.addProcess(processo);
            readyQueue.add(processo);
            logger.log("Carregando " + processo.getProgramName());
        }

        logger.log("------------------------------------------");
    }

    public static void main(String[] args) {
        Escalonador escalonador = new Escalonador(args);
        escalonador.execute();
    }

    public void execute() {
        while (!readyQueue.isEmpty() || !blockedQueue.isEmpty()) {

            if (readyQueue.isEmpty()) {
                // Atualiza fila de prontos com processos desbloqueados
                blockedQueue.updateReadyQueue(readyQueue);
                continue;
            }

            BCP currentProcess = readyQueue.poll();
            boolean processEnded = false;
            int instrucoesExecutadasNoQuantum = 0;

            if (currentProcess != null) {
                logger.log("Executando " + currentProcess.getProgramName());
                currentProcess.setState(EstadoProcesso.EXECUTANDO);

                // Conta troca de contexto
                totalTrocas++;
                
                for (int i = 0; i < quantum; i++) {
                    // Verifica fim do programa
                    if (currentProcess.getProgramCounter() >= currentProcess.getInstructions().length) {
                        processEnded = true;
                        break;
                    }

                    String instrucao = currentProcess.getInstructions()[currentProcess.getProgramCounter()];
                    instrucoesExecutadasNoQuantum++;

                    switch (instrucao) {
                        case "COM":
                            // Apenas conta instrução
                            break;
                        case "E/S":
                            logger.log("E/S iniciada em " + currentProcess.getProgramName());
                            logger.log("Interrompendo " + currentProcess.getProgramName() + " após " + instrucoesExecutadasNoQuantum + " instruções");
                            currentProcess.setState(EstadoProcesso.BLOQUEADO);
                            currentProcess.setWaitTime(2); // 2 quanta de espera
                            blockedQueue.add(currentProcess);
                            currentProcess.increaseProgramCounter();
                            break;
                        case "SAIDA":
                            logger.log(currentProcess.getProgramName() + " terminado. X=" + currentProcess.getRegisterX() + ". Y=" + currentProcess.getRegisterY());
                            currentProcess.setCompletionTime(totalQuantuns + 1); // ciclo atual como tempo de conclusão
                            processosFinalizados.add(currentProcess);
                            tabelaProcessos.removeProcess(currentProcess);
                            processEnded = true;
                            break;
                        default:
                            if (instrucao.startsWith("X=")) {
                                currentProcess.setRegisterX(Integer.parseInt(instrucao.substring(2)));
                            } else if (instrucao.startsWith("Y=")) {
                                currentProcess.setRegisterY(Integer.parseInt(instrucao.substring(2)));
                            }
                            break;
                    }

                    if (instrucao.equals("E/S") || instrucao.equals("SAIDA")) break;
                    currentProcess.increaseProgramCounter();
                }

                totalInstrucoes += instrucoesExecutadasNoQuantum;
                totalQuantuns++;
                
                if (!processEnded && currentProcess.getState() == EstadoProcesso.EXECUTANDO) {
                    logger.log("Interrompendo " + currentProcess.getProgramName() + " após " + instrucoesExecutadasNoQuantum + " instruções");
                    currentProcess.setState(EstadoProcesso.PRONTO);
                    readyQueue.add(currentProcess);
                }

                blockedQueue.updateReadyQueue(readyQueue);
            }
        }
        
        // Estatísticas finais
        logger.log("------------------------------------------");
        double mediaTrocas = Math.round(((double) totalTrocas / numProcessosIniciais) * 100.0) / 100.0;
        double mediaInstrucoes = Math.round(((double) totalInstrucoes / totalQuantuns) * 100.0) / 100.0;
        logger.log("MEDIA DE TROCAS: " + mediaTrocas);
        logger.log("MEDIA DE INSTRUCOES: " + mediaInstrucoes);
        logger.log("QUANTUM: " + quantum);
        logTurnaroundStatistics();
        logger.close();
        System.out.println("Execução finalizada. Verifique o arquivo de log.");
    }

    public void logTurnaroundStatistics() {
        if (processosFinalizados.isEmpty()) return;

        logger.log("------------------------------------------");
        logger.log("--- ESTATISTICAS DE TURNAROUND ---");

        double somaTurnaround = 0;
        processosFinalizados.sort(Comparator.comparing(BCP::getProgramName));

        for (BCP processo : processosFinalizados) {
            int turnaround = processo.getTurnaround();
            logger.log("Processo: " + processo.getProgramName() + " | Turnaround: " + turnaround + " ciclos.");
            somaTurnaround += turnaround;
        }

        double mediaTurnaround = Math.round((somaTurnaround / processosFinalizados.size()) * 100.0) / 100.0;
        logger.log("MEDIA DE TURNAROUND: " + mediaTurnaround);
    }
}
