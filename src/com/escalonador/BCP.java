package com.escalonador;

public class BCP {

	int programCounter;
	enum state {READY, EXECUTING, BLOCKED};
	int registerX;
	int registerY;
	String programName;

	String[] instructions;

}
