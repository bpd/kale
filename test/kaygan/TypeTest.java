package kaygan;

import java.io.StringReader;

import junit.framework.TestCase;

import kaygan.ast.Program;
import kaygan.type.Type;

public class TypeTest extends TestCase
{
	public Type infer(String input)
	{
		Program program = new Parser( new StringReader(input) ).program();
		
		program.link();
		return program.inferType();
	}
	
	public void testFunction()
	{
		Type type = infer(" ( ({ d | {x | [ x d ]} } 2) 3 ) ");
		
		System.out.println("type: " + type);
	}
}
