package dev.cli.commands;

import dev.cli.Command;

public class CompareCommand implements Command {
    @Override
    public void execute(String[] args) throws Exception {

    }

    @Override
    public String getName() {
        return "compare";
    }

    @Override
    public String getUsage() {
        return "compare <file1> <file2>";
    }

    @Override
    public String getDescription() {
        return "Compare two files and print their differences";
    }
}
