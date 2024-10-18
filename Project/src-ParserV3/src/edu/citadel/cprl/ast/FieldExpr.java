package edu.citadel.cprl.ast;

import edu.citadel.cprl.Token;
import edu.citadel.cprl.Type;

/**
 * The abstract syntax tree node for a field expression, which has the form
 * ".id".  A field expression is used as a selector for a record field and
 * for a string length.  The value of a field expression has type integer.
 * For a record field, the value is the field offset.  For a string, the
 * values is always 0 since string length is always at offset 0.
 */
public class FieldExpr extends Expression
  {
    private Token fieldId;

    // Note: value for fieldDecl is assigned in Variable.checkConstraints()
    private FieldDecl fieldDecl;   // nonstructural reference

    /**
     * Construct a field expression with its field name.
     */
    public FieldExpr(Token fieldId)
      {
        super(Type.Integer, fieldId.position());
        this.fieldId = fieldId;
      }

    /**
     * Returns the field identifier token for this field expression.
     */
    public Token fieldId()
      {
        return fieldId;
      }

    /**
     * Returns the field declaration for this field expression.
     */
    public FieldDecl fieldDecl()
      {
        return fieldDecl;
      }

    /**
     * Set the field declaration for this field expression.
     */
    public void setFieldDecl(FieldDecl fieldDecl)
      {
        this.fieldDecl = fieldDecl;
      }

    @Override
    public void checkConstraints()
      {
        // nothing to check
      }

    @Override
    public void emit()
      {
        assert fieldDecl.offset() >= 0 : "Invalid value for field offset.";
// ...
      }
  }
