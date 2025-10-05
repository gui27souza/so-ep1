package com.escalonador.core;

import com.escalonador.model.BCP;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class ProgramLoader {

    private int quantum;
    private List<BCP> processos;
    private final String DIRETORIO_PROGRAMAS = "programas";

    public ProgramLoader() {
        this.processos = new ArrayList<>();
    }

    public void carregarTudo(String quantumArg) {
        carregarQuantum(quantumArg);
        carregarProcessos();
    }

    private void carregarQuantum(String quantumArg) {

				if (quantumArg != null) {
						try {
								this.quantum = Integer.parseInt(quantumArg.trim());
								System.out.println("Quantum definido via CLI: " + this.quantum);
								return;
						} catch (NumberFormatException e) {
								System.err.println("Aviso: Valor de quantum da CLI inválido. Tentando ler de arquivo...");
						}
				}

        try (Scanner scanner = new Scanner(new File(DIRETORIO_PROGRAMAS + "/quantum.txt"))) {
            if (scanner.hasNextLine()) {
                this.quantum = Integer.parseInt(scanner.nextLine());
								System.out.println("Quantum definido via arquivo: " + this.quantum);
            }
				} catch (FileNotFoundException e) {
					System.err.println("Erro: quantum.txt não encontrado. Usando quantum = 1.");
					this.quantum = 1;
				} catch (NumberFormatException e) {
					System.err.println("Erro: Conteúdo inválido em quantum.txt. Usando quantum = 1.");
					this.quantum = 1;
				}
    }

    private void carregarProcessos() {
        File diretorio = new File(DIRETORIO_PROGRAMAS);
        File[] arquivos = diretorio.listFiles();

        if (arquivos != null) {
            List<File> listaArquivos = new ArrayList<>(Arrays.asList(arquivos));
            listaArquivos.sort(Comparator.comparing(File::getName));

            for (File arquivo : listaArquivos) {
                if (isValidProgramFile(arquivo)) {
                    try (Scanner scanner = new Scanner(arquivo)) {
                        String programName = scanner.nextLine();
                        ArrayList<String> instructions = new ArrayList<>();
                        while (scanner.hasNextLine()) {
                            instructions.add(scanner.nextLine());
                        }

                        BCP newBCP = new BCP();
                        newBCP.setProgramName(programName);
                        newBCP.setInstructions(instructions.toArray(new String[0]));

                        this.processos.add(newBCP);

                    } catch (FileNotFoundException e) {
                        System.err.println("Arquivo não encontrado: " + arquivo.getName());
                    }
                }
            }
        }
    }
		private boolean isValidProgramFile(File arquivo) {

			int fileNumber;
			try {
				fileNumber = Integer.parseInt(arquivo.getName().substring(0, 2));
			} catch (NumberFormatException e) {
				return false;
			}

			boolean isValidFileNumber = (fileNumber > 0 && fileNumber <= 10);

			return (
				arquivo.isFile() &&
				arquivo.getName().endsWith(".txt") &&
				!arquivo.getName().equals("quantum.txt") &&
				arquivo.getName().length() == 6 &&
					isValidFileNumber
			);
		}

    public int getQuantum() {
        return this.quantum;
    }

    public List<BCP> getProcessos() {
        return this.processos;
    }
}