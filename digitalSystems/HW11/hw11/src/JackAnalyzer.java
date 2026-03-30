import java.io.File;
import java.util.ArrayList;

public class JackAnalyzer {

    public static void main(String[] args) {
        // The input is a path to a .jack file or a directory containing .jack files
        File jackFileDir = new File(args[0]);
        ArrayList<File> files = new ArrayList<>();

        // If the input is a single .jack file, add it to the list of files
        if (jackFileDir.isFile() && args[0].endsWith(".jack")) {
            files.add(jackFileDir);

        // If the input is a directory, retrieve all .jack files from the directory
        } else if (jackFileDir.isDirectory()) {
            files = getJackFiles(jackFileDir);
        }

        // For each .jack file, compile it into a .vm file
        for (File file : files) {
            // Determine the output file name by changing the extension from .jack to .vm
            String fileOutName = file.toString().substring(0, file.toString().length() - 5) + ".vm";
            File fileOutFile = new File(fileOutName);

            // Create a CompilationEngine instance to compile the Jack file
            CompilationEngine compilationEngine = new CompilationEngine(file, fileOutFile);
            compilationEngine.compileClass();  // Start compiling the class
        }
    }

    /*
     * Retrieves all .jack files in a given directory.
     * 
     * @param jackFileDir the directory to search for .jack files
     * @return a list of .jack files found in the directory
     */
    public static ArrayList<File> getJackFiles(File jackFileDir) {
        File[] files = jackFileDir.listFiles();  // Get all files in the directory
        ArrayList<File> fResults = new ArrayList<>();  // List to store .jack files

        if (files != null) {
            // Iterate through the files and add those with a .jack extension to the list
            for (File file : files) {
                if (file.getName().endsWith(".jack")) {
                    fResults.add(file);
                }
            }
        }
        return fResults;
    }
}