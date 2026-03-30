import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CompilationEngine {
    
    BufferedWriter writer;
    public JackTokenizer tokenizer;
    int tabCounter;

    public CompilationEngine(File input, File output) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(output));
        this.tokenizer = new JackTokenizer(input);
        this.tabCounter = 0;
    }

    public void compileClass() throws IOException{
        writer.write("<class>\n");
        tabCounter++;

        // 'class'
        tokenizer.advance();
        writeNextToken();
        
        // className
        tokenizer.advance();
        writeNextToken();
        
        // {
        tokenizer.advance();
        writeNextToken();
        
        // check if varDec exists
        tokenizer.advance();
        while(tokenizer.tokenType() == "KEYWORD" && (tokenizer.keyWord().equals("static") || tokenizer.keyWord().equals("field"))){
            compileClassVarDec();
            tokenizer.advance();
        }

        // check if subroutineDec exists
        while(tokenizer.tokenType() == "KEYWORD" && (tokenizer.keyWord().equals("function") || tokenizer.keyWord().equals("constructor")
           || tokenizer.keyWord().equals("method"))){
            compileSubroutine();
            tokenizer.advance();
        }

        // }
        writeNextToken();
        tabCounter--;
        writer.write("</class>");
    }
    
    public void compileClassVarDec() throws IOException{
        writer.write(StringOps.repeat("    ", tabCounter)+"<classVarDec>\n");
        tabCounter++;
        // write static|field
        writeNextToken();

        // type
        tokenizer.advance();
        writeNextToken();

        //varName
        tokenizer.advance();
        writeNextToken();

        //check if more variables exist
        tokenizer.advance();
        while(tokenizer.symbol() != ';'){
            writeNextToken();
            tokenizer.advance();
            writeNextToken();
            tokenizer.advance();
        }

        // ;
        writeNextToken();

        tabCounter--; 
        writer.write(StringOps.repeat("    ", tabCounter) + "</classVarDec>\n");
    }
    
    public void compileSubroutine() throws IOException{
        writer.write(StringOps.repeat("    ", tabCounter)+"<subroutineDec>\n");
        tabCounter++;

        // write constructor | function | method
        writeNextToken();

        // write void | type
        tokenizer.advance();
        writeNextToken();

        // subroutineName
        tokenizer.advance();
        writeNextToken();

        // (
        tokenizer.advance();
        writeNextToken();

        // parameterList
        tokenizer.advance();
        compileParameterList();

        //)
        writeNextToken();

        // subroutineBody
        compileSubroutineBody();

        tabCounter--; 
        writer.write(StringOps.repeat("    ", tabCounter) + "</subroutineDec>\n");
    }
    
    public void compileParameterList() throws IOException{
        writer.write(StringOps.repeat("    ", tabCounter)+"<parameterList>\n");
        tabCounter++;
        
        // check if parmeter exist
        if(tokenizer.tokenType() != "SYMBOL"){
            //write parameter type
            writeNextToken();

            // varName
            tokenizer.advance();
            writeNextToken();

            //check if more parameters exist
            tokenizer.advance();
            while(tokenizer.tokenType() != "SYMBOL" || tokenizer.symbol() != ')'){
                //write ','
                writeNextToken();
                
                //get next token
                tokenizer.advance();
            }
        }
        tabCounter--; 
        writer.write(StringOps.repeat("    ", tabCounter) + "</parameterList>\n");
    }
    
    public void compileSubroutineBody() throws IOException{
        writer.write(StringOps.repeat("    ", tabCounter)+"<subroutineBody>\n");
        tabCounter++;

        // {
        tokenizer.advance();
        writeNextToken();
        
        // check if declarations of variables exist
        tokenizer.advance();
        while(tokenizer.tokenType() == "KEYWORD" && tokenizer.keyWord().equals("var")){
            compileVarDec();
            tokenizer.advance();
        }
        
        compileStatements();

        // }
        writeNextToken();
        
        tabCounter--;
        writer.write(StringOps.repeat("    ", tabCounter)+"</subroutineBody>\n");
    }
    
    public void compileVarDec() throws IOException{
        writer.write(StringOps.repeat("    ", tabCounter)+"<varDec>\n");
        tabCounter++;

        // write var
        writeNextToken();

        // write type
        tokenizer.advance();
        writeNextToken();
        
        // write varName
        tokenizer.advance();
        writeNextToken();

        // check if more variables exist
        tokenizer.advance();
        while(tokenizer.symbol() != ';'){
            writeNextToken();
            tokenizer.advance();
            writeNextToken();
            tokenizer.advance();
        }

        // write ;
        writeNextToken();

        tabCounter--;
        writer.write(StringOps.repeat("    ", tabCounter) + "</varDec>\n");
    }


    public void compileStatements() throws IOException{
        writer.write(StringOps.repeat("    ", tabCounter) + "<statements>\n");
        tabCounter++;

        while(tokenizer.tokenType()== "KEYWORD" && TypesMap.isStatement(tokenizer.keyWord())){
            switch(tokenizer.keyWord()){
                case "let":
                    compileLet();
                    break;
                case "if":
                    compileIf();
                    break;
                case "while":
                    compileWhile();
                    break;
                case "do":
                    compileDo();
                    break;
                case "return":
                    compileReturn();
                    break;
            }
        }

        tabCounter--;
        writer.write(StringOps.repeat("    ", tabCounter) + "</statements>\n");
    }
    
    public void compileLet() throws IOException{
        writer.write(StringOps.repeat("    ", tabCounter) + "<letStatement>\n");
        tabCounter++;
        
        // write let
        writeNextToken();

        // write VarName
        tokenizer.advance();
        writeNextToken();

        // check if '[' exists
        tokenizer.advance();
        if(tokenizer.tokenType() == "SYMBOL" && tokenizer.symbol() == '['){
            writeNextToken();
            
            // write expression
            tokenizer.advance();
            compileExpression();
            
            //write ']'
            writeNextToken();

            // get next token (=)
            tokenizer.advance();
        }

        // write =
        writeNextToken();

        // write expression
        tokenizer.advance();
        compileExpression();

        //write ';'
        writeNextToken();

        //advance to next token
        tokenizer.advance();

        tabCounter--;
        writer.write(StringOps.repeat("    ", tabCounter) + "</letStatement>\n");
    }
    
    public void compileIf() throws IOException {
        writer.write(StringOps.repeat("    ", tabCounter) + "<ifStatement>\n");
        tabCounter++;

        // write if
        writeNextToken();

        // write '('
        tokenizer.advance();
        writeNextToken();

        // write expression
        tokenizer.advance();
        compileExpression();

        // write ')'
        writeNextToken();

        //write '{'
        tokenizer.advance();
        writeNextToken();

        // write statements
        tokenizer.advance();
        compileStatements();

        // write '}'
        writeNextToken();

        //check if else exists
        tokenizer.advance();
        if(tokenizer.tokenType() == "KEYWORD" && tokenizer.keyWord().equals("else")){
            //write else
            writeNextToken();

            // write '{'
            tokenizer.advance();
            writeNextToken();

            // write statements
            tokenizer.advance();
            compileStatements();

            // write '}'
            writeNextToken();

            // read next token
            tokenizer.advance();
        }

        tabCounter--;
        writer.write(StringOps.repeat("    ", tabCounter) + "</ifStatement>\n");
    }
    
    public void compileWhile() throws IOException{
        writer.write(StringOps.repeat("    ", tabCounter) + "<whileStatement>\n");
        tabCounter++;

        // write while
        writeNextToken();

        //write ')'
        tokenizer.advance();
        writeNextToken();

        //write expression
        tokenizer.advance();
        compileExpression();

        //write ')'
        writeNextToken();

        //write '{'
        tokenizer.advance();
        writeNextToken();

        //write statements
        tokenizer.advance();
        compileStatements();

        //write '}'
        writeNextToken();

        //get the next token
        tokenizer.advance();

        tabCounter--;
        writer.write(StringOps.repeat("    ", tabCounter) + "</whileStatement>\n");
    }
    
    public void compileDo() throws IOException{
        writer.write(StringOps.repeat("    ", tabCounter) + "<doStatement>\n");
        tabCounter++;

        //write do
        writeNextToken();

    //write subroutine call
        //write subroutineName | className | varName
        tokenizer.advance();
        writeNextToken();

        //check if there is a '.'
        tokenizer.advance();
        compileSubroutineCall();
      
        tokenizer.advance();
        writeNextToken();

        //get the next token
        tokenizer.advance();

        tabCounter--;
        writer.write(StringOps.repeat("    ", tabCounter) + "</doStatement>\n");
    }

    public void compileReturn() throws IOException{
        writer.write(StringOps.repeat("    ", tabCounter) + "<returnStatement>\n");
        tabCounter++;

        //write return
        writeNextToken();

        //check if expression exists
        tokenizer.advance();
        if(tokenizer.tokenType() != "SYMBOL" || tokenizer.symbol() != ';'){
            //write expression
            compileExpression();
        }

        //write ';'
        writeNextToken();

        //get next token
        tokenizer.advance();

        tabCounter--;
        writer.write(StringOps.repeat("    ", tabCounter) + "</returnStatement>\n");
    }

    public void compileExpression() throws IOException{
        writer.write(StringOps.repeat("    ", tabCounter) + "<expression>\n");
        tabCounter++;
        
        //write term
        compileTerm();

        //check if operation exists
        while(tokenizer.tokenType() == "SYMBOL" && TypesMap.containsOperation(tokenizer.symbol())){
            //write operation
            writeNextToken();

            //write term
            tokenizer.advance();
            compileTerm();
        }
        tabCounter--;
        writer.write(StringOps.repeat("    ", tabCounter) + "</expression>\n");
    }

    public void compileTerm() throws IOException{
        writer.write(StringOps.repeat("    ", tabCounter) + "<term>\n");
        tabCounter++;

        // write intConst | stringConst | keyConst | varName | subroutineName | '(' | unaryOp
        writeNextToken();

        // check if term starts with '(' | unaryOp
        if(tokenizer.tokenType() == "SYMBOL"){
            if(tokenizer.symbol() == '(') {
                //write expression
                tokenizer.advance();
                compileExpression();

                //write ')'
                writeNextToken();

                //get next token
                tokenizer.advance();
            }
            else if(tokenizer.symbol() == '-' || tokenizer.symbol() == '~') {
                //write term
                tokenizer.advance();
                compileTerm();
            }
        }
        else{
            //check if term does not end (next token is '[' | '(' | '.' )
            tokenizer.advance();
            if(tokenizer.tokenType() == "SYMBOL"){
                if(tokenizer.symbol() == '[') {
                    //write '['
                    writeNextToken();

                    //write expression
                    tokenizer.advance();
                    compileExpression();

                    //write ']'
                    writeNextToken();
                        
                    //get next token
                    tokenizer.advance();
                }
                else if(tokenizer.symbol() == '(' || tokenizer.symbol() == '.'){
                    //write subroutine call 
                    compileSubroutineCall();
                     
                     //get next token
                     tokenizer.advance();
                }
            }
        }
        
        tabCounter--;
        writer.write(StringOps.repeat("    ", tabCounter) + "</term>\n");
    }

    private void compileSubroutineCall() throws IOException{

        //check if there is a '.'
        if(tokenizer.tokenType() == "SYMBOL" && tokenizer.symbol() == '.'){
            //write '.'
            writeNextToken();

            //write subroutineName
            tokenizer.advance();
            writeNextToken();

            //get next token
            tokenizer.advance();
        }

        //write '('
        writeNextToken();

        //write expression list
        tokenizer.advance();
        compileExpressionList();

        //write ')'
        writeNextToken();
    }

    public int compileExpressionList() throws IOException{
        int counter = 0;
        writer.write(StringOps.repeat("    ", tabCounter) + "<expressionList>\n");
        tabCounter++;
        
        //check if expression exists
        while(tokenizer.tokenType() != "SYMBOL" || tokenizer.symbol() != ')') {
            if(tokenizer.tokenType() == "SYMBOL" && tokenizer.symbol() == ','){
                
                //write ','
                writeNextToken();

                //get next token
                tokenizer.advance();
            }
            else{
                counter++;
                //write expression
                compileExpression();
            }
        }
        tabCounter--;
        writer.write(StringOps.repeat("    ", tabCounter) + "</expressionList>\n");
        return counter;
    }

    public void close() throws IOException{
        writer.close();
    }

    public void writeNextToken() throws IOException{
        String type;
        switch (tokenizer.tokenType()){
            case "KEYWORD":
                type = "keyword";
                writer.write( StringOps.repeat(" ", tabCounter) + "<" + type + "> " + tokenizer.keyWord() + " </" + type + ">\n");
                break;
            case "SYMBOL":
                type = "symbol";
                String content = tokenizer.symbol() + "";
                if (tokenizer.tokenType() == "SYMBOL") {
                    content = TypesMap.getXmlOp(tokenizer.symbol());  // Check if this returns null
                    if (content == null) {
                        content = tokenizer.symbol() + "";  // Fallback to the original symbol

                }
            }
                writer.write(StringOps.repeat(" ", tabCounter) + "<" + type + "> " + content + " </" + type + ">\n");
                break;
            case "IDENTIFIER":
                type = "identifier";
                writer.write(StringOps.repeat(" ", tabCounter) + "<" + type + "> " + tokenizer.identifier() + " </" + type + ">\n");
                break;
            case "INT_CONST":
                type = "integerConstant";
                writer.write(StringOps.repeat(" ", tabCounter) + "<" + type + "> " + tokenizer.intVal() + " </" + type + ">\n");
                break;
            case "STRING_CONST":
                type = "stringConstant";
                writer.write(StringOps.repeat(" ", tabCounter) + "<" + type + "> " + tokenizer.stringVal() + " </" + type + ">\n");
                break;
        }
    }
}
