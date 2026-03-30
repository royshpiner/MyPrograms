import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;

public class VMWriter {
    // PrintWriter for writing output to the .vm file
    private PrintWriter pw;

    
    // Enumeration for the segments in the VM (e.g., constant, argument, local, etc.)
    public static enum SEGMENT {CONST, ARG, LOCAL, STATIC, THIS, THAT, POINTER, TEMP, NONE};
    
    // Enumeration for the arithmetic/logical commands in VM (e.g., add, sub, eq, etc.)
    public static enum COMMAND {ADD, SUB, NEG, EQ, GT, LT, AND, OR, NOT};
    
    // HashMaps to map each segment and command to its corresponding string representation
    private static HashMap<SEGMENT, String> segmentStringHashMap = new HashMap<SEGMENT, String>();
    private static HashMap<COMMAND, String> commandStringHashMap = new HashMap<COMMAND, String>();
    
    // Static block to initialize the segment and command mappings
    static {
        // Mapping each SEGMENT enum to its corresponding string representation
        segmentStringHashMap.put(SEGMENT.CONST, "constant");
        segmentStringHashMap.put(SEGMENT.ARG, "argument");
        segmentStringHashMap.put(SEGMENT.LOCAL, "local");
        segmentStringHashMap.put(SEGMENT.STATIC, "static");
        segmentStringHashMap.put(SEGMENT.THIS, "this");
        segmentStringHashMap.put(SEGMENT.THAT, "that");
        segmentStringHashMap.put(SEGMENT.POINTER, "pointer");
        segmentStringHashMap.put(SEGMENT.TEMP, "temp");





        // Mapping each COMMAND enum to its corresponding string representation
        commandStringHashMap.put(COMMAND.ADD, "add");
        commandStringHashMap.put(COMMAND.SUB, "sub");
        commandStringHashMap.put(COMMAND.NEG, "neg");
        commandStringHashMap.put(COMMAND.EQ, "eq");
        commandStringHashMap.put(COMMAND.GT, "gt");
        commandStringHashMap.put(COMMAND.LT, "lt");
        commandStringHashMap.put(COMMAND.AND, "and");
        commandStringHashMap.put(COMMAND.OR, "or");
        commandStringHashMap.put(COMMAND.NOT, "not");
    }

    public VMWriter(File Out) {
        try {
            pw = new PrintWriter(Out);  // Open the output file for writing
        } catch (FileNotFoundException e) {
            e.printStackTrace();  // Print error if the file cannot be opened
        }
    }

    public void writePush(SEGMENT seg, int index) {
        writeCommand("push", segmentStringHashMap.get(seg), String.valueOf(index));
    }

    public void writePop(SEGMENT seg, int index) {
        writeCommand("pop", segmentStringHashMap.get(seg), String.valueOf(index));
    }

    public void writeArithmetic(COMMAND commands) {
        writeCommand(commandStringHashMap.get(commands), "", "");
    }

    public void writeLabel(String label) {
        writeCommand("label", label, "");
    }

    public void writeGoto(String label) {
        writeCommand("goto", label, "");
    }

    public void writeIf(String label) {
        writeCommand("if-goto", label, "");
    }

    public void writeCall(String name, int args) {
        writeCommand("call", name, String.valueOf(args));
    }

    public void writeFunction(String name, int local) {
        writeCommand("function", name, String.valueOf(local));
    }

    public void writeReturn() {
        writeCommand("return", "", "");
    }

    public void writeCommand(String str, String arg_1, String arg_2) {
        pw.print(str + " " + arg_1 + " " + arg_2 + "\n");  // Write the command to the output file
    }

    public void close() {
        pw.close();  // Close the PrintWriter, finalizing the output file
    }
}