package kaygan.ast;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import kaygan.ast.ast.*;

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
	
	protected void ws()
	{
		// eat whitespace tokens
		while( peek().type == TokenType.WS )
		{
			System.out.println("eating whitespace");
			next();
		}
	}
	
	protected Function function()
	{
		return null;
	}
	
	protected Callsite callsite()
	{
		return null;
	}
	
	protected Array array()
	{
		return null;
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
		throw new RuntimeException(message);
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
		ws();
		Token between = next();
		ws();
		Exp right = range_endpoint();
		
		return new Range( left, between, right );
	}
	
	protected Bind bind()
	{
		Symbol symbol = symbol();
		ws();
		next();
		ws();
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
		ws();
		while( peek().type != TokenType.EOF )
		{
			Exp exp = exp();
			System.out.println(exp);
			expressions.add( exp );
			ws();
		}
		return expressions;
	}
}
