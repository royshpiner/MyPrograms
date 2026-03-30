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
//FunctionSys.init0
//function label
(Sys.init)
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
//push constant 4000
@4000
D=A
@SP
A=M
M=D
@SP
M=M+1
//pop pointer 0
@SP
M=M-1
A=M
D=M
@THIS
M=D
//push constant 5000
@5000
D=A
@SP
A=M
M=D
@SP
M=M+1
//pop pointer 1
@SP
M=M-1
A=M
D=M
@THAT
M=D
//CallSys.main0
//push return address
@Sys.init$ret.2
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
@Sys.main
0;JMP
(Sys.init$ret.2)
//pop temp 1
@SP
M=M-1
A=M
D=M
@6
M=D
//lable
(Sys.init$LOOP)
//goto
@Sys.init$LOOP
0;JMP
//FunctionSys.main5
//function label
(Sys.main)
//Save Counter = nVars
@5
D=A
@Counter
M=D
//if nVars == 0 Continue
@CONT1
D;JEQ
//Init Local Variables
(LocalInit1)
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
@LocalInit1
D;JGT
(CONT1)
//push constant 4001
@4001
D=A
@SP
A=M
M=D
@SP
M=M+1
//pop pointer 0
@SP
M=M-1
A=M
D=M
@THIS
M=D
//push constant 5001
@5001
D=A
@SP
A=M
M=D
@SP
M=M+1
//pop pointer 1
@SP
M=M-1
A=M
D=M
@THAT
M=D
//push constant 200
@200
D=A
@SP
A=M
M=D
@SP
M=M+1
//pop LCL 1
@LCL
D=M
@1
D=D+A
@addr
M=D
@SP
M=M-1
A=M
D=M
@addr
A=M
M=D
//push constant 40
@40
D=A
@SP
A=M
M=D
@SP
M=M+1
//pop LCL 2
@LCL
D=M
@2
D=D+A
@addr
M=D
@SP
M=M-1
A=M
D=M
@addr
A=M
M=D
//push constant 6
@6
D=A
@SP
A=M
M=D
@SP
M=M+1
//pop LCL 3
@LCL
D=M
@3
D=D+A
@addr
M=D
@SP
M=M-1
A=M
D=M
@addr
A=M
M=D
//push constant 123
@123
D=A
@SP
A=M
M=D
@SP
M=M+1
//CallSys.add121
//push return address
@Sys.main$ret.3
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
@Sys.add12
0;JMP
(Sys.main$ret.3)
//pop temp 0
@SP
M=M-1
A=M
D=M
@5
M=D
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
//push LCL 2
@LCL
D=M
@2
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
//push LCL 3
@LCL
D=M
@3
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
//push LCL 4
@LCL
D=M
@4
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
//FunctionSys.add120
//function label
(Sys.add12)
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
//push constant 4002
@4002
D=A
@SP
A=M
M=D
@SP
M=M+1
//pop pointer 0
@SP
M=M-1
A=M
D=M
@THIS
M=D
//push constant 5002
@5002
D=A
@SP
A=M
M=D
@SP
M=M+1
//pop pointer 1
@SP
M=M-1
A=M
D=M
@THAT
M=D
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
//push constant 12
@12
D=A
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