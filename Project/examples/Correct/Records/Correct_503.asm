   CALL _main
   HALT
_writePoint:
   LDCSTR "Point("
   PUTSTR 6
   LDLADDR -8
   LOADW
   PUTINT
   LDCSTR ", "
   PUTSTR 2
   LDLADDR -8
   LDCINT 4
   ADD
   LOADW
   PUTINT
   LDCSTR ")"
   PUTSTR 1
   RET 8
_lessThan:
   LDLADDR -17
   LDLADDR -16
   LOADW
   LDLADDR -8
   LOADW
   BGE L8
   LDCB 1
   BR L9
L8:
   LDLADDR -16
   LOADW
   LDLADDR -8
   LOADW
   BE L6
   LDCB 0
   BR L7
L6:
   LDLADDR -16
   LDCINT 4
   ADD
   LOADW
   LDLADDR -8
   LDCINT 4
   ADD
   LOADW
   BGE L4
   LDCB 1
   BR L5
L4:
   LDCB 0
L5:
L7:
L9:
   STOREB
   RET 16
_sort:
   PROC 16
   LDLADDR 20
   LDCINT 1
   STOREW
L10:
   LDLADDR 20
   LOADW
   LDCINT 10
   LDCINT 1
   SUB
   BG L11
   LDLADDR 12
   LDLADDR -4
   LOADW
   LDLADDR 20
   LOADW
   LDCINT 8
   MUL
   ADD
   LOAD 8
   STORE 8
   LDLADDR 8
   LDLADDR 20
   LOADW
   LDCINT 1
   SUB
   STOREW
L12:
   LDLADDR 8
   LOADW
   LDCINT 0
   BGE L16
   LDCB 0
   BR L17
L16:
   ALLOC 1
   LDLADDR 12
   LOAD 8
   LDLADDR -4
   LOADW
   LDLADDR 8
   LOADW
   LDCINT 8
   MUL
   ADD
   LOAD 8
   CALL _lessThan
L17:
   BZ L13
   LDLADDR -4
   LOADW
   LDLADDR 8
   LOADW
   LDCINT 1
   ADD
   LDCINT 8
   MUL
   ADD
   LDLADDR -4
   LOADW
   LDLADDR 8
   LOADW
   LDCINT 8
   MUL
   ADD
   LOAD 8
   STORE 8
   LDLADDR 8
   LDLADDR 8
   LOADW
   LDCINT 1
   SUB
   STOREW
   BR L12
L13:
   LDLADDR -4
   LOADW
   LDLADDR 8
   LOADW
   LDCINT 1
   ADD
   LDCINT 8
   MUL
   ADD
   LDLADDR 12
   LOAD 8
   STORE 8
   LDLADDR 20
   LDLADDR 20
   LOADW
   LDCINT 1
   ADD
   STOREW
   BR L10
L11:
   RET 4
_writeArray:
   PROC 4
   LDLADDR 8
   LDCINT 0
   STOREW
L18:
   LDLADDR 8
   LOADW
   LDCINT 10
   LDCINT 1
   SUB
   BG L19
   LDCSTR "   "
   PUTSTR 3
   LDLADDR -4
   LOADW
   LDLADDR 8
   LOADW
   LDCINT 8
   MUL
   ADD
   LOAD 8
   CALL _writePoint
   PUTEOL
   LDLADDR 8
   LDLADDR 8
   LOADW
   LDCINT 1
   ADD
   STOREW
   BR L18
L19:
   RET 4
_main:
   PROC 84
   LDLADDR 12
   LDCINT 0
   LDCINT 5
   LDCINT 2
   LDCINT 5
   LDCINT 99
   LDCINT -99
   LDCINT 6
   LDCINT 5
   LDCINT -6
   LDCINT -8
   LDCINT 10
   LDCINT 0
   LDCINT 12
   LDCINT 5
   LDCINT -1
   LDCINT 5
   LDCINT 16
   LDCINT 5
   LDCINT 0
   LDCINT -3
   STORE 80
   LDCSTR "initial array:"
   PUTSTR 14
   PUTEOL
   LDLADDR 12
   CALL _writeArray
   PUTEOL
   LDLADDR 12
   CALL _sort
   LDCSTR "sorted array:"
   PUTSTR 13
   PUTEOL
   LDLADDR 12
   CALL _writeArray
   RET 0