//Bootstrap Code
@256
D=A
@SP
M=D
//push return address
@$ret.0
D=A
@SP
A=M
M=D
@SP
M=M+1
//push LCL
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
//push ARG
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
//push THIS
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
//push THAT
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1
//Set new ARG
@0
D=A
@5
D=D+A
@SP
D=M-D
@ARG
M=D
//Set Local
@SP
D=M
@LCL
M=D
//call function
@Sys.init
0;JMP
//CallSys.init0
//push return address
@$ret.1
D=A
@SP
A=M
M=D
@SP
M=M+1
//push LCL
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
//push ARG
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
//push THIS
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
//push THAT
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1
//Set new ARG
@0
D=A
@5
D=D+A
@SP
D=M-D
@ARG
M=D
//Set Local
@SP
D=M
@LCL
M=D
//call function
@Sys.init
0;JMP
($ret.1)
//FunctionMain.fibonacci0
//function label
(Main.fibonacci)
//Save Counter = nVars
@0
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
//push constant 2
@2
D=A
@SP
A=M
M=D
@SP
M=M+1
//lt
@SP
M=M-1
A=M
D=M
@SP
M=M-1
A=M
D=M-D
@TRUE1
D;JLT
@0
D=A
@SP
A=M
M=D
@CONT1
0;JMP
(TRUE1)
@1
A=-A
D=A
@SP
A=M
M=D
(CONT1)
@SP
M=M+1
//if-goto
@SP
M=M-1
A=M
D=M
@Main.fibonacci$N_LT_2
D;JNE
//goto
@Main.fibonacci$N_GE_2
0;JMP
//lable
(Main.fibonacci$N_LT_2)
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
//lable
(Main.fibonacci$N_GE_2)
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
//push constant 2
@2
D=A
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
//CallMain.fibonacci1
//push return address
@Main.fibonacci$ret.2
D=A
@SP
A=M
M=D
@SP
M=M+1
//push LCL
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
//push ARG
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
//push THIS
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
//push THAT
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1
//Set new ARG
@1
D=A
@5
D=D+A
@SP
D=M-D
@ARG
M=D
//Set Local
@SP
D=M
@LCL
M=D
//call function
@Main.fibonacci
0;JMP
(Main.fibonacci$ret.2)
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
//push constant 1
@1
D=A
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
//CallMain.fibonacci1
//push return address
@Main.fibonacci$ret.3
D=A
@SP
A=M
M=D
@SP
M=M+1
//push LCL
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
//push ARG
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
//push THIS
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
//push THAT
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1
//Set new ARG
@1
D=A
@5
D=D+A
@SP
D=M-D
@ARG
M=D
//Set Local
@SP
D=M
@LCL
M=D
//call function
@Main.fibonacci
0;JMP
(Main.fibonacci$ret.3)
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
//FunctionSys.init0
//function label
(Sys.init)
//Save Counter = nVars
@0
D=A
@Counter
M=D
//if nVars == 0 Continue
@CONT2
D;JEQ
//Init Local Variables
(LocalInit2)
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
@LocalInit2
D;JGT
(CONT2)
//push constant 4
@4
D=A
@SP
A=M
M=D
@SP
M=M+1
//CallMain.fibonacci1
//push return address
@Sys.init$ret.4
D=A
@SP
A=M
M=D
@SP
M=M+1
//push LCL
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
//push ARG
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
//push THIS
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
//push THAT
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1
//Set new ARG
@1
D=A
@5
D=D+A
@SP
D=M-D
@ARG
M=D
//Set Local
@SP
D=M
@LCL
M=D
//call function
@Main.fibonacci
0;JMP
(Sys.init$ret.4)
//lable
(Sys.init$END)
//goto
@Sys.init$END
0;JMP
//infinite loop
(ENDLOOP)
@ENDLOOP
0;JMP