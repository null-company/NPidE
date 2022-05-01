lexer grammar clj_lexer;
// Lexers
//--------------------------------------------------------------------

STRING : '"' ( ~'"' | '\\' '"' )* '"' ;
INT
   : '0' | [1-9] [0-9]*
   ;
// FIXME: Doesn't deal with arbitrary read radixes, BigNums
FLOAT
    : '-'? [0-9]+ FLOAT_TAIL
    | '-'? 'Infinity'
    | '-'? 'NaN'
    ;
OPEN_BRACKET: '(' -> pushMode(OPEN_BRACKET_MODE);
CLOSE_BRACKET: ')';

OPEN_SQUARE_BRACKET: '[';
CLOSE_SQUARE_BRACKET: ']';
OPEN_CURSIVE_BRACKET: '{';
CLOSE_CURSIVE_BRACKET: '}';
OPEN_SET_BRACKET: '#{';

SINGLE_QUOTE: '\'';
BACK_TICK: '`';

TILDA: '~';
TILDA_AT: '~@';

CARET: '^';
AT: '@';

NUMBER_SIGN: '#';
NUMBER_OPEN_BRACKET: '#(';

NUMBER_CARET: '#^';
NUMBER_QUOTE: '#\'';

NUMBER_PLUS: '#+';
NUMBER_UNDERSCORE: '#_';

COLON: ':';
fragment
FLOAT_TAIL
    : FLOAT_DECIMAL FLOAT_EXP
    | FLOAT_DECIMAL
    | FLOAT_EXP
    ;

fragment
FLOAT_DECIMAL
    : '.' [0-9]+
    ;

fragment
FLOAT_EXP
    : [eE] '-'? [0-9]+
    ;
fragment
HEXD: [0-9a-fA-F] ;
HEX: '0' [xX] HEXD+ ;
BIN: '0' [bB] [10]+ ;
LONG: '-'? [0-9]+[lL]?;
BIGN: '-'? [0-9]+[nN];

CHAR_U
    : '\\' 'u'[0-9D-Fd-f] HEXD HEXD HEXD ;
CHAR_NAMED
    : '\\' ( 'newline'
           | 'return'
           | 'space'
           | 'tab'
           | 'formfeed'
           | 'backspace' ) ;
CHAR_ANY
    : '\\' . ;

NIL : 'nil';

BOOLEAN : 'true' | 'false' ;

SYMBOL
    : '.'
    | '/'
    | NAME
    ;

NS_SYMBOL
    : NAME '/' SYMBOL
    ;

PARAM_NAME: '%' ((('1'..'9')('0'..'9')*)|'&')? ;

// Fragments
//--------------------------------------------------------------------

fragment
NAME: SYMBOL_HEAD SYMBOL_REST* (':' SYMBOL_REST+)* ;

fragment
SYMBOL_HEAD
    : ~('0' .. '9'
        | '^' | '`' | '\'' | '"' | '#' | '~' | '@' | ':' | '/' | '%' | '(' | ')' | '[' | ']' | '{' | '}' // FIXME: could be one group
        | [ \n\r\t,] // FIXME: could be WS
        )
    ;

fragment
SYMBOL_REST
    : SYMBOL_HEAD
    | '0'..'9'
    | '.'
    ;

// Discard
//--------------------------------------------------------------------

fragment
WS : [ \n\r\t,] ;

fragment
COMMENT: ';' ~[\r\n]* ;

TRASH
    : ( WS | COMMENT )
    ;
UN_ : [\u0000-\uFFFF];

mode OPEN_BRACKET_MODE;
DEFN : ('defn' | 'def') -> popMode;
FUNC_NAME
: ('.'
    | '/'
    | NAME) -> popMode;

UN: [\u0000-\uFFFF];
WS_: WS->popMode;