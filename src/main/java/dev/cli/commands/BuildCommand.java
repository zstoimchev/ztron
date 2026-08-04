package dev.cli.commands;

import dev.core.models.Hash;
import dev.core.services.MerkleTreeService;

import java.nio.file.Files;
import java.nio.file.Path;

public class BuildCommand implements Command {
    @Override
    public void execute(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Error: File path required");
            System.err.println("Usage: " + getUsage());
            System.exit(1);
        }

        // Parse arguments
        CommandLineArgs parsedArgs = parseArguments(args);

        Path filePath = Path.of(parsedArgs.file);

        // Validate file exists
        if (!Files.exists(filePath)) {
            System.err.println("Error: File not found: " + filePath);
            System.exit(1);
        }

        // Build configuration
        MerkleTreeConfig config = MerkleTreeConfig.builder()
                .chunkSize(parsedArgs.chunkSize)
                .hashAlgorithm(parsedArgs.algorithm)
                .build();

        // Create service and build tree
        ServiceFactory factory = new ServiceFactory(config);
        MerkleTreeService service = factory.createMerkleTreeService();

        Hash rootHash = service.buildTreeHash(filePath);

        // Output result
        if (parsedArgs.verbose) {
            System.out.println("File: " + filePath);
            System.out.println("Chunk size: " + parsedArgs.chunkSize + " bytes");
            System.out.println("Algorithm: " + parsedArgs.algorithm);
            System.out.println("Merkle root: " + formatter.format(rootHash));
        } else {
            System.out.println(formatter.format(rootHash));
        }
    }

    private CommandLineArgs parseArguments(String[] args) {
        CommandLineArgs result = new CommandLineArgs();
        result.file = args[0];  // First argument is always the file

        // Parse optional flags
        for (int i = 1; i < args.length; i++) {
            String arg = args[i];

            if (arg.equals("--chunk-size") || arg.equals("-c")) {
                if (i + 1 >= args.length) {
                    throw new IllegalArgumentException("--chunk-size requires a value");
                }
                result.chunkSize = Integer.parseInt(args[++i]);
            } else if (arg.equals("--algorithm") || arg.equals("-a")) {
                if (i + 1 >= args.length) {
                    throw new IllegalArgumentException("--algorithm requires a value");
                }
                result.algorithm = MerkleTreeConfig.HashAlgorithm.valueOf(args[++i].toUpperCase());
            } else if (arg.equals("--verbose") || arg.equals("-v")) {
                result.verbose = true;
            } else {
                throw new IllegalArgumentException("Unknown option: " + arg);
            }
        }

        return result;
    }

    @Override
    public String getName() {
        return "build";
    }

    @Override
    public String getUsage() {
        return "build <file> [--chunk-size <size>] [--algorithm <algo>] [--verbose]";
    }

    @Override
    public String getDescription() {
        return "Build a Merkle tree from a file and output the root hash";
    }

    /**
     * Internal class to hold parsed command-line arguments.
     */
    private static class CommandLineArgs {
        String file;
        int chunkSize = 4096;  // Default
        MerkleTreeConfig.HashAlgorithm algorithm = MerkleTreeConfig.HashAlgorithm.SHA256;  // Default
        boolean verbose = false;
    }
}