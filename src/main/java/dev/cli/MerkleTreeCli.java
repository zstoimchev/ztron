package dev.cli;

import dev.cli.commands.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MerkleTreeCli {
    private final Map<String, Command> commands;

    public MerkleTreeCli(List<Command> commands) {
        this.commands = new HashMap<>();
        for (Command command : commands) this.commands.put(command.getName(), command);
    }


    public void run(String[] args) throws Exception {
        if (args.length == 0) showUsage();
        String commandName = args[0].toLowerCase();

        if ("help".equals(commandName) || "--help".equals(commandName) || "-h".equals(commandName)) {
            if (args.length > 1) showCommandHelp(args[1]);
            else showUsage();
        }

        if ("version".equals(commandName) || "--version".equals(commandName) || "-v".equals(commandName)) showVersion();

        Command command = commands.get(commandName);
        if (command == null) throw new IllegalArgumentException("Unknown command: " + commandName);

        String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
        command.execute(commandArgs);
    }

    private void showUsage() {
        String commandList = commands
                .values()
                .stream()
                .map(command -> String.format("  %-12s %s", command.getName(), command.getDescription()))
                .collect(Collectors.joining("\n"));

        System.out.printf("""
                Usage: ztron <command> [options]
                Commands: %s
                Options:
                  -h, --help       Show help
                  -v, --version    Show version
                %n""", commandList);

        System.exit(0);
    }

    private void showCommandHelp(String commandName) {
        Command command = commands.get(commandName);

        if (command == null) throw new IllegalArgumentException("Unknown command: " + commandName);

        System.out.println(command.getName());
        System.out.println();
        System.out.println(command.getDescription());
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  ztron " + command.getUsage());

        System.exit(0);
    }

    private void showVersion() {
        System.out.println("zTron v0.0.1");
        System.exit(0);
    }
}