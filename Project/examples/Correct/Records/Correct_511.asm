   PROGRAM 24
   LDGADDR 0
   LDCINT 0
   LDCINT 5
   LDCINT 1
   LDCINT 3
   LDCINT -2
   LDCINT 99
   STORE 24
   CALL _main
   HALT
_main:
   PROC 4
   LDLADDR 8
   LDCINT 0
   STOREW
L0:
   LDLADDR 8
   LOADW
   LDCINT 2
   BG L1
   LDGADDR 0
   LDLADDR 8
   LOADW
   LDCINT 8
   MUL
   ADD
   LOAD 8
   CALL _writePoint
   LDLADDR 8
   LDLADDR 8
   LOADW
   LDCINT 1
   ADD
   STOREW
   BR L0
L1:
   RET 0
_writePoint:
   LDCSTR "("
   PUTSTR 1
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
   PUTEOL
   RET 8
