package edu.citadel.cprl;

import edu.citadel.common.Position;
import edu.citadel.common.ErrorHandler;
import edu.citadel.common.ParserException;
import edu.citadel.common.InternalCompilerException;

import edu.citadel.cprl.ast.*;

import java.io.IOException;
import java.util.*;

/**
 * This class uses recursive descent to perform syntax analysis of
 * the CPRL source language and to generate an abstract syntax tree.
 */
public final class Parser
  {
    private Scanner scanner;
    private IdTable idTable;
    private ErrorHandler errorHandler;
    private LoopContext  loopContext = new LoopContext();
    private SubprogramContext subprogramContext = new SubprogramContext();

    /**
     * Symbols that can follow a statement.
     */
    private final Set<Symbol> stmtFollowers = EnumSet.of(
// ...
      );

    /**
     * Symbols that can follow a subprogram declaration.
     */
    private final Set<Symbol> subprogDeclFollowers = EnumSet.of(
// ...
      );

    /**
     * Symbols that can follow a factor.
     */
    private final Set<Symbol> factorFollowers = EnumSet.of(
        Symbol.semicolon,   Symbol.loopRW,      Symbol.thenRW,
        Symbol.rightParen,  Symbol.andRW,       Symbol.orRW,
        Symbol.equals,      Symbol.notEqual,    Symbol.lessThan,
        Symbol.lessOrEqual, Symbol.greaterThan, Symbol.greaterOrEqual,
        Symbol.plus,        Symbol.minus,       Symbol.times,
        Symbol.divide,      Symbol.modRW,       Symbol.rightBracket,
        Symbol.comma,       Symbol.bitwiseAnd,  Symbol.bitwiseOr,
        Symbol.bitwiseXor,  Symbol.leftShift,   Symbol.rightShift
      );

    /**
     * Symbols that can follow an initial declaration.
     * Set is computed dynamically based on the scope level.
     */
    private Set<Symbol> initialDeclFollowers()
      {
        // An initial declaration can always be followed by another
        // initial declaration, regardless of the scope level.
        var followers = EnumSet.of(Symbol.constRW, Symbol.varRW, Symbol.typeRW);

        if (idTable.scopeLevel() == ScopeLevel.GLOBAL)
            followers.addAll(EnumSet.of(Symbol.procRW, Symbol.funRW));
        else
          {
            followers.addAll(stmtFollowers);
            followers.remove(Symbol.elseRW);
          }

        return followers;
      }

    /**
     * Construct a parser with the specified scanner, identifier
     * table, and error handler.
     */
    public Parser(Scanner scanner, IdTable idTable, ErrorHandler errorHandler)
      {
        this.scanner = scanner;
        this.idTable = idTable;
        this.errorHandler = errorHandler;
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>program = initialDecls subprogramDecls .</code>
     *
     * @return The parsed program.  Returns a program with an empty list
     *         of initial declarations and an empty list of subprogram
     *         declarations if parsing fails.
     */
    public Program parseProgram() throws IOException
      {
        try
          {
            var initialDecls = parseInitialDecls();
            var subprogDecls = parseSubprogramDecls();

            // match(Symbol.EOF)
            // Let's generate a better error message than "Expecting "End-of-File" but ..."
            if (scanner.symbol() != Symbol.EOF)
              {
                var errorMsg = "Expecting \"proc\" or \"fun\" but found \""
                             + scanner.token() + "\" instead.";
                throw error(errorMsg);
              }

            return new Program(initialDecls, subprogDecls);
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(EnumSet.of(Symbol.EOF));
            return new Program();
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>initialDecls = { initialDecl } .</code>
     *
     * @return The list of initial declarations.
     */
    private List<InitialDecl> parseInitialDecls() throws IOException
      {
        var initialDecls = new ArrayList<InitialDecl>(10);

        while (scanner.symbol().isInitialDeclStarter())
            initialDecls.add(parseInitialDecl());

        return initialDecls;
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>initialDecl = constDecl | varDecl | typeDecl .</code>
     *
     * @return The parsed initial declaration.  Returns an
     *         empty initial declaration if parsing fails.
     */
    private InitialDecl parseInitialDecl() throws IOException
      {
// ...   throw an internal error if the symbol is not one of constRW, varRW, or typeRW
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>constDecl = "const" constId ":=" [ "-" ] literal ";" .</code>
     *
     * @return The parsed constant declaration.  Returns an
     *         empty initial declaration if parsing fails.
     */
    private InitialDecl parseConstDecl() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>literal = intLiteral | charLiteral | stringLiteral | "true" | "false" .</code>
     *
     * @return The parsed literal token.  Returns a default token if parsing fails.
     */
    private Token parseLiteral() throws IOException
      {
        try
          {
            if (scanner.symbol().isLiteral())
              {
                var literal = scanner.token();
                matchCurrentSymbol();
                return literal;
              }
            else
                throw error("Invalid literal expression.");
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(factorFollowers);
            return new Token();
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>varDecl = "var" identifiers ":" ( typeName | arrayTypeConstr | stringTypeConstr)
     *               [ ":=" initializer] ";" .</code>
     *
     * @return The parsed variable declaration.  Returns an
     *         empty initial declaration if parsing fails.
     */
    private InitialDecl parseVarDecl() throws IOException
      {
        try
          {
            match(Symbol.varRW);
            var identifiers = parseIdentifiers();
            match(Symbol.colon);

            Type varType;
            var symbol = scanner.symbol();
            if (symbol.isPredefinedType() || symbol == Symbol.identifier)
                varType = parseTypeName();
            else if (symbol == Symbol.arrayRW)
                varType = parseArrayTypeConstr();
            else if (symbol == Symbol.stringRW)
                varType = parseStringTypeConstr();
            else
              {
                var errorMsg = "Expecting a type name, reserved word \"array\", "
                             + "or reserved word \"string\".";
                throw error(errorMsg);
              }

            Initializer initializer = EmptyInitializer.instance();
            if (scanner.symbol() == Symbol.assign)
              {
                matchCurrentSymbol();
                initializer = parseInitializer();
              }

            match(Symbol.semicolon);

            var varDecl = new VarDecl(identifiers, varType, initializer,
                                      idTable.scopeLevel());

            for (SingleVarDecl decl : varDecl.singleVarDecls())
                idTable.add(decl);

            return varDecl;
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(initialDeclFollowers());
            return EmptyInitialDecl.instance();
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>identifiers = identifier { "," identifier } .</code>
     *
     * @return The list of identifier tokens.  Returns an empty list if parsing fails.
     */
    private List<Token> parseIdentifiers() throws IOException
      {
        try
          {
            var identifiers = new ArrayList<Token>(10);
            var idToken = scanner.token();
            match(Symbol.identifier);
            identifiers.add(idToken);

            while (scanner.symbol() == Symbol.comma)
              {
                matchCurrentSymbol();
                idToken = scanner.token();
                match(Symbol.identifier);
                identifiers.add(idToken);
              }

            return identifiers;
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(EnumSet.of(Symbol.colon, Symbol.greaterThan));
            return Collections.emptyList();
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>initializer = constValue | compositeInitializer .</code>
     *
     * @return The parsed initializer.  Returns an empty
     *         initializer if parsing fails.
     */
    private Initializer parseInitializer() throws IOException
      {
        try
          {
            var symbol = scanner.symbol();
            if (symbol == Symbol.identifier || symbol.isLiteral() || symbol == Symbol.minus)
              {
                var expr = parseConstValue();
                return expr instanceof ConstValue constValue ? constValue
                                           : EmptyInitializer.instance();
              }
            else if (symbol == Symbol.leftBrace)
                return parseCompositeInitializer();
            else
              {
                var errorMsg = "Expecting literal, identifier, or left brace.";
                throw error(errorMsg);
              }
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(initialDeclFollowers());
            return EmptyInitializer.instance();
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>compositeInitializer = "{" initializer { "," initializer } "}" .</code>
     *
     * @return The parsed composite initializer.  Returns an empty composite
     *         initializer if parsing fails.
     */
    private CompositeInitializer parseCompositeInitializer() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>typeDecl = arrayTypeDecl | recordTypeDecl | stringTypeDecl .</code>
     *
     * @return The parsed type declaration.  Returns an
     *         empty initial declaration parsing fails.
     */
    private InitialDecl parseTypeDecl() throws IOException
      {
        assert scanner.symbol() == Symbol.typeRW;

        try
          {
            return switch (scanner.lookahead(4).symbol())
              {
                case Symbol.arrayRW  -> parseArrayTypeDecl();
                case Symbol.recordRW -> parseRecordTypeDecl();
                case Symbol.stringRW -> parseStringTypeDecl();
                default ->
                  {
                    var errorPos = scanner.lookahead(4).position();
                    throw error(errorPos, "Invalid type declaration.");
                  }
              };
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            matchCurrentSymbol();   // force scanner past "type"
            recover(initialDeclFollowers());
            return EmptyInitialDecl.instance();
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>arrayTypeDecl = "type" typeId "=" "array" "[" intConstValue "]"
     *                       "of" typeName ";" .</code>
     *
     * @return The parsed array type declaration.  Returns an
     *         empty initial declaration if parsing fails.
     */
    private InitialDecl parseArrayTypeDecl() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>arrayTypeConstr = "array" "[" intConstValue "]" "of" typeName .</code>
     *
     * @return The array type defined by this array type constructor.
     *         Returns an empty array type if parsing fails.
     */
    private ArrayType parseArrayTypeConstr() throws IOException
      {
        try
          {
            match(Symbol.arrayRW);
            match(Symbol.leftBracket);
            var numElements = parseIntConstValue();
            match(Symbol.rightBracket);
            match(Symbol.ofRW);
            var elemType = parseTypeName();
            var nElements = numElements.intValue();
            var typeName  = "array[" + nElements + "] of " + elemType;
            return new ArrayType(typeName, nElements, elemType);
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(EnumSet.of(Symbol.semicolon));
            return new ArrayType("_", 0, Type.UNKNOWN);
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>recordTypeDecl = "type" typeId "=" "record" "{" fieldDecls "}" ";" .</code>
     *
     * @return The parsed record type declaration.  Returns
     *         an empty initial declaration if parsing fails.
     */
    private InitialDecl parseRecordTypeDecl() throws IOException
      {
        try
          {
            match(Symbol.typeRW);
            var typeId = scanner.token();
            match(Symbol.identifier);
            match(Symbol.equals);
            match(Symbol.recordRW);
            match(Symbol.leftBrace);

            List<FieldDecl> fieldDecls;
            try
              {
                idTable.openScope(ScopeLevel.RECORD);
                fieldDecls = parseFieldDecls();
              }
            finally
              {
                idTable.closeScope();
              }

            match(Symbol.rightBrace);
            match(Symbol.semicolon);

            var typeDecl = new RecordTypeDecl(typeId, fieldDecls);
            idTable.add(typeDecl);
            return typeDecl;
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(initialDeclFollowers());
            return EmptyInitialDecl.instance();
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>fieldDecls = { fieldDecl } .</code>
     *
     * @return A list of field declarations.
     */
    private List<FieldDecl> parseFieldDecls() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>fieldDecl = fieldId ":" typeName ";" .</code>
     *
     * @return The parsed field declaration.  Returns null if parsing fails.
     */
    private FieldDecl parseFieldDecl() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>stringTypeDecl = "type" typeId "=" "string" "[" intConstValue "]" ";" .</code>
     *
     * @return The parsed string type declaration.  Returns an
     *         empty initial declaration if parsing fails.
     */
    private InitialDecl parseStringTypeDecl() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>stringTypeConstr = "string" "[" intConstValue "]" .</code>
     *
     * @return The string type defined by this string type constructor.
     *         Returns an empty string type if parsing fails.
     */
    private StringType parseStringTypeConstr() throws IOException
      {
        try
          {
            match(Symbol.stringRW);
            match(Symbol.leftBracket);
            var numElements = parseIntConstValue();
            match(Symbol.rightBracket);
            var nElements = numElements.intValue();
            var typeName  = "string[" + nElements + "]";
            return new StringType(typeName, nElements);
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(EnumSet.of(Symbol.semicolon));
            return new StringType("_", 0);
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>typeName = "Integer" | "Boolean" | "Char" | typeId .</code>
     *
     * @return The parsed named type.  Returns Type.UNKNOWN if parsing fails.
     */
    private Type parseTypeName() throws IOException
      {
        try
          {
            switch (scanner.symbol())
              {
                case IntegerRW ->
                  {
                    matchCurrentSymbol();
                    return Type.Integer;
                  }
                case BooleanRW ->
                  {
                    matchCurrentSymbol();
                    return Type.Boolean;
                  }
                case CharRW ->
                  {
                    matchCurrentSymbol();
                    return Type.Char;
                  }
                case identifier ->
                  {
                    var typeId = scanner.token();
                    matchCurrentSymbol();
                    var decl = idTable.get(typeId.text());

                    if (decl != null)
                      {
                        if (  decl instanceof ArrayTypeDecl
                           || decl instanceof RecordTypeDecl
                           || decl instanceof StringTypeDecl)
                          {
                            return decl.type();
                          }
                        else
                          {
                            var errorMsg = "Identifier \"" + typeId + "\" is not a valid type name.";
                            throw error(typeId.position(), errorMsg);
                          }
                      }
                    else
                      {
                        var errorMsg = "Identifier \"" + typeId + "\" has not been declared.";
                        throw error(typeId.position(), errorMsg);
                      }
                  }
                default -> throw error("Invalid type name.");
              }

          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(EnumSet.of(Symbol.semicolon,  Symbol.comma,
                               Symbol.rightParen, Symbol.leftBrace));
            return Type.UNKNOWN;
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>subprogramDecls = { subprogramDecl } .</code>
     *
     * @return The list of subprogram declarations.
     */
    private List<SubprogramDecl> parseSubprogramDecls() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>subprogramDecl = procedureDecl | functionDecl .</code>
     *
     * @return The parsed subprogram declaration.  Returns an
     *         empty subprogram declaration if parsing fails.
     */
    private SubprogramDecl parseSubprogramDecl() throws IOException
      {
// ...   throw an internal error if the symbol is not one of procRW or funRW
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>procedureDecl = "proc" procId "(" [ parameterDecls ] ")"
     *                       "{" initialDecls statements "}" .</code>
     *
     * @return The parsed procedure declaration.  Returns an
     *         empty subprogram declaration if parsing fails.
     */
    private SubprogramDecl parseProcedureDecl() throws IOException
      {
        try
          {
            match(Symbol.procRW);
            var procId = scanner.token();
            match(Symbol.identifier);

            var procDecl = new ProcedureDecl(procId);
            idTable.add(procDecl);
            match(Symbol.leftParen);

            try
              {
                idTable.openScope(ScopeLevel.LOCAL);

                if (scanner.symbol().isParameterDeclStarter())
                    procDecl.setParameterDecls(parseParameterDecls());

                match(Symbol.rightParen);
                match(Symbol.leftBrace);
                procDecl.setInitialDecls(parseInitialDecls());

                subprogramContext.beginSubprogramDecl(procDecl);
                procDecl.setStatements(parseStatements());
                subprogramContext.endSubprogramDecl();
              }
            finally
              {
                idTable.closeScope();
              }

            match(Symbol.rightBrace);
            return procDecl;
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(subprogDeclFollowers);
            return EmptySubprogramDecl.instance();
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>functionDecl = "fun" funcId "(" [ parameterDecls ] ")" ":" typeName
     *                      "{" initialDecls statements "}" .</code>
     *
     * @return The parsed function declaration.  Returns an
     *         empty subprogram declaration if parsing fails.
     */
    private SubprogramDecl parseFunctionDecl() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>parameterDecls = parameterDecl { "," parameterDecl } .</code>
     *
     * @return A list of parameter declarations.
     */
    private List<ParameterDecl> parseParameterDecls() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>parameterDecl = [ "var" ] paramId ":" typeName .</code>
     *
     * @return The parsed parameter declaration.  Returns null if parsing fails.
     */
    private ParameterDecl parseParameterDecl() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>statements = { statement } .</code>
     *
     * @return A list of statements.
     */
    private List<Statement> parseStatements() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>statement = assignmentStmt | procedureCallStmt | compoundStmt | ifStmt
     *                 | loopStmt       | forLoopStmt       | exitStmt     | readStmt
     *                 | writeStmt      | writelnStmt       | returnStmt .</code>
     *
     * @return The parsed statement.  Returns an empty statement if parsing fails.
     */
    private Statement parseStatement() throws IOException
      {
        try
          {
            if (scanner.symbol() == Symbol.identifier)
              {
                // Handle identifiers based on how they are declared,
                // or use the lookahead symbol if not declared.
                var idStr = scanner.text();
                var decl  = idTable.get(idStr);

                if (decl != null)
                  {
                    if (decl instanceof VariableDecl)
                        return parseAssignmentStmt();
                    else if (decl instanceof ProcedureDecl)
                        return parseProcedureCallStmt();
                    else
                        throw error("Identifier \"" + idStr + "\" cannot start a statement.");
                  }
                else
                  {
// ...   Big Hint: Read the book!
                  }
              }
            else
              {
                return switch (scanner.symbol())
                  {
                    case Symbol.leftBrace -> parseCompoundStmt();
                    case Symbol.ifRW      -> parseIfStmt();
// ...
                    default -> throw internalError(scanner.token()
                                   + " cannot start a statement.");
                  };
              }
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);

            // Error recovery here is complicated for identifiers since they can both
            // start a statement and appear elsewhere in the statement.  (Consider,
            // for example, an assignment statement or a procedure call statement.)
            // Since the most common error is to declare or reference an identifier
            // incorrectly, we will assume that this is the case and advance to the
            // end of the current statement before performing error recovery.
            scanner.advanceTo(EnumSet.of(Symbol.semicolon, Symbol.rightBrace));
            recover(stmtFollowers);
            return EmptyStatement.instance();
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>assignmentStmt = variable ":=" expression ";" .</code>
     *
     * @return The parsed assignment statement.  Returns
     *         an empty statement if parsing fails.
     */
    private Statement parseAssignmentStmt() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>compoundStmt = "{" statements "}" .<\code>
     *
     * @return The parsed compound statement.  Returns an empty statement if parsing fails.
     */
    private Statement parseCompoundStmt() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>ifStmt = "if" booleanExpr "then" statement  [ "else" statement ] .</code>
     *
     * @return The parsed if statement.  Returns an empty statement if parsing fails.
     */
    private Statement parseIfStmt() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>loopStmt = [ "while" booleanExpr ] "loop" statement .</code>
     *
     * @return The parsed loop statement.  Returns an empty statement if parsing fails.
     */
    private Statement parseLoopStmt() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>forLoopStmt = "for" varId "in" intExpr ".." intExpr "loop" statement .</code>
     *
     * @return The parsed for-loop statement.  Returns an empty statement if parsing fails.
     */
    private Statement parseForLoopStmt() throws IOException
      {
        try
          {
            // create a new scope for the loop variable
            idTable.openScope(ScopeLevel.LOCAL);

            match(Symbol.forRW);
            var loopId = scanner.token();
            match(Symbol.identifier);
            match(Symbol.inRW);
            var rangeStart = parseExpression();
            match(Symbol.dotdot);
            var rangeEnd = parseExpression();

            // Create an implicit variable declaration for the loop variable and add
            // it to the list of initial declarations for the subprogram declaration.
            var varDecl = new VarDecl(List.of(loopId), Type.Integer,
                                      EmptyInitializer.instance(), ScopeLevel.LOCAL);
            var subprogDecl = subprogramContext.subprogramDecl();
            assert subprogDecl != null;
            subprogDecl.initialDecls().add(varDecl);

            // Add the corresponding single variable declaration to the identifier tables.
            var loopSvDecl = varDecl.singleVarDecls().getLast();
            idTable.add(loopSvDecl);

            // Create loop variable to add to AST class ForLoopStmt
            var loopVariable = new Variable(loopSvDecl, loopId.position(),
                                            Collections.emptyList());
            match(Symbol.loopRW);
            var forLoopStmt = new ForLoopStmt(loopVariable, rangeStart, rangeEnd);
            loopContext.beginLoop(forLoopStmt);
            forLoopStmt.setStatement(parseStatement());
            loopContext.endLoop();

            return forLoopStmt;
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(stmtFollowers);
            return EmptyStatement.instance();
          }
        finally
          {
            idTable.closeScope();
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>exitStmt = "exit" [ "when" booleanExpr ] ";" .</code>
     *
     * @return The parsed exit statement.  Returns an empty statement if parsing fails.
     */
    private Statement parseExitStmt() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>readStmt = "read" variable ";" .</code>
     *
     * @return The parsed read statement.  Returns an empty statement if parsing fails.
     */
    private Statement parseReadStmt() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>writeStmt = "write" expressions ";" .</code>
     *
     * @return The parsed write statement.  Returns an empty statement if parsing fails.
     */
    private Statement parseWriteStmt() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>expressions = expression [ "," expression ] .</code>
     *
     * @return A list of expressions.
     */
    private List<Expression> parseExpressions() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>writelnStmt = "writeln" [ expressions ] ";" .</code>
     *
     * @return The parsed writeln statement.  Returns an empty statement if parsing fails.
     */
    private Statement parseWritelnStmt() throws IOException
      {
        try
          {
            match(Symbol.writelnRW);

            List<Expression> expressions;
            if (scanner.symbol().isExprStarter())
                expressions = parseExpressions();
            else
                expressions = Collections.emptyList();

            match(Symbol.semicolon);

            return new OutputStmt(expressions, true);
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(stmtFollowers);
            return EmptyStatement.instance();
          }
      }

    /**
     * Parse the following grammar rules:<br>
     * <code>procedureCallStmt = procId "(" [ actualParameters ] ")" ";" .<br>
     *       actualParameters = expressions .</code>
     *
     * @return The parsed procedure call statement.  Returns
     *         an empty statement if parsing fails.
     */
    private Statement parseProcedureCallStmt() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>returnStmt = "return" [ expression ] ";" .</code>
     *
     * @return The parsed return statement.  Returns an empty statement if parsing fails.
     */
    private Statement parseReturnStmt() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rules:<br>
     * <code>variable = ( varId | paramId ) { indexExpr | fieldExpr } .<br>
     *       indexExpr = "[" expression "]" .<br>
     *       fieldExpr = "." fieldId .</code>
     * <br>
     * This helper method provides common logic for methods parseVariable() and
     * parseVariableExpr().  The method does not handle any ParserExceptions but
     * throws them back to the calling method where they can be handled appropriately.
     *
     * @return The parsed variable.
     * @throws ParserException if parsing fails.
     * @see #parseVariable()
     * @see #parseVariableExpr()
     */
    private Variable parseVariableCommon() throws IOException, ParserException
      {
        var idToken = scanner.token();
        match(Symbol.identifier);
        var decl = idTable.get(idToken.text());

        if (decl == null)
          {
            var errorMsg = "Identifier \"" + idToken + "\" has not been declared.";
            throw error(idToken.position(), errorMsg);
          }
        else if (!(decl instanceof VariableDecl))
          {
            var errorMsg = "Identifier \"" + idToken + "\" is not a variable.";
            throw error(idToken.position(), errorMsg);
          }

        var variableDecl  = (VariableDecl) decl;

        var selectorExprs = new ArrayList<Expression>(5);

        while (scanner.symbol().isSelectorStarter())
          {
            if (scanner.symbol() == Symbol.leftBracket)
              {
                // parse index expression
                match(Symbol.leftBracket);
                selectorExprs.add(parseExpression());
                match(Symbol.rightBracket);
              }
            else if (scanner.symbol() == Symbol.dot)
              {
                // parse field expression
                match(Symbol.dot);
                var fieldId = scanner.token();
                match(Symbol.identifier);
                selectorExprs.add(new FieldExpr(fieldId));
              }
          }

        return new Variable(variableDecl, idToken.position(), selectorExprs);
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>variable = ( varId | paramId ) { indexExpr | fieldExpr } .</code>
     *
     * @return The parsed variable.  Returns null if parsing fails.
     */
    private Variable parseVariable() throws IOException
      {
        try
          {
            return parseVariableCommon();
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(EnumSet.of(Symbol.assign, Symbol.semicolon));
            return null;
          }
      }

    /**
     * Parse the following grammar rules:<br>
     * <code>expression = relation { logicalOp relation } .<br>
     *       logicalOp = "and" | "or" . </code>
     *
     * @return The parsed expression.
     */
    private Expression parseExpression() throws IOException
      {
        var expr = parseRelation();

        while (scanner.symbol().isLogicalOperator())
          {
            var operator = scanner.token();
            matchCurrentSymbol();
            expr = new LogicalExpr(expr, operator, parseRelation());
          }

        return expr;
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>relation = simpleExpr [ relationalOp simpleExpr ] .<br>
     *   relationalOp = "=" | "!=" | "&lt;" | "&lt;=" | "&gt;" | "&gt;=" .</code>
     *
     * @return The parsed relational expression.
     */
    private Expression parseRelation() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rules:<br>
     * <code>simpleExpr = [ signOp ] term { addingOp term } .<br>
     *       signOp = "+" | "-" .<br>
     *       addingOp  = "+" | "-" | "|" | "^" .</code>
     *
     * @return The parsed simple expression.
     */
    private Expression parseSimpleExpr() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rules:<br>
     * <code>term = factor { multiplyingOp factor } .<br>
     *       multiplyingOp = "*" | "/" | "mod" | "&" | "<<" | ">>" .</code>
     *
     * @return The parsed term expression.
     */
    private Expression parseTerm() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>factor = ("not" | "~") factor | literal | constId | variableExpr
     *              | functionCallExpr | "(" expression ")" .</code>
     *
     * @return The parsed factor expression.  Returns an empty expression if parsing fails.
     */
    private Expression parseFactor() throws IOException
      {
        try
          {
            var symbol = scanner.symbol();

            if (symbol == Symbol.notRW || symbol == Symbol.bitwiseNot)
              {
                var operator = scanner.token();
                matchCurrentSymbol();
                return new NotExpr(operator, parseFactor());
              }
            else if (symbol.isLiteral())
              {
                // Handle constant literals separately from constant identifiers.
                return parseConstValue();
              }
            else if (symbol == Symbol.identifier)
              {
                // Three possible cases: a declared constant, a variable
                // expression, or a function call expression.  Use lookahead
                // tokens and declaration to determine correct parsing action.
                var idStr = scanner.text();
                var decl  = idTable.get(idStr);

                if (decl != null)
                  {
                    if (decl instanceof ConstDecl)
                        return parseConstValue();
                    else if (decl instanceof VariableDecl)
                        return parseVariableExpr();
                    else if (decl instanceof FunctionDecl)
                        return parseFunctionCallExpr();
                    else
                      {
                        var errorPos = scanner.position();
                        var errorMsg = "Identifier \"" + idStr
                                     + "\" is not valid as an expression.";

                        // special recovery when procedure call is used as a function call
                        if (decl instanceof ProcedureDecl)
                          {
                            scanner.advance();
                            if (scanner.symbol() == Symbol.leftParen)
                              {
                                scanner.advanceTo(Symbol.rightParen);
                                scanner.advance();   // advance past the right paren
                              }
                          }

                        throw error(errorPos, errorMsg);
                      }
                  }
                else
                  {
                    // Make parsing decision using an additional lookahead symbol.
                    if (scanner.lookahead(2).symbol() == Symbol.leftParen)
                        return parseFunctionCallExpr();
                    else
                        throw error("Identifier \"" + scanner.token()
                                  + "\" has not been declared.");
                  }
              }
            else if (symbol == Symbol.leftParen)
              {
                matchCurrentSymbol();
                var expr = parseExpression();   // save expression
                match(Symbol.rightParen);
                return expr;
              }
            else
                throw error("Invalid expression.");
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(factorFollowers);
            return EmptyExpression.instance();
          }
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>constValue = ( [ "-" ] literal ) | constId .</code>
     *
     * @return The parsed constant value.  Returns
     *         an empty expression if parsing fails.
     */
    private Expression parseConstValue() throws IOException
      {
// ...
      }

    /**
     * Parse the following grammar rule:<br>
     * <code>variableExpr = variable .</code>
     *
     * @return The parsed variable expression.  Returns
     *         an empty expression if parsing fails.
     */
    private Expression parseVariableExpr() throws IOException
      {
        try
          {
            var variable = parseVariableCommon();
            return new VariableExpr(variable);
          }
        catch (ParserException e)
          {
            errorHandler.reportError(e);
            recover(factorFollowers);
            return EmptyExpression.instance();
          }
      }

    /**
     * Parse the following grammar rules:<br>
     * <code>functionCallExpr = funcId "(" [ actualParameters ] ")" .<br>
     *       actualParameters = expressions .</code>
     *
     * @return The parsed function call expression.  Returns
     *         an empty expression if parsing fails.
     */
    private Expression parseFunctionCallExpr() throws IOException
      {
// ...
      }

    // Utility parsing methods

    /**
     * Wrapper around method parseConstValue() that always
     * returns a valid constant integer value.
     */
    private ConstValue parseIntConstValue() throws IOException
      {
        var token = new Token(Symbol.intLiteral, new Position(), "1");
        var defaultConstValue = new ConstValue(token);

        var intConstValue = parseConstValue();

        if (intConstValue instanceof EmptyExpression)
            intConstValue = defaultConstValue;   // Error has already been reported.
        else if (intConstValue.type() != Type.Integer)
          {
            var errorMsg = "Constant value should have type Integer.";
            // no error recovery required here
            errorHandler.reportError(error(intConstValue.position(), errorMsg));
            intConstValue = defaultConstValue;
          }

        return (ConstValue) intConstValue;
      }

    /**
     * Check that the current scanner symbol is the expected symbol.  If it
     * is, then advance the scanner.  Otherwise, throw a ParserException.
     */
    private void match(Symbol expectedSymbol) throws IOException, ParserException
      {
        if (scanner.symbol() == expectedSymbol)
            scanner.advance();
        else
          {
            var errorMsg = "Expecting \"" + expectedSymbol + "\" but found \""
                         + scanner.token() + "\" instead.";
            throw error(errorMsg);
          }
      }

    /**
     * Advance the scanner.  This method represents an unconditional
     * match with the current scanner symbol.
     */
    private void matchCurrentSymbol() throws IOException
      {
        scanner.advance();
      }

    /**
     * Advance the scanner until the current symbol is one of the
     * symbols in the specified set of follows.
     */
    private void recover(Set<Symbol> followers) throws IOException
      {
        scanner.advanceTo(followers);
      }

    /**
     * Create a parser exception with the specified error message and
     * the current scanner position.
     */
    private ParserException error(String errorMsg)
      {
        return error(scanner.position(), errorMsg);
      }

    /**
     * Create a parser exception with the specified error position and error message.
     */
    private ParserException error(Position errorPos, String errorMsg)
      {
        return new ParserException(errorPos, errorMsg);
      }

    /**
     * Create an internal compiler exception with the specified error
     * message and the current scanner position.
     */
    private InternalCompilerException internalError(String errorMsg)
      {
        return new InternalCompilerException(scanner.position(), errorMsg);
      }
  }
