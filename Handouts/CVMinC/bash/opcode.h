#ifndef OPCODE_H
#define OPCODE_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#include <wchar.h>
#include <stdbool.h>
#include <locale.h>

// The set of opcodes for the CPRL virtual machine.

// halt opcode
#define HALT      0

// load opcodes (move data from memory to top of stack)
#define LOAD     10
#define LOADB    11
#define LOAD2B   12
#define LOADW    13
#define LDCB     14
#define LDCCH    15
#define LDCINT   16
#define LDCSTR   17
#define LDLADDR  18
#define LDGADDR  19

// optimized loads for special constants
#define LDCB0    20
#define LDCB1    21
#define LDCINT0  22
#define LDCINT1  23

// store opcodes (move data from top of stack to memory)
#define STORE    30
#define STOREB   31
#define STORE2B  32
#define STOREW   33

// compare/branch opcodes
#define BR       40
#define BE       41
#define BNE      42
#define BG       43
#define BGE      44
#define BL       45
#define BLE      46
#define BZ       47
#define BNZ      48

// type conversion opcodes
#define INT2BYTE 50
#define BYTE2INT 51

// logical not opcode
#define NOT      60

// bitwise and shift opcodes
#define BITAND   61
#define BITOR    62
#define BITXOR   63
#define BITNOT   64
#define SHL      65
#define SHR      66

// arithmetic opcodes
#define ADD     70
#define SUB     71
#define MUL     72
#define DIV     73
#define MOD     74
#define NEG     75
#define INC     76
#define DEC     77

// I/O opcodes
#define GETCH   80
#define GETINT  81
#define GETSTR  82
#define PUTBYTE 83
#define PUTCH   84
#define PUTINT  85
#define PUTEOL  86
#define PUTSTR  87

// program/procedure opcodes
#define PROGRAM 90
#define PROC    91
#define CALL    92
#define RET     93
#define ALLOC   94

// optimized returns for special constants
#define RET0   100
#define RET4   101

// prototypes

/**
 * Returns a string representation for an opcode.  Returns Byte.toString(n) if
 * the argument does not have a value equal to any of the declared opcodes.
 */
char* toString(int n);

/**
 * Returns true if this opcode has no operands.
 */
bool isZeroOperandOpcode (int opcode);

/**
 * Returns true if this opcode has a byte operand.
 */
bool isByteOperandOpcode(int opcode);

/**
 * Returns true if this opcode has an int operand.
 */
bool isIntOperandOpcode(int opcode);

#endif
