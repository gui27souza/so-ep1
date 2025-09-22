package com.escalonador.model;

import com.escalonador.util.EstadoProcesso;

public class BCP {

    private String programName;
    private EstadoProcesso state;
    private int programCounter;
    private int registerX;
    private int registerY;
    private String[] instructions;
    private int waitTime;

    // Construtor
    public BCP() {
        this.state = EstadoProcesso.PRONTO;
        this.programCounter = 0;
        this.registerX = 0;
        this.registerY = 0;
        this.waitTime = 0;
    }

    // Getters
    public String getProgramName() {
        return programName;
    }

    public EstadoProcesso getState() {
        return state;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public int getRegisterX() {
        return registerX;
    }

    public int getRegisterY() {
        return registerY;
    }

    public String[] getInstructions() {
        return instructions;
    }

    public int getWaitTime() {
        return waitTime;
    }

    // Setters
    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public void setState(EstadoProcesso state) {
        this.state = state;
    }

    public void setProgramCounter(int programCounter) {
        this.programCounter = programCounter;
    }

    public void setRegisterX(int registerX) {
        this.registerX = registerX;
    }

    public void setRegisterY(int registerY) {
        this.registerY = registerY;
    }

    public void setInstructions(String[] instructions) {
        this.instructions = instructions;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public void increaseProgramCounter() {
        programCounter++;
    }

    public void decreaseWaitTime() {
        waitTime--;
    }

    public void dict() {
        System.out.println("DICT " + programName);
        System.out.println("State: " + state);
        System.out.println("Program Counter: " + programCounter);
        System.out.println("Register X: " + registerX);
        System.out.println("Register Y: " + registerY);
        System.out.println("Instructions:");
        for (String instruction : instructions) {
            System.out.println("\t- " + instruction);
        }
        System.out.println("Waiting Time: " + waitTime);
    }
}