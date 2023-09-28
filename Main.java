import java.io.FileInputStream;
import java.io.IOException;

/**
 * CISS-111 Group Project 1
 * Write a disassembler for Commodore 64 (C64) program in the 6502/6510 assembly language.
 */
public class Main {

    static Object[][] Opcodes = {
            /* Name, Imm,  ZP,   ZPX,  ZPY,  ABS, ABSX, ABSY,  IND, INDX, INDY, IMPL, REL */
            {"ADC", 0x69, 0x65, 0x75, null, 0x6d, 0x7d, 0x79, null, 0x61, 0x71, null, null},
            {"AND", 0x29, 0x25, 0x35, null, 0x2d, 0x3d, 0x39, null, 0x21, 0x31, null, null},
            {"ASL", null, 0x06, 0x16, null, 0x0e, 0x1e, null, null, null, null, 0x0a, null},
            {"BIT", null, 0x24, null, null, 0x2c, null, null, null, null, null, null, null},
            {"BPL", null, null, null, null, null, null, null, null, null, null, null, 0x10},
            {"BMI", null, null, null, null, null, null, null, null, null, null, null, 0x30},
            {"BVC", null, null, null, null, null, null, null, null, null, null, null, 0x50},
            {"BVS", null, null, null, null, null, null, null, null, null, null, null, 0x70},
            {"BCC", null, null, null, null, null, null, null, null, null, null, null, 0x90},
            {"BCS", null, null, null, null, null, null, null, null, null, null, null, 0xb0},
            {"BNE", null, null, null, null, null, null, null, null, null, null, null, 0xd0},
            {"BEQ", null, null, null, null, null, null, null, null, null, null, null, 0xf0},
            {"BRK", null, null, null, null, null, null, null, null, null, null, 0x00, null},
            {"CMP", 0xc9, 0xc5, 0xd5, null, 0xcd, 0xdd, 0xd9, null, 0xc1, 0xd1, null, null},
            {"CPX", 0xe0, 0xe4, null, null, 0xec, null, null, null, null, null, null, null},
            {"CPY", 0xc0, 0xc4, null, null, 0xcc, null, null, null, null, null, null, null},
            {"DEC", null, 0xc6, 0xd6, null, 0xce, 0xde, null, null, null, null, null, null},
            {"EOR", 0x49, 0x45, 0x55, null, 0x4d, 0x5d, 0x59, null, 0x41, 0x51, null, null},
            {"CLC", null, null, null, null, null, null, null, null, null, null, 0x18, null},
            {"SEC", null, null, null, null, null, null, null, null, null, null, 0x38, null},
            {"CLI", null, null, null, null, null, null, null, null, null, null, 0x58, null},
            {"SEI", null, null, null, null, null, null, null, null, null, null, 0x78, null},
            {"CLV", null, null, null, null, null, null, null, null, null, null, 0xb8, null},
            {"CLD", null, null, null, null, null, null, null, null, null, null, 0xd8, null},
            {"SED", null, null, null, null, null, null, null, null, null, null, 0xf8, null},
            {"INC", null, 0xe6, 0xf6, null, 0xee, 0xfe, null, null, null, null, null, null},
            {"JMP", null, null, null, null, 0x4c, null, null, 0x6c, null, null, null, null},
            {"JSR", null, null, null, null, 0x20, null, null, null, null, null, null, null},
            {"LDA", 0xa9, 0xa5, 0xb5, null, 0xad, 0xbd, 0xb9, null, 0xa1, 0xb1, null, null},
            {"LDX", 0xa2, 0xa6, null, 0xb6, 0xae, null, 0xbe, null, null, null, null, null},
            {"LDY", 0xa0, 0xa4, 0xb4, null, 0xac, 0xbc, null, null, null, null, null, null},
            {"LSR", null, 0x46, 0x56, null, 0x4e, 0x5e, null, null, null, null, 0x4a, null},
            {"NOP", null, null, null, null, null, null, null, null, null, null, 0xea, null},
            {"ORA", 0x09, 0x05, 0x15, null, 0x0d, 0x1d, 0x19, null, 0x01, 0x11, null, null},
            {"TAX", null, null, null, null, null, null, null, null, null, null, 0xaa, null},
            {"TXA", null, null, null, null, null, null, null, null, null, null, 0x8a, null},
            {"DEX", null, null, null, null, null, null, null, null, null, null, 0xca, null},
            {"INX", null, null, null, null, null, null, null, null, null, null, 0xe8, null},
            {"TAY", null, null, null, null, null, null, null, null, null, null, 0xa8, null},
            {"TYA", null, null, null, null, null, null, null, null, null, null, 0x98, null},
            {"DEY", null, null, null, null, null, null, null, null, null, null, 0x88, null},
            {"INY", null, null, null, null, null, null, null, null, null, null, 0xc8, null},
            {"ROR", null, 0x66, 0x76, null, 0x6e, 0x7e, null, null, null, null, 0x6a, null},
            {"ROL", null, 0x26, 0x36, null, 0x2e, 0x3e, null, null, null, null, 0x2a, null},
            {"RTI", null, null, null, null, null, null, null, null, null, null, 0x40, null},
            {"RTS", null, null, null, null, null, null, null, null, null, null, 0x60, null},
            {"SBC", 0xe9, 0xe5, 0xf5, null, 0xed, 0xfd, 0xf9, null, 0xe1, 0xf1, null, null},
            {"STA", null, 0x85, 0x95, null, 0x8d, 0x9d, 0x99, null, 0x81, 0x91, null, null},
            {"TXS", null, null, null, null, null, null, null, null, null, null, 0x9a, null},
            {"TSX", null, null, null, null, null, null, null, null, null, null, 0xba, null},
            {"PHA", null, null, null, null, null, null, null, null, null, null, 0x48, null},
            {"PLA", null, null, null, null, null, null, null, null, null, null, 0x68, null},
            {"PHP", null, null, null, null, null, null, null, null, null, null, 0x08, null},
            {"PLP", null, null, null, null, null, null, null, null, null, null, 0x28, null},
            {"STX", null, 0x86, null, 0x96, 0x8e, null, null, null, null, null, null, null},
            {"STY", null, 0x84, 0x94, null, 0x8c, null, null, null, null, null, null, null},
            {"???", null, null, null, null, null, null, null, null, null, null, null, null}
    };

    /** Begin by opening the file passed as an argument. Read the first two bytes to determine where
     *  in memory this program is being loaded. Beginning at the next byte, set a program counter and
     *  begin disassembling instructions.
     *
     * @param bytes
     */
    // hexdump goes here...
    private static void hexdump(byte[] bytes) {
        //...
    }


    /** // disassemble goes here...
     *you need to know the length if the byte array.
     * How many  bytes do I have remaining on the file
     *
     * @param bytes
     */
    private static void disassemble (byte[] bytes) {
        // ...
    }

    /**
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        FileInputStream in = new FileInputStream("congrats.prg");

        byte[] file = in.readAllBytes();
        in.close();

        hexdump(file);
        disassemble(file);
    }
}

//Printf

/**
 *
 Working with a translation table.
 Working with existing data to build a new representation.
 Working with data type manipulations.
 Working with switch expressions.
 Working with unsigned byte data.
 Working with unique file formats.
 Confirmation program produces desired results.

 *
 * Byte 0 - 1   -> load address in little-endian format.
 * Byte 2 - end -> program as assembled from source.
 *
 * Addressing
 *    Mode       Example     Bytes
 * ----------    -------     -----
 * Implied       INX           1
 * Immediate     ORA #$44      2
 * Zero Page     ORA $44       2
 * Zero Page,X   LDA $44,X     2
 * Zero Page,Y   STX $44,Y     2
 * Absolute      ORA $4400     3
 * Absolute,X    ORA $4400,X   3
 * Absolute,Y    ORA $4400,Y   3
 * Relative      BNE $4400     2
 * Indirect      JMP $4400     3
 * Indirect,X    ORA ($44,X)   2
 * Indirect,Y    ORA ($44),Y   2
 *
 *
 *
 Translate opcode.
 Determine the addressing mode being used.
 Convert to full opcode with proper addressing form.
 Display each opcode with byte(s) and address.
 *
 *
 *
 *3000: A9 25 85 FB A9 30 8D FC .%...0..
 * 3008: 00 A0 00 B1 FB F0 07 20 .......
 * 3010: D2 FF C8 4C 0B 30 A2 00 ...L.0..
 * 3018: BD 54 30 F0 07 20 D2 FF .T0.. ..
 * 3020: E8 4C 18 30 60 20 20 20 .L.0`
 * 3028: 20 D5 C4 C4 C4 C4 C4 C4  .......
 * 3030: C4 C4 C4 C4 C4 C4 C4 C4 ........
 * 3038: C4 C4 C9 0D 20 20 20 20 ....
 * 3040: C7 43 4F 4E 47 52 41 54 .CONGRAT
 * 3048: 55 4C 41 54 49 4F 4E 53 ULATIONS
 * 3050: 21 C8 0D 00 20 20 20 20 !...
 * 3058: C7 20 20 20 59 4F 55 20 .   YOU
 * 3060: 44 49 44 20 49 54 21 20 DID IT!
 * 3068: 20 C8 0D 20 20 20 20 CA  ..    .
 * 3070: C6 C6 C6 C6 C6 C6 C6 C6 ........
 * 3078: C6 C6 C6 C6 C6 C6 C6 C6 ........
 * 3080: CB 0D 00
 *
 * 3000:   A9 25      LDA #$25
 * 3002:   85 FB      STA $FB
 * 3004:   A9 30      LDA #$30
 * 3006:   8D FC 00   STA $00FC
 * 3009:   A0 00      LDY #$00
 * 300B:   B1 FB      LDA ($FB),Y
 * 300D:   F0 07      BEQ $3016
 * 300F:   20 D2 FF   JSR $FFD2
 * 3012:   C8         INY
 * 3013:   4C 0B 30   JMP $300B
 * 3016:   A2 00      LDX #$00
 * 3018:   BD 54 30   LDA $3054,X
 * 301B:   F0 07      BEQ $3024
 * 301D:   20 D2 FF   JSR $FFD2
 * 3020:   E8         INX
 * 3021:   4C 18 30   JMP $3018
 * 3024:   60         RTS
 * 3025:   20 20 20   JSR $2020
 * 3028:   20 D5 C4   JSR $C4D5
 * 302B:   C4 C4      CPY $C4
 * 302D:   C4 C4      CPY $C4
 * 302F:   C4 C4      CPY $C4
 * 3031:   C4 C4      CPY $C4
 * 3033:   C4 C4      CPY $C4
 * 3035:   C4 C4      CPY $C4
 * 3037:   C4 C4      CPY $C4
 * 3039:   C4 C9      CPY $C9
 * 303B:   0D 20 20   ORA $2020
 * 303E:   20 20 C7   JSR $C720
 * 3041:   43         ???
 * 3042:   4F         ???
 * 3043:   4E 47 52   LSR $5247
 * 3046:   41 54      EOR ($54,X)
 * 3048:   55 4C      EOR $4C,X
 * 304A:   41 54      EOR ($54,X)
 * 304C:   49 4F      EOR #$4F
 * 304E:   4E 53 21   LSR $2153
 * 3051:   C8         INY
 * 3052:   0D 00 20   ORA $2000
 * 3055:   20 20 20   JSR $2020
 * 3058:   C7         ???
 * 3059:   20 20 20   JSR $2020
 * 305C:   59 4F 55   EOR $554F,Y
 * 305F:   20 44 49   JSR $4944
 * 3062:   44         ???
 * 3063:   20 49 54   JSR $5449
 * 3066:   21 20      AND ($20,X)
 * 3068:   20 C8 0D   JSR $0DC8
 * 306B:   20 20 20   JSR $2020
 * 306E:   20 CA C6   JSR $C6CA
 * 3071:   C6 C6      DEC $C6
 * 3073:   C6 C6      DEC $C6
 * 3075:   C6 C6      DEC $C6
 * 3077:   C6 C6      DEC $C6
 * 3079:   C6 C6      DEC $C6
 * 307B:   C6 C6      DEC $C6
 * 307D:   C6 C6      DEC $C6
 * 307F:   C6 CB      DEC $CB
 * 3081:   0D         ???
 * 3082:   00         ???
 *
 */

