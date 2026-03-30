// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/4/Fill.asm

// Runs an infinite loop that listens to the keyboard input. 
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel. When no key is pressed, 
// the screen should be cleared.

//// Replace this comment with your code.
@state // if white state = 0, else state = 1
M=0
(RESETPOINTER) //reset screen pointer to first pixel
  @SCREEN
  D=A
  @i
  M=D

(ISPRESSED) // check if a key is pressed
  @KBD
  D=M
  @BLACK
  D;JNE

(WHITE)
  //check the previous state - if white don't reset pointer (and update to current state)
  @state
  D=M
  M=0
  @COLORWHITE
  D;JEQ
  //reset screen pointer to first pixel
  @RESETPOINTER
  0;JMP
(COLORWHITE)
  //get address and increment to next one
  @i
  D=M
  M=M+1
  //Color the address white
  A=D
  M=0
  //check overflow
  @i
  D=M
  @KBD
  D=D-A
  @RESETPOINTER
  D;JEQ
  @ISPRESSED
  0;JMP

(BLACK)
  //check the previous state - if black don't reset pointer
  @state
  D=M
  M=1
  @COLORBLACK
  D;JGT
  //reset screen pointer to first pixel
  @RESETPOINTER
  0;JMP
(COLORBLACK)
  //get address and increment to next one
  @i
  D=M
  M=M+1
  //Color the address white
  A=D
  M=-1
  //check overflow
  @i
  D=M
  @KBD
  D=D-A
  @RESETPOINTER
  D;JEQ
  @ISPRESSED
  0;JMP