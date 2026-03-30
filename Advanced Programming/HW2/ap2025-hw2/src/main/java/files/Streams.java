package files;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Streams {
    /**
     * Read from an InputStream until a quote character (") is found, then read
     * until another quote character is found and return the bytes in between the two quotes.
     * If no quote character was found return null, if only one, return the bytes from the quote to the end of the stream.
     *
     * @param in
     * @return A list containing the bytes between the first occurrence of a quote character and the second.
     */
    public static List<Byte> getQuoted(InputStream in) throws IOException {
        List<Byte> result = new ArrayList<>();  //crate a list of bytes for computing the result
        boolean inQuotes = false;    // for future checking if we are currently between quotes
        int currentByte;  // returns a int number we would check ascii
        while ((currentByte = in.read()) != -1) { //read all charecters until the end of the input
            if (currentByte == '"') {    //check if char is a quote
                if (inQuotes) {   // if found second quote
                    return result;
                } else {
                    inQuotes = true;  //found the first quote
                }
            } else if (inQuotes) { //if not a quote add to line
                result.add((byte) currentByte);     // Add byte to result if inside quotes

            }
        }
        if (inQuotes){  //if finished without finding second quotes
            return result;
        }    
            return null;
    }


    /**
     * Read from the input until a specific string is read, return the string read up to (not including) the endMark.
     *
     * @param in      the Reader to read from
     * @param endMark the string indicating to stop reading.
     * @return The string read up to (not including) the endMark (if the endMark is not found, return up to the end of the stream).
     */
    public static String readUntil(Reader in, String endMark) throws IOException {
        BufferedReader br = new BufferedReader(in); // make it a buffuered reader so it will read a string
        StringBuilder result = new StringBuilder();   // string builder for managing the result 
        String currentLine;
        
        while ((currentLine = br.readLine()) != null) {  // read lines until the end of the file
            if (currentLine.contains(endMark)) { // If the endMark is found, stop reading and return up to that point
                result.append(currentLine, 0, currentLine.indexOf(endMark));
                break;
            }
            result.append(currentLine).append("\n"); //add current line and new line for the next one
        }
                return result.toString();
                
    }

    /**
     * Copy bytes from input to output, ignoring all occurrences of badByte.
     *
     * @param in
     * @param out
     * @param badByte
     */
    public static void filterOut(InputStream in, OutputStream out, byte badByte) throws IOException {
        int currentByte;
        int unSighnedBadByte;
        while ((currentByte = in.read()) != -1) {
            unSighnedBadByte = badByte & 255; //  255=11111111 so to change from rang -127,128 to 0,255
            if (currentByte != unSighnedBadByte) { //if not the bad byte write do file, else continue.
                out.write(currentByte);
            }
        }
        out.close();// close file
    }


    

    /**
     * Read a 40-bit (unsigned) integer from the stream and return it. The number is represented as five bytes,
     * with the most-significant byte first.
     * If the stream ends before 5 bytes are read, return -1.
     *
     * @param in
     * @return the number read from the stream
     */
    public static long readNumber(InputStream in) throws IOException {
        byte[] bytes = new byte[5]; // for reading 5 bytes
        int bytesRead = in.read(bytes);
        if (bytesRead != 5) { // if we red a different amount of bytes then 5 (it ends without 5 red) return -1
            return -1;
        }
        long result = 0; // long because we need a 40 bit length and int is 32
        int unSighnedByte; 
        for (int i = 0; i < 5; i++) { // shif to move to position , byte=8 bits
            unSighnedByte = (bytes[i] & 255);
            result = (result << 8) | unSighnedByte;  // so signed will be handled
        }
        return result;
    }

}
