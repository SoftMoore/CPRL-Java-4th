   PROGRAM 338
   LDGADDR 0
   LDCSTR "invalid"
   ALLOC 4
   LDCINT 0
   LDCSTR "January"
   ALLOC 4
   LDCINT 31
   LDCSTR "February"
   ALLOC 2
   LDCINT 29
   LDCSTR "March"
   ALLOC 8
   LDCINT 31
   LDCSTR "April"
   ALLOC 8
   LDCINT 30
   LDCSTR "May"
   ALLOC 12
   LDCINT 31
   LDCSTR "June"
   ALLOC 10
   LDCINT 30
   LDCSTR "July"
   ALLOC 10
   LDCINT 31
   LDCSTR "August"
   ALLOC 6
   LDCINT 31
   LDCSTR "September"
   LDCINT 30
   LDCSTR "October"
   ALLOC 4
   LDCINT 31
   LDCSTR "November"
   ALLOC 2
   LDCINT 30
   LDCSTR "December"
   ALLOC 2
   LDCINT 31
   STORE 338
   CALL _main
   HALT
_writelnMonth:
   LDCSTR "Month "
   PUTSTR 6
   LDLADDR -26
   LOAD 22
   PUTSTR 9
   LDCSTR " has a maximum of "
   PUTSTR 18
   LDLADDR -26
   LDCINT 22
   ADD
   LOADW
   PUTINT
   LDCSTR " days."
   PUTSTR 6
   PUTEOL
   RET 26
_main:
   PROC 4
   LDLADDR 8
   LDCINT 1
   STOREW
L0:
   LDLADDR 8
   LOADW
   LDCINT 12
   BG L1
   LDGADDR 0
   LDLADDR 8
   LOADW
   LDCINT 26
   MUL
   ADD
   LOAD 26
   CALL _writelnMonth
   LDLADDR 8
   LDLADDR 8
   LOADW
   LDCINT 1
   ADD
   STOREW
   BR L0
L1:
   RET 0
