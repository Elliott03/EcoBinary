package com.ecobinary.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class CommandService {
    private String command;
    private String output;
    public CommandService() {

    }
    public CommandService(String command) {
        this.command = command;

    }

    @Override
    public String toString() {
        return "CommandService{" +
                "command='" + command + '\'' +
                ", output='" + output + '\'' +
                '}';
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getOutput() {
        return output;
    }

    public void acquireOutput() {
        char[] ch = new char[command.length()];

        for (int i = 0; i < command.length(); i++) {
            ch[i] = command.charAt(i);
            if (ch[i] == ' ') {
                ch[i] = ',';

            }
        }
        String endCommand = String.valueOf(ch);

        String[] commandArray = endCommand.split(",");


        ProcessBuilder pb = new ProcessBuilder(commandArray);

        Process p = null;
        try {
            p = pb.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String readline;
        while(true) {
            try {
                if (!((readline = reader.readLine()) != null)) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.output  = this.output + "\n" + readline;
            System.out.println(this.output);
        }
    }

    public void setOutput(String output) {

        this.output = output;
    }
}
