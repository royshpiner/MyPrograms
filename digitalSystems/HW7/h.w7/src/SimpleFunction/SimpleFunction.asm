//FunctionSimpleFunction.test2
//function label
(SimpleFunction.test)
//Save Counter = nVars
@2
D=A
@Counter
M=D
//if nVars == 0 Continue
@CONT0
D;JEQ
//Init Local Variables
(LocalInit0)
@0
D=A
@SP
A=M
M=D
@SP
M=M+1
//Counter-- and Loop
@Counter
M=M-1
D=M
@LocalInit0
D;JGT
(CONT0)
//push LCL 0
@LCL
D=M
@0
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
//push LCL 1
@LCL
D=M
@1
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
//add
@SP
M=M-1
A=M
D=M
@SP
M=M-1
A=M
M=D+M
@SP
M=M+1
//not
@SP
M=M-1
A=M
D=M
M=!M
@SP
M=M+1
//push ARG 0
@ARG
D=M
@0
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
//add
@SP
M=M-1
A=M
D=M
@SP
M=M-1
A=M
M=D+M
@SP
M=M+1
//push ARG 1
@ARG
D=M
@1
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
//sub
@SP
M=M-1
A=M
D=M
@SP
M=M-1
A=M
M=M-D
@SP
M=M+1
//Return
//store the endFrame
@LCL
D=M
@endFrame
M=D
//compute return address
D=M
@5
D=D-A
A=D
D=M
@retAddress
M=D
//*ARG = pop()
@SP
A=M-1
D=M
@ARG
A=M
M=D
//update SP
D=A+1
@SP
M=D
//reposition THAT
@1
D=A
@endFrame
A=M-D
D=M
@THAT
M=D
//reposition THIS
@2
D=A
@endFrame
A=M-D
D=M
@THIS
M=D
//reposition ARG
@3
D=A
@endFrame
A=M-D
D=M
@ARG
M=D
//reposition LCL
@4
D=A
@endFrame
A=M-D
D=M
@LCL
M=D
//Go-To retAddress
@retAddress
A=M
0;JMP
//infinite loop
(ENDLOOP)
@ENDLOOP
0;JMP