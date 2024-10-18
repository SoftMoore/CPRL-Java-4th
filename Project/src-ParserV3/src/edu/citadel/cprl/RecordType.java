package edu.citadel.cprl;

import edu.citadel.cprl.ast.FieldDecl;

import java.util.List;
import java.util.HashMap;

/**
 * This class encapsulates the language concept of a record type
 * in the programming language CPRL.
 */
public class RecordType extends Type
  {
    // Use a hash map for efficient lookup of field names.
    private HashMap<String, FieldDecl> fieldNameMap = new HashMap<>();

    private List<FieldDecl> fieldDecls;

    /**
     * Construct a record type with the specified type name, list of
     * field declarations, and size.
     */
    public RecordType(String typeName, List<FieldDecl> fieldDecls)
      {
        super(typeName, 0);
// ... In call to superclass constructor, 0 is not correct as the size for the record type.
// ... What is the size for the record type?  Hint: Read the book.
        this.fieldDecls = fieldDecls;

        for (FieldDecl fieldDecl : fieldDecls)
            fieldNameMap.put(fieldDecl.idToken().text(), fieldDecl);

        // compute fieldDecl offsets
        for (int i = 1; i < fieldDecls.size(); ++i)
          {
            FieldDecl prevDecl = fieldDecls.get(i - 1);
            fieldDecls.get(i).setOffset(prevDecl.offset() + prevDecl.size());
          }
      }

    /**
     * Returns true if this record type contains a field with the specified field name.
     */
    public boolean containsField(String fieldName)
      {
        return fieldNameMap.containsKey(fieldName);
      }

    /**
     * Returns the field declaration associated with the identifier string.
     * Returns null if the identifier string is not found.
     */
    public FieldDecl get(String idStr)
      {
        return fieldNameMap.get(idStr);
      }

    /**
     * Returns a list of field declarations for this record type.
     */
    public List<FieldDecl> fieldDecls()
      {
        return fieldDecls;
      }
  }
