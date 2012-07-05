package kaygan.cell;

import junit.framework.TestCase;

public class CellTest extends TestCase
{
	public void testSimple()
	{
		Object o = CellReader.parse(" 12 ");
		
		System.out.println("o => " + o);
		System.out.println("o.type => " + o.getClass().getSimpleName());
	}
	
	public void testBind()
	{
		Object o = CellReader.parse(" a:2 ");
		
		System.out.println("o => " + o.toString());
		System.out.println("o.type => " + o.getClass().getSimpleName());
		
		
	}
	
	public void testSequence()
	{
		Cell o = (Cell)CellReader.parse(" a: [ 2 ] ");
		
		System.out.println("==================================");
		System.out.println("o => " + o.toCellString());
		System.out.println("o.type => " + o.getClass().getSimpleName());
	}
	
	public void testChain()
	{
		Cell o = (Cell)CellReader.parse(" b: (a 3) ");
		
		System.out.println("==================================");
		System.out.println("o => " + o.toCellString());
		System.out.println("o.type => " + o.getClass().getSimpleName());
	}
	
	public void testNestedSequence()
	{
		Cell o = (Cell)CellReader.parse(" a: [b c [d e] f] ");
		
		System.out.println("==================================");
		System.out.println("o => " + o.toString());
		System.out.println("o => " + o.toCellString());
		System.out.println("o.type => " + o.getClass().getSimpleName());
	}
}
