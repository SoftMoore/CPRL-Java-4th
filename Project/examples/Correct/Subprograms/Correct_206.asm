   PROGRAM 4
   LDGADDR 0
   LDCINT 0
   STOREW
   CALL _main
   HALT
_p:
   PROC 4
   LDLADDR 8
   LDCINT 12
   STOREW
   LDCSTR "n = "
   PUTSTR 4
   LDLADDR 8
   LOADW
   PUTINT
   PUTEOL
   LDGADDR 0
   LDCINT 5
   STOREW
   RET 0
_main:
   LDCSTR "x = "
   PUTSTR 4
   LDGADDR 0
   LOADW
   PUTINT
   PUTEOL
   CALL _p
   LDCSTR "x = "
   PUTSTR 4
   LDGADDR 0
   LOADW
   PUTINT
   PUTEOL
   RET 0
