import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    private static SymbolTable symbolTable;
    private static Code coder;

    public static void main(String[] args) {
        symbolTable = new SymbolTable();
        coder = new Code();
        if (args.length != 1) {
            System.err.println("Usage: java Main <file.asm or directory path>");
            System.exit(1);
        }

        File inputFile = new File(args[0]);
        try {
            if (inputFile.isDirectory()) {
                // Process all .asm files in the directory
                File[] files = inputFile.listFiles((dir, name) -> name.endsWith(".asm"));
                if (files != null) {
                    for (File file : files) {
                        processAssemblyFile(file);
                    }
                }
            } else if (inputFile.isFile() && inputFile.getName().endsWith(".asm")) {
                // Process a single assembly file
                processAssemblyFile(inputFile);
            } else {
                System.err.println("Error: Invalid file or directory");
                System.exit(1);
            }
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            System.exit(1);
        }
    }

    public static String toBinary16Bit(int address) {
        String binary = Integer.toBinaryString(address);
        String paddedBinary = String.format("%16s", binary).replace(' ', '0');
        return paddedBinary;
    }

    private static boolean isNumeric(String str) {
    try {
        Integer.parseInt(str);
        return true;
    } catch (NumberFormatException e) {
        return false;
    }
}


private static void processAssemblyFile(File asmFile) throws IOException {
        String hackFileName = asmFile.getPath().replaceAll("\\.asm$", ".hack");
        Parser parser1 = new Parser(asmFile);
        String symbol;
        int lineNumber=0;
        //first itteration for symbol addresses
        while (parser1.hasMoreLines()) {
            if (parser1.instructionType() != "L"){
                lineNumber++;
            }
            if (parser1.instructionType() == "L") {
                symbol = parser1.symbol();
                symbolTable.addEntry(symbol, lineNumber);
            }
            parser1.advance();
        }
            

            //make new file to write too
            BufferedWriter writer = new BufferedWriter(new FileWriter(hackFileName));

        //second itteration
        Parser parser2 = new Parser(asmFile);
        String binaryCode;
        while (parser2.hasMoreLines()) {
            String cmdType = parser2.instructionType();
            binaryCode = "";
            switch (cmdType) {
                case "A":
                    symbol = parser2.symbol();
                    int address;
                    if (isNumeric(symbol)) {
                        address = Integer.parseInt(symbol); // Convert the numeric symbol to an address
                    } else {
                        address = symbolTable.getAddress(symbol); // Get the address from the symbol table
                    }
                    binaryCode = toBinary16Bit(address);
                    break;

                case "C":
                    String destStr = parser2.dest();
                    String compStr = parser2.comp();
                    String jumpStr = parser2.jump();
                    String destBits = coder.dest(destStr);
                    String compBits = coder.comp(compStr);
                    String jumpBits = coder.jump(jumpStr);
                    binaryCode = "111" + compBits + destBits + jumpBits;
                    break;

                case "L":
                    break;

            }
            if (!binaryCode.trim().isEmpty()) {
                writer.write(binaryCode);
                if (parser2.hasMoreLines()) {
                    writer.newLine();
                }
            }
            parser2.advance();
        }
        
        writer.close();
    }
}
