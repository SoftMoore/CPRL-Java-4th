package edu.citadel.cprl.ast;

import edu.citadel.common.CodeGenException;
import edu.citadel.common.ConstraintException;

import edu.citadel.cprl.Type;

/**
 * The abstract syntax tree node for a for-loop statement.
 */
public class ForLoopStmt extends LoopStmt
  {
    private Variable   loopVar;
    private Expression rangeStart;
    private Expression rangeEnd;

    /**
     * Construct a for-loop statement with the specified
     * loop variable and range expressions.
     */
    public ForLoopStmt(Variable loopVar, Expression rangeStart, Expression rangeEnd)
      {
        this.loopVar    = loopVar;
        this.rangeStart = rangeStart;
        this.rangeEnd   = rangeEnd;
      }

    @Override
    public void checkConstraints()
      {
        assert loopVar    != null && loopVar.type() == Type.Integer;
        assert rangeStart != null && rangeEnd != null;

        try
          {
            loopVar.checkConstraints();
            rangeStart.checkConstraints();
            rangeEnd.checkConstraints();
            statement().checkConstraints();

            if (rangeStart.type() != Type.Integer)
              {
                var errorMsg = "The first expression of a range should have type Integer.";
                throw error(rangeStart.position(), errorMsg);
              }

            if (rangeEnd.type() != Type.Integer)
              {
                var errorMsg = "The second expression of a range should have type Integer.";
                throw error(rangeEnd.position(), errorMsg);
              }

            if (rangeStart instanceof ConstValue val1 && rangeEnd instanceof ConstValue val2)
              {
                if (val1.intValue() > val2.intValue())
                  {
                    var errorMsg = "Invalid range for loop variable.";
                    throw error(val2.position(), errorMsg);
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
        // initialize loop variable
        loopVar.emit();
        rangeStart.emit();
        emitStoreInst(Type.Integer);

        emitLabel(L1);

        // check that value of loop variable is <= range end
        var loopVarExpr = new VariableExpr(loopVar);
        loopVarExpr.emit();
        rangeEnd.emit();
        emit("BG " +  L2);

        statement().emit();

        // increment loop variable
        loopVar.emit();
        loopVar.emit();
        emit("LOADW");
        emit("LDCINT 1");
        emit("ADD");
        emit("STOREW");

        emit("BR " + L1);
        emitLabel(L2);
      }
  }
