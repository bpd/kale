package kaygan;

import java.io.StringReader;

import junit.framework.TestCase;

public class CellReaderTest extends TestCase
{
	public void testSimple() throws Exception
	{
		String input = "a: [ name: 'Brian' age: 28 42 test: [ key2: 14 27 ] justAKey:14 aRange:[15:16] ]";
		
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
