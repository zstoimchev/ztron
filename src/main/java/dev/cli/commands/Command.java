package dev.cli.commands;

public interface Command {
    void execute(String[] args) throws Exception;

    String getName();

    String getUsage();

    String getDescription();
}