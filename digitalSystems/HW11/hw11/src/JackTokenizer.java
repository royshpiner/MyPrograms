import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JackTokenizer {
 
    
    private String token; 
    public static enum TYPE {KEYWORD, SYMBOL, IDENTIFIER, INT_CONST, STRING_CONST, NONE}; // tokens possible
    private TYPE tokenType; 
    private int pointer; 
    private ArrayList<String> tokens; 
    private static Pattern tokenPatterns; 
    private static String keywordRegExp; //regexps
    private static String symbolRegExp;
    private static String intRegExp;
    private static String stringRegExp;
    private static String idRegExp;
    public static enum KEYWORD {CLASS, METHOD, FUNCTION, CONSTRUCTOR, INT, BOOLEAN, CHAR, VOID,
        VAR, STATIC, FIELD, LET, DO, IF, ELSE, WHILE, RETURN, TRUE, FALSE, NULL, THIS};
    private static HashMap<String, KEYWORD> keyword = new HashMap<>(); // Mapping of keywords
    private static HashSet<Character> operators = new HashSet<>(); // Set of valid operators
    // Static initializer to populate the keyword map and operator set
    static {
        keyword.put("class", KEYWORD.CLASS);
        keyword.put("constructor", KEYWORD.CONSTRUCTOR);
        keyword.put("function", KEYWORD.FUNCTION);
        keyword.put("method", KEYWORD.METHOD);
        keyword.put("field", KEYWORD.FIELD);
        keyword.put("static", KEYWORD.STATIC);
        keyword.put("var", KEYWORD.VAR);
        keyword.put("int", KEYWORD.INT);
        keyword.put("char", KEYWORD.CHAR);
        keyword.put("boolean", KEYWORD.BOOLEAN);
        keyword.put("void", KEYWORD.VOID);
        keyword.put("true", KEYWORD.TRUE);
        keyword.put("false", KEYWORD.FALSE);
        keyword.put("null", KEYWORD.NULL);
        keyword.put("this", KEYWORD.THIS);
        keyword.put("let", KEYWORD.LET);
        keyword.put("do", KEYWORD.DO);
        keyword.put("if", KEYWORD.IF);
        keyword.put("else", KEYWORD.ELSE);
        keyword.put("while", KEYWORD.WHILE);
        keyword.put("return", KEYWORD.RETURN);
        operators.add('+');
        operators.add('-');
        operators.add('*');
        operators.add('/');
        operators.add('&');
        operators.add('|');
        operators.add('<');
        operators.add('>');
        operators.add('=');
    }

    /**
     * Opens the input .jack file and prepares it for tokenization.
     * It removes comments and unnecessary whitespace before processing.
     */
    public JackTokenizer(File file) {
        try {
            Scanner scan = new Scanner(file);
            String preprocessed = "";
            String line = "";

            // Read file line by line, removing inline comments and trimming spaces
            while(scan.hasNext()){
                line = noComments(scan.nextLine()).trim();
                if (!line.isEmpty()) {
                    preprocessed += line + "\n";
                }
            }
            // Remove block comments and initialize regular expressions
            preprocessed = noBlockComments(preprocessed).trim();
            initRegs();

            // Tokenize the processed input
            Matcher match = tokenPatterns.matcher(preprocessed);
            tokens = new ArrayList<>();
            pointer = 0;
            while (match.find()) {
                tokens.add(match.group());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        token = "";
        tokenType = TYPE.NONE;
    }

    /**
     * Initializes regular expressions needed for token matching.
     */
    private void initRegs() {
        keywordRegExp = "";
        for (String seg : keyword.keySet()) {
            keywordRegExp += seg + "|";
        }
        symbolRegExp = "[\\&\\*\\+\\(\\)\\.\\/\\,\\-\\]\\;\\~\\}\\|\\{\\>\\=\\[\\<]";
        intRegExp = "[0-9]+";
        stringRegExp = "\"[^\"\n]*\"";
        idRegExp = "[a-zA-Z_]\\w*";
        tokenPatterns = Pattern.compile(idRegExp + "|" + keywordRegExp + symbolRegExp + "|" + intRegExp + "|" + stringRegExp);
    }

    /**
     * Checks if there are more tokens in the input.
     */
    public boolean hasMoreTokens() {
        return (pointer < tokens.size());
    }

    /**
     * Advances to the next token and determines its type.
     * Throws an exception if no tokens remain.
     */
    public void advance() {
        if (hasMoreTokens()) {
            token = tokens.get(pointer);
            pointer++;
        } else {
            throw new IllegalStateException("No more tokens available.");
        }

        if (token.matches(keywordRegExp)) {
            tokenType = TYPE.KEYWORD;
        } else if (token.matches(symbolRegExp)) {
            tokenType = TYPE.SYMBOL;
        } else if (token.matches(intRegExp)) {
            tokenType = TYPE.INT_CONST;
        } else if (token.matches(stringRegExp)) {
            tokenType = TYPE.STRING_CONST;
        } else if (token.matches(idRegExp)) {
            tokenType = TYPE.IDENTIFIER;
        } else {
            throw new IllegalArgumentException("Invalid token: " + token);
        }
    }

    /**
     * Returns the current token.
     */
    public String getCurrentToken() {
        return token;
    }

    /**
     * Returns the type of the current token.
     */
    public TYPE tokenType() {
        return tokenType;
    }

    /**
     * Returns the keyword of the current token, if applicable.
     */
    public KEYWORD keyWord() {
        if (tokenType == TYPE.KEYWORD) {
            return keyword.get(token);
        } else {
            throw new IllegalStateException("Current token is not a keyword.");
        }
    }

    /**
     * Returns the symbol of the current token, if applicable.
     */
    public char symbol() {
        if (tokenType == TYPE.SYMBOL) {
            return token.charAt(0);
        } else {
            throw new IllegalStateException("Current token is not a symbol.");
        }
    }

    /**
     * Returns the identifier of the current token, if applicable.
     */
    public String identifier() {
        if (tokenType == TYPE.IDENTIFIER) {
            return token;
        } else {
            throw new IllegalStateException("Current token is not an identifier.");
        }
    }

    /**
     * Returns the integer value of the current token, if applicable.
     */
    public int intVal() {
        if (tokenType == TYPE.INT_CONST) {
            return Integer.parseInt(token);
        } else {
            throw new IllegalStateException("Current token is not an integer constant.");
        }
    }

    /**
     * Returns the string value of the current token (without surrounding quotes), if applicable.
     */
    public String stringVal() {
        if (tokenType == TYPE.STRING_CONST) {
            return token.substring(1, token.length() - 1);
        } else {
            throw new IllegalStateException("Current token is not a string constant.");
        }
    }

    /**
     * Moves the pointer back by one token.
     */
    public void pointerBack() {
        if (pointer > 0) {
            pointer--;
            token = tokens.get(pointer);
        }
    }

    /**
     * Checks if the current token is an operator.
     */
    public boolean isOp() {
        return operators.contains(symbol());
    }

    /**
     * Removes inline comments (text after "//") from a string.
     */
    public static String noComments(String str) {
        int position = str.indexOf("//");
        if (position != -1)
            str = str.substring(0, position);
        return str;
    }

    /**
     * Removes block comments from a string.
     */
    public static String noBlockComments(String str) {
        int startIndex = str.indexOf("/*");
        if (startIndex == -1) 
            return str;
        String result = str;
        int endIndex = str.indexOf("*/");
        while (startIndex != -1) {
            if (endIndex == -1)
                return str.substring(0, startIndex - 1);
            result = result.substring(0, startIndex) + result.substring(endIndex + 2);
            startIndex = result.indexOf("/*");
            endIndex = result.indexOf("*/");
        }
        return result;
    }
}