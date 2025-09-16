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
		escalonador.execute();
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

	public void execute() {

		while (!readyQueue.isEmpty() || !blockedQueue.isEmpty()) {

			BCP currentProcess = readyQueue.poll();
			boolean processEnded = false;

			if (currentProcess != null) {
				currentProcess.setState(EstadoProcesso.EXECUTANDO);
				for (int i = 0; i < quantum; i++) {

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
						proccessTables.remove(currentProcess);
						processEnded = true;
						break;
					}
				}

				if (!processEnded && currentProcess.getState() == EstadoProcesso.EXECUTANDO) {
					currentProcess.setState(EstadoProcesso.PRONTO);
					readyQueue.add(currentProcess);
				}
			}

			Iterator<BCP> iterator = blockedQueue.iterator();
			while (iterator.hasNext()) {
				BCP processoBloqueado = iterator.next();
				processoBloqueado.decreaseWaitTime();
				if (processoBloqueado.getWaitTime() <= 0) {
					processoBloqueado.setState(EstadoProcesso.PRONTO);
					readyQueue.add(processoBloqueado);
					iterator.remove();
				}
			}

		}
	}

}
