package kale;

import java.io.StringReader;

import junit.framework.TestCase;
import kale.Parser;
import kale.Token;
import kale.TokenType;
import kale.ast.Expression;

public class ExpressionParseTest extends TestCase
{
	public void testParseOp1()
	{
		Parser parser = new Parser( new StringReader(" a.b.c + a") );
		
		Expression id = parser.expression();
		
		System.out.println( id );
	}
	
	public void testParseInvoke1()
	{
		Parser parser = new Parser( new StringReader(" a.b.c() + a") );
		
		Expression id = parser.expression();
		
		System.out.println( id );
	}
	
	public void testParseInvoke2()
	{
		Parser parser = new Parser( new StringReader(" a.b.c() + a() - 2") );
		
		Expression id = parser.expression();
		
		System.out.println( id );
	}
	
	public void testParseInvoke3()
	{
		String input = " a.b.c() + a( 2 ) - b( 2 + 3 ) * c( 3 + 4, 7 )";
		Parser parser = new Parser( new StringReader(input) );
		
		Expression id = parser.expression();
		
		System.out.println( id );
	}
	
	public void testParseInvoke4()
	{
		String input = " (a.b.c() + a( 2 )) - b( 2 + 3 ) * c( 3 + 4, 7 )";
		Parser parser = new Parser( new StringReader(input) );
		
		Expression id = parser.expression();
		
		System.out.println( id );
	}
	
	public void testParseInvoke5()
	{
		String input = " a.b.c() + ( a( 2 ) - b( 2 + 3 ) * c( 3 + 4, 7 ) )";
		Parser parser = new Parser( new StringReader(input) );
		
		Expression id = parser.expression();
		
		System.out.println( id );
	}
	
	public void testParseInvoke6()
	{
		// invoking a function reference returned from a.b.c(), then add 2
		String input = " a.b.c()() + 2 )";
		Parser parser = new Parser( new StringReader(input) );
		
		Expression id = parser.expression();
		
		System.out.println( id );
	}
	
	public void testParseInvoke7()
	{
		// invoking a function reference returned from a.b.c(), then add 2
		String input = " a.b.c().next() + 2 )";
		Parser parser = new Parser( new StringReader(input) );
		
		Expression id = parser.expression();
		
		System.out.println( id );
	}
	
	public void testParseInvoke8()
	{
		// invoking a function reference returned from a.b.c(), then add 2
		String input = " a.b.c()( 7 - 4, 13 ) + 2 )";
		Parser parser = new Parser( new StringReader(input) );
		
		Expression id = parser.expression();
		
		System.out.println( id );
	}
	
	public void parseTest()
	{
		Token[] tokens = new Token[]{
			new Token( TokenType.Symbol, 0, 0, "p" ),
			new Token( TokenType.DOT,    0, 0, "." ),
			new Token( TokenType.Symbol, 0, 0, "a" ),
			new Token( TokenType.Symbol, 0, 0, "+" ),
			new Token( TokenType.Symbol, 0, 0, "p" ),
			new Token( TokenType.Symbol, 0, 0, "b" ),
		};
	}
}
