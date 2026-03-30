import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class Parser {
    private HashMap<Integer, String> hm;
    private int currentIndex;

    public Parser(File file){
        this.hm = new HashMap<Integer , String>() ;
        String line;
        int lineNumber=0;
        this.currentIndex=1;
        try(BufferedReader br = new BufferedReader(new FileReader(file))){

            while ((line = br.readLine())!= null){
                if (line != null) {
                    line = line.trim();
                }
                if (!line.isBlank() && !line.startsWith("//")){
                    hm.put(++lineNumber, line);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();

        }
    }

    public boolean hasMoreLines(){
      return this.currentIndex -1 < hm.size();
    }
    public String advance(){
        if (hasMoreLines()){
            this.currentIndex++;
            return hm.get(currentIndex);
        }
        else return null;
    }
    public String instructionType(){
        String currentInstruction = hm.get(currentIndex);
        if (currentInstruction.startsWith("@") && currentInstruction.length() > 1){
            return "A";
        }
        if(currentInstruction.startsWith("(") && currentInstruction.endsWith(")")){
            return "L";
        }
        return "C";
    }
    public String symbol(){
        String currentInstruction = hm.get(currentIndex);
        String result="";
        switch (instructionType()) {
            case "L":
                result = currentInstruction.substring(1,currentInstruction.length()-1);
                break;
            case "A":
                result = currentInstruction.substring(1);
                break;
            default:
                break;
        }
        return result;
    }

    public String dest(){
        String currentInstruction = hm.get(currentIndex);
        String result = null;
        if (instructionType()== "C"){
            int eqindex = currentInstruction.indexOf("=");
            if (eqindex !=-1){
               result = currentInstruction.substring(0, eqindex);
               return result;
            }
            }

        return result;
    }

    public String comp(){
        String currentInstruction = hm.get(currentIndex);
        String result = "";
        if(instructionType() == "C"){
            if(!currentInstruction.contains(";")){
                result = currentInstruction.substring(currentInstruction.indexOf("=")+1);
            } else {
                result = currentInstruction.substring(currentInstruction.indexOf("=")+1, currentInstruction.indexOf(";"));
            }
        }
        return result;
    }
    public String jump(){
        String currentInstruction = hm.get(currentIndex);
        String result = null;
        if (instructionType()== "C"){
            if (currentInstruction.contains(";")){
                result = currentInstruction.substring(currentInstruction.indexOf(";")+1);
            }
        }
        return result;
    }

}
