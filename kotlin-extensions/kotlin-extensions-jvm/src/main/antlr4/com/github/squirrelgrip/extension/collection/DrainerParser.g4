parser grammar DrainerParser;

options {
    tokenVocab = DrainerLexer;
}

predicate: expression EOF;

expression:
  literal #LiteralExpression
  | value #TextExpression
  | glob #GlobExpression
  | START_REGEX regex IN_REGEX_END_REGEX #RegexExpression
  | NOT expression #NotExpression
  | LPAREN expression RPAREN #ParenExpression
  | expression AND expression #AndExpression
  | expression OR expression #OrExpression
  | expression XOR expression #XorExpression
  | expression IMPLIES expression #ImpliesExpression;

literal: BOOLEAN;
text: IN_STRING_ESCAPED_TEXT | IN_STRING_TEXT;
part: ESCAPED_OPERAND | VALUE;

quotedValueExpression: DOUBLE_QUOTE text* IN_STRING_DOUBLE_QUOTE;
quotedGlobExpression: DOUBLE_QUOTE globText+ IN_STRING_DOUBLE_QUOTE;

globText: text* IN_STRING_GLOB_CHAR text*;
globPart: part* GLOB_CHAR part*;

regexPart: IN_REGEX_ESCAPED_TEXT | IN_REGEX_TEXT;

regex: regexPart+;
glob: globPart+ | quotedGlobExpression;
value: part+ | quotedValueExpression;
