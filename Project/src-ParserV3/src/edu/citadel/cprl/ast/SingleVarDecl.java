package edu.citadel.cprl.ast;

import edu.citadel.common.CodeGenException;
import edu.citadel.common.ConstraintException;
import edu.citadel.common.InternalCompilerException;

import edu.citadel.cprl.*;

/**
 * The abstract syntax tree node for a single variable declaration.
 * A single variable declaration has the form
 * <code> var x : Integer [ ":=" initializer ]; </code>
 *
 * Note: A variable declaration where more than one variable is declared
 * is simply a container for multiple single variable declarations.
 */
public final class SingleVarDecl extends InitialDecl implements VariableDecl
  {
    private Initializer initializer;
    private ScopeLevel scopeLevel;
    private int relAddr;   // relative address for variable
                           // introduced by this declaration

    /**
     * Construct a single variable declaration with its identifier,
     * type, initial value, and scope level.
     */
    public SingleVarDecl(Token identifier, Type varType,
                         Initializer initializer, ScopeLevel scopeLevel)
      {
        super(identifier, varType);
        this.initializer = initializer;
        this.scopeLevel  = scopeLevel;
      }

    /**
     * Returns the size (number of bytes) associated with this single variable
     * declaration, which is simply the number of bytes associated with its type.
     */
    public int size()
      {
        return type().size();
      }

    @Override
    public ScopeLevel scopeLevel()
      {
        return scopeLevel;
      }

    /**
     * Sets the relative address for this declaration. <br>
     * Note: This method should be called before calling method relAddr().
     */
    public void setRelAddr(int relAddr)
      {
        this.relAddr = relAddr;
      }

    /**
     * Returns the relative address (offset) associated with this single
     * variable declaration.
     */
    public int relAddr()
      {
        return relAddr;
      }

    @Override
    public void checkConstraints()
      {
        try
          {
            // check constraints only if initializer is not empty
            if (!initializer.isEmpty())
                checkInitializer(type(), initializer);
          }
        catch (ConstraintException e)
          {
            errorHandler().reportError(e);
          }
      }

    private void checkInitializer(Type type, Initializer initializer)
        throws ConstraintException
      {
        if (type.isScalar() || type instanceof StringType)
          {
            // initializer must be a ConstValue of the appropriate type
            if (initializer instanceof ConstValue constValue)
              {
                // check that the initializer has the correct type
                if (!matchTypes(type, constValue))
                  {
                    var errorMsg = "Type mismatch for variable initialization.";
                    throw error(initializer.position(), errorMsg);
                  }
              }
            else
              {
                var errorMsg = "Initializer must be a constant value.";
                throw error(initializer.position(), errorMsg);
              }
          }
        else if (type instanceof ArrayType arrayType)
          {
            // must be a composite initializer with correct number of values
            if (initializer instanceof CompositeInitializer compositeInitializer)
              {
                var initializers = compositeInitializer.initializers();
                if (initializers.size() != arrayType.numElements())
                  {
                    var errorMsg = "Incorrect number of initializers for array type "
                                 + arrayType + ".";
                    throw error(initializer.position(), errorMsg);
                  }

                for (Initializer i : initializers)
                    checkInitializer(arrayType.elementType(), i);
              }
            else
              {
                var errorMsg = "Initializer for an array must be composite.";
                throw error(initializer.position(), errorMsg);
              }
          }
        else if (type instanceof RecordType recordType)
          {
            // initializer must be composite with correct number of values and types
            if (initializer instanceof CompositeInitializer compositeInitializer)
              {
                var initializers = compositeInitializer.initializers();
                var fieldDecls   = recordType.fieldDecls();
                if (initializers.size() != fieldDecls.size())
                  {
                    var errorMsg = "Incorrect number of initializers for record type "
                                 + recordType + ".";
                    throw error(initializer.position(), errorMsg);
                  }

                for (int i = 0; i < initializers.size(); ++i)
                    checkInitializer(fieldDecls.get(i).type(), initializers.get(i));
              }
            else
              {
                var errorMsg = "Initializer for a record must be composite.";
                throw error(initializer.position(), errorMsg);
              }
          }
      }

    /**
     * Adds padding to a composite initializer if needed.
     */
    private void addPadding(Type type, Initializer initializer)
      {
        if (type instanceof ArrayType arrayType)
          {
            // initializer must be a composite with correct number of values and types
            var compositeInitializer = (CompositeInitializer) initializer;
            var initializers = compositeInitializer.initializers();
            var elementType  = arrayType.elementType();

            if (elementType instanceof ArrayType || elementType instanceof RecordType)
              {
                // each initializer must also be composite
                for (Initializer i : initializers)
                    addPadding(elementType, i);
              }
            else if (elementType instanceof StringType stringType)
              {
                // need to add padding only for strings
                // i is index into the array, j is index into the composite initializer
                int i = 0, j = 0;
                while (i < arrayType.numElements())
                  {
                    // initializer must be a constant value with string type
                    var constValue = (ConstValue) initializers.get(j);
                    assert matchTypes(stringType, constValue);
                  if (stringType.size() > constValue.size())
                      {
                        var numBytes = stringType.size() - constValue.size();
                        initializers.add(++j, new Padding(numBytes));
                      }

                    ++i;
                    ++j;
                  }
              }
          }
        else if (type instanceof RecordType recordType)
          {
            // initializer must be a composite with correct number of values and types
            var compositeInitializer = (CompositeInitializer) initializer;
            var initializers = compositeInitializer.initializers();

            var fieldDecls = recordType.fieldDecls();
            int i = 0, j = 0;
            while (i < fieldDecls.size())
              {
                var fieldDecl = fieldDecls.get(i);
                var fieldDeclType = fieldDecl.type();
                var fieldInitializer = initializers.get(j);

                if (fieldDeclType instanceof ArrayType || fieldDeclType instanceof RecordType)
                  {
                    // fieldInitializer must be composite
                    addPadding(fieldDeclType, fieldInitializer);
                  }
                else if (fieldDeclType instanceof StringType stringType)
                  {
                    // need to add padding only for strings
                    // initializer must be a constant value with string type
                    var constValue = (ConstValue) initializers.get(j);
                    assert matchTypes(stringType, constValue);

                    if (stringType.size() > constValue.size())
                      {
                        var numBytes = stringType.size() - constValue.size();
                        initializers.add(++j, new Padding(numBytes));
                      }
                  }

                ++i;
                ++j;
              }
          }
      }

    @Override
    public void emit() throws CodeGenException
      {
        // emit code only if the initializer is not empty
        if (!initializer.isEmpty())
          {
            // load the address of the variable
            if (scopeLevel == ScopeLevel.GLOBAL)
                emit("LDGADDR " + relAddr);
            else
                emit("LDLADDR " + relAddr);

            if (initializer instanceof ConstValue constValue)
              {
                constValue.emit();
                emitStoreInst(constValue.type());
              }
            else if (initializer instanceof CompositeInitializer compositeInitializer)
              {
                addPadding(type(), compositeInitializer);
                compositeInitializer.emit();
                emitStoreInst(compositeInitializer.size());
              }
            else
              {
                var errorMsg = "Unexpected initializer type.";
                throw new InternalCompilerException(position(), errorMsg);
              }
          }
      }
  }
