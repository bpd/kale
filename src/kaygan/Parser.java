package kaygan;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

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
		while( peek().type == TokenType.SymbolPart )
		{
			// symbol (':' symbol)? 
			Token peek2 = peek(2);
			if( peek2.type == TokenType.COLON )
			{
				if( peek(3).type == TokenType.SymbolPart )
				{
					// symbol ':' symbol
					args.add( bind() );
				}
			}
			else if( peek2.type == TokenType.SymbolPart
					|| peek2.type == TokenType.PIPE )
			{
				// this is the next argument, but we
				// can still assume we're in the arguments block
				args.add( symbol() );
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
		List<Token> parts = new ArrayList<Token>();
		while( peek().type == TokenType.SymbolPart )
		{
			parts.add( next() );
			if( peek().type == TokenType.FULL_STOP )
			{
				// consume
				next();
			}
			else
			{
				break;
			}
		}
		
		if( parts.size() == 0 )
		{
			error("Expected Symbol");
		}
		return new Symbol(parts);
	}
	
	protected void error(String message)
	{
		Token token = peek();
		throw new ParseException( token, message );
	}
	
	protected Exp range_endpoint()
	{
		Token peek = peek();
		if( isValue(peek.type) )
		{
			return value();
		}
		else if( peek.type == TokenType.SymbolPart )
		{
			return symbol();
		}
		
		error("Expected value | symbol");
		return null;
	}
	
	protected Range range()
	{
		Exp left = range_endpoint();

		Token between = next();

		Exp right = range_endpoint();
		
		return new Range( left, between, right );
	}
	
	protected Bind bind()
	{
		Symbol symbol = symbol();

		next();

		Exp exp = exp();

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
		
		
		if( peek.type == TokenType.SymbolPart )
		{
			// bind | symbol | range
			
			// rule out bind and range to infer symbol
			
			Token peek2 = peek(2);
			
			if( peek2.type == TokenType.COLON )
			{
				return bind();
			}
			else if( peek2.type == TokenType.BETWEEN )
			{
				return range();
			}
			else
			{
				return symbol();
			}
		}
		
		// default to Value
		return value();
	}
	
	public List<Exp> program()
	{
		List<Exp> expressions = new ArrayList<Exp>();

		while( peek().type != TokenType.EOF )
		{
			expressions.add( exp() );
		}
		return expressions;
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
