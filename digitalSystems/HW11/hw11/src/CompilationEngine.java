import java.io.File;

public class CompilationEngine {
    private SymbolTable st; // Symbol table for the jack class.
    private VMWriter vm; // the output file.
    private JackTokenizer tk; // the input file but in tokeen of jack languge.
    private String className; // the class name.
    private String subroutineName; // the method name.
    private int labelIndex; // the label index.

    /*
     * Creates a new compilation engine with the given input and output.
     * the next routine called (by the JackAnalyzer module) must be compileClass.
     */
    public CompilationEngine(File in, File out) {
        tk = new JackTokenizer(in);
        st = new SymbolTable();
        vm = new VMWriter(out);
        labelIndex = 0;
    }

     /**
     * return current function name, className.subroutineName
     * @return
     */
    private String currentFunction(){
        if (className.length() != 0 && subroutineName.length() !=0){
            return className + "." + subroutineName;
        }
        return "";
    }

    /**
     * Compiles a type.
     * @return type.
     */
    private String compileType(){
        tk.advance();

        if (tk.tokenType() == JackTokenizer.TYPE.KEYWORD && 
        (tk.keyWord() == JackTokenizer.KEYWORD.INT || tk.keyWord() == JackTokenizer.KEYWORD.CHAR || tk.keyWord() == JackTokenizer.KEYWORD.BOOLEAN)){
            return tk.getCurrentToken();
        }

        if (tk.tokenType() == JackTokenizer.TYPE.IDENTIFIER){
            return tk.identifier();
        }
        message("in|char|boolean|className");
        return "";
    }

     /*
     * Compiles a complete class.
     */
    public void compileClass(){
        tk.advance();

        if (tk.tokenType() != JackTokenizer.TYPE.KEYWORD || tk.keyWord() != JackTokenizer.KEYWORD.CLASS){
            System.out.println(tk.getCurrentToken());
            message("class");
        }

        tk.advance();

        if (tk.tokenType() != JackTokenizer.TYPE.IDENTIFIER){
            message("className");
        }

        className = tk.identifier();
        checkSymbol('{');
        compileClassVarDec();
        compileSubroutine();
        checkSymbol('}');

        if (tk.hasMoreTokens()){
            throw new IllegalStateException("Unexpected tokens");
        }
        vm.close();
    }

    /*
     * Compiles a static varible declaration, or a field declration.
     */
    private void compileClassVarDec(){
        tk.advance();

        if (tk.tokenType() == JackTokenizer.TYPE.SYMBOL && tk.symbol() == '}'){
            tk.pointerBack();
            return;
        }
        if (tk.tokenType() != JackTokenizer.TYPE.KEYWORD){
            message("Keywords");
        }
        if (tk.keyWord() == JackTokenizer.KEYWORD.CONSTRUCTOR || tk.keyWord() == JackTokenizer.KEYWORD.FUNCTION || tk.keyWord() == JackTokenizer.KEYWORD.METHOD){
            tk.pointerBack();
            return;
        }
        if (tk.keyWord() != JackTokenizer.KEYWORD.STATIC && tk.keyWord() != JackTokenizer.KEYWORD.FIELD){
            message("static or field");
        }
        Symbol.KIND kind = null;
        String type = "";
        String name = "";
        switch (tk.keyWord()){
            case STATIC:kind = Symbol.KIND.STATIC;break;
            case FIELD:kind = Symbol.KIND.FIELD;break;
        }
        type = compileType();
        do {
            tk.advance();
            if (tk.tokenType() != JackTokenizer.TYPE.IDENTIFIER){
                message("identifier");
            }
            name = tk.identifier();
            st.define(name,type,kind);
            tk.advance();
            if (tk.tokenType() != JackTokenizer.TYPE.SYMBOL || (tk.symbol() != ',' && tk.symbol() != ';')){
                message("',' or ';'");
            }
            if (tk.symbol() == ';'){
                break;
            }
        }
        while(true);
        compileClassVarDec();
    }
    
    private void compileSubroutine(){
        tk.advance();

        if (tk.tokenType() == JackTokenizer.TYPE.SYMBOL && tk.symbol() == '}'){
            tk.pointerBack();
            return;
        }

        if (tk.tokenType() != JackTokenizer.TYPE.KEYWORD ||
         (tk.keyWord() != JackTokenizer.KEYWORD.CONSTRUCTOR && tk.keyWord() != JackTokenizer.KEYWORD.FUNCTION && tk.keyWord() != JackTokenizer.KEYWORD.METHOD)){
            message("constructor|function|method");
        }

        JackTokenizer.KEYWORD keyword = tk.keyWord();
        st.startSubroutine();

        if (tk.keyWord() == JackTokenizer.KEYWORD.METHOD){
            st.define("this",className, Symbol.KIND.ARG);
        }

        String type = "";
        tk.advance();

        if (tk.tokenType() == JackTokenizer.TYPE.KEYWORD && tk.keyWord() == JackTokenizer.KEYWORD.VOID){
            type = "void";
        }
        else {
            tk.pointerBack();
            type = compileType();
        }

        tk.advance();

        if (tk.tokenType() != JackTokenizer.TYPE.IDENTIFIER){
            message("subroutineName");
        }

        subroutineName = tk.identifier();
        checkSymbol('(');
        compileParameterList();
        checkSymbol(')');
        compileSubroutineBody(keyword);
        compileSubroutine();
    }

    private void compileSubroutineBody(JackTokenizer.KEYWORD keyword){
        checkSymbol('{');
        compileVarDec();
        wrtieFunctionDec(keyword);
        compileStatement();
        checkSymbol('}');
    }

    private void wrtieFunctionDec(JackTokenizer.KEYWORD keyword){
        vm.writeFunction(currentFunction(),st.varCount(Symbol.KIND.VAR));

        if (keyword == JackTokenizer.KEYWORD.METHOD){
            vm.writePush(VMWriter.SEGMENT.ARG, 0);
            vm.writePop(VMWriter.SEGMENT.POINTER,0);
        }

        else if (keyword == JackTokenizer.KEYWORD.CONSTRUCTOR){
            vm.writePush(VMWriter.SEGMENT.CONST,st.varCount(Symbol.KIND.FIELD));
            vm.writeCall("Memory.alloc", 1);
            vm.writePop(VMWriter.SEGMENT.POINTER,0);
        }
    }

    private void compileStatement(){
        tk.advance();

        if (tk.tokenType() == JackTokenizer.TYPE.SYMBOL && tk.symbol() == '}'){
            tk.pointerBack();
            return;
        }

        if (tk.tokenType() != JackTokenizer.TYPE.KEYWORD){
            message("keyword");
        }

        else {
            switch (tk.keyWord()){
                case LET:
                    compileLet();break;
                case IF:
                    compileIf();break;
                case WHILE:
                    compilesWhile();break;
                case DO:
                    compileDo();break;
                case RETURN:
                    compileReturn();break;
                default:
                    message("'let'|'if'|'while'|'do'|'return'");
            }
        }
        compileStatement();
    }

    private void compileParameterList(){
        tk.advance();

        if (tk.tokenType() == JackTokenizer.TYPE.SYMBOL && tk.symbol() == ')'){
            tk.pointerBack();
            return;
        }

        String type = "";
    
        tk.pointerBack();

        do {
            type = compileType();
            tk.advance();
            if (tk.tokenType() != JackTokenizer.TYPE.IDENTIFIER){
                message("identifier");
            }
            st.define(tk.identifier(),type, Symbol.KIND.ARG);
            tk.advance();
            if (tk.tokenType() != JackTokenizer.TYPE.SYMBOL || (tk.symbol() != ',' && tk.symbol() != ')')){
                message("',' or ')'");
            }
            if (tk.symbol() == ')'){
                tk.pointerBack();
                break;
            }
        }

        while(true);
    }

    private void compileVarDec(){
        tk.advance();
        if (tk.tokenType() != JackTokenizer.TYPE.KEYWORD || tk.keyWord() != JackTokenizer.KEYWORD.VAR){
            tk.pointerBack();
            return;
        }
        String type = compileType();
        do {
            tk.advance();
            if (tk.tokenType() != JackTokenizer.TYPE.IDENTIFIER){
                message("identifier");
            }
            st.define(tk.identifier(),type, Symbol.KIND.VAR);
            tk.advance();
            if (tk.tokenType() != JackTokenizer.TYPE.SYMBOL || (tk.symbol() != ',' && tk.symbol() != ';')){
                message("',' or ';'");
            }
            if (tk.symbol() == ';'){
                break;
            }
        }
        while(true);
        compileVarDec();
    }

    private void compileDo() {
        compileSubroutineCall();
        checkSymbol(';');

        vm.writePop(VMWriter.SEGMENT.TEMP,0);
    }

    private void compileLet(){
        tk.advance();

        if (tk.tokenType() != JackTokenizer.TYPE.IDENTIFIER){
            message("varName");
        }

        String varName = tk.identifier();
        tk.advance();

        if (tk.tokenType() != JackTokenizer.TYPE.SYMBOL || (tk.symbol() != '[' && tk.symbol() != '=')){
            message("'['|'='");
        }

        boolean expresExist = false;
        if (tk.symbol() == '['){
            expresExist = true;
            vm.writePush(getSeg(st.kindOf(varName)),st.indexOf(varName));
            compileExpression();
            checkSymbol(']');
            vm.writeArithmetic(VMWriter.COMMAND.ADD);
        }

        if (expresExist) {
            tk.advance();
        }

        compileExpression();
        checkSymbol(';');

        if (expresExist) {
            vm.writePop(VMWriter.SEGMENT.TEMP,0);            
            vm.writePop(VMWriter.SEGMENT.POINTER,1);
            vm.writePush(VMWriter.SEGMENT.TEMP,0);
            vm.writePop(VMWriter.SEGMENT.THAT,0);
        }
        else {
            vm.writePop(getSeg(st.kindOf(varName)), st.indexOf(varName));
        }
    }

    /**
     * return  segment for input kind.
     * @param kind
     * @return
     */
    private VMWriter.SEGMENT getSeg(Symbol.KIND kind){
        switch (kind){
            case FIELD:
                return VMWriter.SEGMENT.THIS;
            case STATIC:
                return VMWriter.SEGMENT.STATIC;
            case VAR:
                return VMWriter.SEGMENT.LOCAL;
            case ARG:
                return VMWriter.SEGMENT.ARG;
            default:
                return VMWriter.SEGMENT.NONE;
        }
    }

    private void compilesWhile(){
        String tLabel = newLabel();
        String cLabel = newLabel();

        vm.writeLabel(tLabel);
        checkSymbol('(');
        compileExpression();
        checkSymbol(')');
        vm.writeArithmetic(VMWriter.COMMAND.NOT);
        vm.writeIf(cLabel);
        checkSymbol('{');
        compileStatement();
        checkSymbol('}');
        vm.writeGoto(tLabel);
        vm.writeLabel(cLabel);
    }

    private String newLabel(){
        return "LABEL_" + (labelIndex++);
    }

    private void compileReturn(){
        tk.advance();

        if (tk.tokenType() == JackTokenizer.TYPE.SYMBOL && tk.symbol() == ';'){
            vm.writePush(VMWriter.SEGMENT.CONST,0);
        }

        else {
            tk.pointerBack();
            compileExpression();
            checkSymbol(';');
        }

        vm.writeReturn();
    }

    /**
     * Compiles an if statement.
     * possibly with a trailing else clause.
     */
    private void compileIf(){
        String elseL = newLabel();
        String endL = newLabel();

        checkSymbol('(');
        compileExpression();
        checkSymbol(')');
        vm.writeArithmetic(VMWriter.COMMAND.NOT);
        vm.writeIf(elseL);
        checkSymbol('{');
        compileStatement();
        checkSymbol('}');
        vm.writeGoto(endL);
        vm.writeLabel(elseL);
        tk.advance();
        if (tk.tokenType() == JackTokenizer.TYPE.KEYWORD && tk.keyWord() == JackTokenizer.KEYWORD.ELSE){
            checkSymbol('{');
            compileStatement();
            checkSymbol('}');
        }
        else {
            tk.pointerBack();
        }

        vm.writeLabel(endL);
    }

    private void compileTerm(){
        tk.advance();

        if (tk.tokenType() == JackTokenizer.TYPE.IDENTIFIER){
            String tempId = tk.identifier();
            tk.advance();

            if (tk.tokenType() == JackTokenizer.TYPE.SYMBOL && tk.symbol() == '['){
                vm.writePush(getSeg(st.kindOf(tempId)),st.indexOf(tempId));
                compileExpression();
                checkSymbol(']');
                vm.writeArithmetic(VMWriter.COMMAND.ADD);
                vm.writePop(VMWriter.SEGMENT.POINTER,1);
                vm.writePush(VMWriter.SEGMENT.THAT,0);
            }
            else if (tk.tokenType() == JackTokenizer.TYPE.SYMBOL && (tk.symbol() == '(' || tk.symbol() == '.')){
                tk.pointerBack();
                tk.pointerBack();
                compileSubroutineCall();
            }
            else {
                tk.pointerBack();
                vm.writePush(getSeg(st.kindOf(tempId)), st.indexOf(tempId));
            }
        } else{
            if (tk.tokenType() == JackTokenizer.TYPE.INT_CONST){
                vm.writePush(VMWriter.SEGMENT.CONST,tk.intVal());
            }
            else if (tk.tokenType() == JackTokenizer.TYPE.STRING_CONST){
                String str = tk.stringVal();
                vm.writePush(VMWriter.SEGMENT.CONST,str.length());
                vm.writeCall("String.new",1);
                for (int i = 0; i < str.length(); i++){
                    vm.writePush(VMWriter.SEGMENT.CONST,(int)str.charAt(i));
                    vm.writeCall("String.appendChar",2);
                }
            }
            else if(tk.tokenType() == JackTokenizer.TYPE.KEYWORD && tk.keyWord() == JackTokenizer.KEYWORD.TRUE){
                vm.writePush(VMWriter.SEGMENT.CONST,0);
                vm.writeArithmetic(VMWriter.COMMAND.NOT);

            }
            else if(tk.tokenType() == JackTokenizer.TYPE.KEYWORD && tk.keyWord() == JackTokenizer.KEYWORD.THIS){
                vm.writePush(VMWriter.SEGMENT.POINTER,0);

            }
            else if(tk.tokenType() == JackTokenizer.TYPE.KEYWORD && (tk.keyWord() == JackTokenizer.KEYWORD.FALSE || tk.keyWord() == JackTokenizer.KEYWORD.NULL)){
                vm.writePush(VMWriter.SEGMENT.CONST,0);
            }
            else if (tk.tokenType() == JackTokenizer.TYPE.SYMBOL && tk.symbol() == '('){
                compileExpression();
                checkSymbol(')');
            }
            else if (tk.tokenType() == JackTokenizer.TYPE.SYMBOL && (tk.symbol() == '-' || tk.symbol() == '~')){
                char s = tk.symbol();
                compileTerm();
                if (s == '-'){
                    vm.writeArithmetic(VMWriter.COMMAND.NEG);
                }
                else
                    vm.writeArithmetic(VMWriter.COMMAND.NOT);
            }
            else {
                message("integerConstant|stringConstant|keywordConstant|'(' expression ')'|unaryOp term");
            }
        }
    }

    /**
     * Compiles a subroutine call.
     */
    private void compileSubroutineCall(){
        tk.advance();

        if (tk.tokenType() != JackTokenizer.TYPE.IDENTIFIER) {
            message("identifier");
        }
        String name = tk.identifier();
        int args = 0;

        tk.advance();

        if (tk.tokenType() == JackTokenizer.TYPE.SYMBOL && tk.symbol() == '('){
            vm.writePush(VMWriter.SEGMENT.POINTER,0);
            args = compileExpressionList() + 1;
            checkSymbol(')');
            vm.writeCall(className + '.' + tk, args);
        }
        else if (tk.tokenType() == JackTokenizer.TYPE.SYMBOL && tk.symbol() == '.'){
            String objName = name;
            tk.advance();
            if (tk.tokenType() != JackTokenizer.TYPE.IDENTIFIER){
                message("identifier");
            }
            name = tk.identifier();
            String type = st.typeOf(objName);
            if (type.equals("int")||type.equals("boolean")||type.equals("char")||type.equals("void")){
                message("Type is not built-in");
            }
            else if (type.equals("")){
                name = objName + "." + name;
            }
            else {
                args = 1;
                vm.writePush(getSeg(st.kindOf(objName)), st.indexOf(objName));
                name = st.typeOf(objName) + "." + name;
            }
            checkSymbol('(');
            args += compileExpressionList();
            checkSymbol(')');
            vm.writeCall(name,args);
        }
        else {
            message("'('|'.'");
        }
    }

    /**
     * Compiles an expression.
     */
    private void compileExpression(){
        compileTerm();
        do {
            tk.advance();
            if (tk.tokenType() == JackTokenizer.TYPE.SYMBOL && tk.isOp()){
                String operationCommand = "";
                switch (tk.symbol()){
                    case '+':
                        operationCommand = "add";
                        break;
                    case '-':
                        operationCommand = "sub";
                        break;
                    case '*':
                        operationCommand = "call Math.multiply 2";
                        break;
                    case '/':
                        operationCommand = "call Math.divide 2";
                        break;
                    case '<':
                        operationCommand = "lt";
                        break;
                    case '>':
                        operationCommand = "gt";
                        break;
                    case '=':
                        operationCommand = "eq";
                        break;
                    case '&':
                        operationCommand = "and";
                        break;
                    case '|':
                        operationCommand = "or";
                        break;
                }
                compileTerm();
                vm.writeCommand(operationCommand,"","");
            }
            else {
                tk.pointerBack();
                break;
            }
        }
        while (true);
    }

    /**
     * Compiles a (possibly empty) comma-separated list of expressions.
     * @return nargs.
     */
    private int compileExpressionList(){
        int nargs = 0;
        tk.advance();
        if (tk.tokenType() == JackTokenizer.TYPE.SYMBOL && tk.symbol() == ')'){
            tk.pointerBack();
        }
        else {
            nargs = 1;
            tk.pointerBack();
            compileExpression();
            do {
                tk.advance();
                if (tk.tokenType() == JackTokenizer.TYPE.SYMBOL && tk.symbol() == ','){
                    compileExpression();
                    nargs++;
                }
                else {
                    tk.pointerBack();
                    break;
                }
            }
            while (true);
        }
        return nargs;
    }
    
    /**
     * throw an exception to report errors.
     * @param val
     */
    private void message(String val){
        throw new IllegalStateException("Expected token missing : " + val + " Current token: " + tk.getCurrentToken());
    }

    /**
     * check symbol when we know there must be such symbol.
     * @param symbol
     */
    private void checkSymbol(char symbol){
        tk.advance();
        if (tk.tokenType() != JackTokenizer.TYPE.SYMBOL || tk.symbol() != symbol) 
            message("'" + symbol + "'");
    }
}

