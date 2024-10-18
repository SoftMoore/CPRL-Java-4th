package edu.citadel.cprl.ast;

import edu.citadel.common.ConstraintException;

import edu.citadel.cprl.Token;
import edu.citadel.cprl.Type;
import edu.citadel.cprl.StringType;

/**
 * The abstract syntax tree node for a string type declaration.
 */
public class StringTypeDecl extends InitialDecl
  {
    private ConstValue capacity;

    /**
     * Construct a string type declaration with the specified type name and capacity.
     *
     * @param typeId   The identifier token containing the string type name.
     * @param capacity The maximum number of characters in the string.
     */
    public StringTypeDecl(Token typeId, ConstValue capacity)
      {
        super(typeId, new StringType(typeId.text(), capacity.intValue()));
        this.capacity = capacity;
      }

    @Override
    public void checkConstraints()
      {
// ...
      }
  }
