package com.escalonador.util;

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

    public void carregarTudo() {
        carregarQuantum();
        carregarProcessos();
    }

    private void carregarQuantum() {
        try (Scanner scanner = new Scanner(new File(DIRETORIO_PROGRAMAS + "/quantum.txt"))) {
            if (scanner.hasNextLine()) {
                this.quantum = Integer.parseInt(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.err.println("Erro: quantum.txt não encontrado. Usando quantum = 1.");
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
                if (arquivo.isFile() && !arquivo.getName().equals("quantum.txt")) {
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

    public int getQuantum() {
        return this.quantum;
    }

    public List<BCP> getProcessos() {
        return this.processos;
    }
}