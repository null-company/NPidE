grammar CDM8;

s:
    (inst)*?
;

inst
     :
      ID COLON #def
     |ID #usage
     |.+? #nomatters
;

COLON:':';

REGISTER:
    R1 | R2 | R3 | R0
;
R1: 'r1';
R2: 'r2';
R3: 'r3';
R0: 'r0';

/* Instruction that have two registers as target*/
RR_INSTR:
    'ld' | 'st' | 'move' | 'add' |'addc' |' sub'| 'cmp'| 'and' |'or' | 'xor'
;

/* Instruction that take one register parametr */
R_INSTR:
    'neg'| 'dec'| 'inc'| 'shr'| 'shra'| 'shla'| 'rol' |'push'| 'pop'|
    'stsp'| 'ldsp'| 'tst'| 'clr'
;
/* Instruction that take one const and one register parametrs */
RC_INSTR:
    'ldc' | 'ldi' | 'ldsa'
;

C_INSTR:
    'beq'| 'bz'| 'bne'| 'bnz'| 'bhs'| 'bcs'| 'blo'|
    'bcc'| 'bmi'| 'bpl'| 'bvs'| 'bvc'| 'bhi'|'bls'|
    'bge'| 'blt'| 'bgt'| 'ble'| 'br' |
    'jsr' | 'osix' | 'setsp' | 'addsp'
;

INSTR:
    'wait'| 'halt' | 'nop' | 'rts' | 'crc' | 'rti' | 'pushall'| 'popall' | 'ioi'
;

C_PSEUDO_INSTR
    : 'asect' |  'dc' | 'ds' | 'run'
;
ID_PSEUDO_INSTR
    : 'rsect' | 'tplate'
;

CMP_KEYWORD
: 'gt'| 'lt'| 'le'| 'ge'| 'mi'| 'pl'| 'eq'| 'ne'| 'z'| 'nz'| 'cs'| 'cc'| 'vs'| 'vc' | 'hi' | 'lo' | 'hs'| 'ls'
;

LOOP_KEYWORD
    :'continue'
    | 'break'
    ;
MACRO
    :'macro'
;

C_MACRO_INST
    : 'mpop' | 'mpush' 
;

R_MACRO_INST
    : 'save'
;

PREDEFINED_MACRO_INSTRUCTIONS
    : 'jmp'| 'jsrr'| 'shl'| 'banything'| 'bngt'| 'bnge'| 'bneq'|
                          'bnne'| 'bnlt'| 'bnle'| 'bnhi'| 'bnhs'| 'bncs'| 'bnlo'|
                          'bnls'| 'bncc'| 'bnmi'| 'bnpl'| 'bnfalse'| 'bntrue'|
                          'bnvs'| 'bnvc'| 'define'| 'ldv'| 'stv'
;
WS
:    [ \t\u00A0\uFEFF\u2003,] + -> channel(1)
;
NEWLINE
  : ('\r' ? '\n' | '\r') ->channel(1);

NUMBER
   : '0' [xX] HEX+
   | '0' [b] BIN+
   | INT
   ;

fragment INT
   : '0' | [1-9] [0-9]*
   ;

fragment HEX
    : [0-9a-fA-F]
    ;
fragment BIN
    : '0' | '1'
    ;

ID: Identifier;
fragment Identifier
    :   IdentifierNondigit
        (   IdentifierNondigit
        |   Digit
        )*
    ;

fragment
IdentifierNondigit
    :   Nondigit
    |   UniversalCharacterName
    //|   // other implementation-defined characters...
    ;

fragment Nondigit
    :   [a-zA-Z_]
    ;

fragment Digit
    :   [0-9]
    ;
fragment UniversalCharacterName
    :   '\\u' HexQuad
    |   '\\U' HexQuad HexQuad
    ;

fragment HexQuad
    :   HexadecimalDigit HexadecimalDigit HexadecimalDigit HexadecimalDigit
    ;
fragment HexadecimalDigit
        :   [0-9a-fA-F]
        ;

COMMENT
 : '#' ~[\r\n\f]*
 ;