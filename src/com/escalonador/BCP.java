package com.escalonador;

public class BCP {

	private String programName;
	private EstadoProcesso state;
	private int programCounter;
	private int registerX;
	private int registerY;
	private String[] instructions;
	private int waitTime;

	public BCP() {
		this.state = EstadoProcesso.PRONTO;
		this.programCounter = 0;
		this.registerX = 0;
		this.registerY = 0;
		waitTime = 0;
	}

	void setProgramName(String programName) { this.programName = programName; }
	void setState(EstadoProcesso state) { this.state = state; }
	void setProgramCounter(int programCounter) { this.programCounter = programCounter; }
	void setRegisterX(int registerX) { this.registerX = registerX; }
	void setRegisterY(int registerY) { this.registerY = registerY; }
	void setInstructions(String[] instructions) { this.instructions = instructions; }
	void setWaitTime(int waitTime) { this.waitTime = waitTime; }

	String getProgramName() { return programName; }
	EstadoProcesso getState() { return state; }
	int getProgramCounter() { return programCounter; }
	int getRegisterX() { return registerX; }
	int getRegisterY() { return registerY; }
	String[] getInstructions() { return instructions; }
	int getWaitTime() { return waitTime; }

	void increaseProgramCounter() { programCounter++; }
	void decreaseWaitTime() { waitTime--; }

	void dict() {
		System.out.println("DICT "+programName);
		System.out.println("State: "+state);
		System.out.println("Program Counter: "+programCounter);
		System.out.println("Register X: "+registerX);
		System.out.println("Register Y: "+registerY);
		System.out.println("Instructions:");
		for(String instruction : instructions) System.out.println("\t-"+instruction);
		System.out.println("Waiting Time: "+waitTime);
	}
}
