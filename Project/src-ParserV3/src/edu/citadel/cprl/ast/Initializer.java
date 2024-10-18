package edu.citadel.cprl.ast;

import edu.citadel.common.Position;
import edu.citadel.common.CodeGenException;

/**
 * Interface for a variable initializer, which is either a constant value,
 * padding, or a composite constant value.  The initializer classes implement
 * a variant of the Composite Pattern.
 */
public sealed interface Initializer
    permits ConstValue, CompositeInitializer, Padding, EmptyInitializer
  {
    /**
     * Return the number of bytes for this initializer.
     */
    public int size();

    /**
     * Return the position of this initializer.
     */
    public Position position();

    /**
     * Returns true only if the initializer contains no values.
     */
    public default boolean isEmpty()
      {
        return size() == 0;
      }

    /**
     * Check semantic/contextual constraints.
     */
    public void checkConstraints();
    
    /**
     * Emit object code.
     *
     * @throws CodeGenException if the method is unable to generate object code.
     */
    public void emit() throws CodeGenException;
  }
