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
//FunctionClass1.set0
//function label
(Class1.set)
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
//pop static 0
@SP
M=M-1
A=M
D=M
@Class10
M=D
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
//pop static 1
@SP
M=M-1
A=M
D=M
@Class11
M=D
//push constant 0
@0
D=A
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
//FunctionClass1.get0
//function label
(Class1.get)
//Save Counter = nVars
@0
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
//push static 0
@Class10
D=M
@SP
A=M
M=D
@SP
M=M+1
//push static 1
@Class11
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
//push constant 6
@6
D=A
@SP
A=M
M=D
@SP
M=M+1
//push constant 8
@8
D=A
@SP
A=M
M=D
@SP
M=M+1
//CallClass1.set2
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
@2
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
@Class1.set
0;JMP
(Sys.init$ret.2)
//pop temp 0
@SP
M=M-1
A=M
D=M
@5
M=D
//push constant 23
@23
D=A
@SP
A=M
M=D
@SP
M=M+1
//push constant 15
@15
D=A
@SP
A=M
M=D
@SP
M=M+1
//CallClass2.set2
//push return address
@Sys.init$ret.3
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
@2
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
@Class2.set
0;JMP
(Sys.init$ret.3)
//pop temp 0
@SP
M=M-1
A=M
D=M
@5
M=D
//CallClass1.get0
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
@Class1.get
0;JMP
(Sys.init$ret.4)
//CallClass2.get0
//push return address
@Sys.init$ret.5
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
@Class2.get
0;JMP
(Sys.init$ret.5)
//lable
(Sys.init$END)
//goto
@Sys.init$END
0;JMP
//FunctionClass2.set0
//function label
(Class2.set)
//Save Counter = nVars
@0
D=A
@Counter
M=D
//if nVars == 0 Continue
@CONT3
D;JEQ
//Init Local Variables
(LocalInit3)
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
@LocalInit3
D;JGT
(CONT3)
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
//pop static 0
@SP
M=M-1
A=M
D=M
@Class20
M=D
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
//pop static 1
@SP
M=M-1
A=M
D=M
@Class21
M=D
//push constant 0
@0
D=A
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
//FunctionClass2.get0
//function label
(Class2.get)
//Save Counter = nVars
@0
D=A
@Counter
M=D
//if nVars == 0 Continue
@CONT4
D;JEQ
//Init Local Variables
(LocalInit4)
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
@LocalInit4
D;JGT
(CONT4)
//push static 0
@Class20
D=M
@SP
A=M
M=D
@SP
M=M+1
//push static 1
@Class21
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