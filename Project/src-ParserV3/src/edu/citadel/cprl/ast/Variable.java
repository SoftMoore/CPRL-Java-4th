package edu.citadel.cprl.ast;

import edu.citadel.common.CodeGenException;
import edu.citadel.common.ConstraintException;
import edu.citadel.common.Position;

import edu.citadel.cprl.ArrayType;
import edu.citadel.cprl.RecordType;
import edu.citadel.cprl.StringType;
import edu.citadel.cprl.ScopeLevel;
import edu.citadel.cprl.Type;

import java.util.List;

/**
 * The abstract syntax tree node for a variable, which is any named variable
 * that can appear on the left-hand side of an assignment statement.
 */
public class Variable extends Expression
  {
    private List<Expression> selectorExprs;
    private VariableDecl decl;   // nonstructural reference

    /**
     * Construct a variable with a reference to its declaration,
     * its position, and a list of selector expressions.
     */
    public Variable(VariableDecl decl, Position position, List<Expression> selectorExprs)
      {
        super(decl.type(), position);
        this.decl = decl;
        this.selectorExprs = selectorExprs;
      }

    /**
     * Construct a variable from a variable expression.
     */
    public Variable(VariableExpr varExpr)
      {
        this(varExpr.decl(), varExpr.position(), varExpr.selectorExprs());
      }

    /**
     * Returns the declaration for this variable.
     */
    public VariableDecl decl()
      {
        return decl;
      }

    /**
     * Returns the list of selector expressions for the variable.  Returns an
     * empty list if the variable is not an array, string, or record variable.
     */
    public List<Expression> selectorExprs()
      {
        return selectorExprs;
      }

    @Override
    public void checkConstraints()
      {
        try
          {
            assert decl instanceof SingleVarDecl || decl instanceof ParameterDecl;

            for (Expression expr : selectorExprs)
              {
                expr.checkConstraints();

                // Each selector expression must correspond to
                // an array type, a record type, or a string type.

                if (type() instanceof ArrayType arrayType)
                  {
                    // Applying the selector effectively changes the
                    // variable's type to the element type of the array.
                    setType(arrayType.elementType());

                    // check that the selector expression is not a field expression
// ...

                    // check that the type of the index expression is Integer
// ...
                  }
                else if (type() instanceof RecordType recType)
                  {
                    // check that the selector expression is a field expression
// ...

                    // Applying the selector effectively changes the
                    // variable's type to the type of the field.
                    var fieldExpr = (FieldExpr) expr;
                    var fieldId   = fieldExpr.fieldId();

                    if (recType.containsField(fieldId.text()))
                      {
                        var fieldDecl = recType.get(fieldId.text());
                        fieldExpr.setFieldDecl(fieldDecl);
                        setType(fieldDecl.type());
                      }
                    else
                      {
                        var errorMsg = "\"" + fieldId.text()
                                     + "\" is not a valid field name for " + recType + ".";
                        throw error(fieldId.position(), errorMsg);
                      }
                  }
                else if (type() instanceof StringType)
                  {
                    // Selector can be field expression .length (type Integer)
                    // or an index expression for the characters (type Char).

                    if (expr instanceof FieldExpr fieldExpr)
                      {
                        // Applying length field selector effectively changes
                        // the variable's type to Integer.
                        setType(Type.Integer);

                        // check that the field identifier is "length"
                        var fieldId = fieldExpr.fieldId();
                        if (!fieldId.text().equals("length"))
                          {
                            var errorMsg = "Field name must be \"length\" for strings.";
                            throw error(fieldId.position(), errorMsg);
                          }
                      }
                    else
                      {
                        // Applying an index selector effectively changes
                        // the variable's type to Char.
                        setType(Type.Char);

                        // must be an index expression; check that the type is Integer
                        if (expr.type() != Type.Integer)
                          {
                            var errorMsg = "Index expression must have type Integer.";
                            throw error(expr.position(), errorMsg);
                          }
                      }
                  }
                else
                  {
                    var errorMsg = "Selector expression not allowed for variable of type "
                                 + type();
                    throw error(expr.position(), errorMsg);
                  }
              }
          }
        catch (ConstraintException e)
          {
            errorHandler().reportError(e);
          }
      }

    @Override
    public void emit() throws CodeGenException
      {
        if (decl instanceof ParameterDecl pDecl && pDecl.isVarParam())
          {
            // address of actual parameter is value of var parameter
            emit("LDLADDR " + decl.relAddr());
            emit("LOADW");
          }
        else if (decl.scopeLevel() == ScopeLevel.GLOBAL)
            emit("LDGADDR " + decl.relAddr());
        else
            emit("LDLADDR " + decl.relAddr());

        var type = decl.type();

        // For an array, record, or string, at this point the base address is on the
        // top of the stack.  We need to replace it by the sum base address + offset
        for (Expression expr : selectorExprs)
          {
            if (type instanceof ArrayType arrayType)
              {
                expr.emit();   // emit the index

                // multiply by size of array element type to get offset
// ...

                // Note: No code to perform bounds checking for the index to
                // ensure that the index is >= 0 and < number of elements.

                emit("ADD");   // add offset to the base address

                type = arrayType.elementType();
              }
            else if (type instanceof RecordType)
              {
                var fieldExpr = (FieldExpr) expr;

                if (fieldExpr.fieldDecl().offset() != 0)
                  {
                    // add offset to the base address
// ...
                  }

                type = fieldExpr.fieldDecl().type();
              }
            else if (type instanceof StringType)
              {
                if (expr instanceof FieldExpr)
                  {
                    // The only allowed field expression for strings is length, which
                    // is at offset 0; we don't need to emit code for the offset.
                  }
                else   // selector expression must be an index expression
                  {
                    // skip over length (type Integer)
                    emit("LDCINT " + Type.Integer.size());
                    emit("ADD");

                    expr.emit();   // emit index expression

                    // multiply by size of type Char to get offset
                    emit("LDCINT " + Type.Char.size());
                    emit("MUL");

                    emit("ADD");   // add offset to the base address

                    // Note: No code to perform bounds checking for the index to
                    // ensure that the index is >= 0 and < string capacity.
                  }
              }
          }
      }
  }
