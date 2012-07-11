grammar kaygan;


Comment:	'/*' (.)* '*/' {skip();};

WS	:	' ' | '\t' | '\r' | '\n';

Binary :	'0b' ('0' | '1')+ ;
	
Hex	:	'0x' ('0'..'9'|'a'..'f'|'A'..'F')+ ;

Int	:	'0'..'9'+ ;

Real	:	Int '.' Int ;

SymbolPart:	( '0' | ~('{' | '}' | '(' | ')' | '[' | ']' | ':' | '.' | '|' | WS ) )+ ;



num	:	Binary | Hex | Int | Real ;

string	:	'"' ( ~('"') )* '"' ;

value	:	num | string ;

symbol 	:	SymbolPart ('.' SymbolPart)* ;

arg	:	symbol (WS* ':' WS* symbol)? ;

args	:	(arg WS*)+ '|' WS* ;

bind	:	symbol WS* ':' WS* exp ;
	
function:	'{' WS^* args? (exp WS*)+ '}' ;
	
array	:	'[' WS* (exp WS*)* ']' ;

callsite:	'(' WS* (exp WS*)+ ')' ;
	
exp 	:	function | array | callsite | bind | symbol | value ;
	
program	:	WS* (exp WS*)* ;

