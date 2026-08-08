package dev.cli.commands;

import dev.cli.Command;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HelpCommandTest {
    private PrintStream originalOut;
    private ByteArrayOutputStream output;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void printsGeneralHelp() {
        Command build = new TestCommand("build", "build <file>", "Build a Merkle tree");
        Command version = new TestCommand("version", "version", "Show version information");
        HelpCommand help = new HelpCommand(List.of(build, version));

        help.execute(new String[0]);

        String result = output.toString();
        assertTrue(result.contains("ztron"));
        assertTrue(result.contains("Usage: ztron <command> [options]"));
        assertTrue(result.contains("build"));
        assertTrue(result.contains("Build a Merkle tree"));
        assertTrue(result.contains("version"));
    }

    @Test
    void printsHelpForSpecificCommand() {
        Command build = new TestCommand("build", "build <file>", "Build a Merkle tree");
        HelpCommand help = new HelpCommand(List.of(build));

        help.execute(new String[]{"build"});

        String result = output.toString();
        assertTrue(result.contains("Command: build"));
        assertTrue(result.contains("Build a Merkle tree"));
        assertTrue(result.contains("ztron build <file>"));
    }

    @Test
    void specificCommandLookupIsCaseInsensitive() {
        Command build = new TestCommand("build", "build <file>", "Build a Merkle tree");
        HelpCommand help = new HelpCommand(List.of(build));

        help.execute(new String[]{"BUILD"});

        assertTrue(output.toString().contains("Command: build"));
    }

    @Test
    void throwsForUnknownCommand() {
        HelpCommand help = new HelpCommand(List.of());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> help.execute(new String[]{"unknown"}));

        assertEquals("Unknown command: unknown", exception.getMessage());
    }

    private record TestCommand(String name, String usage, String description) implements Command {
        @Override
        public void execute(String[] args) { }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getUsage() {
            return usage;
        }

        @Override
        public String getDescription() {
            return description;
        }
    }
}
