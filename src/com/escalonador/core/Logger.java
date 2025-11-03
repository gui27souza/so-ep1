package com.escalonador.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {
    
    private PrintWriter printWriter;
		private String outputDirectory = "output";

	public Logger(int quantum) {
		String quantumFormatado = String.format("%02d", quantum);
		String fileName = "log" + quantumFormatado + ".txt";

		File directory = new File(outputDirectory);
		if (!directory.exists()) {
			directory.mkdirs();
		}

		try {
			FileWriter fileWriter = new FileWriter(outputDirectory + "/" + fileName);
			this.printWriter = new PrintWriter(fileWriter);
		} catch (IOException e) {
			System.err.println("Erro ao criar o arquivo de log: " + e.getMessage());
			this.printWriter = null;
		}
	}

    public void log(String message) {
        if (printWriter != null) {
            printWriter.println(message);
        }
    }

    public void close() {
        if (printWriter != null) {
            printWriter.close();
        }
    }
}

