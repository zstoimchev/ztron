package dev.cli.commands;

import dev.cli.Command;

public class VersionCommand implements Command {

    @Override
    public void execute(String[] args) {
        System.out.println("zTron 0.0.1");
        System.out.println("Java " + System.getProperty("java.version"));
    }

    @Override
    public String getName() {
        return "version";
    }

    @Override
    public String getUsage() {
        return "version";
    }

    @Override
    public String getDescription() {
        return "Show version information";
    }
}
