package kaygan;

import java.io.StringReader;

import junit.framework.TestCase;
import kaygan.ast.*;

public class ParseTest extends TestCase
{
	public void testParse1()
	{
		String input = " asdf \"1234\" ";
		Parser parser = new Parser(new StringReader(input));
		
		Program program = parser.program();
		
		assertEquals( 2, program.size() );
		assertTrue( program.get(0) instanceof Symbol );
		assertTrue( program.get(1) instanceof Str );
		
	}
	
	public void testParseBind()
	{
		String input = " asdf asdf:1234 ";
		Parser parser = new Parser(new StringReader(input));
		
		Program program = parser.program();
		
		assertEquals( 2, program.size() );
		assertTrue( program.get(0) instanceof Symbol );
		assertTrue( program.get(1) instanceof Bind );
		
		Bind bind = (Bind)program.get(1);
		assertTrue( bind.exp instanceof Num );
	}
	
	public void testParseFunction1()
	{
		String input = " { a b | a + b }  ";
		Parser parser = new Parser(new StringReader(input));
		
		Program program = parser.program();
		
		assertEquals( 1, program.size() );
		assertTrue( program.get(0) instanceof Function );
		
		Function f = (Function)program.get(0);
		
		assertEquals( 2, f.args.length );
		assertEquals( 3, f.size() );
	}
	
	public void testParseFunction2()
	{
		String input = " { a:Int b:Int | a - b }  ";
		Parser parser = new Parser(new StringReader(input));
		
		Program program = parser.program();
		
		assertEquals( 1, program.size() );
		assertTrue( program.get(0) instanceof Function );
		
		Function f = (Function)program.get(0);
		
		assertEquals( 2, f.args.length );
		assertTrue( f.args[0] instanceof Bind );
		assertTrue( f.args[1] instanceof Bind );
		
		assertEquals( 3, f.size() );
	}
	
	public void testParseCallsite()
	{
		String input = " (b + 2)  ";
		Parser parser = new Parser(new StringReader(input));
		
		Program program = parser.program();
		
		assertEquals( 1, program.size() );
		assertTrue( program.get(0) instanceof Callsite );
		
		Callsite c = (Callsite)program.get(0);
		
		assertEquals( 3, c.size() );
		assertTrue( c.get(0) instanceof Symbol );
		assertTrue( c.get(1) instanceof Symbol );
		assertTrue( c.get(2) instanceof Num );
	}
	
	public void testParseArray()
	{
		String input = " [1 2 b]  ";
		Parser parser = new Parser(new StringReader(input));
		
		Program program = parser.program();
		
		assertEquals( 1, program.size() );
		assertTrue( program.get(0) instanceof Array );
		
		Array a = (Array)program.get(0);
		
		assertEquals( 3, a.size() );
		assertTrue( a.get(0) instanceof Num );
		assertTrue( a.get(1) instanceof Num );
		assertTrue( a.get(2) instanceof Symbol );
	}
}
