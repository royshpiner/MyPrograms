#include "fs.h"
int fd = -1;
unsigned char bitmap[MAX_BLOCKS / 8];
superblock sb;
inode inode_table[MAX_FILES];
#define min(a, b) ((a) < (b) ? (a) : (b))



// Find an inode by filename
int find_inode ( const char * filename ){
    for(int i=0 ; i < MAX_FILES ; i++) {
        if(inode_table[i].used && strcmp(inode_table[i].name, filename)==0) {
            
            return i;
        }
    }
    return -1;
}

int find_free_inode() {
    for (int i = 0; i < MAX_FILES; i++) {
        if (!inode_table[i].used) {
            return i;
        }
    }
    return -1; // No free inode found
}

int find_free_block (){
    for (int i = 0; i < MAX_BLOCKS; i++) {
        if (!(bitmap[i / 8] & (1 << (i % 8)))) {
            return i; // Found a free block
        }
    }
    return -1; // No free block found
}
void read_inode(int inode_num, inode* target) {
    off_t offset = (2 * BLOCK_SIZE) + inode_num * sizeof(inode);

    if (lseek(fd, offset, SEEK_SET) == -1) {
        return; // Error seeking
    }
    read(fd, target, sizeof(inode));

}

// Write an inode to disk
void write_inode ( int inode_num , const inode * source ){
    off_t offset = (2 * BLOCK_SIZE) + inode_num * sizeof(inode);
    if (lseek(fd, offset, SEEK_SET) == -1) {
        return; // Error seeking
    }
    write(fd, source, sizeof(inode));
}
// Mark a block as used

void mark_block_used ( int block_num ){
    bitmap[block_num / 8] |= (1 << (block_num % 8));
    sb.free_blocks--;
}
// Mark a block as free
void mark_block_free ( int block_num ){
    bitmap[block_num / 8] &= ~(1 << (block_num % 8));
    sb.free_blocks++;
}
   
int fs_format(const char* disk_path) {
    fd = open(disk_path, O_RDWR | O_CREAT | O_TRUNC, 0644);
    if (fd < 0) {
        return -1; // Could not create disk file
    }

    // Ensure the file is 10MB (2560 * 4096)
    if (lseek(fd, MAX_BLOCKS * BLOCK_SIZE - 1, SEEK_SET) == -1 || write(fd, "", 1) != 1) {
        close(fd);
        return -1; // Error setting disk size
    }

    sb.total_blocks = MAX_BLOCKS;
    sb.block_size = BLOCK_SIZE;
    sb.free_blocks = MAX_BLOCKS - 10; // All blocks except metadata (0–9)
    sb.total_inodes = MAX_FILES;
    sb.free_inodes = MAX_FILES;

    // Write superblock to block 0
    if (lseek(fd, 0, SEEK_SET) == -1 || write(fd, &sb, BLOCK_SIZE) != BLOCK_SIZE) {
        close(fd);
        return -1;
    }
    for (int i = 0; i < MAX_BLOCKS / 8; i++) {
        bitmap[i] = 0;
    }

    // Mark metadata blocks [0–9] as used
    for (int i = 0; i < 10; i++) {
        bitmap[i / 8] |= (1 << (i % 8));
    }

    // Write bitmap to block 1
    if (lseek(fd, BLOCK_SIZE, SEEK_SET) == -1 || write(fd, bitmap, BLOCK_SIZE) != BLOCK_SIZE) {
        close(fd);
        return -1;
    }
    //initialize inode table
    for (int i = 0; i < MAX_FILES; i++) {
        inode_table[i].used = 0;
    }
    if (lseek(fd, 2 * BLOCK_SIZE, SEEK_SET) == -1 || write(fd, inode_table, BLOCK_SIZE*8) != BLOCK_SIZE*8) {
        close(fd);
        return -1; // Error writing inode table blocks
    }
    
    close(fd);
    return 0;

}

int fs_mount(const char* disk_path){
    fd = open(disk_path, O_RDWR);
    if (fd < 0) {
        return -1; // Could not open disk file
    }
    // Read superblock from block 0
    if (lseek(fd, 0, SEEK_SET) == -1 || read(fd, &sb, BLOCK_SIZE) != BLOCK_SIZE) {
        close(fd);
        fd =-1;
        return -1; // Error reading superblock
    }
    // Validate the superblock
    if (sb.total_blocks != MAX_BLOCKS || sb.block_size != BLOCK_SIZE || sb.total_inodes != MAX_FILES) {
        close(fd);
        fd = -1;
        return -1;  // Invalid filesystem
    }
    // Read bitmap
    if (lseek(fd, BLOCK_SIZE, SEEK_SET) == -1 || read(fd, bitmap, BLOCK_SIZE) != BLOCK_SIZE) {
        close(fd);
        fd =-1;
        return -1; // Error reading bitmap
    }
    if (lseek(fd, BLOCK_SIZE*2, SEEK_SET) == -1 || read(fd, inode_table, BLOCK_SIZE*8) != BLOCK_SIZE*8) {
        close(fd);
        fd =-1;
        return -1; // Error reading bitmap
    }
    
    return 0; // Successfully mounted filesystem
}

void fs_unmount(){
    if (fd == -1) {
        return; // Not mounted
    }
     // write superblock from block 0
    if (lseek(fd, 0, SEEK_SET) == -1 || write(fd, &sb, BLOCK_SIZE) != BLOCK_SIZE) {
        return ; // Error reading superblock
    }
        // write bitmap
    if (lseek(fd, BLOCK_SIZE, SEEK_SET) == -1 || write(fd, bitmap, BLOCK_SIZE) != BLOCK_SIZE) {
        return ; // Error reading bitmap
    }
    if (lseek(fd, BLOCK_SIZE*2, SEEK_SET) == -1 || write(fd, inode_table, BLOCK_SIZE*8) != BLOCK_SIZE*8) {
        return ; // Error reading bitmap
    }
    close(fd);
    fd = -1; // Mark as unmounted
}

int fs_create(const char* filename){
    // Check if filesystem is mounted
    if (fd == -1) {
        return -3; // Filesystem not mounted
    }
    if (strlen(filename) > MAX_FILENAME) {
        return -3; // Filename too long
    }
    if (find_inode(filename) != -1) {
        return -1; // File already exists
    }
    int inode_num = find_free_inode();
    if (inode_num == -1) {
        return -2; // No free inodes available
    }
    inode new_inode;
    new_inode = inode_table[inode_num]; // Initialize new inode
    new_inode.used = 1;
    strncpy(new_inode.name, filename, MAX_FILENAME);
    new_inode.size = 0;
    for(int i = 0; i < MAX_DIRECT_BLOCKS; i++) {
        new_inode.blocks[i] = -1; // Initialize blocks to -1 (not allocated)
    }
    sb.free_inodes--;
    write_inode(inode_num, &new_inode);
    inode_table[inode_num] = new_inode; // Update inode table
    return 0; // File created successfully
}

int fs_list(char filenames[][MAX_FILENAME], int max_files){
    int count = 0;
    for (int i = 0; i < MAX_FILES && count < max_files; i++) {
        if (inode_table[i].used) {
            strncpy(filenames[count], inode_table[i].name, MAX_FILENAME);
            count++;
        }
    }
    return count; // Return number of files found

}

int fs_write(const char* filename, const void* data, int size){
    if (fd == -1) {
        return -3; // Filesystem not mounted
    }
    int inode_num = find_inode(filename);
    if (inode_num == -1) {
        return -1; // File not found
    }
    inode file_inode;
    read_inode(inode_num, &file_inode);

    if (size > sb.block_size * MAX_DIRECT_BLOCKS) {
        return -2; // Data too large for file
    }
    
    int required_blocks = (size + BLOCK_SIZE - 1) / BLOCK_SIZE; // Calculate number of blocks needed
    int current_blocks = (inode_table[inode_num].size +BLOCK_SIZE - 1) / BLOCK_SIZE; // Current number of blocks used
    if (required_blocks > sb.free_blocks + current_blocks) {
        return -2; // Not enough free blocks
    }
    //free existing blocks if necessary
    for (int i=0 ; i< MAX_DIRECT_BLOCKS ; i++){
        if(file_inode.blocks[i] != -1) {
            mark_block_free(file_inode.blocks[i]);
            file_inode.blocks[i] = -1; // Free the block
        }
    }
    // Allocate new blocks 
    for (int i = 0; i < required_blocks; i++) {
        int block_num = find_free_block();
        mark_block_used(block_num);
        file_inode.blocks[i] = block_num;


        if (lseek(fd, block_num * BLOCK_SIZE, SEEK_SET) == -1) {
            return -3; // Error seeking to block
        }
        if (write(fd, data + i * BLOCK_SIZE, BLOCK_SIZE) != BLOCK_SIZE) {
            return -3; // Error writing block
        }
    }
    file_inode.size = size; // Update file size
    write_inode(inode_num, &file_inode); // Write updated inode back to disk

    return 0; // Write successful
}

int fs_read(const char* filename, void* buffer, int size){
    if (fd == -1){
        return -3; // Filesystem not mounted
    }
    int inode_num = find_inode(filename);
    if (inode_num == -1) {
        return -1; // File not found
    }
    inode file_inode;
    read_inode(inode_num, &file_inode);
    int bytes_to_read = min(size, file_inode.size); 
    int bytes_read = 0;
    for (int i = 0; i < MAX_DIRECT_BLOCKS && bytes_read < bytes_to_read; i++) {
        if (file_inode.blocks[i] != -1) {
            if (lseek(fd, file_inode.blocks[i] * BLOCK_SIZE, SEEK_SET) == -1) {
                return -3; // Error seeking to block
            }
            int bytes_in_block = min(BLOCK_SIZE, bytes_to_read - bytes_read);
            if (read(fd, buffer + bytes_read, bytes_in_block) != bytes_in_block) {
                return -3; // Error reading block
            }
            bytes_read += bytes_in_block;
        }
    }
    return bytes_read; // Return number of bytes read
} 
int fs_delete(const char* filename){

    if (fd == -1){
        return -2; // Filesystem not mounted
    }
    int inode_num = find_inode(filename);
    if (inode_num == -1) {
        return -1; // File not found
    }
    inode file_inode;
    read_inode(inode_num, &file_inode);
    for(int i = 0; i < MAX_DIRECT_BLOCKS; i++) {
        if (file_inode.blocks[i] != -1) {
            mark_block_free(file_inode.blocks[i]); // Free each block and increased the free block count
        }

    }
    file_inode.used = 0; // Mark inode as unused
    write_inode(inode_num, &file_inode); // Write updated inode back to disk
    sb.free_inodes++; // Increment free inodes count
    return 0; // File deleted successfully
}  




