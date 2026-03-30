// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/4/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)
// The algorithm is based on repetitive addition.

//// Replace this comment with your code.
//Reset R2 to 0
    @R2
    M=0
(LOOP) //Main multiplication loop
    @R1
    D=M
    @END
    D;JEQ
    //Add R0 value to R2
    @R0
    D=M
    @R2
    M=D+M
    //Decrease the counter
    @R1
    M=M-1
    //goto LOOP
    @LOOP
    0;JMP
(END)
    //Infinite loop
    @END
    0;JMP  