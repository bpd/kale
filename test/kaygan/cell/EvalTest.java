package kaygan.cell;

import junit.framework.TestCase;
import kaygan.Env;
import kaygan.cell.Cell.Type;

public class EvalTest extends TestCase
{
	public void testType1()
	{
		// everything is an instance of Nil
		assertTrue( Type.Atom.isInstance(Type.Nil) );
		
		assertFalse( Type.Atom.isInstance(Type.Cons) );
		
		assertFalse( Type.Nil.isInstance(Type.Cons) );
		assertFalse( Type.Nil.isInstance(Type.Atom) );
		
		assertTrue( Type.Num.isInstance(Type.Atom) );
		assertTrue( Type.Str.isInstance(Type.Atom) );
		assertTrue( Type.Symbol.isInstance(Type.Atom) );
		
		assertTrue( Type.Sequence.isInstance(Type.Cons) );
		assertTrue( Type.Chain.isInstance(Type.Cons) );
	}
	
	public void testEval1()
	{
		Env env = new Env();
		
		Cell cell = CellReader.parse(" a:2 b:2 ");
		
		Cell result = env.eval(cell);
		
		System.out.println(env);
		
		System.out.println("result: " + result);
	}
	
	
	public void testEval2()
	{
		Env env = new Env();
		
		Cell cell = CellReader.parse(" 3 ");
		
		Cell result = env.eval(cell);
		
		System.out.println("result: " + result);
		
		System.out.println(env);
	}
}
