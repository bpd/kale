package kale;

import java.io.InputStreamReader;

import junit.framework.TestCase;
import kale.Parser;
import kale.ast.CompilationUnit;

public class ParserTest extends TestCase
{
	public void testParseType()
	{
		Parser parser = new Parser(
			new InputStreamReader(
				ParserTest.class.getResourceAsStream("type.kl")) );
		
		CompilationUnit unit = parser.parse();
		
		unit.test();
	}
	
	public void testParseType1()
	{
		Parser parser = new Parser(
			new InputStreamReader(
				ParserTest.class.getResourceAsStream("type1.kl")) );
		
		CompilationUnit unit = parser.parse();
		
		unit.test();
	}
	
	public void testParseType2()
	{
		Parser parser = new Parser(
			new InputStreamReader(
				ParserTest.class.getResourceAsStream("type2.kl")) );
		
		CompilationUnit unit = parser.parse();
		
		unit.test();
	}
	
	public void testParseType3()
	{
		Parser parser = new Parser(
			new InputStreamReader(
				ParserTest.class.getResourceAsStream("type3.kl")) );
		
		CompilationUnit unit = parser.parse();
		
		unit.test();
	}
	
	public void testParseFunction()
	{
		Parser parser = new Parser(
			new InputStreamReader(
				ParserTest.class.getResourceAsStream("function.kl")) );
		
		CompilationUnit unit = parser.parse();
		
		System.out.println( unit.toString() );
	}
	
	public void testParseIfStatement()
	{
		Parser parser = new Parser(
			new InputStreamReader(
				ParserTest.class.getResourceAsStream("if.kl")) );
		
		CompilationUnit unit = parser.parse();
		
		unit.test();
	}
	
	public void testParseOperator()
	{
		Parser parser = new Parser(
			new InputStreamReader(
				ParserTest.class.getResourceAsStream("operator.kl")) );
		
		CompilationUnit unit = parser.parse();
		
		unit.test();
	}
	
	public void testParseInterface()
	{
		Parser parser = new Parser(
			new InputStreamReader(
				ParserTest.class.getResourceAsStream("interface1.kl")) );
		
		CompilationUnit unit = parser.parse();
		
		System.out.println( unit );
		
		unit.test();
	}
	
	public void testToString()
	{
		Parser parser = new Parser(
			new InputStreamReader(
				ParserTest.class.getResourceAsStream("tostring.kl")) );
		
		CompilationUnit unit = parser.parse();
		
		System.out.println( unit );
		
		Object o = unit.execute();
		
		assertNotNull( o );
		
		assertEquals( o.toString(), "{ name=\"Brian\" }" );
	}
	
	public void testWhile()
	{
		Parser parser = new Parser(
			new InputStreamReader(
				ParserTest.class.getResourceAsStream("while.kl")) );
		
		CompilationUnit unit = parser.parse();
		
		Object o = unit.execute();
		
		assertEquals( 10, o );
	}
	
	public void testTypeCheck1()
	{
		Parser parser = new Parser(
			new InputStreamReader(
				ParserTest.class.getResourceAsStream("typeCheck1.kl")) );
		
		CompilationUnit unit = parser.parse();
		
		Throwable e = null;
		try
		{
			unit.compile();
		}
		catch(RuntimeException re)
		{
			e = re;
		}
		assertNotNull( e );
		assertTrue( unit.hasErrors() );
	}
	
	public void testTypeCheck2()
	{
		Parser parser = new Parser(
			new InputStreamReader(
				ParserTest.class.getResourceAsStream("typeCheck2.kl")) );
		
		CompilationUnit unit = parser.parse();
		
		Throwable e = null;
		try
		{
			unit.compile();
		}
		catch(RuntimeException re)
		{
			e = re;
		}
		assertNotNull( e );
		assertTrue( unit.hasErrors() );
		
		// 3 pairs of 'bad return type' and 'return type does not match signature' errors
		assertEquals( 6, unit.getErrors().size() );
	}
	
	public void testEmptyFunction()
	{
		Parser parser = new Parser(
			new InputStreamReader(
				ParserTest.class.getResourceAsStream("emptyFunction.kl")) );
		
		CompilationUnit unit = parser.parse();
		
		Object result = unit.execute();
		
		assertNull( result );
	}
}
