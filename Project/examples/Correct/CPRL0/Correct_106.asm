   PROGRAM 4
   LDGADDR 0
   LDCINT 1
   STOREW
   CALL _main
   HALT
_main:
L2:
   LDGADDR 0
   LOADW
   LDCINT 5
   BG L3
   LDGADDR 0
   LDGADDR 0
   LOADW
   LDCINT 1
   ADD
   STOREW
   BR L2
L3:
   LDCSTR "x = "
   PUTSTR 4
   LDGADDR 0
   LOADW
   PUTINT
   PUTEOL
   RET 0
