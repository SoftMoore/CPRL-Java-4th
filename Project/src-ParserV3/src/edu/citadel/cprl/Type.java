package edu.citadel.cprl;

import edu.citadel.cvm.Constants;

/**
 * This class encapsulates the language types for the programming language CPRL.
 * Type sizes are initialized to values appropriate for the CPRL virtual machine.
 */
public class Type
  {
    private String typeName;
    private int size;

    // predefined types
    public static final Type Boolean = new Type("Boolean", Constants.BYTES_PER_BOOLEAN);
    public static final Type Integer = new Type("Integer", Constants.BYTES_PER_INTEGER);
    public static final Type Char    = new Type("Char",    Constants.BYTES_PER_CHAR);

    // an address of the target machine
    public static final Type Address = new Type("Address", Constants.BYTES_PER_ADDRESS);

    // compiler-internal types
    public static final Type UNKNOWN = new Type("UNKNOWN");
    public static final Type none    = new Type("none");

    /**
     * Construct a new type with the specified type name and size.
     */
    protected Type(String typeName, int size)
      {
        this.typeName = typeName;
        this.size = size;
      }

    /**
     * Construct a new type with the specified type name.
     * Size is initially set to 0.
     */
    protected Type(String typeName)
      {
        this(typeName, 0);
      }

    /**
     * Returns the number of machine addressable units
     * (e.g., bytes or words) for this type.
     */
    public int size()
      {
        return size;
      }

    /**
     * Sets the number of machine addressable units
     * (e.g., bytes or words) for this type.
     */
    public void setSize(int size)
      {
        this.size = size;
      }

    /**
     * Returns true if and only if this type is a scalar type.
     * The scalar types in CPRL are Integer, Boolean, Char.
     */
    public boolean isScalar()
      {
        return this.equals(Integer) || this.equals(Boolean) || this.equals(Char);
      }

    /**
     * Returns true if and only if this type is a composite type.  The
     * composite types in CPRL are array types, record types, and string types.
     */
    public boolean isComposite()
      {
        return !isScalar();
      }

    /**
     * String literals contain quotes and possibly escape characters,
     * so we need to compute the actual capacity for a string literal.
     */
    private static int capacityOf(String literalText)
      {
        // subtract 2 for the double quotes at each end
        int capacity = literalText.length() - 2;

        // assume that the literal text was parsed correctly by the compiler
        int i = 1;
        while (i <= literalText.length() - 3)
          {
            if (literalText.charAt(i) == '\\')
              {
                --capacity;   // subtract for an escaped character
                ++i;          // skip over the escaped character
              }

            ++i;
          }

        return capacity;
      }

    /**
     * Returns the type of a literal symbol.  For example, if the
     * symbol is an intLiteral, then Type.Integer is returned.
     * Returns UNKNOWN if the symbol is not a valid literal symbol.
     */
    public static Type typeOf(Token literal)
      {
        var symbol = literal.symbol();
        if (symbol == Symbol.intLiteral)
            return Type.Integer;
        else if (symbol == Symbol.charLiteral)
            return Type.Char;
        else if (symbol == Symbol.trueRW || symbol == Symbol.falseRW)
            return Type.Boolean;
        else if (symbol == Symbol.stringLiteral)
            return new StringType(capacityOf(literal.text()));
        else
            return Type.UNKNOWN;
      }

    /**
     * Returns the name for this type.
     */
    @Override
    public String toString()
      {
        return typeName;
      }

    @Override
    public int hashCode()
      {
        return typeName.hashCode();
      }

    @Override
    public boolean equals(Object obj)
      {
        return (this == obj)
            || (obj instanceof Type other && typeName.equals(other.typeName));
      }
  }
