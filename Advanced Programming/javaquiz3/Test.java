import java.util.*;
import java.io.*;

class Test {
    public static <T> T identity(T t1) {
        return t1;
      }
      static List<Integer> sort(List<Integer> input) {
        List<Integer> output = new ArrayList<>();
        Set<Integer> set = new TreeSet<>();

        for (Integer element : input) {
          set.add(element);
        }
    
        for (Integer element : set) {
          output.add(element);
        }
        
        return output;
      }


    static List<Byte> readGather(RandomAccessFile file, List<Long> indices) throws IOException  {
    List<Byte> output = new ArrayList<>();

    for (Long index : indices) {
        file.seek(index); // Move the file pointer to the specified index
        output.add(file.readByte()); // Read the byte at the index and add it to the output
    }
    return output;
  }
    public static void main(String[] args) {
      String a1 = "test1";
      Integer b1 = 7;
  
      String a2 = identity(a1);
      Integer b2 = identity(b1);
    }
  }