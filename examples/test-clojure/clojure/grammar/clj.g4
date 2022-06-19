/* Reworked for grammar specificity by Reid Mckenzie. Did a bunch of
   work so that rather than reading "a bunch of crap in parens" some
   syntactic information is preserved and recovered. Dec. 14 2014.
   Converted to ANTLR 4 by Terence Parr. Unsure of provence. I see
   it commited by matthias.koester for clojure-eclipse project on
   Oct 5, 2009:
   https://code.google.com/p/clojure-eclipse/
   Seems to me Laurent Petit had a version of this. I also see
   Jingguo Yao submitting a link to a now-dead github project on
   Jan 1, 2011.
   https://github.com/laurentpetit/ccw/tree/master/clojure-antlr-grammar
   Regardless, there are some issues perhaps related to "sugar";
   I've tried to fix them.
   This parses https://github.com/weavejester/compojure project.
   I also note this is hardly a grammar; more like "match a bunch of
   crap in parens" but I guess that is LISP for you ;)
 */

parser grammar clj;

options {tokenVocab=clj_lexer;}
s: form*;
form: ID #global_def
    | ID #def
    | .+? #nomatters;
//form: literal
//    | list_
//    | vector
//    | map_
//    | reader_macro
//    ;
//
//forms: form* ;
//
//list_: OPEN_BRACKET forms CLOSE_BRACKET;
//
//vector: OPEN_SQUARE_BRACKET forms CLOSE_SQUARE_BRACKET ;
//
//map_: OPEN_CURSIVE_BRACKET (form form)* CLOSE_CURSIVE_BRACKET ;
//
//set_: OPEN_SET_BRACKET forms CLOSE_CURSIVE_BRACKET ;
//
//reader_macro
//    : lambda_
//    | meta_data
//    | regex
//    | var_quote
//    | host_expr
//    | set_
//    | tag
//    | discard
//    | dispatch
//    | deref
//    | quote
//    | backtick
//    | unquote
//    | unquote_splicing
//    | gensym
//    ;
//
//// TJP added '&' (gather a variable number of arguments)
//quote
//    : SINGLE_QUOTE form
//    ;
//
//backtick
//    : BACK_TICK form
//    ;
//
//unquote
//    : TILDA form
//    ;
//
//unquote_splicing
//    : TILDA_AT form
//    ;
//
//tag
//    : CARET form form
//    ;
//
//deref
//    : AT form
//    ;
//
//gensym
//    : SYMBOL NUMBER_SIGN
//    ;
//
//lambda_
//    : NUMBER_OPEN_BRACKET form* CLOSE_BRACKET
//    ;
//
//meta_data
//    : NUMBER_CARET (map_ form | form)
//    ;
//
//var_quote
//    : NUMBER_QUOTE symbol
//    ;
//
//host_expr
//    : NUMBER_PLUS form form
//    ;
//
//discard
//    : NUMBER_UNDERSCORE form
//    ;
//
//dispatch
//    : NUMBER_SIGN symbol form
//    ;
//
//regex
//    : NUMBER_SIGN string_
//    ;
//
//literal
//    : string_
//    | number
//    | character
//    | nil_
//    | BOOLEAN
//    | keyword
//    | symbol
//    | param_name
//    ;
//
//string_: STRING;
//hex_: HEX;
//bin_: BIN;
//bign: BIGN;
//number
//    : FLOAT
//    | hex_
//    | bin_
//    | bign
//    | LONG
//    ;
//
//character
//    : named_char
//    | u_hex_quad
//    | any_char
//    ;
//named_char: CHAR_NAMED ;
//any_char: CHAR_ANY ;
//u_hex_quad: CHAR_U ;
//
//nil_: NIL;
//
//keyword: macro_keyword | simple_keyword;
//simple_keyword: COLON symbol;
//macro_keyword: COLON COLON symbol;
//
//symbol: ns_symbol | simple_sym;
//simple_sym: SYMBOL;
//ns_symbol: NS_SYMBOL;
//
//param_name: PARAM_NAME;

