import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

class JackTokenizer {

    File f;               // The Jack source file
    BufferedReader reader; // Reader to read the source file
    public String currentToken; // Holds the current token
    String currentLine;   // Holds the current line from the file
    String currentType;   // Type of the current token KEYWORD, SYMBOL, IDENTIFIER...
    boolean multilineComment; // Flag to track if inside a multiline comment

    // Constructor that initializes the tokenizer with a source file
    public JackTokenizer(File source) throws IOException {
        this.f = source;                             // Set the source file
        this.reader = new BufferedReader(new FileReader(source)); // Create a BufferedReader
        this.currentToken = "";                      // Initialize currentToken to empty
        this.currentLine = "";                       // Initialize currentLine to empty
        this.multilineComment = false;               // Initialize multilineComment to false
    }

    // Returns true if there are more tokens in the file
    public boolean hasMoreTokens() throws IOException {
        return (reader.ready() || !currentLine.equals(""));  // Check if reader is ready or the current line has content
    }
    
    // Retrieves the next line from the source file
    void getNextLine() throws IOException {
        currentLine = reader.readLine();  // Read the next line from the file
        
        // Handle ending of multiline comment
        if (currentLine.indexOf("*/") != -1) {
            currentLine = currentLine.substring(currentLine.indexOf("*/") + 2);  // Remove everything before "*/"
            multilineComment = false;  // Exit multiline comment mode
        } 
        // If we are inside a multiline comment, ignore the current line
        else if (multilineComment) {
            currentLine = "";  // Keep the line empty to skip it
        } 
        // Handle single-line comments (starting with "//")
        else if (currentLine.indexOf("//") != -1) {
            currentLine = currentLine.substring(0, currentLine.indexOf("//"));  // Remove everything after "//"
        } 
        // Handle the start of a multiline comment (starting with "/**")
        else if (currentLine.indexOf("/**") != -1) {
            currentLine = currentLine.substring(0, currentLine.indexOf("/**"));  // Remove everything before "/**"
            multilineComment = true;  // Enter multiline comment mode
        }
        
        // Trim the line to remove any leading or trailing whitespace
        currentLine = currentLine.trim();
    }

    // Advances to the next token in the current line
    public void advance() throws IOException {
        // Skip over empty lines or comments
        while (currentLine.length() == 0) {
            getNextLine();  // Get the next non-empty line
        }

        int counter = 1;  // Counter for character position in the current line
        StringBuilder token = new StringBuilder();  // To store the current token
        char c = currentLine.charAt(0);  // Get the first character of the current line
        char terminate = ' ';  // Termination character for string tokens

        // Check if the first character is a symbol and handle it
        if (TypesMap.contains(c + "")) {
            currentToken = c + "";  // Set current token to the symbol
            currentType = "SYMBOL";  // Mark token type as SYMBOL
            currentLine = currentLine.substring(counter);  // Remove symbol from the line
            currentLine = currentLine.trim();  // Trim the line
            return;  // Return to process the next token
        }
        // Handle string literals (starting and ending with '"')
        else if (c == '"') {
            terminate = '"';  // Set the termination character to '"'
            c = currentLine.charAt(counter++);  // Move past the opening quote
        }

        // Process the token character by character until the end of the token
        while (c != terminate && counter < currentLine.length() && !TypesMap.contains(c + "")) {
            token.append(c);  // Append the current character to the token
            c = currentLine.charAt(counter++);  // Move to the next character
        }

        currentToken = token.toString();  // Set the token to the string built so far
        
        // If it's a string constant, mark the type and update the remaining line
        if (terminate == '"') {
            currentType = "STRING_CONST";  // Mark token type as STRING_CONST
            currentLine = currentLine.substring(counter).trim();  // Update current line
        }
        else {
            // Handle integer constants, keywords, and identifiers
            char first = token.charAt(0);  // Get the first character of the token
            if (first >= 48 && first <= 57) {  // Check if it's a number (digit)
                currentType = "INT_CONST";  // Mark token type as INT_CONST
            }
            else if (TypesMap.contains(currentToken)) {  // Check if it's a keyword
                currentType = "KEYWORD";  // Mark token type as KEYWORD
            }
            else {
                currentType = "IDENTIFIER";  // Mark token type as IDENTIFIER
            }
            
            // Update the current line with the remaining unprocessed part
            currentLine = currentLine.substring(counter - 1).trim(); 
        }
    }

    // Returns the type of the current token (e.g., KEYWORD, SYMBOL, etc.)
    public String tokenType() {
        return currentType;
    }

    // Returns the current token if it's a keyword
    public String keyWord() {
        return currentToken;
    }
    
    // Returns the current token if it's a symbol
    public char symbol() {
        return currentToken.charAt(0);
    }

    // Returns the current token if it's an identifier
    public String identifier() {
        return currentToken;
    }

    // Returns the current token as an integer if it's an integer constant
    public int intVal() {
        return Integer.parseInt(currentToken);
    }

    // Returns the current token as a string if it's a string constant
    public String stringVal() {
        return currentToken;
    }

    // Closes the reader when done processing
    public void close() throws IOException {
        reader.close();
    }
}