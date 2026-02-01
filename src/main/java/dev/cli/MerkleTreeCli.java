package dev.cli;

import dev.cli.commands.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MerkleTreeCli {
    private final Map<String, Command> commands;

    public MerkleTreeCli() {
        this.commands = new HashMap<>();
        registerCommands();
    }

    private void registerCommands() {
        registerCommand(new BuildCommand());
        registerCommand(new VerifyCommand());
        registerCommand(new CompareCommand());
        registerCommand(new HelpCommand());
    }

    private void registerCommand(Command command) {
        commands.put(command.getName(), command);
    }

    public void run(String[] args) throws Exception {
        if (args.length == 0) {
            showUsage();
            return;
        }

        String commandName = args[0].toLowerCase();

        // Handle help
        if ("help".equals(commandName) || "--help".equals(commandName) || "-h".equals(commandName)) {
            if (args.length > 1) showCommandHelp(args[1]);
            else showUsage();
            return;
        }

        // Handle version
        if ("version".equals(commandName) || "--version".equals(commandName) || "-v".equals(commandName)) {
            showVersion();
            return;
        }

        // Find and execute command
        Command command = commands.get(commandName);
        if (command == null) {
            System.err.println("Unknown command: " + commandName);
            System.err.println("Run 'merkle help' for usage information.");
            System.exit(1);
        }

        // Pass remaining arguments to command
        String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
        command.execute(commandArgs);
    }

    private void showUsage() {
        System.out.println("Merkle Tree CLI - File integrity verification using Merkle trees");
        System.out.println();
        System.out.println("Usage: merkle <command> [options]");
        System.out.println();
        System.out.println("Commands:");
        for (Command cmd : commands.values()) {
            if (!(cmd instanceof HelpCommand)) {  // Don't show help in the list
                System.out.printf("  %-12s %s%n", cmd.getName(), cmd.getDescription());
            }
        }
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -h, --help       Show this help message");
        System.out.println("  -v, --version    Show version information");
        System.out.println();
        System.out.println("Run 'merkle help <command>' for more information on a specific command.");
    }

    private void showCommandHelp(String commandName) {
        Command command = commands.get(commandName);
        if (command == null) {
            System.err.println("Unknown command: " + commandName);
            System.exit(1);
        }

        System.out.println("Command: " + command.getName());
        System.out.println();
        System.out.println("Description:");
        System.out.println("  " + command.getDescription());
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  merkle " + command.getUsage());
        System.out.println();
        System.out.println("Examples:");

        switch (command) {
            case BuildCommand ignored -> {
                System.out.println("  merkle build document.pdf");
                System.out.println("  merkle build document.pdf --chunk-size 8192");
                System.out.println("  merkle build document.pdf -c 8192 --algorithm SHA512");
            }
            case VerifyCommand ignored -> {
                System.out.println("  merkle verify document.pdf a3f5b8c9d2e1f4a7...");
                System.out.println("  merkle verify document.pdf $(cat hash.txt)");
                System.out.println("  merkle verify document.pdf a3f5b8c9 --chunk-size 8192");
            }
            case CompareCommand ignored -> {
                System.out.println("  merkle compare file1.pdf file2.pdf");
                System.out.println("  merkle compare original.txt modified.txt --chunk-size 8192");
            }
            default -> {
            }
        }
    }

    private void showVersion() {
        System.out.println("Merkle Tree CLI version 0.0.1");
        System.out.println("Built with Java " + System.getProperty("java.version"));
    }
}