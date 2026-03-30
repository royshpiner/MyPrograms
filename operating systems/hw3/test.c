#include "fs.h"
#include <stdio.h>
#include <string.h>

/**
 * @brief Main function demonstrating basic filesystem operations
 * 
 * @return 0 on successful execution
 */
int main() {
    int result;
    
    // Step 1: Format a new filesystem
    // This creates a fresh 10MB disk image file called "disk.img"
    printf("Formatting filesystem...\n");
    result = fs_format("disk.img");
    if (result != 0) {
        fprintf(stderr, "Error formatting filesystem (code: %d)\n", result);
        return 1;
    }

    // Step 1.5: Format the filesystem again
    // This creates a fresh 10MB disk image file called "disk.img"
    printf("Formatting filesystem...\n");
    result = fs_format("disk.img");
    if (result != 0) {
        fprintf(stderr, "Error formatting filesystem (code: %d)\n", result);
        return 1;
    }
    printf("Successfully formatted filesystem again\n");
    printf("--------------------------------\n");
    printf("\n");




    // Step 2: Mount the filesystem
    // This opens the disk image and prepares it for operations
    printf("Mounting filesystem...\n");
    result = fs_mount("disk.img");
    if (result != 0) {
        fprintf(stderr, "Error mounting filesystem (code: %d)\n", result);
        return 1;
    }
    printf("Successfully formatted and mounted filesystem\n");
    printf("--------------------------------\n");
    printf("\n");



    // Step 2.1: Try to delete a file that doesn't exist
    printf("Trying to delete a file that doesn't exist...\n");
    result = fs_delete("file1.txt");
    if (result == -1) {
        printf("Success!! File doesn't exist\n");
    }
    printf("--------------------------------\n");
    printf("\n");



    // Step 2.2: Try to mount the filesystem again  
    printf("Trying to mount filesystem again...\n");
    result = fs_mount("disk.img");
    if (result != 0) {
        fprintf(stderr, "Error mounting filesystem (code: %d)\n", result);
        return 1;
    }
    printf("Successfully mounted filesystem again\n");
    // Step 2.3: Create new files
    // This allocates an inode for a file named "file1.txt"
    printf("Creating file1.txt...\n");
    result = fs_create("file1.txt");
    if (result != 0) {
        fprintf(stderr, "Error creating file (code: %d)\n", result);
        fs_unmount();
        return 1;
    }
    printf("Successfully created file1.txt\n");
    printf("--------------------------------\n");
    printf("\n");



    printf("Creating file2.txt...\n");
    result = fs_create("file2.txt");
    if (result != 0) {
        fprintf(stderr, "Error creating file (code: %d)\n", result);
        fs_unmount();
        return 1;
    }
    printf("Successfully created file2.txt\n");
    printf("--------------------------------\n");
    printf("\n");



    printf("Creating file2.txt with same name...\n");
    result = fs_create("file2.txt");
    if (result == -1) {
        printf("Success!! File already exists\n");
    }
    printf("--------------------------------\n");
    printf("\n");



    // Step 4: Write data to the file
    // This allocates blocks and writes the string data to the file
    printf("Writing to file...\n");
    char data[] = "Hello, filesystem!";  // Note that sizeof(data) includes the null terminator
    result = fs_write("file1.txt", data, sizeof(data));
    if (result != 0) {
        fprintf(stderr, "Error writing to file (code: %d)\n", result);
        fs_unmount();
        return 1;
    }
    printf("Successfully wrote to file1.txt\n");
    printf("--------------------------------\n");
    printf("\n");



    printf("Writing again to file1...\n");
    char data2[] = "Hello, This message should overwrite the previous one";
    result = fs_write("file1.txt", data2, sizeof(data2));
    if (result != 0) {
        fprintf(stderr, "Error writing to file (code: %d)\n", result);
        fs_unmount();
        return 1;
    }
    printf("Successfully wrote again to file1.txt\n");
    printf("--------------------------------\n");
    printf("\n");




    printf("Reading from file1...\n");
    char buffer[100];  // Buffer to hold the file content
    memset(buffer, 0, sizeof(buffer));  // Initialize buffer to zeros
    result = fs_read("file1.txt", buffer, sizeof(buffer));
    if (result < 0) {
        fprintf(stderr, "Error reading from file (code: %d)\n", result);
        fs_unmount();
        return 1;
    }
    printf("Read: %s\n", buffer);
    if (strcmp(buffer, data2) != 0) {
        fprintf(stderr, "Error: Read data does not match written data\n");
        fs_unmount();
        return 1;
    }
    printf("Successfully overwrote the previous data in file1.txt\n");
    printf("--------------------------------\n");
    printf("\n");

    // Test for long file names
    printf("Creating file with long name...\n");
    char long_name[MAX_FILENAME + 1];
    memset(long_name, 'a', MAX_FILENAME + 1);
    printf("Long name: %s\n", long_name); 
    result = fs_create(long_name);
    if (result == -3) {
        printf("Success!! File with long name wasn't created\n");
    } else {
        fprintf(stderr, "Error: File with long name was created\n");
        fs_unmount();
        return 1;
    }
    printf("--------------------------------\n");
    printf("\n");



    // List all files
    printf("Creating 10 files and listing them...\n");
    printf("Listing all files...\n");
    // Expected list
    char expected_filenames[100][MAX_FILENAME] = {0};
    for (int i = 0; i < 10; i++) {
        sprintf(expected_filenames[i], "file%d.txt", i + 1);
    }

    // Actual list
    char filenames[100][MAX_FILENAME];
    fs_create("file1.txt");
    fs_create("file2.txt");
    fs_create("file3.txt");
    fs_create("file4.txt");
    fs_create("file5.txt");
    fs_create("file6.txt");
    fs_create("file7.txt");
    fs_create("file8.txt");
    fs_create("file9.txt");
    fs_create("file10.txt");
    result = fs_list(filenames, 10);
    if (result < 0) {
        fprintf(stderr, "Error listing files (code: %d)\n", result);
        fs_unmount();
        return 1;
    }
    char curr_filename[MAX_FILENAME];
    for (int i = 0; i < result; i++) {
        if (strcmp(filenames[i], expected_filenames[i]) == 0) {
        } else {
            printf("Error!! File %s is not in the list\n", filenames[i]);
            fs_unmount();
            return 1;
        }
    }
    printf("There are %d files in the list\n", result);
    printf("Success!! File1.txt and file2.txt are in the list\n");
    printf("--------------------------------\n");
    printf("\n");


    // First clean the filesystem
    printf("Cleaning filesystem...\n");
    for (int i = 0; i < 10; i++) {
        printf("Deleting file %s\n", filenames[i]);
        if (fs_delete(filenames[i]) != 0) {
            fprintf(stderr, "Error deleting file %s (code: %d)\n", filenames[i], result);
            fs_unmount();
            return 1;
        }
    }
    // Check if the filesystem is empty
    char empty_filenames[100][MAX_FILENAME];
    result = fs_list(filenames, 100);
    if (result != 0) {
        fprintf(stderr, "Error! Filesystem should be empty(Files in system: %d)\n", result);
        fs_unmount();
        return 1;
    }
    printf("Success!! Filesystem has been cleaned\n");
    printf("--------------------------------\n");
    printf("\n");



    // Listing an empty filesystem
    printf("Listing an empty filesystem...\n");
    result = fs_list(filenames, 100);
    if (result == 0) {
        printf("Success!! No files in the list\n");
    } else {
        fprintf(stderr, "Error listing files (code: %d)\n", result);
        fs_unmount();
        return 1;
    }
    printf("--------------------------------\n");
    printf("\n");



    // Filling filesystem to capacity
    printf("Filling filesystem to capacity...\n");
    char big_filenames[256][MAX_FILENAME];
    char test_remount[256][MAX_FILENAME];
    for (int i = 0; i < 256; i++) {
        char filename[MAX_FILENAME];
        sprintf(filename, "file%d.txt", i + 1);
        strcpy(big_filenames[i], filename);
        strcpy(test_remount[i], filename);
        fs_create(filename);
        fs_write(filename, "Hello, filesystem!", 1000);
    }

    printf("Success!! Filesystem has created 256 files, to full capacity\n");
    printf("--------------------------------\n");
    printf("\n");



    // Try to create another file, should fail
    printf("Trying to create another file, should fail...\n");
    result = fs_create("file257.txt");
    if (result == -2) {
        printf("Success!! File 257 wasn't created due to out of space\n");
    }
    printf("--------------------------------\n");
    printf("\n");



    // Checking persistence after remount
    printf("Checking persistence after remount...\n");

    char test_remount_from_disk[256][MAX_FILENAME];
    if (fs_list(test_remount_from_disk, 256) != 256) {
        fprintf(stderr, "Error listing files - not matching number of files expected (256) (code: %d)\n", result);
        return 1;
    }

    // for (int i = 0; i < 256; i++) {
    //     printf("test_remount_from_disk[%d]: %s\n", i, test_remount_from_disk[i]);
    // }
    
    // Unmount the filesystem
    printf("Unmounting filesystem...\n");
    fs_unmount();

    // Remount the filesystem again
    printf("Mounting filesystem again...\n");
    result = fs_mount("disk.img");
    if (result != 0) {
        fprintf(stderr, "Error mounting filesystem (code: %d)\n", result);
        return 1;
    }
    // Listing all files again
    test_remount_from_disk[256][MAX_FILENAME];
    result = fs_list(test_remount_from_disk, 256);
    // for (int i = 0; i < 256; i++) {
    //     if (strcmp(test_remount_from_disk[i], "") != 0) {
    //         printf("test_remount_from_disk[%d]: %s\n", i, test_remount_from_disk[i]);
    //     }
    // }
    if (result != 256) {
        fprintf(stderr, "Listing data failed after remount (code: %d)\n", result);
        return 1;
    }
    // Checking if the files are still there
    for (int i = 0; i < 256; i++) {
        if (strcmp(test_remount_from_disk[i], test_remount[i]) != 0) {
            printf("test_remount_from_disk[%d]: %s\n", i, test_remount_from_disk[i]);
            printf("test_remount[%d]: %s\n", i, test_remount[i]);
            fprintf(stderr, "Persistence failed after remount (code: %d)\n", result);
            return 1;
        }
    }
    printf("Success!! Files are still there after remount\n");
    printf("--------------------------------\n");
    printf("\n");


    
    // Remove all files
    printf("Removing all files...\n");
    for (int i = 0; i < 256; i++) {
        if (fs_delete(big_filenames[i]) != 0) {
            fprintf(stderr, "Error deleting file %s (code: %d)\n", big_filenames[i], result);
            fs_unmount();
            return 1;
        }
    }
    // Try to delete a file that doesn't exist
    printf("Trying to delete a file that doesn'tx exist...\n");
    result = fs_delete("file257.txt");
    if (result == -1) {
        printf("Success!! File doesn't exist\n");
    }

    // Make sure no files
    result = fs_list(filenames, 100);
    if (result == 0) {
        printf("Success!! No files in the list\n");
    } else {
        fprintf(stderr, "Error: Files are in the list\n");
        fs_unmount();
        return 1;
    }
    printf("All files have been removed\n");
    printf("--------------------------------\n");
    printf("\n");


    // Try to read from a deleted file
    printf("Trying to read from a deleted file...\n");
    result = fs_read("file1.txt", buffer, sizeof(buffer));
    if (result == -1) {
        printf("Success!! File is deleted\n");
    }
    printf("--------------------------------\n");
    printf("\n");


    // Trying to write more than 12 * 4096 bytes to a file
    printf("Trying to write more than 12 * 4096 bytes to a file...\n");
    printf("Creating file1.txt...\n");
    result = fs_create("file1.txt");
    if (result != 0) {
        fprintf(stderr, "Error creating file (code: %d)\n", result);
        fs_unmount();
        return 1;
    }
    char big_data[MAX_DIRECT_BLOCKS * BLOCK_SIZE + 1];
    memset(big_data, 'a', MAX_DIRECT_BLOCKS * BLOCK_SIZE + 1);
    result = fs_write("file1.txt", big_data, MAX_DIRECT_BLOCKS * BLOCK_SIZE + 1);
    printf("Result: %d\n", result);
    if (result == -2) {
        printf("Success!! Size is too big\n");
    } else {
        fprintf(stderr, "Error: Too big of a file\n");
        fs_unmount();
        return 1;
    }
    printf("--------------------------------\n");
    printf("\n");


    // Writing twice to a file, overflowing in the second time
    printf("Writing twice to a file, overflowing in the second time...\n");
    char write1[MAX_DIRECT_BLOCKS * BLOCK_SIZE];
    memset(write1, 'a', MAX_DIRECT_BLOCKS * BLOCK_SIZE);
    result = fs_write("file1.txt", write1, MAX_DIRECT_BLOCKS * BLOCK_SIZE);
    if (result != 0) {
        fprintf(stderr, "Error writing to file (code: %d)\n", result);
        fs_unmount();
        return 1;
    } else {
        printf("Max size was written to file1.txt\n");
    }
    printf("Writing again to file1.txt with too much data\n");
    char write2[MAX_DIRECT_BLOCKS * BLOCK_SIZE + 1];
    memset(write2, 'b', MAX_DIRECT_BLOCKS * BLOCK_SIZE + 1);
    result = fs_write("file1.txt", write2, MAX_DIRECT_BLOCKS * BLOCK_SIZE + 1);
    if (result == -2) {
        printf("Success!! File is full\n");
    } else {
        fprintf(stderr, "Error: File is not full\n");
        fs_unmount();
        return 1;
    }
    printf("--------------------------------\n");
    printf("\n");


    // Deleting files and reusing the same inode
    printf("Deleting files and reusing the same inode...\n");
    printf("Creating files and deleting them...\n");
    fs_create("file1.txt");
    fs_create("file2.txt");
    fs_create("file3.txt");
    fs_delete("file1.txt");
    result = fs_create("file1.txt");
    fs_delete("file2.txt");
    fs_delete("file3.txt");
    if (result != 0) {
        fprintf(stderr, "Error deleting file (code: %d)\n", result);
        fs_unmount();
        return 1;
    }
    printf("Success!! File1.txt was created again\n");
    printf("--------------------------------\n");
    printf("\n");



    // Unmount the filesystem
    printf("Unmounting filesystem...\n");
    fs_unmount();
    // Make sure the filesystem is unmounted
    result = fs_create("file257.txt");
    if (result == 0) {
        printf("ERROR! Filesystem is not unmounted\n");
        printf("A file was created after unmounting\n");
        fs_unmount();
        return 1;
    }
    if (result == -3) {
        printf("Success!! Filesystem is unmounted\n");
    }
    printf("--------------------------------\n");
    printf("\n");



    // Test completed successfully
    printf("Test completed successfully.\n");
    return 0;
}
