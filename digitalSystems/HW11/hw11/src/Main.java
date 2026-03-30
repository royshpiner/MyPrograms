import java.io.File;
import java.util.ArrayList;

public class Main {

    /*
     * This function iterates through all the files in the provided directory
     * and filters out only the .jack files.
     *
     * @param directory The directory to search for .jack files
     * @return A list of .jack files found in the directory
     */
    public static ArrayList<File> getJackFiles(File directory){
        File[] files = directory.listFiles();  // List all files in the directory
        ArrayList<File> result = new ArrayList<File>();  // List to store .jack files
        if (files == null) return result;  // Return empty list if the directory is empty or cannot be accessed
        for (File f : files){
            // Check if the file has a .jack extension and add it to the result list
            if (f.getName().endsWith(".jack")){
                result.add(f);
            }
        }
        return result;  // Return the list of .jack files
    }

    /*
     * The main method which serves as the entry point to the program.
     * It handles both individual .jack files and directories containing multiple .jack files.
     * Each .jack file is processed to produce a corresponding .vm file.
     *
     * @param args Command line arguments: the first argument is either a .jack file or a directory
     */
    public static void main(String[] args) {
        // The input file or directory is specified as the first argument
        String fileInName = args[0];
        File input = new File(fileInName);  // Create a File object for the input
        String fileOutPath = "";  // To store the output file path
        File out;  // Output file object
        ArrayList<File> jackFiles = new ArrayList<File>();  // List to store .jack files

        // If the input is a single .jack file, add it to the list of files
        if (input.isFile()) {
            jackFiles.add(input);
        } 
        // If the input is a directory, get all the .jack files from the directory
        else if (input.isDirectory()) {
            jackFiles = getJackFiles(input);
        }

        // For each .jack file, generate a corresponding .vm file
        for (File f : jackFiles) {
            // Create the output file path by replacing the .jack extension with .vm
            fileOutPath = f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(".")) + ".vm";
            out = new File(fileOutPath);  // Create a new File object for the output file

            // Create a CompilationEngine to compile the Jack file into VM code
            CompilationEngine compilationEngine = new CompilationEngine(f, out);
            compilationEngine.compileClass();  // Start the compilation process for the current class
        }
    }
}