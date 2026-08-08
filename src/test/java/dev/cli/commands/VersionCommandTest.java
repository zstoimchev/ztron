package dev.cli.commands;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class VersionCommandTest {
    private PrintStream originalOut;
    private ByteArrayOutputStream output;
    private final String VERSION = "0.0.0";

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
    void printsVersion() {
        VersionCommand command = new VersionCommand(VERSION);
        command.execute(new String[0]);
        String result = output.toString();
        assertTrue(result.contains("zTron"));
        assertTrue(result.contains("0.0.1"));
    }

    @Test
    void printsJavaVersion() {
        VersionCommand command = new VersionCommand(VERSION);
        command.execute(new String[0]);
        String result = output.toString();
        assertTrue(result.contains("Java"));
        assertTrue(result.contains(System.getProperty("java.version")));
    }
}
