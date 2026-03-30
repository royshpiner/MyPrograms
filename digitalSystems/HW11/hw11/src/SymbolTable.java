import java.util.HashMap;
public class SymbolTable {
    // HashMap to store class-level variables (static and field)
    private HashMap<String, Symbol> classtable;
    
    // HashMap to store subroutine-level variables (arguments and local variables)
    private HashMap<String, Symbol> methodtable;
    
    // HashMap to keep track of variable indices for each symbol kind (ARG, FIELD, STATIC, VAR)
    private HashMap<Symbol.KIND, Integer> indeices;

    /*
     * Constructor to initialize the symbol table.
     * Sets up class-level and method-level variable tables, and initializes the index values for each variable kind.
     */
    public SymbolTable() {
        classtable = new HashMap<String, Symbol>();  // Initialize class symbol table
        methodtable = new HashMap<String, Symbol>();  // Initialize method symbol table
        indeices = new HashMap<Symbol.KIND, Integer>();  // Initialize index tracker for variable kinds
        
        // Set initial index values for ARG, FIELD, STATIC, and VAR to 0
        indeices.put(Symbol.KIND.ARG, 0);
        indeices.put(Symbol.KIND.FIELD, 0);
        indeices.put(Symbol.KIND.STATIC, 0);
        indeices.put(Symbol.KIND.VAR, 0);
    }

    /*
     * Starts a new subroutine scope by resetting the subroutine's symbol table.
     * Resets the indices for ARG and VAR kinds to 0 for the new subroutine.
     */
    public void startSubroutine() {
        methodtable.clear();  // Clear the method-level symbol table
        indeices.put(Symbol.KIND.VAR, 0);  // Reset index for local variables
        indeices.put(Symbol.KIND.ARG, 0);  // Reset index for arguments
    }

    /*
     * Defines (adds to the table) a new variable with the given name, type, and kind.
     * If the variable is ARG or VAR, it is added to the method-level table; if it is STATIC or FIELD, it is added to the class-level table.
     * The index for the given kind is assigned and incremented.
     */
    public void define(String strName, String strType, Symbol.KIND strKind) {
        if (strKind == Symbol.KIND.ARG || strKind == Symbol.KIND.VAR) {
            // For ARG or VAR variables, assign to the method-level table
            int index = indeices.get(strKind);  // Get current index for the kind
            Symbol symbol = new Symbol(strType, strKind, index);  // Create a new symbol for the variable
            indeices.put(strKind, index + 1);  // Increment the index for the next variable of this kind
            methodtable.put(strName, symbol);  // Add the symbol to the method table
        } else if (strKind.equals(Symbol.KIND.STATIC) || strKind.equals(Symbol.KIND.FIELD)) {
            // For STATIC or FIELD variables, assign to the class-level table
            int index = indeices.get(strKind);  // Get current index for the kind
            Symbol symbol = new Symbol(strType, strKind, index);  // Create a new symbol for the variable
            indeices.put(strKind, index + 1);  // Increment the index for the next variable of this kind
            classtable.put(strName, symbol);  // Add the symbol to the class table
        }
    }

    /*
     * Returns the number of variables of the given kind already defined in the table.
     * @param strKind The kind of variable (ARG, FIELD, STATIC, VAR)
     * @return The count of variables of that kind
     */
    public int varCount(Symbol.KIND strKind) {
        return indeices.get(strKind);  // Return the count of variables for the given kind
    }

    /*
     * Returns the kind of the named variable.
     * If the variable is not found, returns NONE.
     * @param name The name of the variable
     * @return The kind of the variable (e.g., ARG, FIELD, STATIC, VAR, NONE)
     */
    public Symbol.KIND kindOf(String name) {
        Symbol symbol = Up(name);  // Look up the symbol by name
        if (symbol != null)
            return symbol.getKind();  // Return the kind if found
        return Symbol.KIND.NONE;  // Return NONE if the symbol is not found
    }

    /*
     * Returns the type of the named variable.
     * If the variable is not found, returns an empty string.
     * @param name The name of the variable
     * @return The type of the variable, or an empty string if not found
     */
    public String typeOf(String name) {
        Symbol symbol = Up(name);  // Look up the symbol by name
        if (symbol != null)
            return symbol.getType();  // Return the type if found
        return "";  // Return empty string if the symbol is not found
    }

    /*
     * Returns the index of the named variable.
     * If the variable is not found, returns -1.
     * @param name The name of the variable
     * @return The index of the variable, or -1 if not found
     */
    public int indexOf(String name) {
        Symbol symbol = Up(name);  // Look up the symbol by name
        if (symbol != null)
            return symbol.getIndex();  // Return the index if found
        return -1;  // Return -1 if the symbol is not found
    }

    /*
     * Helper method to look up a symbol by its name in both the class and method tables.
     * @param name The name of the variable
     * @return The symbol if found, or null if not found
     */
    private Symbol Up(String name) {
        // First check the class-level symbol table
        if (classtable.get(name) != null) {
            return classtable.get(name);
        }
        // If not found in the class table, check the method-level symbol table
        else if (methodtable.get(name) != null) {
            return methodtable.get(name);
        }
        // Return null if the symbol is not found in either table
        else {
            return null;
        }
    }
}