   CALL _main
   HALT
_main:
   PROC 28
   LDLADDR 8
   LDCINT 0
   LDCINT 1
   LDCINT 2
   LDCINT 253
   LDCINT 254
   LDCINT 255
   STORE 24
   LDCSTR "array: "
   PUTSTR 7
   LDLADDR 32
   LDCINT 0
   STOREW
L0:
   LDLADDR 32
   LOADW
   LDCINT 5
   BG L1
   LDLADDR 8
   LDLADDR 32
   LOADW
   LDCINT 4
   MUL
   ADD
   LOADW
   PUTINT
   LDCSTR "  "
   PUTSTR 2
   LDLADDR 32
   LDLADDR 32
   LOADW
   LDCINT 1
   ADD
   STOREW
   BR L0
L1:
   PUTEOL
   RET 0