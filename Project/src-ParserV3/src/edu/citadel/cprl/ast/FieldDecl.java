package edu.citadel.cprl.ast;

import edu.citadel.cprl.Token;
import edu.citadel.cprl.Type;

/**
 * The abstract syntax tree node for a field declaration.
 * A field declaration has the form `x : Integer;`.
 * A record declaration can contain multiple field declarations.
 */
public class FieldDecl extends Declaration
  {
    private int offset;   // offset for this field within the record; initialized
                          // to 0 but updated when constructing the record type

    // The size (number of bytes) associated with this field declaration,
    // which is simply the number of bytes associated with its type.
    private int size;

    /**
     * Construct a field declaration with its identifier and type.
     */
    public FieldDecl(Token fieldId, Type type)
      {
        super(fieldId, type);
        offset = 0;
        size   = type.size();
      }

    /**
     * Returns the offset for this field.
     */
    public int offset()
      {
        return offset;
      }

    /**
     * Sets the offset for this field.
     */
    public void setOffset(int offset)
      {
        this.offset = offset;
      }

    /**
     * Returns the size (number of bytes) for this field.
     */
    public int size()
      {
        return size;
      }

    @Override
    public void checkConstraints()
      {
        assert type() != Type.UNKNOWN && type() != Type.none && type() != Type.Address
            : "Invalid CPRL type in field declaration.";
      }
  }
