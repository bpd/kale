package kaygan;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaygan.ast.*;

public class Parser
{
	private final Lexer lexer;
	
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
	
	protected Function function()
	{
		Token open = next();
		
		List<Exp> args = new ArrayList<Exp>();
		List<Exp> contents = new ArrayList<Exp>();
		
		// args
		// accumulate expressions while they match the arg rule,
		// though it may turn out (if we don't find '|')
		// that what we thought were args were actually exps
		// in an argless function
		gather_args:
		while( peek().type == TokenType.Symbol )
		{
			// symbol (':' symbol)? 
			switch( peek(2).type )
			{
			case COLON:
				// symbol ':' symbol
				Bind bind = bind();
				if( !(bind.exp instanceof Symbol) )
				{
					error("Arguments can only be bound to symbols");
				}
				args.add( bind );
				break;

			case Symbol:
			case PIPE:
				// this is the next argument, but we
				// can still assume we're in the arguments block
				args.add( symbol() );
				break;

			case CLOSE_BRACE:
				// single expression function, fall through to content
				break gather_args;

			default:
				error("Expected (':' '|' Symbol), found ");
			}
		}
		
		// check args?
		if( peek().type == TokenType.PIPE )
		{
			next(); 
		}
		else
		{
			// if we don't find a pipe after we're done parsing
			// things that looked like arguments, that means it
			// turns out the things we thought were arugments
			// were just exps in the function body
			contents.addAll( args );
			args.clear();
		}

		final Map<String, Symbol> argNames = new HashMap<String, Symbol>();
		
		for( Exp arg : args )
		{
			Symbol symbol = null;
			if( arg instanceof Bind )
			{
				Bind bind = (Bind)arg;
				symbol = bind.symbol;
			}
			else if( arg instanceof Symbol )
			{
				symbol = (Symbol)arg;
			}
			
			// verify the symbol hasn't already been used for an
			// argument in this function
			if( symbol != null )
			{
				String key = symbol.symbol();
				
				if( argNames.containsKey(key) )
				{
					error( symbol.symbol,  "Symbol already bound" );
				}
				argNames.put( key, symbol );
			}
		}
		
		while( peek().type != TokenType.CLOSE_BRACE )
		{
			contents.add( exp() );
		}
		
		Token close = next();
		
		return new Function(open, close, args, contents);
	}
	
	protected Callsite callsite()
	{
		Token open = next();
		List<Exp> exps = new ArrayList<Exp>();
		
		// callsite requires at least one expression
		exps.add( exp() );
		
		// parse the rest of the expressions
		while( peek().type != TokenType.CLOSE_PAREN )
		{
			exps.add( exp() );
		}
		
		Token close = next();
		
		return new Callsite(open, close, exps );
	}
	
	protected Array array()
	{
		Token open = next();
		List<Exp> exps = new ArrayList<Exp>();
		
		while( peek().type != TokenType.CLOSE_BRACKET )
		{
			exps.add( exp() );
		}
		
		Token close = next();
		
		return new Array( open, close, exps );
	}
	
	protected boolean isNum(TokenType type)
	{
		return type == TokenType.Binary
				|| type == TokenType.Hex
				|| type == TokenType.Int
				|| type == TokenType.Real;
	}
	
	protected boolean isValue(TokenType type)
	{
		return isNum(type) || type == TokenType.String;
	}
	
	protected Value value()
	{
		Token peek = peek();
		if(	isNum(peek.type) )
		{
			return new Num(next());
		}
		else if( peek.type == TokenType.String )
		{
			return new Str(next());
		}
		
		error("Expected Num | String");
		return null;
	}
	
	protected Symbol symbol()
	{
		Token symbol = next();
		if( symbol.type != TokenType.Symbol )
		{
			error(symbol, "Expected Symbol");
		}
		
		return new Symbol( symbol );
	}
	
	protected void error(String message)
	{
		error( peek(), message );
	}
	
	protected void error(Token token, String message)
	{
		throw new ParseException( token, message );
	}
	
	protected Exp range_endpoint()
	{
		Token peek = peek();
		if( isValue(peek.type) )
		{
			return value();
		}
		else if( peek.type == TokenType.Symbol )
		{
			return symbol();
		}
		
		error("Expected value | symbol");
		return null;
	}
	
	protected Bind bind()
	{
		Symbol symbol = symbol();

		next();

		Exp exp = exp();
		
		if( exp instanceof Bind )
		{
			error("Cannot bind to a bind");
		}

		return new Bind( symbol, exp );
	}
	
	protected Exp exp()
	{
		Token peek = peek();
		
		if( peek.type == TokenType.OPEN_BRACE )
		{
			return function();
		}
		else if( peek.type == TokenType.OPEN_BRACKET )
		{
			return array();
		}
		else if( peek.type == TokenType.OPEN_PAREN )
		{
			return callsite();
		}
		
		if( peek(2).type == TokenType.COLON )
		{
			return bind();
		}
		
		if( peek.type == TokenType.Symbol )
		{
			// bind | symbol
			
			// rule out bind to infer symbol
			return symbol();
		}
		
		// default to Value
		return value();
	}
	
	public Program program()
	{
		List<Exp> expressions = new ArrayList<Exp>();

		while( peek().type != TokenType.EOF )
		{
			expressions.add( exp() );
		}
		return new Program(expressions);
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
