package edu.citadel.cprl.ast;

import edu.citadel.common.Position;

/**
 * An empty initializer passes constraint checks and emits no code.
 * It is returned from parsing initializers as an alternative to
 * returning null when parsing errors are encountered. <br>
 * (implements the singleton pattern)
 */
public final class EmptyInitializer implements Initializer
  {
    private static EmptyInitializer instance = new EmptyInitializer();

    private EmptyInitializer() { }   // private constructor

    /**
     * Returns the single instance of this class.
     */
    public static EmptyInitializer instance()
      {
        return instance;
      }

    @Override
    public final int size()
      {
        return 0;
      }

    @Override
    public final Position position()
      {
        return new Position();
      }

    @Override
    public void checkConstraints()
      {
        // nothing to check
      }

    @Override
    public void emit()
      {
        // nothing to emit
      }
  }
