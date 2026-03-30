package dict;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.TreeMap;

/**
 * Implements a persistent dictionary that can be held entirely in memory.
 * When flushed, it writes the entire dictionary back to a file.
 * <p>
 * The file format has one keyword per line:
 * <pre>word:def</pre>
 * <p>
 * Note that an empty definition list is allowed (in which case the entry would have the form: <pre>word:</pre>
 *
 * @author talm
 */
public class InMemoryDictionary extends TreeMap<String, String> implements PersistentDictionary {
    private static final long serialVersionUID = 1L; // (because we're extending a serializable class)
    private final File dictFile;

    public InMemoryDictionary(File dictFile) {
        this.dictFile=dictFile;
   
    }

    @Override
    public void open() throws IOException {
        this.clear();
        if (!dictFile.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(dictFile))) {  // create buffered reader from file to read it's lines, may throw an exeption if "bad" file
            String line;
            while ((line = br.readLine()) != null) { //continue reading lines from file until got to the end
                String[] split = line.split(":", 2);   //split the line by ":" to two parts
                String key = split[0]; // The key is the first part before ":"
                String value;
                if (split.length==2){      // if there is a value, add it , else, an empty string
                    value = split[1];
                }
                else{
                    value = "";
                }
                this.put(key, value);       //add line to treemap with its key (before ":") and then value
            }
          }  catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(dictFile))) { // as for open but for writing lines
            for (String key : this.keySet()) {   // run on all the words(keys) we have 
                String value = this.get(key); // Retrieve the value for the key
                if (value !=null){   // check if there is a value for the word, as for open , a definition
                    bw.write(key + ":" + value); //if there is a definition, write in the format requested
                }
                else{
                    bw.write(key + ":"+"");  //if no definition, leave empty
                }
               
                bw.newLine();   //add new line for the next key
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
}
}
