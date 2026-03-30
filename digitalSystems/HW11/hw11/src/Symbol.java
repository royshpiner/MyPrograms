public class Symbol {

    // Enum to define the kind of variable (STATIC, FIELD, ARG, VAR, NONE)
    public static enum KIND {STATIC, FIELD, ARG, VAR, NONE}; 
    
    private String type;  // The type of the variable (e.g., int, boolean, etc.)
    private KIND kind;    // The kind of the variable (STATIC, FIELD, ARG, VAR)
    private int index;    // The index of the variable (position in the respective scope)

    /*
     * Constructor to create a Symbol object with a specific type, kind, and index.
     * 
     * @param type  the type of the variable (e.g., int, boolean, etc.)
     * @param kind  the kind of the variable (STATIC, FIELD, ARG, VAR)
     * @param index the index of the variable in the symbol table
     */
    public Symbol(String type, KIND kind, int index) {
        this.type = type;  // Initialize the type of the variable
        this.kind = kind;  // Initialize the kind of the variable
        this.index = index;  // Initialize the index of the variable
    }

    /*
     * Returns the type of the symbol (e.g., int, boolean, etc.)
     * 
     * @return the type of the symbol
     */
    public String getType() {
        return type;
    }

    /*
     * Returns the kind of the symbol (STATIC, FIELD, ARG, VAR).
     * 
     * @return the kind of the symbol
     */
    public KIND getKind() {
        return kind;
    }

    /*
     * Returns the index of the symbol (position in the respective scope).
     * 
     * @return the index of the symbol
     */
    public int getIndex() {
        return index;
    }

    /*
     * Provides a string representation of the symbol, including its type, kind, and index.
     * 
     * @return a string representation of the symbol object
     */
    public String toString() {
        return "Symbol{" + "type='" + type + '\'' + ", kind=" + kind + ", index=" + index + '}';
    }
}