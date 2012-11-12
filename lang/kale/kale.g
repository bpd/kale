grammar kale;

options
{
  output = AST;
  backtrack = true;
}

// http://golang.org/ref/spec

// ID can't start with a number or any 'special' character

ID  :	 ~('\t'|'\n'|'\r'|' '|'\''|'"'|'('|')'|'['|']'|'{'|'}'|'.'|','|';'|'='|'&'|'0'..'9')  /*('a'..'z'|'A'..'Z'|'_')*/ 
	(~('\t'|'\n'|'\r'|' '|'\''|'"'|'('|')'|'['|']'|'{'|'}'|'.'|','|';'|'='|'&'))*
    ;
    
SELECTOR
    :	ID ('.' ID)* ;

COMMENT
    :   '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    |   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    ;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;
    
INT : ('0'..'9')+ ;

STRING
    :  '"' ( ~('"') )* '"'
    ;


//-- basics --//
qualified_id	: ID ('.' ID)* ;


		
//-- Program Structure --//
program		:	package_decl
			(imports)?
			(func_decl | type_decl | interface_decl)* ;

package_decl	: 'package' qualified_id ;

imports		: 'import' STRING ( ','  STRING)* ;


block		: '{' (statement ';')* '}' ;


//-- Function Declaration --//
func_decl	: ID signature block ;

		
signature	: 'operator'? param_list type? ;

param_list	: '(' (type_spec (',' type_spec)*)? ')' ;

type_spec	: ID type ;



//-- expression --//


statement	: assignment
		| if_stmt
		| for_stmt
		| while_stmt
		| return_stmt
		| expression
		;
		
if_stmt		: 'if' expression block ('else' 'if' block)* ('else' block)? ;

for_stmt	: 'for' '(' (assignment (',' assignment)*)? ';' expression? ';' expression? ')' block ;

while_stmt	: 'while' '(' expression ')' block ;
		
assignment	: qualified_id '=' expression ;

return_stmt	: 'return' expression ;

expression	: operand
		| operation
		| '(' expression ')'
		;

operand		: literal
		| qualified_id
		| invocation
		| func_lit
		| pointer_ref
		;

operation	: operand ID operand ;

invocation	: qualified_id '(' (expression (',' expression)*)? ')' ;
		
literal		: INT
		| STRING ;
		
func_lit	: 'func' signature block ;

pointer_ref	: '&' qualified_id ;


//-- Type Declaration --//

type_decl	: 'type' ID? '{' (field_decl | func_decl)* '}' ;

interface_decl	: 'interface' ID? '{' (ID signature ';')* '}' ;

field_decl	: ID type ';' ;

type		: qualified_id
		| type_lit ;

type_lit	: func_type
		| interface_decl
		;

func_type	: 'func' signature ;

