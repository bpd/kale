package kaygan;

import java.io.StringReader;

import junit.framework.TestCase;

public class CellReaderTest extends TestCase
{
	public void testSimple() throws Exception
	{
		String input = "a: [ name: 'Brian' age: 28 42 test: [ key2: 14 27 ] justAKey:14 aRange:[15:16] 84 ]";
		
		CellReader reader = new CellReader(new StringReader(input));
		try
		{
			Cell cell = reader.readCell();
			
			System.out.println( "CELL: " + cell );
		}
		finally
		{
			reader.close();
		}
	}
	
	public void testSimple2() throws Exception
	{
		// removed all the ':' from Simple, should become a simple sequence
		String input = "a [ name  \n 'Brian'  \n age   \n 28  \n 42 test \n [ key2 14 27 ] justAKey 14 aRange [15:16] 84 ]";
		
		CellReader reader = new CellReader(new StringReader(input));
		try
		{
			Cell cell = reader.readCell();
			
			System.out.println( "CELL: " + cell );
		}
		finally
		{
			reader.close();
		}
	}
	
	public void testSimple3() throws Exception
	{
		// removed all the ':' from Simple, should become a simple sequence
		String input = " a: 2  b:3  c:4  d: a + b + c ";
		
		CellReader reader = new CellReader(new StringReader(input));
		try
		{
			Cell cell = reader.readCell();
			
			System.out.println( "CELL: " + cell );
		}
		finally
		{
			reader.close();
		}
	}
	
	public void testSimple4() throws Exception
	{
		// removed all the ':' from Simple, should become a simple sequence
		String input = " a: 2  b:3  c:4  d: a [+ b [+ c] ] ";
		
		CellReader reader = new CellReader(new StringReader(input));
		try
		{
			Cell cell = reader.readCell();
			
			System.out.println( "CELL: " + cell );
		}
		finally
		{
			reader.close();
		}
	}
	
	public void testMultiLine() throws Exception
	{
		String input = "a: [ name: 'Brian' age: 28 42 test: [ key2: 14 27 ] justAKey:14 aRange:[15:16] 84 ]";
		
		CellReader reader = new CellReader(new StringReader(input));
		try
		{
			Cell cell = reader.readCell();
			
			System.out.println( "CELL: " + cell );
		}
		finally
		{
			reader.close();
		}
	}
	
	public void testOrdinalKeyCollision() throws Exception
	{
		// FIXME the first '1' is stored as a string key, the second is as an integer.
		//       ideally they should both be stored as atoms, but that doesn't
		//       change the logical collision of 1 and 1
		
		// possible solution: make that distinction at the reader level (Integer key vs String)
		// and let the type checker figure it out and warn where appropriate?
		
		String input = "a: [ 1:22 22 ]";
		
		CellReader reader = new CellReader(new StringReader(input));
		try
		{
			Cell cell = reader.readCell();
			
			System.out.println( "CELL: " + cell );
		}
		finally
		{
			reader.close();
		}
	}
}
