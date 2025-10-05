package com.escalonador.core;

import com.escalonador.model.BCP;
import com.escalonador.model.TabelaProcessos;
import com.escalonador.queue.BlockedQueue;
import com.escalonador.queue.ReadyQueue;
import com.escalonador.util.EstadoProcesso;
import java.util.List;

public class Escalonador {

    private TabelaProcessos tabelaProcessos;
    private ReadyQueue readyQueue;
    private BlockedQueue blockedQueue;
    private int quantum;
    private Logger logger;
    private int numProcessosIniciais;
    
    // Variáveis para as estatísticas
    private int totalTrocas;
    private int totalInstrucoes;
    private int totalQuantuns;
    
    public Escalonador(String[] args) {
				ProgramLoader loader = new ProgramLoader();
				String quantumArg = (args.length > 0) ? args[0] : null;
        loader.carregarTudo(quantumArg);

        this.quantum = loader.getQuantum();
        List<BCP> processosCarregados = loader.getProcessos();

        this.logger = new Logger(this.quantum);
        this.tabelaProcessos = new TabelaProcessos();
        this.readyQueue = new ReadyQueue();
        this.blockedQueue = new BlockedQueue();
        numProcessosIniciais = processosCarregados.size();

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
                // Se a fila de prontos estiver vazia, atualiza a fila de bloqueados
                blockedQueue.updateReadyQueue(readyQueue);
                continue;
                // if (readyQueue.isEmpty()) {
                //     // Se a fila de prontos continuar vazia, espera até que um processo seja desbloqueado
                //     continue; 
                // }
            }

            BCP currentProcess = readyQueue.poll();
            boolean processEnded = false;
            int instrucoesExecutadasNoQuantum = 0;

            if (currentProcess != null) {
                logger.log("Executando " + currentProcess.getProgramName());
                currentProcess.setState(EstadoProcesso.EXECUTANDO);
                
                // Incrementa o contador de trocas de contexto
                totalTrocas++;
                
                for (int i = 0; i < quantum; i++) {
                    // Verifica se chegou ao fim do programa
                    if (currentProcess.getProgramCounter() >= currentProcess.getInstructions().length) {
                        processEnded = true;
                        break;
                    }

                    String currentInstruction = currentProcess.getInstructions()[currentProcess.getProgramCounter()];
                    instrucoesExecutadasNoQuantum++;

                    if (currentInstruction.startsWith("X=")) {
                        int registerValue = Integer.parseInt(currentInstruction.substring(2));
                        currentProcess.setRegisterX(registerValue);
                    } else if (currentInstruction.startsWith("Y=")) {
                        int registerValue = Integer.parseInt(currentInstruction.substring(2));
                        currentProcess.setRegisterY(registerValue);
                    } else if (currentInstruction.equals("COM")) {
                        // Não faz nada além de contar a instrução
                    } else if (currentInstruction.equals("E/S")) {
                        logger.log("E/S iniciada em " + currentProcess.getProgramName());
                        currentProcess.setState(EstadoProcesso.BLOQUEADO);
                        currentProcess.setWaitTime(2); // tempo de espera de 2 quanta
                        blockedQueue.add(currentProcess);
                        currentProcess.increaseProgramCounter();
                        break;
                    } else if (currentInstruction.equals("SAIDA")) {
                        logger.log(currentProcess.getProgramName() + " terminado. X=" + currentProcess.getRegisterX() + ". Y=" + currentProcess.getRegisterY());
                        tabelaProcessos.removeProcess(currentProcess);
                        processEnded = true;
                        break;
                    }
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
        
        // Log das estatísticas finais
        logger.log("------------------------------------------");
        double mediaTrocas = (double) totalTrocas / numProcessosIniciais;
        double mediaInstrucoes = (double) totalInstrucoes / totalQuantuns;
        logger.log("MEDIA DE TROCAS: " + mediaTrocas);
        logger.log("MEDIA DE INSTRUCOES: " + mediaInstrucoes);
        logger.log("QUANTUM: " + quantum);
        
        logger.close();
        System.out.println("Execução finalizada. Verifique o arquivo de log.");
    }
}