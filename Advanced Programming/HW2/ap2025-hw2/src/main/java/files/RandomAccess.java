package files;

import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccess {
    /**
     * Treat the file as an array of (unsigned) 8-bit values and sort them
     * in-place using a bubble-sort algorithm.
     * You may not read the whole file into memory!
     *
     * @param file
     */
    public static void sortBytes(RandomAccessFile file) throws IOException {
        long fileLength = file.length();  // get the length of file
        if (fileLength < 2) return;  // if the file is in length 1 or less we have nothing to sort
        for (long i = 0; i < fileLength - 1; i++) { // bubble sort algoritem for our file
            for (long j = 0; j < fileLength - i - 1; j++) {
                file.seek(j);      //find byte at posetion j
                byte byte1 = file.readByte();  
                int firstNum = (byte1 & 255); //to unsigned
                byte byte2 = file.readByte(); // to unsigned
                int secondNum = (byte2 & 255);

                if (firstNum > secondNum) {  // as the bubble sort works, we compare them and swap if needed
                    file.seek(j);          
                    file.writeByte(byte2);   // put what was in j+1 to index j
                    file.writeByte(byte1);   // do the opposite
                }
            }
        }
    }

    /**
     * Treat the file as an array of unsigned 24-bit values (stored MSB first) and sort
     * them in-place using a bubble-sort algorithm.
     * You may not read the whole file into memory!
     *
     * @param file
     * @throws IOException
     */
    public static void sortTriBytes(RandomAccessFile file) throws IOException {
        long fileLength = file.length();  // Get the length of the file
        if (fileLength < 3) return;  // If the file is too small, there's nothing to sort
        long fileSize = fileLength / 3;  // Number of 3-byte (24-bit) integers
        for (long i = 0; i < fileSize - 1; i++) { //bubble sort , changing the values to int for comparisons
            for (long j = 0; j < fileSize - i - 1; j++) {
                file.seek(j * 3);  // got to the 3 byte valo
                byte[] byte1 = new byte[3]; //3 byte long array as size to compare
                file.readFully(byte1);  // Read 3 bytes into array
                byte[] byte2 = new byte[3];
                file.readFully(byte2);  // Read 3 bytes
                int firstNumCombined = ((byte1[0] & 255) << 16) | ((byte1[1] & 255) << 8) | (byte1[2] & 255);  //change the variable to unsigned and combine the 3 bytes
                int secondNumCombined = ((byte2[0] & 255) << 16) | ((byte2[1] & 255) << 8) | (byte2[2] & 255); //change the variable to unsigned and combine the 3 bytes
                if (firstNumCombined > secondNumCombined) {
                    file.seek(j * 3);
                    file.write(byte2);  // put what was in j+1 to index j (3 bytes)
                    file.write(byte1);  // do the opposite
                }
            }
        }

    }
}
