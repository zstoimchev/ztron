package dev.cli.commands;

import dev.cli.Command;

import java.util.List;
import java.util.stream.Collectors;

public class HelpCommand implements Command {

    private final List<Command> commands;

    public HelpCommand(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            showGeneralHelp();
            return;
        }
        showCommandHelp(args[0]);
    }

    private void showGeneralHelp() {
        String commandList = commands
                .stream()
                .map(command -> String.format("  %-12s %s", command.getName(), command.getDescription()))
                .collect(Collectors.joining("\n"));

        System.out.printf("""
                Usage: ztron <command> [options]
                
                Commands:
                %s
                
                Options:
                  -h, --help       Show help
                  -v, --version    Show version
                %n""", commandList);
    }

    private void showCommandHelp(String commandName) {
        Command command = commands
                .stream()
                .filter(cmd -> cmd.getName().equalsIgnoreCase(commandName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown command: " + commandName));

        System.out.printf("""
                Command: %s
                Description: %s
                Usage: ztron %s
                %n""", command.getName(), command.getDescription(), command.getUsage());
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getUsage() {
        return "help [command]";
    }

    @Override
    public String getDescription() {
        return "Show help information";
    }
}
