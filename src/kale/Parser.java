package kale;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import kale.ast.*;

public class Parser
{
	private final Lexer lexer;
	
	private final List<Token> badTokens = new ArrayList<Token>();
	
	public Parser( Reader reader)
	{
		this.lexer = new Lexer( reader );
	}
	
	protected Token peek()
	{
		return lexer.peek();
	}
	
	protected Token peek(int lookAhead)
	{
		return lexer.peek(lookAhead);
	}
	
	protected Token next()
	{
		return lexer.next();
	}
	
	public List<Token> getTokenList()
	{
		return lexer.getTokenList();
	}
	
	public List<Token> getBadTokens()
	{
		return badTokens;
	}
	
	protected boolean isLiteral(TokenType type)
	{
		return 	type == TokenType.Int
				|| type == TokenType.String;
	}
	
	protected ASTNode literal()
	{
		Token peek = peek();
		if(	peek.type == TokenType.Int )
		{
			return new IntLiteral(next());
		}
		else if( peek.type == TokenType.String )
		{
			return new StringLiteral(next());
		}
		
		error("Expected Num | String");
		return null;
	}
	
	// returns QualifiedId | Id
	Id id()
	{
		Token token = require(TokenType.Symbol);
		
		return new Id( token, null );
	}
	
	Id qualId()
	{		
		Id id = new Id( require(TokenType.Symbol) );
		
		Token nextToken = peek();
		
		while( nextToken.type == TokenType.DOT )
		{
			next(); // consume DOT
			// Symbol is the only thing that can follow Dot
			
			id = new Id( require(TokenType.Symbol), id );
			
			nextToken = peek();
		}
				
		return id;
	}
	
	Expression expression()
	{
		Expression left = operand();
		
		if( left != null )
		{
			while( peek().type == TokenType.Symbol )
			{
				// operators must be an Id (not a QualifiedId)
				Id opExpr = id();
				
				Expression right = expression();
		
				left = new Operation( opExpr, left, right );
			}
		}
		return left;
	}
	
	Expression operand()
	{
		Token token = peek();
		
		if( token.type == TokenType.Int )
		{
			return new IntLiteral( next() );
		}
		else if( token.type == TokenType.String )
		{
			return new StringLiteral( next() );
		}
		else if( token.type == TokenType.Boolean )
		{
			return new BooleanLiteral( next() );
		}
		else if( token.type == TokenType.OPEN_PAREN )
		{
			// '(' expression ')'
			
			next(); // consume '('
			
			Expression exp = expression();
			
			require( TokenType.CLOSE_PAREN );
			
			return exp;
		}
		// else if Keyword? separate from Symbol?
		
		else if( token.type == TokenType.Symbol )
		{
			// qualified_id | invocation | operation
			
			Id id = qualId();
			
			if( peek().type == TokenType.OPEN_PAREN )
			{
				return invocation(id);
			}
			
			return id;
		}

		// invalid token for this context
		return null; //throw new ParseException(peek(), "Bad token");
	}
	
	// a()(1,2).x(3,4)(3,4)
	// MemberAccess[
	//   Invocation[
	//     Invocation[
	//       a,
	//       args:[]
	//     ],
	//     args:[ 1, 2 ]
	//   ]
	// ]
	Expression invocation(ASTNode target)
	{
		// invocation
		next(); // consume '('
		
		List<Expression> args = new ArrayList<Expression>();
		
		if( !found(TokenType.CLOSE_PAREN) )
		{
			args.add( expression() );
			
			Token nextToken = peek();
			
			while( nextToken.type == TokenType.COMMA )
			{
				next(); // consume ','
				
				args.add( expression() );
				
				nextToken = peek();
			}
			
			require( TokenType.CLOSE_PAREN );
		}
		
		Invocation invocation = new Invocation(
						target,
						args.toArray( new Expression[ args.size() ]) );
		
		Token nextToken = peek();
		if( nextToken.type == TokenType.OPEN_PAREN )
		{
			// a()(1,2)
			return invocation( invocation );
		}
		else if( nextToken.type == TokenType.DOT )
		{
			// a().x

			next(); // consume '.'
			
			Token token = require(TokenType.Symbol);
			
			// the member access is an Id with the invocation as 'previous'
			Id id = new Id( token, invocation );
			
			// check for invocation of MemberAccess:
			// a().x(1,2)
			nextToken = peek();
			if( nextToken.type == TokenType.OPEN_PAREN )
			{
				return invocation(id);
			}
			// TODO check for assignment of result
			// a().x = 4;
//			else if( nextToken.type == TokenType.EQUALS )
//			{
//				// assignment()
//			}
			
			return id;
		}
		return invocation;
	}
	
	protected Token require( TokenType type )
	{
		Token next = peek();
		while( next.type != type )
		{
			if( next.type == TokenType.EOF )
			{
				throw new ParseException(next, "EOF before " + type);
			}
			next = next(); // consume bad token
			
			badTokens.add( next );
			
			next = peek();
		}
		
		// token matched, so consume it
		return next();
	}
	
	protected Token require( TokenType type, String value )
	{
		Token next = peek();
		while( next.type != type || !next.value.equals(value) )
		{
			if( next.type == TokenType.EOF )
			{
				throw new ParseException(next, "EOF before " + type);
			}
			
			next(); // consume bad token, TODO add to bad token list
			next = peek();
		}

		// token matched, so consume it
		return next();
	}
	
	protected boolean found( TokenType type )
	{
		if( peek().type == type )
		{
			next();
			return true;
		}
		return false;
	}
	
	protected void error(String message)
	{
		error( peek(), message );
	}
	
	protected void error(Token token, String message)
	{
		throw new ParseException( token, message );
	}
	
	public CompilationUnit parse()
	{
		require( TokenType.Keyword, "package" );
		
		Id packageName = qualId();
		
		//require( TokenType.SEMICOLON );
		
		Throwable parseException = null;
		
		List<TypeDecl> types = new ArrayList<TypeDecl>();
		List<InterfaceDecl> interfaces = new ArrayList<InterfaceDecl>();
		List<FunctionDecl> functions = new ArrayList<FunctionDecl>();
		try
		{
			Token next = peek();
			while( next.type != TokenType.EOF )
			{
				if( next.type == TokenType.Keyword )
				{
					if( next.value.equals("type") )
					{
						// type_decl
						types.add( type_decl() );
					}
					else if( next.value.equals("interface") )
					{
						// interface_decl
						interfaces.add( interface_decl() );
					}
					else
					{
						// error: invalid keyword position
						Token badToken = next(); // consume bad token
						
						badTokens.add( badToken );
						
						throw new ParseException(peek(), "Invalid keyword position '" 
									+ peek().value + "' at offset " + peek().beginOffset);
					}
				}
				else if( next.type == TokenType.Symbol )
				{
					// assume func_decl: ID signature block
					functions.add( func_decl() );
				}
				else
				{
					// FIXME: illegal token... for now just eat it
					System.out.println("Illegal token: " + next);
					next();
				}
				
				next = peek();
			}
		}
		catch( ParseException e )
		{
			parseException = e;
		}
		
		PackageDecl packageDecl = new PackageDecl(
			packageName,
			types.toArray(new TypeDecl[types.size()]),
			functions.toArray(new FunctionDecl[functions.size()]),
			interfaces.toArray(new InterfaceDecl[interfaces.size()])
		);
		
		CompilationUnit unit = new CompilationUnit( packageDecl );
		
		if( badTokens.size() > 0 )
		{
			// AST construction required error correction, mark as invalid
			unit.setValid( false );
		}
		
		if( parseException != null )
		{
			unit.error( parseException.getMessage() );
		}
		
		return unit;
	}
	
	public TypeDecl type_decl()
	{
		require(TokenType.Keyword, "type");
		
		Id typeName = id();
		
		require( TokenType.OPEN_BRACE );
		
		// expect (field_decl | func_decl)*
		
		List<FieldDecl> fields = new ArrayList<FieldDecl>();
		List<FunctionDecl> methods = new ArrayList<FunctionDecl>();
		
		Token next = peek();
		while( next.type == TokenType.Symbol )
		{
			if( peek(2).type == TokenType.Symbol )
			{
				// field_decl
				fields.add( field_decl() );
			}
			else
			{
				// assume func_decl
				methods.add( func_decl() );
			}
			
			next = peek();
		}
		
		
		require( TokenType.CLOSE_BRACE );
		
		return new TypeDecl(	
						typeName,
						fields.toArray(new FieldDecl[fields.size()]),
						methods.toArray(new FunctionDecl[methods.size()]) );
	}
	
	public InterfaceDecl interface_decl()
	{
		require(TokenType.Keyword, "interface");
		
		Id typeName = id();
		
		require( TokenType.OPEN_BRACE );
		
		// expect (named_signature)*
		
		List<FunctionSignature> signatures = new ArrayList<FunctionSignature>();
		
		Token next = peek();
		while( next.type == TokenType.Symbol )
		{
			Id name = id();
			
			Signature signature = signature();
			
			require(TokenType.SEMICOLON);
			
			signatures.add( new FunctionSignature( name, signature ) );
			
			next = peek();
		}
		
		require( TokenType.CLOSE_BRACE );
		
		return new InterfaceDecl(	
						typeName,
						signatures.toArray(new FunctionSignature[signatures.size()]) );
	}
	
	FieldDecl field_decl()
	{
		// field_decl: ID type ';'
		
		Id id = id();
		
		Id type = type();
		
		require(TokenType.SEMICOLON);
		
		return new FieldDecl( id, type );
	}
	
	Id type()
	{
		// type: qualified_id | type_lit
		
		return qualId(); // TODO: type_list and fix Type
	}
	
	ASTNode type_lit()
	{
		// type_lit: func_type | interface_decl
		
		return null;
	}
	
	FunctionDecl func_decl()
	{
		// func_decl: ID signature block
		
		Id name = id();
		
		Signature signature = signature();
		
		Expression[] block = block();
		
		return new FunctionDecl( name, signature, block );
	}
	
	Signature signature()
	{
		// signature: 'operator'? param_list type?
		Token operatorToken = null;
		
		Token next = peek();
		if( next.type == TokenType.Keyword )
		{
			operatorToken = require(TokenType.Keyword, "operator");
			next = peek();
		}
		
		require(TokenType.OPEN_PAREN);
		
		List<ParamDecl> params = new ArrayList<ParamDecl>();
		
		next = peek();
		while( next.type == TokenType.Symbol )
		{
			// type_spec: ID type
			Id id = id();
			Id type = type();
			
			params.add( new ParamDecl( id, type ) );
			
			next = peek();
			if( next.type == TokenType.COMMA )
			{
				next(); // consume ','
				next = peek();
			}
		}
		
		require(TokenType.CLOSE_PAREN);
		
		Id type = null;
		
		if( peek().type == TokenType.Symbol )
		{
			type = type();
		}
		else
		{
			type = new Id("void");
		}
		
		return new Signature(	operatorToken != null,
								params.toArray( new ParamDecl[params.size()] ),
								type );
	}
	
	Expression[] block()
	{
		require(TokenType.OPEN_BRACE);
		
		List<Expression> statements = new ArrayList<Expression>();
		
		Token next = peek();
		while( (next.type == TokenType.Symbol || next.type == TokenType.Keyword)
			&& next.type != TokenType.EOF )
		{
			Expression stmt = statement();
			if( stmt == null )
			{
				stmt = new Expression()
				{
					{
						error("Illegal token");
					}
				};
			}
			statements.add( stmt );
			
			next = peek();
		}
		
		require(TokenType.CLOSE_BRACE);
		
		return statements.toArray(new Expression[statements.size()]);
	}
	
	Expression statement()
	{
		// statement : if_stmt | for_stmt | while_stmt | return_stmt
		//             | assignment | expression
		Token next = peek();
		if( next.type == TokenType.Keyword )
		{
			if( next.value.equals("if") )
			{
				// if_stmt
				next(); // consume 'if'
				
				Expression condition = expression();
				Expression[] ifBlock = block();
				Expression nestedIf = null;
				Expression[] elseBlock = null;
				
				next = peek();
				if( next.type == TokenType.Keyword && next.value.equals("else") )
				{
					next(); // consume 'else'
					
					next = peek();
					if( next.type == TokenType.Keyword && next.value.equals("if") )
					{
						// read nested if
						nestedIf = statement();
					}
					else
					{
						// read fallback else
						elseBlock = block();
					}
				}
				
				return new IfStatement(	condition,
										ifBlock,
										nestedIf,
										elseBlock );
				
			}
			else if( next.value.equals("while") )
			{
				// while_stmt
				next(); // consume 'while'
				
				Expression condition = expression();
				
				Expression[] block = block();
				
				return new WhileLoop( condition, block );
			}
			else if( next.value.equals("return") )
			{
				// return_stmt
				Token returnToken = next(); // consume 'return'
				
				Expression returnExp = expression();
				
				if( returnExp == null )
				{
					throw new ParseException( returnToken, "Expected expression, found: " + returnExp);
				}
				
				require(TokenType.SEMICOLON);
				
				return new ReturnStatement( returnExp );
			}
			else
			{
				// error: unknown keyword
				throw new ParseException(next, "Unknown keyword: " + next.value);
			}
		}
		else
		{
			// assignment | expression
			
			// whether assignment or (valid) expression,
			// 
			
			Expression exp = expression();
			
			if( exp instanceof Id )
			{
				// we attempted to parse a full expression,
				// but only got an ID back... which means
				// the next production is not an invocation, etc.
				// so we can safely assume it was a token
				// that made the expression parser terminate,
				// so we'll take the leap and assume it's a '='
				
				if( peek().type == TokenType.EQUALS )
				{
					next(); // consume '='
					
					Expression value = expression();
					
					require(TokenType.SEMICOLON);
					
					return new Assignment( (Id)exp, value );
				}
			}
			else if( exp instanceof Invocation )
			{
				// someObject.setProperty(value);
				//
				require(TokenType.SEMICOLON);
				
				return exp;
			}
			
			// TODO single-expression lambdas should be implemented later,
			//      but for now...
			exp.error("Unexpected expression, expected statement");
			return null;
		}
	}
	
	public static class ParseException extends RuntimeException
	{
		private static final long serialVersionUID = 1L;
		
		public final Token token;
		
		public ParseException(Token token, String message)
		{
			super( message );
			
			this.token = token;
		}
	}
}
