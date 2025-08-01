#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#include <wchar.h>
#include <stdbool.h>
#include <locale.h>
#include "opcode.h"


/**
 * This C program implements a virtual machine for the programming language
 * CPRL.  It interprets instructions for a hypothetical CPRL computer.
 */

typedef int8_t byte;    // analogous to type byte in Java

// declare prototypes
void loadProgram(FILE* fp);
void run();
void error(wchar_t* message);

const bool  DEBUG  = false;
const char* SUFFIX = ".obj";

const int BYTES_PER_INTEGER = 4;
const int BYTES_PER_CHAR    = 2;
const int BYTES_PER_CONTEXT = 8;    // 2 saved addresses: PC and BP


// exit return value for failure
const int FAILURE = -1;

// virtual machine constant for false
const byte FALSE = (byte) 0;

// virtual machine constant for true
const byte TRUE = (byte) 1;

// default memory size (in bytes) for the virtual machine
#define NUM_BYTES_MEMORY 8192    // 8*K

// computer memory (for the virtual CPRL machine)
byte memory[NUM_BYTES_MEMORY];

// program counter (index of the next instruction in memory)
int pc = 0;

// base pointer
int bp = 0;

// stack pointer (index of the top of the stack)
int sp = 0;

// bottom of the stack
int sb = 0;

// true if the virtual computer is currently running
bool running = false;

/**
 * This function initializes a CPRL virtual machine, loads into memory the
 * byte code from the file specified by args[1], and runs the byte code.
 */
int main(int argc, char* argv[])
  {
    if (argc != 2)
        error(L"Usage: cvm filename\n");

// set locale based on environment
#if defined(_WIN64) || defined(_WIN32)
    setlocale(LC_ALL, ".UTF-8");   // works for windows
#elif defined(__linux__) || defined(__MACH__) || defined(__unix__)
    setlocale(LC_ALL, "");         // works for bash
#endif

    // initialize memory
    for (int i = 0; i < NUM_BYTES_MEMORY; ++i)
        memory[i] = 0;

    char* filename = argv[1];

    // check that filename ends in ".obj"
    char *dot = strrchr(filename, '.');
    if (!dot || strcmp(dot, ".obj") != 0)
      {
        printf("... appending \".obj\" to %s\n", filename);
        char* newfilename = (char*) malloc((strlen(filename) + 5)*sizeof(char));
        strcpy(newfilename, filename);
        strcat(newfilename, ".obj");
        filename = newfilename;
        printf("... filename changed to %s\n", filename);
      }

    FILE *fp = fopen(filename, "rb");
    if (fp)
      {
        loadProgram(fp);
        run();
      }
    else
      {
        fprintf(stderr, "Error opening file %s\n", filename);
        exit(FAILURE);
      }
  }

/**
 * Loads the program into memory.
 *
 * @param fp pointer to the object code file
 */
void loadProgram(FILE* fp)
  {
    // byte buffer for reading code file
    byte buffer[4096];    // 4K

    int address = 0;
    int bytesRead;
    int inByte;

    bytesRead = fread(buffer, 1, sizeof(buffer), fp);
    while (bytesRead > 0)
      {
        for(int i = 0; i < bytesRead; ++i)
            memory[address++] = buffer[i];

        bytesRead = fread(buffer, 1, sizeof(buffer), fp);
      }

    bp = address;
    sb = address;
    sp = bp - 1;
    fclose(fp);
  }

// Start: helper functions and internal machine instructions that do NOT correspond to opcodes
// -------------------------------------------------------------------------------------------

/**
 * Print an error message and exit with nonzero status code.
 */
void error(wchar_t* message)
  {
    fwprintf(stderr, L"%ls\n", message);
    exit(FAILURE);
  }

/**
 * Converts 2 bytes to a wide char.  The bytes passed as arguments are
 * ordered with b0 as the high order byte and b1 as the low order byte.
 */
wchar_t bytesToChar(byte b0, byte b1)
  {
    return (wchar_t) ((((int) b0 << 8) & 0x0000FF00) | ((int) b1 & 0x000000FF));
  }

/**
 * Converts 4 bytes to an int.  The bytes passed as arguments are
 * ordered with b0 as the high order byte and b3 as the low order byte.
 */
int bytesToInt(byte b0, byte b1, byte b2, byte b3)
  {
    return (int) b0 << 24 & 0xFF000000
         | (int) b1 << 16 & 0x00FF0000
         | (int) b2 << 8  & 0x0000FF00
         | (int) b3       & 0x000000FF;
  }

/**
 * Converts a wide char to an array of 2 bytes.  The elements in array
 * bytes are ordered with the byte at index 0 as the high order byte
 * and the byte at index 1 as the low order byte.
 */
void charToBytes(wchar_t c, byte* bytes)
  {
    bytes[0] = (byte) ((c >> 8) & 0x00FF);
    bytes[1] = (byte) ((c >> 0) & 0x00FF);
  }

/**
 * Converts an int to an array of 4 bytes.  The elements in array
 * bytes are ordered with the byte at index 0 as the high order byte
 * and the byte at index 3 as the low order byte.
 */
void intToBytes(int n, byte* bytes)
  {
    bytes[0] = (byte) ((n >> 24) & 0x000000FF);
    bytes[1] = (byte) ((n >> 16) & 0x000000FF);
    bytes[2] = (byte) ((n >> 8)  & 0x000000FF);
    bytes[3] = (byte) ((n >> 0)  & 0x000000FF);
  }

/**
 * Pop the top byte off the stack and return its value.
 */
byte popByte()
  {
    return memory[sp--];
  }

/**
 * Pop the top (wide) character off the stack and return its value.
 */
wchar_t popChar()
  {
    byte b1 = popByte();
    byte b0 = popByte();
    return bytesToChar(b0, b1);
  }

/**
 * Pop the top integer off the stack and return its value.
 */
int popInt()
  {
    byte b3 = popByte();
    byte b2 = popByte();
    byte b1 = popByte();
    byte b0 = popByte();
    return bytesToInt(b0, b1, b2, b3);
  }

/**
 * Push a byte onto the stack.
 */
void pushByte(byte b)
  {
    memory[++sp] = b;
  }

/**
 * Push a (wide) character onto the stack.
 */
void pushChar(wchar_t c)
  {
    byte bytes[2];
    charToBytes(c, bytes);
    pushByte(bytes[0]);
    pushByte(bytes[1]);
  }

/**
 * Push an integer onto the stack.
 */
void pushInt(int n)
  {
    byte bytes[4];
    intToBytes(n, bytes);
    pushByte(bytes[0]);
    pushByte(bytes[1]);
    pushByte(bytes[2]);
    pushByte(bytes[3]);
  }

/**
 * Fetch the next instruction/byte from memory.
 */
byte fetchByte()
  {
    return memory[pc++];
  }

/**
 * Fetch the next instruction wide char operand from memory.
 */
wchar_t fetchChar()
  {
    byte b0 = fetchByte();
    byte b1 = fetchByte();
    return bytesToChar(b0, b1);
  }

/**
 * Fetch the next instruction int operand from memory.
 */
int fetchInt()
  {
    byte b0 = fetchByte();
    byte b1 = fetchByte();
    byte b2 = fetchByte();
    byte b3 = fetchByte();
    return bytesToInt(b0, b1, b2, b3);
  }

/**
 * Returns the (wide) character at the specified memory address.
 * Does not alter pc, sp, or bp.
 */
wchar_t getCharAtAddr(int address)
  {
    byte b0 = memory[address + 0];
    byte b1 = memory[address + 1];
    return bytesToChar(b0, b1);
  }

/**
 * Returns the integer at the specified memory address.
 * Does not alter pc, sp, or bp.
 */
int getIntAtAddr(int address)
  {
    byte b0 = memory[address + 0];
    byte b1 = memory[address + 1];
    byte b2 = memory[address + 2];
    byte b3 = memory[address + 3];
    return bytesToInt(b0, b1, b2, b3);
  }

/**
 * Returns the word at the specified memory address.
 * Does not alter pc, sp, or bp.
 */
int getWordAtAddr(int address)
  {
    return getIntAtAddr(address);
  }

/**
 * Writes the wide char value to the specified memory address.
 * Does not alter pc, sp, or bp.
 */
void putCharToAddr(wchar_t value, int address)
  {
    byte bytes[2];
    charToBytes(value, bytes);
    memory[address + 0] = bytes[0];
    memory[address + 1] = bytes[1];
  }

/**
 * Writes the integer value to the specified memory address.
 * Does not alter pc, sp, or bp.
 */
void putIntToAddr(int value, int address)
  {
    byte bytes[4];
    intToBytes(value, bytes);
    memory[address + 0] = bytes[0];
    memory[address + 1] = bytes[1];
    memory[address + 2] = bytes[2];
    memory[address + 3] = bytes[3];
  }

/**
 * Writes the word value to the specified memory address.
 * Does not alter pc, sp, or bp.
 */
void putWordToAddr(int value, int address)
  {
    putIntToAddr(value, address);
  }

/**
 * Read a line from stdin into s (excluding '\n'); return length.
 */
size_t getln (wchar_t s[], size_t lim)
  {
    size_t i = 0;
    wint_t c;

    while (--lim > 0 && (c = getwchar()) != WEOF && c != L'\n')
      {
        if (c != L'\r')
            s[i++] = c;
      }

    s[i] = L'\0';

    return i;
  }

// -----------------------------------------------------------------------------------------
// End: helper functions and internal machine instructions that do NOT correspond to opcodes
// Start: machine instructions corresponding to opcodes
// -----------------------------------------------------------------------------------------

void add()
  {
    int operand2 = popInt();
    int operand1 = popInt();
    pushInt(operand1 + operand2);
  }

void allocate()
  {
    int numBytes = fetchInt();
    sp = sp + numBytes;
    if (sp >= NUM_BYTES_MEMORY)
        error(L"*** Out of memory ***");
  }

void bitAnd()
  {
    int operand2 = popInt();
    int operand1 = popInt();
    pushInt(operand1 & operand2);
  }

void bitOr()
  {
    int operand2 = popInt();
    int operand1 = popInt();
    pushInt(operand1 | operand2);
  }

void bitXor()
  {
    int operand2 = popInt();
    int operand1 = popInt();
    pushInt(operand1 ^ operand2);
  }

 void bitNot()
  {
    int operand = popInt();
    pushInt(~operand);
  }

void branch()
  {
    int displacement = fetchInt();
    pc = pc + displacement;
  }

void branchEqual()
  {
    int displacement = fetchInt();
    int operand2 = popInt();
    int operand1 = popInt();

    if (operand1 == operand2)
        pc = pc + displacement;
  }

void branchNotEqual()
  {
    int displacement = fetchInt();
    int operand2 = popInt();
    int operand1 = popInt();

    if (operand1 != operand2)
        pc = pc + displacement;
  }

void branchGreater()
  {
    int displacement = fetchInt();
    int operand2 = popInt();
    int operand1 = popInt();

    if (operand1 > operand2)
        pc = pc + displacement;
  }

void branchGreaterOrEqual()
  {
    int displacement = fetchInt();
    int operand2 = popInt();
    int operand1 = popInt();

    if (operand1 >= operand2)
        pc = pc + displacement;
  }

void branchLess()
  {
    int displacement = fetchInt();
    int operand2 = popInt();
    int operand1 = popInt();

    if (operand1 < operand2)
        pc = pc + displacement;
  }

void branchLessOrEqual()
  {
    int displacement = fetchInt();
    int operand2 = popInt();
    int operand1 = popInt();

    if (operand1 <= operand2)
        pc = pc + displacement;
  }

void branchZero()
  {
    int  displacement = fetchInt();
    byte value = popByte();

    if (value == 0)
        pc = pc + displacement;
  }

void branchNonZero()
  {
    int  displacement = fetchInt();
    byte value = popByte();

    if (value != 0)
        pc = pc + displacement;
  }

void byteToInteger()
  {
    byte b = popByte();
    pushInt((int) b);
  }

void call()
  {
    int displacement = fetchInt();

    pushInt(bp);   // dynamic link
    pushInt(pc);   // return address

    // set bp to starting address of new frame
    bp = sp - BYTES_PER_CONTEXT + 1;

    // set pc to first statement of called procedure
    pc = pc + displacement;
  }

void decrement()
  {
    int operand = popInt();
    pushInt(operand - 1);
  }

void divide()
  {
    int operand2 = popInt();
    int operand1 = popInt();

    if (operand2 != 0)
        pushInt(operand1/operand2);
    else
        error(L"*** FAULT: Divide by zero ***");
  }

void getCh()
  {
    int destAddr = popInt();
    wint_t ch = getwchar();

    if (ch == WEOF)
        error(L"Invalid input: EOF");

    putCharToAddr((wchar_t) ch, destAddr);
  }

void getInt()
  {
    int n;
    int destAddr = popInt();

    int result = wscanf(L"%d", &n);
    if (result != EOF)
        putIntToAddr(n, destAddr);
    else
        error(L"Invalid input");
  }

void getString()
  {
    int destAddr = popInt();
    int capacity = fetchInt();

    wchar_t* data = (wchar_t*) malloc(sizeof(wchar_t)*capacity);
    int length = getln(data, capacity);

    putIntToAddr(length, destAddr);
    destAddr = destAddr + BYTES_PER_INTEGER;
    for (int i = 0; i < length; ++i)
      {
        putCharToAddr(data[i], destAddr);
        destAddr = destAddr + BYTES_PER_CHAR;
      }

    free(data);
  }

void halt()
  {
    running = false;
  }

void increment()
  {
    int operand = popInt();
    pushInt(operand + 1);
  }

void intToByte()
  {
    int n = popInt();
    pushByte((byte) n);
  }

void load()
  {
    int length  = fetchInt();
    int address = popInt();

    for (int i = 0; i < length; ++i)
        pushByte(memory[address + i]);
  }

void loadConstByte()
  {
    byte b = fetchByte();
    pushByte(b);
  }

void loadConstByteZero()
  {
    pushByte((byte) 0);
  }

void loadConstByteOne()
  {
    pushByte((byte) 1);
  }

void loadConstCh()
  {
    char ch = fetchChar();
    pushChar(ch);
  }

void loadConstInt()
  {
    int value = fetchInt();
    pushInt(value);
  }

void loadConstIntZero()
  {
    pushInt(0);
  }

void loadConstIntOne()
  {
    pushInt(1);
  }

void loadConstStr()
  {
    int capacity = fetchInt();
    pushInt(capacity);

    // fetch each character and push it onto the stack
    for (int i = 0; i < capacity; ++i)
        pushChar(fetchChar());
  }

void loadLocalAddress()
  {
    int displacement = fetchInt();
    pushInt(bp + displacement);
  }

void loadGlobalAddress()
  {
    int displacement = fetchInt();
    pushInt(sb + displacement);
  }

void loadByte()
  {
    int  address = popInt();
    byte b = memory[address];
    pushByte(b);
  }

void load2Bytes()
  {
    int  address = popInt();
    byte b0 = memory[address + 0];
    byte b1 = memory[address + 1];
    pushByte(b0);
    pushByte(b1);
  }

void loadWord()
  {
    int address = popInt();
    int word = getWordAtAddr(address);
    pushInt(word);
  }

void modulo()
  {
    int operand2 = popInt();
    int operand1 = popInt();
    pushInt(operand1%operand2);
  }

void multiply()
  {
    int operand2 = popInt();
    int operand1 = popInt();
    pushInt(operand1*operand2);
  }

void negate()
  {
    int operand1 = popInt();
    pushInt(-operand1);
  }

void not()
  {
    byte operand = popByte();
    pushByte(operand == FALSE ? TRUE : FALSE);
  }

void procedure()
  {
    allocate();
  }

void program()
  {
    int varLength = fetchInt();

    bp = sb;
    sp = bp + varLength - 1;

    if (sp >= NUM_BYTES_MEMORY)
        error(L"*** Out of memory ***");
  }

void putChar()
  {
    putwchar(popChar());
  }

void putByte()
  {
    wprintf(L"%d", popByte());
  }

void putInt()
  {
    wprintf(L"%d", popInt());
  }

void putEOL()
  {
    putwchar(L'\n');
  }

void putString()
  {
    int capacity = fetchInt();

    // number of bytes in the string
    int numBytes = BYTES_PER_INTEGER + capacity*BYTES_PER_CHAR;

    int addr = sp - numBytes + 1;
    int strLength = getIntAtAddr(addr);
    addr = addr + BYTES_PER_INTEGER;

//    setlocale(LC_CTYPE, "en_US.UTF-8");

    for (int i = 0; i < strLength; ++i)
      {
        putwchar(getCharAtAddr(addr));
        addr = addr + BYTES_PER_CHAR;
      }
    fflush(stdout);

    // remove (pop) the string off the stack
    sp = sp - capacity;
  }

void returnInst()
  {
    int paramLength = fetchInt();
    pc = getIntAtAddr(bp + BYTES_PER_INTEGER);
    sp = bp - paramLength - 1;
    bp = getIntAtAddr(bp);
  }

void returnZero()
  {
    pc = getIntAtAddr(bp + BYTES_PER_INTEGER);
    sp = bp - 1;
    bp = getIntAtAddr(bp);
  }

void returnFour()
  {
    pc = getIntAtAddr(bp + BYTES_PER_INTEGER);
    sp = bp - 5;
    bp = getIntAtAddr(bp);
  }

void shiftLeft()
  {
    int operand2 = popInt();
    int operand1 = popInt();

    // zero out all except rightmost 5 bits of shiftAmount
    int shiftAmount = operand2 & 0b11111;

    pushInt(operand1 << shiftAmount);
  }

void shiftRight()
  {
    int operand2 = popInt();
    int operand1 = popInt();

    // zero out all except rightmost 5 bits of shiftAmount
    int shiftAmount = operand2 & 0b11111;

    pushInt(operand1 >> shiftAmount);
  }

void store()
  {
    int length   = fetchInt();
    int destAddr = getIntAtAddr(sp - length - 3);

    // pop bytes of data, storing in reverse order
    for (int i = length - 1; i >= 0; --i)
        memory[destAddr + i] = popByte();

    popInt();   // remove destAddr from stack
  }

void storeByte()
  {
    byte value = popByte();
    int  destAddr = popInt();
    memory[destAddr] = value;
  }

void store2Bytes()
  {
    byte byte1 = popByte();
    byte byte0 = popByte();
    int  destAddr = popInt();
    memory[destAddr + 0] = byte0;
    memory[destAddr + 1] = byte1;
  }

void storeWord()
  {
    int value = popInt();
    int destAddr = popInt();
    putWordToAddr(value, destAddr);
  }

void subtract()
  {
    int operand2 = popInt();
    int operand1 = popInt();
    int result   = operand1 - operand2;
    pushInt(result);
  }

// -----------------------------------------------------------------------------------------
// End: machine instructions corresponding to opcodes
// -----------------------------------------------------------------------------------------

/**
 * Prints values of internal registers to standard output.
 */
void printRegisters()
  {
    printf("PC=%d, BP=%d, SB=%d, SP=%d\n", pc, bp, sb, sp);
  }

/**
 * Prints a view of memory to standard output.
 */
void printMemory()
  {
    int  memAddr = 0;
    byte byte0;
    byte byte1;
    byte byte2;
    byte byte3;

    while (memAddr < sb)
      {
        // Prints "PC ->" in front of the correct memory address
        if (pc == memAddr)
            printf("PC ->");
        else
            printf("     ");

        int opcode = memory[memAddr];
        char* opcodeStr = toString(opcode);

        if (isZeroOperandOpcode(opcode))
          {
            printf("%4d:  %s\n", memAddr, opcodeStr);
            ++memAddr;
          }
        else if (isByteOperandOpcode(opcode))
          {
            printf("%4d:  %s", memAddr, opcodeStr);
            ++memAddr;
            printf(" %d\n", memory[memAddr++]);

          }
        else if (isIntOperandOpcode(opcode))
          {
            printf("%4d:  %s", memAddr, opcodeStr);
            ++memAddr;
            byte0 = memory[memAddr++];
            byte1 = memory[memAddr++];
            byte2 = memory[memAddr++];
            byte3 = memory[memAddr++];
            printf(" %d\n", bytesToInt(byte0, byte1, byte2, byte3));
          }
        else if (opcode == LDCCH)
          {
            // special case: LDCCH
            printf("%4d:  %s", memAddr, opcodeStr);
            ++memAddr;
            byte0 = memory[memAddr++];
            byte1 = memory[memAddr++];
            printf(" %c\n", bytesToChar(byte0, byte1));

          }
        else if (opcode == LDCSTR)
          {
            // special case: LDCSTR
            printf("%4d:  %s", memAddr, opcodeStr);
            ++memAddr;
            // now print the string
            printf("  \"");
            byte0 = memory[memAddr++];
            byte1 = memory[memAddr++];
            byte2 = memory[memAddr++];
            byte3 = memory[memAddr++];
            int strLength = bytesToInt(byte0, byte1, byte2, byte3);
            for (int i = 0; i < strLength; ++i)
              {
                byte0 = memory[memAddr++];
                byte1 = memory[memAddr++];
                printf("%c", bytesToChar(byte0, byte1));
               }
             printf("\"\n");
          }
        else
            printf("*** PrintMemory: Unknown opcode %d ***\n", opcode);
      }

    // now print remaining values that compose the stack
    for (memAddr = sb; memAddr <= sp; ++memAddr)
      {
        // Prints "SB ->", "BP ->", and "SP ->" in front of the correct memory address
        if (sb == memAddr)
            printf("SB ->");
        else if (bp == memAddr)
            printf("BP ->");
        else if (sp == memAddr)
            printf("SP ->");
        else
            printf("     ");

        printf("%4d:  %d\n", memAddr, memory[memAddr]);
      }

    printf("\n");
  }

/**
 * Prompt user and wait for user to press the enter key.
 */
void pause()
  {
    int ch;
    printf("Press enter to continue...\n");
    ch = getchar();
    while (ch != '\n' && ch != EOF)
        ch = getchar();
  }

void run()
  {
    running = true;
    pc = 0;

    while (running)
      {
        if (DEBUG)
          {
            printRegisters();
            printMemory();
            pause();
          }

        switch (fetchByte())
          {
            case ADD:      add();                  break;
            case ALLOC:    allocate();             break;
            case BITAND:   bitAnd();               break;
            case BITOR:    bitOr();                break;
            case BITXOR:   bitXor();               break;
            case BITNOT:   bitNot();               break;
            case BR:       branch();               break;
            case BE:       branchEqual();          break;
            case BNE:      branchNotEqual();       break;
            case BG:       branchGreater();        break;
            case BGE:      branchGreaterOrEqual(); break;
            case BL:       branchLess();           break;
            case BLE:      branchLessOrEqual();    break;
            case BZ:       branchZero();           break;
            case BNZ:      branchNonZero();        break;
            case BYTE2INT: byteToInteger();        break;
            case CALL:     call();                 break;
            case DEC:      decrement();            break;
            case DIV:      divide();               break;
            case GETCH:    getCh();                break;
            case GETINT:   getInt();               break;
            case GETSTR:   getString();            break;
            case HALT:     halt();                 break;
            case INC:      increment();            break;
            case INT2BYTE: intToByte();            break;
            case LDCB:     loadConstByte();        break;
            case LDCB0:    loadConstByteZero();    break;
            case LDCB1:    loadConstByteOne();     break;
            case LDCCH:    loadConstCh();          break;
            case LDCINT:   loadConstInt();         break;
            case LDCINT0:  loadConstIntZero();     break;
            case LDCINT1:  loadConstIntOne();      break;
            case LDCSTR:   loadConstStr();         break;
            case LDLADDR:  loadLocalAddress();     break;
            case LDGADDR:  loadGlobalAddress();    break;
            case LOAD:     load();                 break;
            case LOADB:    loadByte();             break;
            case LOAD2B:   load2Bytes();           break;
            case LOADW:    loadWord();             break;
            case MOD:      modulo();               break;
            case MUL:      multiply();             break;
            case NEG:      negate();               break;
            case NOT:      not();                  break;
            case PROC:     procedure();            break;
            case PROGRAM:  program();              break;
            case PUTBYTE:  putByte();              break;
            case PUTCH:    putChar();              break;
            case PUTEOL:   putEOL();               break;
            case PUTINT:   putInt();               break;
            case PUTSTR:   putString();            break;
            case RET:      returnInst();           break;
            case RET0:     returnZero();           break;
            case RET4:     returnFour();           break;
            case SHL:      shiftLeft();            break;
            case SHR:      shiftRight();           break;
            case STORE:    store();                break;
            case STOREB:   storeByte();            break;
            case STORE2B:  store2Bytes();          break;
            case STOREW:   storeWord();            break;
            case SUB:      subtract();             break;
            default:       error(L"invalid machine instruction");
          }
      }
  }
