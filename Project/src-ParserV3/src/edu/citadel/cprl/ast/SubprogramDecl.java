package edu.citadel.cprl.ast;

import edu.citadel.cprl.Token;

import edu.citadel.cvm.Constants;

import java.util.Collections;
import java.util.List;

/**
 * Base class for CPRL procedures and functions.
 */
public abstract class SubprogramDecl extends Declaration
  {
    private List<ParameterDecl> paramDecls   = Collections.emptyList();
    private List<InitialDecl>   initialDecls = Collections.emptyList();
    private List<Statement>     statements   = Collections.emptyList();

    private int varLength = 0;        // # bytes of all declared variables
    private String subprogramLabel;   // label of address of first statement

    /**
     * Construct a subprogram declaration with the specified subprogram identifier.
     */
    public SubprogramDecl(Token subprogramId)
      {
        super(subprogramId);
        subprogramLabel = "_" + subprogramId;
      }

    /**
     * Returns the list of initial declarations for this subprogram.
     */
    public List<InitialDecl> initialDecls()
      {
        return initialDecls;
      }

    /**
     * Set the list of initial declarations for this subprogram.
     */
    public void setInitialDecls(List<InitialDecl> initialDecls)
      {
        this.initialDecls = initialDecls;
      }

    /**
     * Returns the list of parameter declarations for this subprogram.
     */
    public List<ParameterDecl> parameterDecls()
      {
        return paramDecls;
      }

    /**
     * Set the list of parameter declarations for this subprogram.
     */
    public void setParameterDecls(List<ParameterDecl> paramDecls)
      {
        this.paramDecls = paramDecls;
      }

    /**
     * Returns the list of statements for this subprogram.
     */
    public List<Statement> statements()
      {
        return statements;
      }

    /**
     * Set the list of statements for this subprogram.
     */
    public void setStatements(List<Statement> statements)
      {
        this.statements = statements;
      }

    /**
     * Returns the number of bytes required for all variables in the initial declarations.
     */
    protected int varLength()
      {
        return varLength;
      }

    /**
     * Returns the label associated with the first statement of the subprogram.
     */
    public String subprogramLabel()
      {
        return subprogramLabel;
      }

    /**
     * Returns the number of bytes for all parameters.
     */
    public int paramLength()
      {
        int paramLength = 0;

        for (ParameterDecl decl : paramDecls)
            paramLength += decl.size();

        return paramLength;
      }

    @Override
    public void checkConstraints()
      {
        for (ParameterDecl paramDecl : paramDecls)
            paramDecl.checkConstraints();

        for (InitialDecl decl : initialDecls)
            decl.checkConstraints();

        for (Statement statement : statements)
            statement.checkConstraints();
      }

    /**
    * Set the relative address (offset) for each variable and
    * parameter, and compute the length of all variables.
    */
    protected void setRelativeAddresses()
      {
        // initial relative address for a subprogram
        int currentAddr = Constants.BYTES_PER_CONTEXT;

        for (InitialDecl decl : initialDecls)
          {
            if (decl instanceof VarDecl varDecl)
              {
                // set relative address for single variable declarations
                for (SingleVarDecl svDecl : varDecl.singleVarDecls())
                  {
                    svDecl.setRelAddr(currentAddr);
                    currentAddr = currentAddr + svDecl.size();
                  }
              }
          }

        // compute length of all variables by subtracting initial relative address
        varLength = currentAddr - Constants.BYTES_PER_CONTEXT;

        // set relative address for parameters
        if (paramDecls.size() > 0)
          {
            // initial relative address for a subprogram parameter
            currentAddr = 0;

            // we need to process the parameter declarations in reverse order
            var iter = paramDecls.listIterator(paramDecls.size());
            while (iter.hasPrevious())
              {
                var decl = iter.previous();
                currentAddr = currentAddr - decl.size();
                decl.setRelAddr(currentAddr);
              }
          }
      }
  }
