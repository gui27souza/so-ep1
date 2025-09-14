package com.escalonador;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Escalonador {

	ArrayList<BCP> proccessTables;

	LinkedList<BCP> readyQueue;
	LinkedList<BCP> blockedQueue;

	int quantum;

	public static void main(String[] args) {
		Escalonador escalonador = new Escalonador();

		escalonador.loadPrograms();
	}

	public Escalonador() {
		this.proccessTables = new ArrayList<>();
		this.readyQueue = new LinkedList<>();
		this.blockedQueue = new LinkedList<>();
	}

	public void loadPrograms() {
		File programsDirectory = new File("programas");
		File[] programFiles = programsDirectory.listFiles();

		if (programFiles != null) {

			ArrayList<File> filesList = new ArrayList<>(Arrays.asList(programFiles));
			filesList.sort(Comparator.comparing(File::getName));

			for (File file : filesList) {
				if (file.isFile() && file.getName().endsWith(".txt")) {
					try {
						Scanner scanner = new Scanner(file);

						if (file.getName().equals("quantum.txt")) {
							this.quantum = Integer.parseInt(scanner.nextLine());
							scanner.close();
						} else {

							String programName = scanner.nextLine();
							ArrayList<String> instructions = new ArrayList<>();
							while (scanner.hasNextLine()) {
								instructions.add(scanner.nextLine());
							}
							scanner.close();

							BCP newBCP = new BCP();
							newBCP.setProgramName(programName);
							newBCP.setInstructions(instructions.toArray(new String[0]));

							proccessTables.add(newBCP);
							readyQueue.add(newBCP);
						}

						System.out.println("Arquivo "+file.getName()+" lido com sucesso!");

					} catch (FileNotFoundException e) {
						System.err.println("Arquivo não encontrado: " + file.getName());
					}
				}
			}
		}
	}

}
