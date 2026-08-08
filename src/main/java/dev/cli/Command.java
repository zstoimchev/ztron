package dev.cli;

public interface Command {
    void execute(String[] args) throws Exception;

    String getName();

    String getUsage();

    String getDescription();
}