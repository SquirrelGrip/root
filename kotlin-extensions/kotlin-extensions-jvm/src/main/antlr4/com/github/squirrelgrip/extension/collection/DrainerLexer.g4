lexer grammar DrainerLexer;

BOOLEAN: 'true' | 'TRUE' | 'false' | 'FALSE';
GLOB_CHAR: [*?];
LPAREN: '(';
RPAREN: ')';
AND: '&';
OR: '|';
XOR: '^';
NOT: '!';
IMPLIES: '=>';
DOUBLE_QUOTE: '"' {setText("");} -> pushMode(IN_STRING);
ESCAPED_OPERAND: '\\' ([\\"?*~()!&|^=]) {setText(getText().substring(1));};
START_REGEX: '~' {setText("");} -> pushMode(IN_REGEX);
VALUE: ~[ \\"?*~()!&|^=]+;
WS: [ \t\r\n]+ -> skip;

mode IN_STRING;
IN_STRING_DOUBLE_QUOTE: '"' {setText("");} -> popMode;
IN_STRING_ESCAPED_TEXT: '\\' ([\\"*?]) {setText(getText().substring(1));};
IN_STRING_TEXT: ~[\\"*?]+;
IN_STRING_GLOB_CHAR: [*?];

mode IN_REGEX;
IN_REGEX_END_REGEX: '~' {setText("");} -> popMode;
IN_REGEX_ESCAPED_TEXT: '\\' ([\\~]) {setText(getText().substring(1));};
IN_REGEX_TEXT: ~[\\~]+;