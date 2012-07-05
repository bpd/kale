package kaygan.cell;

import junit.framework.TestCase;
import kaygan.cell.Cell.Type;

public class CellTest extends TestCase
{
	public void testReadNumber()
	{
		Object o = CellReader.parse(" 12 ");
		
		assertEquals( new Atom(new Integer(12), Type.Num), o );
	}
	
	public void testReadMultipleNumber()
	{
		Object o = CellReader.parse(" 12 12.2 13 0xaf ");
		
		assertTrue( o instanceof Cons );
		
		Cons cell = (Cons)o;
		
		assertEquals( cell.left, new Atom(new Integer(12), Type.Num) );
		assertEquals( ((Cons)cell.right).left, new Atom(new Double(12.2), Type.Num) );
		assertEquals( ((Cons)((Cons)cell.right).right).left, new Atom(new Integer(13), Type.Num) );
		assertEquals( ((Cons)((Cons)((Cons)cell.right).right).right).left, new Atom(new Integer(0xaf), Type.Num) );
	}
	
	public void testReadReal()
	{
		Object o = CellReader.parse(" 12.2 ");
		
		assertEquals( new Atom(new Double(12.2), Type.Num), o );
	}
	
	public void testReadRange()
	{
		Object o = CellReader.parse(" 0:24 ");
		
		assertTrue( o instanceof Cons );
		
		Cons cell = (Cons)o;
		
		assertEquals(  new Atom(new Integer(0), Type.Num), cell.left );
		assertEquals(  new Atom(new Integer(24), Type.Num), cell.right );
	}
	
	public void testBind()
	{
		Object o = CellReader.parse(" a:2 ");
		
		System.out.println("o => " + o.toString());
		System.out.println("o.type => " + o.getClass().getSimpleName());
	}
	
	public void testMultiBind()
	{
		Object o = CellReader.parse(" a:2 b:2 ");
		
		System.out.println("o => " + o.toString());
		System.out.println("o.type => " + o.getClass().getSimpleName());
	}
	
	public void testSequence()
	{
		Cons o = (Cons)CellReader.parse(" a: [ 2 ] ");
		
		System.out.println("==================================");
		System.out.println("o => " + o.toString());
		System.out.println("o => " + o.toCellString());
		System.out.println("o.type => " + o.getClass().getSimpleName());
	}
	
	public void testChain()
	{
		Cons o = (Cons)CellReader.parse(" b: (a 3) ");
		
		System.out.println("==================================");
		System.out.println("o => " + o.toString());
		System.out.println("o => " + o.toCellString());
		System.out.println("o.type => " + o.getClass().getSimpleName());
	}
	
	public void testNestedSequence()
	{
		Cons o = (Cons)CellReader.parse(" a: [b c [d e] f] ");
		
		System.out.println("==================================");
		System.out.println("o => " + o.toString());
		System.out.println("o => " + o.toCellString());
		System.out.println("o.type => " + o.getClass().getSimpleName());
	}
}
