import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            File source = new File(args[0]);
            File output;
            File[] filesArray;
            
            CodeWriter writer = null;
            Parser parser = null;
            
            if(source.isDirectory()){ 
                filesArray = source.listFiles();
                output = new File(args[0] + "/" + source.getName() + ".asm");
                writer = new CodeWriter(output);
                // Check if Sys.vm is present in the directory before writing bootstrap code
                boolean sysVmExists = false;
                for (File f : filesArray) {
                    if (f.getName().equals("Sys.vm")) {
                        sysVmExists = true;
                        break;
                    }
                }
                if (sysVmExists) {
                    writer.writeBootstrapCode();
                }
            }
            else {
                filesArray = new File[1];
                filesArray[0] = source;
                output = new File(args[0].substring(0, args[0].length() - 3) + ".asm"); // Corrected the substring index for ".vm"
                writer = new CodeWriter(output);
            }

            for(File f : filesArray){
                if(!f.getName().endsWith(".vm")) continue;
                parser = new Parser(f);
                writer.fileName = f.getName().substring(0,f.getName().length()-3); // Corrected the substring index for ".vm"
                while (parser.hasMoreLines()) {
                    parser.advance();
                    switch (parser.commandType()){
                        case "arithmetic":
                            writer.writeArithmetic(parser.arg1());
                            break;
                        case "pop":
                        case "push":
                            String segment = parser.arg1();
                            switch (segment) {
                                case "local":
                                    segment = "LCL";
                                    break;
                                case "argument":
                                    segment = "ARG";
                                    break;
                                case "this":
                                    segment = "THIS";
                                    break;
                                case "that":
                                    segment = "THAT";
                                    break; }
                            writer.WritePushPop(parser.commandType(), segment, parser.arg2());
                            break;
                        case "label":
                            writer.writeLabel(parser.arg1());
                            break;
                        case "goto":
                            writer.writeGoto(parser.arg1());
                            break;
                        case "if-goto":
                            writer.writeIf(parser.arg1());
                            break;
                        case "function":
                            writer.writeFunction(parser.arg1(), parser.arg2());
                            break;
                        case "call":
                            writer.writeCall(parser.arg1(), parser.arg2());
                            break;
                        case "return":
                            writer.writeReturn();
                            break;
                        case "comment":
                            break;
                    }
                }
            }

            writer.infiniteLoop();
            writer.close();
            if (parser != null) {
                parser.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
