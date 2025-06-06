package edu.citadel.assembler.optimize;

import edu.citadel.assembler.Symbol;
import edu.citadel.assembler.Token;
import edu.citadel.assembler.ast.*;

import java.util.List;

/**
 * Improves branching instructions where an unconditional branch is used
 * to branch over a conditional branch (which can occur when an exit
 * statement appears at the end of a loop).  For example, the following code
 * <code>
     BZ L1
     BR L0
  L1: ...
 * </code>
 * can be improved as follows:
 * <code>
     BNZ L0
  L1: ...
 * </code>
 */
public class BranchingReduction implements Optimization
  {
    @Override
    public void optimize(List<Instruction> instructions, int instNum)
      {
        // quick check that there are at least 3 instructions remaining
        if (instNum > instructions.size() - 3)
            return;

        var instruction0 = instructions.get(instNum);
        var instruction1 = instructions.get(instNum + 1);
        var instruction2 = instructions.get(instNum + 2);

        var symbol0 = instruction0.opcode().symbol();
        var symbol1 = instruction1.opcode().symbol();

        // make sure that we have a conditional branch followed by BR
        // instruction, and that the label argument for the conditional
        // branch immediately follows the BR instruction.
        if (isConditionalBranch(symbol0) && symbol1 == Symbol.BR)
          {
            var inst0 = (InstructionOneArg) instruction0;
            var inst1 = (InstructionOneArg) instruction1;

            if (containsLabel(instruction2.labels(), inst0.arg()))
              {
                // combine labels for instructions 0 and 1
                var labels = combineLabels(inst0.labels(), inst1.labels());

                // get argument label from inst1
                var arg = inst1.arg();

                // make the new branch instruction
                var branchInst = makeDualBranchInst(labels, symbol0, arg);
                instructions.set(instNum, branchInst);

                // remove the unconditional branch instruction
                instructions.remove(instNum + 1);
              }
          }
      }

    /**
     * Returns true if the symbol is a conditional branch; that is,
     * if the symbol is one of BG, BGE, BL, BLE, BE, BNe, BZ, BNZ.
     */
    private static boolean isConditionalBranch(Symbol s)
      {
        return s == Symbol.BG || s == Symbol.BGE
            || s == Symbol.BL || s == Symbol.BLE
            || s == Symbol.BE || s == Symbol.BNE
            || s == Symbol.BZ || s == Symbol.BNZ;
      }

    /**
     * Returns dual conditional branch instruction with the specified instruction
     * labels and label argument.  For example, if parameter s has the value
     * Symbol.BG, then an instruction of type InstructionBLE is returned.
     */
    private static Instruction makeDualBranchInst(List<Token> labels, Symbol s, Token labelArg)
      {
        return switch (s)
          {
            case BG  -> new InstructionBLE(labels, new Token(Symbol.BLE), labelArg);
            case BGE -> new InstructionBL(labels,  new Token(Symbol.BL),  labelArg);
            case BL  -> new InstructionBGE(labels, new Token(Symbol.BGE), labelArg);
            case BLE -> new InstructionBG(labels,  new Token(Symbol.BG),  labelArg);
            case BE  -> new InstructionBNE(labels, new Token(Symbol.BNE), labelArg);
            case BNE -> new InstructionBE(labels,  new Token(Symbol.BE),  labelArg);
            case BZ  -> new InstructionBNZ(labels, new Token(Symbol.BNZ), labelArg);
            case BNZ -> new InstructionBZ(labels,  new Token(Symbol.BZ),  labelArg);
            default  -> throw new IllegalArgumentException("Illegal conditional branch instruction " + s);
          };
      }

    /**
     * Returns true if the text of the second parameter label equals the
     * text of one of the labels in the list.  Returns false otherwise.
     */
    private static boolean containsLabel(List<Token> labels, Token label)
      {
        var labelStr = label.text() + ":";
        for (var l : labels)
          {
            if (l.text().equals(labelStr))
                return true;
          }

        return false;
      }

    /**
     * Combines the lists of labels into a single list.
     */
    public static List<Token> combineLabels(List<Token> labels1, List<Token> labels2)
      {
        if (labels1.isEmpty())
            return labels2;
        else if (labels2.isEmpty())
            return labels1;
        else
          {
            labels1.addAll(labels2);
            return labels1;
          }
      }
  }
