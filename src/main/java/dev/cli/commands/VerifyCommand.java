package dev.cli.commands;

import dev.cli.Command;

public class VerifyCommand implements Command {
    @Override
    public void execute(String[] args) throws Exception {

    }

    @Override
    public String getName() {
        return "verify";
    }

    @Override
    public String getUsage() {
        return "verify <file> <hash>";
    }

    @Override
    public String getDescription() {
        return "Verify the integrity of a file against a given hash";
    }
}
