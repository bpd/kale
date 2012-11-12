package kale;

import java.io.StringReader;

import junit.framework.TestCase;

import kale.Parser;
import kale.ast.Expression;

public class PositionTest extends TestCase
{
	public void testPosition()
	{
		String input = " 1 + 2 ";
		
		Parser parser = new Parser(new StringReader(input));
		
		Expression exp = parser.expression();
		
		System.out.println("offset : " + exp.getBeginOffset());
		System.out.println("length: " + exp.getEndOffset());
		
		System.out.println("exp: " + exp);
	}
}
