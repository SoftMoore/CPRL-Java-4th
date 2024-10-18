   PROGRAM 36
   LDGADDR 0
   LDCSTR "0123456789ABCDEF"
   STORE 36
   CALL _main
   HALT
_main:
   PROC 4
   LDLADDR 8
   LDCINT 111226127
   STOREW
   LDCSTR "i = "
   PUTSTR 4
   LDLADDR 8
   LOADW
   PUTINT
   PUTEOL
   LDCSTR "to binary string: "
   PUTSTR 18
   ALLOC 68
   LDLADDR 8
   LOADW
   CALL _intToBinaryString
   PUTSTR 32
   PUTEOL
   LDCSTR "   to hex string: "
   PUTSTR 18
   ALLOC 20
   LDLADDR 8
   LOADW
   CALL _intToHexString
   PUTSTR 8
   PUTEOL
   PUTEOL
   RET 0
_intToBinaryString:
   PROC 76
   LDLADDR 80
   LDCINT 0
   STOREW
L0:
   LDLADDR 80
   LOADW
   LDCINT 32
   LDCINT 1
   SUB
   BG L1
   LDLADDR 76
   LDCINT 1
   LDCINT 32
   LDCINT 1
   SUB
   LDLADDR 80
   LOADW
   SUB
   SHL
   STOREW
   LDLADDR -4
   LOADW
   LDLADDR 76
   LOADW
   BITAND
   LDCINT 0
   BNE L4
   LDLADDR 8
   LDCINT 4
   ADD
   LDLADDR 80
   LOADW
   LDCINT 2
   MUL
   ADD
   LDCCH '0'
   STORE2B
   BR L5
L4:
   LDLADDR 8
   LDCINT 4
   ADD
   LDLADDR 80
   LOADW
   LDCINT 2
   MUL
   ADD
   LDCCH '1'
   STORE2B
L5:
   LDLADDR 80
   LDLADDR 80
   LOADW
   LDCINT 1
   ADD
   STOREW
   BR L0
L1:
   LDLADDR 8
   LDCINT 32
   STOREW
   LDLADDR -72
   LDLADDR 8
   LOAD 68
   STORE 68
   RET 4
_intToHexString:
   PROC 20
   LDLADDR 8
   LDCINT 4
   ADD
   LDCINT 0
   LDCINT 2
   MUL
   ADD
   LDGADDR 0
   LDCINT 4
   ADD
   LDLADDR -4
   LOADW
   LDCINT 28
   SHR
   LDCINT 15
   BITAND
   LDCINT 2
   MUL
   ADD
   LOAD2B
   STORE2B
   LDLADDR 8
   LDCINT 4
   ADD
   LDCINT 1
   LDCINT 2
   MUL
   ADD
   LDGADDR 0
   LDCINT 4
   ADD
   LDLADDR -4
   LOADW
   LDCINT 24
   SHR
   LDCINT 15
   BITAND
   LDCINT 2
   MUL
   ADD
   LOAD2B
   STORE2B
   LDLADDR 8
   LDCINT 4
   ADD
   LDCINT 2
   LDCINT 2
   MUL
   ADD
   LDGADDR 0
   LDCINT 4
   ADD
   LDLADDR -4
   LOADW
   LDCINT 20
   SHR
   LDCINT 15
   BITAND
   LDCINT 2
   MUL
   ADD
   LOAD2B
   STORE2B
   LDLADDR 8
   LDCINT 4
   ADD
   LDCINT 3
   LDCINT 2
   MUL
   ADD
   LDGADDR 0
   LDCINT 4
   ADD
   LDLADDR -4
   LOADW
   LDCINT 16
   SHR
   LDCINT 15
   BITAND
   LDCINT 2
   MUL
   ADD
   LOAD2B
   STORE2B
   LDLADDR 8
   LDCINT 4
   ADD
   LDCINT 4
   LDCINT 2
   MUL
   ADD
   LDGADDR 0
   LDCINT 4
   ADD
   LDLADDR -4
   LOADW
   LDCINT 12
   SHR
   LDCINT 15
   BITAND
   LDCINT 2
   MUL
   ADD
   LOAD2B
   STORE2B
   LDLADDR 8
   LDCINT 4
   ADD
   LDCINT 5
   LDCINT 2
   MUL
   ADD
   LDGADDR 0
   LDCINT 4
   ADD
   LDLADDR -4
   LOADW
   LDCINT 8
   SHR
   LDCINT 15
   BITAND
   LDCINT 2
   MUL
   ADD
   LOAD2B
   STORE2B
   LDLADDR 8
   LDCINT 4
   ADD
   LDCINT 6
   LDCINT 2
   MUL
   ADD
   LDGADDR 0
   LDCINT 4
   ADD
   LDLADDR -4
   LOADW
   LDCINT 4
   SHR
   LDCINT 15
   BITAND
   LDCINT 2
   MUL
   ADD
   LOAD2B
   STORE2B
   LDLADDR 8
   LDCINT 4
   ADD
   LDCINT 7
   LDCINT 2
   MUL
   ADD
   LDGADDR 0
   LDCINT 4
   ADD
   LDLADDR -4
   LOADW
   LDCINT 15
   BITAND
   LDCINT 2
   MUL
   ADD
   LOAD2B
   STORE2B
   LDLADDR 8
   LDCINT 8
   STOREW
   LDLADDR -24
   LDLADDR 8
   LOAD 20
   STORE 20
   RET 4