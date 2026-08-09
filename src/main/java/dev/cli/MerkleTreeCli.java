package dev.cli;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MerkleTreeCli {
    private final Map<String, Command> commands;

    public MerkleTreeCli(List<Command> commands) {
        this.commands = new HashMap<>();
        for (Command command : commands) this.commands.put(command.getName(), command);
    }

    public void run(String[] args) throws Exception {
        String commandName = args.length == 0 ? "help" : args[0].toLowerCase();

        if ("-h".equals(commandName) || "--help".equals(commandName)) commandName = "help";
        if ("-v".equals(commandName) || "--version".equals(commandName)) commandName = "version";

        Command command = commands.get(commandName);
        if (command == null) throw new IllegalArgumentException("Unknown command: " + commandName);

        String[] commandArgs = args.length > 0 ? Arrays.copyOfRange(args, 1, args.length) : new String[0];
        command.execute(commandArgs);
    }
}