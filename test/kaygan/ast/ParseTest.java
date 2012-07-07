package kaygan.ast;

import java.io.StringReader;
import java.util.List;

import junit.framework.TestCase;
import kaygan.ast.ast.*;

public class ParseTest extends TestCase
{
	public void testParse1()
	{
		String input = " asdf \"1234\" ";
		Parser parser = new Parser(new StringReader(input));
		
		List<Exp> exps = parser.program();
		
		assertEquals( 2, exps.size() );
		assertTrue( exps.get(0) instanceof Symbol );
		assertTrue( exps.get(1) instanceof Str );
		
	}
	
	public void testParseBind()
	{
		String input = " asdf asdf:1234 ";
		Parser parser = new Parser(new StringReader(input));
		
		List<Exp> exps = parser.program();
		
		assertEquals( 2, exps.size() );
		assertTrue( exps.get(0) instanceof Symbol );
		assertTrue( exps.get(1) instanceof Bind );
		
		Bind bind = (Bind)exps.get(1);
		assertTrue( bind.exp instanceof Num );
	}
	
	public void testParseFunction1()
	{
		String input = " { a b | a + b }  ";
		Parser parser = new Parser(new StringReader(input));
		
		List<Exp> exps = parser.program();
		
		assertEquals( 1, exps.size() );
		assertTrue( exps.get(0) instanceof Function );
		
		Function f = (Function)exps.get(0);
		
		assertEquals( 2, f.args.size() );
		assertEquals( 3, f.contents.size() );
	}
	
	public void testParseFunction2()
	{
		String input = " { a:Int b:Int | a - b }  ";
		Parser parser = new Parser(new StringReader(input));
		
		List<Exp> exps = parser.program();
		
		assertEquals( 1, exps.size() );
		assertTrue( exps.get(0) instanceof Function );
		
		Function f = (Function)exps.get(0);
		
		assertEquals( 2, f.args.size() );
		assertTrue( f.args.get(0) instanceof Bind );
		assertTrue( f.args.get(1) instanceof Bind );
		
		assertEquals( 3, f.contents.size() );
	}
}