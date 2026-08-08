package dev.cli;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MerkleTreeCliTest {

    @Test
    void dispatchesCommandAndPassesRemainingArguments() throws Exception {
        TestCommand build = new TestCommand("build");
        MerkleTreeCli cli = new MerkleTreeCli(List.of(build));

        cli.run(new String[]{"build", "document.pdf"});

        assertTrue(build.executed);
        assertArrayEquals(new String[]{"document.pdf"}, build.receivedArgs);
    }

    @Test
    void noArgumentsDispatchesHelpCommand() throws Exception {
        TestCommand help = new TestCommand("help");
        MerkleTreeCli cli = new MerkleTreeCli(List.of(help));

        cli.run(new String[0]);

        assertTrue(help.executed);
        assertArrayEquals(new String[0], help.receivedArgs);
    }

    @Test
    void shortHelpFlagDispatchesHelpCommand() throws Exception {
        TestCommand help = new TestCommand("help");
        new MerkleTreeCli(List.of(help)).run(new String[]{"-h"});
        assertTrue(help.executed);
    }

    @Test
    void longHelpFlagDispatchesHelpCommand() throws Exception {
        TestCommand help = new TestCommand("help");
        new MerkleTreeCli(List.of(help)).run(new String[]{"--help"});
        assertTrue(help.executed);
    }

    @Test
    void shortVersionFlagDispatchesVersionCommand() throws Exception {
        TestCommand version = new TestCommand("version");
        new MerkleTreeCli(List.of(version)).run(new String[]{"-v"});
        assertTrue(version.executed);
    }

    @Test
    void longVersionFlagDispatchesVersionCommand() throws Exception {
        TestCommand version = new TestCommand("version");
        new MerkleTreeCli(List.of(version)).run(new String[]{"--version"});
        assertTrue(version.executed);
    }

    @Test
    void throwsForUnknownCommand() {
        MerkleTreeCli cli = new MerkleTreeCli(List.of());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cli.run(new String[]{"unknown"})
        );

        assertEquals("Unknown command: unknown", exception.getMessage());
    }

    private static class TestCommand implements Command {
        private final String name;
        private boolean executed;
        private String[] receivedArgs;

        TestCommand(String name) {
            this.name = name;
        }

        @Override
        public void execute(String[] args) {
            executed = true;
            receivedArgs = args;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getUsage() {
            return name;
        }

        @Override
        public String getDescription() {
            return "Test command: " + name;
        }
    }
}
