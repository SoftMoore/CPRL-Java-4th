#include "opcode.h"

char* toString(int n)
  {
    switch(n)
      {
        case HALT:
            return "HALT";
        case LOAD:
            return "LOAD";
        case LOADB:
            return "LOADB";
        case LOAD2B:
            return "LOAD2B";
        case LOADW:
            return "LOADW";
        case LDCB:
            return "LDCB";
        case LDCCH:
            return "LDCCH";
        case LDCINT:
            return "LDCINT";
        case LDCSTR:
            return "LDCSTR";
        case LDLADDR:
            return "LDLADDR";
        case LDGADDR:
            return "LDGADDR";
        case LDCB0:
            return "LDCB0";
        case LDCB1:
            return "LDCB1";
        case LDCINT0:
            return "LDCINT0";
        case LDCINT1:
            return "LDCINT1";
        case STORE:
            return "STORE";
        case STOREB:
            return "STOREB";
        case STORE2B:
            return "STORE2B";
        case STOREW:
            return "STOREW";
        case BR:
            return "BR";
        case BE:
            return "BE";
        case BNE:
            return "BNE";
        case BG:
            return "BG";
        case BGE:
            return "BGE";
        case BL:
            return "BL";
        case BLE:
            return "BLE";
        case BZ:
            return "BZ";
        case BNZ:
            return "BNZ";
        case INT2BYTE:
            return "INT2BYTE";
        case BYTE2INT:
            return "BYTE2INT";
        case NOT:
            return "NOT";
        case BITAND:
            return "BITAND";
        case BITOR:
            return "BITOR";
        case BITXOR:
            return "BITXOR";
        case BITNOT:
            return "BITNOT";
        case SHL:
            return "SHL";
        case SHR:
            return "SHR";
        case ADD:
            return "ADD";
        case SUB:
            return "SUB";
        case MUL:
            return "MUL";
        case DIV:
            return "DIV";
        case MOD:
            return "MOD";
        case NEG:
            return "NEG";
        case INC:
            return "INC";
        case DEC:
            return "DEC";
        case GETCH:
            return "GETCH";
        case GETINT:
            return "GETINT";
        case GETSTR:
            return "GETSTR";
        case PUTBYTE:
            return "PUTBYTE";
        case PUTCH:
            return "PUTCH";
        case PUTINT:
            return "PUTINT";
        case PUTEOL:
            return "PUTEOL";
        case PUTSTR:
            return "PUTSTR";
        case PROGRAM:
            return "PROGRAM";
        case PROC:
            return "PROC";
        case CALL:
            return "CALL";
        case RET:
            return "RET";
        case ALLOC:
            return "ALLOC";
        case RET0:
            return "RET0";
        case RET4:
            return "RET4";
        default:
            return "**Unknown**";
      }
  }

bool isZeroOperandOpcode (int opcode)
  {
    switch (opcode)
      {
        case ADD:
        case BITAND:
        case BITNOT:
        case BITOR:
        case BITXOR:
        case BYTE2INT:
        case DEC:
        case DIV:
        case GETCH:
        case GETINT:
        case HALT:
        case INC:
        case INT2BYTE:
        case LOADB:
        case LOAD2B:
        case LOADW:
        case LDCB0:
        case LDCB1:
        case LDCINT0:
        case LDCINT1:
        case MOD:
        case MUL:
        case NEG:
        case NOT:
        case PUTBYTE:
        case PUTCH:
        case PUTINT:
        case PUTEOL:
        case RET0:
        case RET4:
        case SHL:
        case SHR:
        case STOREB:
        case STORE2B:
        case STOREW:
        case SUB:
            return true;
        default:
            return false;
      };
  }

bool isByteOperandOpcode(int opcode)
  {
    return opcode == LDCB ? true : false;
  }

bool isIntOperandOpcode(int opcode)
  {
    switch (opcode)
      {
        case ALLOC:
        case BR:
        case BE:
        case BNE:
        case BG:
        case BGE:
        case BL:
        case BLE:
        case BZ:
        case BNZ:
        case CALL:
        case GETSTR:
        case LOAD:
        case LDCINT:
        case LDLADDR:
        case LDGADDR:
        case PROC:
        case PROGRAM:
        case PUTSTR:
        case RET:
        case STORE:
            return true;
        default:
            return false;
      };
  }
