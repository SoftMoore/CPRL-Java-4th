package edu.citadel.cprl.ast;

import edu.citadel.common.ConstraintException;

import edu.citadel.cprl.Token;
import edu.citadel.cprl.Type;
import edu.citadel.cprl.ArrayType;

/**
 * The abstract syntax tree node for an array type declaration.
 */
public class ArrayTypeDecl extends InitialDecl
  {
    private ConstValue numElements;

    /**
     * Construct an array type declaration with its identifier, element type, and
     * number of elements.  Note that the index type is always Integer in CPRL.
     *
     * @param typeId      The token containing the identifier for the array.
     * @param elementType The type of elements in the array.
     * @param numElements The number of elements in the array.
     */
    public ArrayTypeDecl(Token typeId, Type elemType, ConstValue numElements)
      {
        super(typeId, new ArrayType(typeId.text(), numElements.intValue(), elemType));
        this.numElements = numElements;
      }

    @Override
    public void checkConstraints()
      {
// ...
      }
  }
