package com.escalonador;

public class BCP {

	String programName;
	EstadoProcesso state;
	int programCounter;
	int registerX;
	int registerY;
	String[] instructions;

	public BCP() {}

	void setProgramName(String programName) { this.programName = programName; }
	void setState(EstadoProcesso state) { this.state = state; }
	void setProgramCounter(int programCounter) { this.programCounter = programCounter; }
	void setRegisterX(int registerX) { this.registerX = registerX; }
	void setRegisterY(int registerY) { this.registerY = registerY; }
	void setInstructions(String[] instructions) { this.instructions = instructions; }

	String getProgramName() { return programName; }
	EstadoProcesso getState() { return state; }
	int getProgramCounter() { return programCounter; }
	int getRegisterX() { return registerX; }
	int getRegisterY() { return registerY; }
	String[] getInstructions() { return instructions; }
}
