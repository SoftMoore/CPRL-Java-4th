   PROGRAM 14
   CALL _main
   HALT
_main:
   LDGADDR 0
   LDCINT 5
   NEG
   STOREW
   LDGADDR 4
   LDCINT 2
   STOREW
   LDGADDR 8
   LDCINT 7
   STOREW
   LDGADDR 12
   LDCB 0
   STOREB
   LDGADDR 13
   LDCB 1
   STOREB
   LDGADDR 0
   LOADW
   LDGADDR 4
   LOADW
   BL L4
   LDCB 0
   BR L5
L4:
   LDGADDR 4
   LOADW
   LDGADDR 8
   LOADW
   BGE L2
   LDCB 1
   BR L3
L2:
   LDCB 0
L3:
L5:
   BZ L6
   LDCSTR "valid"
   PUTSTR 5
   PUTEOL
   BR L7
L6:
   LDCSTR "not valid"
   PUTSTR 9
   PUTEOL
L7:
   LDGADDR 0
   LOADW
   LDGADDR 4
   LOADW
   BL L12
   LDCB 0
   BR L13
L12:
   LDGADDR 0
   LOADW
   LDGADDR 8
   LOADW
   BL L10
   LDCB 1
   BR L11
L10:
   LDCB 0
L11:
L13:
   BZ L14
   LDCSTR "not valid"
   PUTSTR 9
   PUTEOL
   BR L15
L14:
   LDCSTR "valid"
   PUTSTR 5
   PUTEOL
L15:
   LDGADDR 4
   LOADW
   LDGADDR 0
   LOADW
   BLE L20
   LDCB 0
   BR L21
L20:
   LDGADDR 4
   LOADW
   LDGADDR 8
   LOADW
   BGE L18
   LDCB 1
   BR L19
L18:
   LDCB 0
L19:
L21:
   BZ L22
   LDCSTR "not valid"
   PUTSTR 9
   PUTEOL
   BR L23
L22:
   LDCSTR "valid"
   PUTSTR 5
   PUTEOL
L23:
   LDGADDR 4
   LOADW
   LDGADDR 0
   LOADW
   BL L28
   LDCB 0
   BR L29
L28:
   LDGADDR 8
   LOADW
   LDGADDR 0
   LOADW
   NEG
   BG L26
   LDCB 1
   BR L27
L26:
   LDCB 0
L27:
L29:
   BZ L30
   LDCSTR "not valid"
   PUTSTR 9
   PUTEOL
   BR L31
L30:
   LDCSTR "valid"
   PUTSTR 5
   PUTEOL
L31:
   LDGADDR 0
   LOADW
   LDGADDR 4
   LOADW
   BGE L36
   LDCB 1
   BR L37
L36:
   LDGADDR 4
   LOADW
   LDGADDR 8
   LOADW
   BGE L34
   LDCB 1
   BR L35
L34:
   LDCB 0
L35:
L37:
   BZ L38
   LDCSTR "valid"
   PUTSTR 5
   PUTEOL
   BR L39
L38:
   LDCSTR "not valid"
   PUTSTR 9
   PUTEOL
L39:
   LDGADDR 0
   LOADW
   LDGADDR 4
   LOADW
   BGE L44
   LDCB 1
   BR L45
L44:
   LDGADDR 4
   LOADW
   NEG
   LDGADDR 8
   LOADW
   BLE L42
   LDCB 1
   BR L43
L42:
   LDCB 0
L43:
L45:
   BZ L46
   LDCSTR "valid"
   PUTSTR 5
   PUTEOL
   BR L47
L46:
   LDCSTR "not valid"
   PUTSTR 9
   PUTEOL
L47:
   LDGADDR 4
   LOADW
   LDGADDR 0
   LOADW
   BG L52
   LDCB 1
   BR L53
L52:
   LDGADDR 4
   LOADW
   LDGADDR 8
   LOADW
   BGE L50
   LDCB 1
   BR L51
L50:
   LDCB 0
L51:
L53:
   BZ L54
   LDCSTR "valid"
   PUTSTR 5
   PUTEOL
   BR L55
L54:
   LDCSTR "not valid"
   PUTSTR 9
   PUTEOL
L55:
   LDGADDR 0
   LOADW
   NEG
   LDGADDR 4
   LOADW
   BGE L60
   LDCB 1
   BR L61
L60:
   LDGADDR 8
   LOADW
   LDGADDR 4
   LOADW
   BG L58
   LDCB 1
   BR L59
L58:
   LDCB 0
L59:
L61:
   BZ L62
   LDCSTR "not valid"
   PUTSTR 9
   PUTEOL
   BR L63
L62:
   LDCSTR "valid"
   PUTSTR 5
   PUTEOL
L63:
   LDGADDR 8
   LDCINT 0
   STOREW
   LDGADDR 12
   LOADB
   BNZ L66
   LDCB 0
   BR L67
L66:
   LDGADDR 4
   LOADW
   LDGADDR 8
   LOADW
   DIV
   LDCINT 2
   BL L64
   LDCB 1
   BR L65
L64:
   LDCB 0
L65:
L67:
   BZ L68
   LDCSTR "not valid"
   PUTSTR 9
   PUTEOL
   BR L69
L68:
   LDCSTR "valid"
   PUTSTR 5
   PUTEOL
L69:
   LDGADDR 13
   LOADB
   BZ L72
   LDCB 1
   BR L73
L72:
   LDGADDR 4
   LOADW
   LDGADDR 8
   LOADW
   DIV
   LDCINT 0
   BGE L70
   LDCB 1
   BR L71
L70:
   LDCB 0
L71:
L73:
   BZ L74
   LDCSTR "valid"
   PUTSTR 5
   PUTEOL
   BR L75
L74:
   LDCSTR "not valid"
   PUTSTR 9
   PUTEOL
L75:
   LDGADDR 12
   LOADB
   NOT
   BZ L76
   LDCSTR "valid"
   PUTSTR 5
   PUTEOL
   BR L77
L76:
   LDCSTR "not valid"
   PUTSTR 9
   PUTEOL
L77:
   RET 0
