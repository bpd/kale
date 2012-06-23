package kaygan;

import java.util.HashMap;
import java.util.Map;

public class Cell
{
	private final Map<?, ?> entries = new HashMap<Object, Object>();
	
	//private final List<?> members = new ArrayList<Object>();
	
	// Cell Traits:
	//   Key [ Ordered Integer ] => Array   (note: this type notation itself is a cell, except the '=> Array' part)
	//   Value [ Ordered ]
	//   Size
	
	
	
	int size()
	{
		return entries.size();
	}
	
	/**
	 * A cell is a sequence of symbols, denoted [ symbol1 symbol2 ]
	 * 
	 * A symbol that ends with a colon (':') denotes that it is a key
	 *   and the symbol that follows it is the key's associated value
	 * 
	 * Let's define two cells, 'a' and 'b':
	 * 
	 *   a := [ name: "Brian" age: 28 ]
	 *   b := []
	 * 
	 * 'a' is a cell containing a sequence of four symbols, which define
	 *   two key/value pairs, 'name' and 'age' with values '"Brian"' and '28', respectively.
	 * 
	 * Now let's bind another cell to 'a':
	 * 
	 *   a [ name [ substring [ from: b to: 2 ] ] ]
	 * 
	 * 
	 * When the data structure is loaded, as part of a 'linking' step, the 'bind' message
	 *   is sent to 'a', passing the cell `[ name [ substring [ from: b to: 2 ] ] ]` as an argument
	 * 
	 * 
	 * Defining a cell that binds arguments
	 *   (with the default bind method, which requires a cell of unbound symbols as the first element of the cell)
	 * 
	 *   a: [ [b c] b [ a: c ] ]
	 *   
	 * This defines a cell 'a' that takes arguments 'b' and 'c' and invokes b with the cell `[ a: c ] as an argument
	 *   (after b is bound to the argument cell in the linking process)
	 *   
	 *   TODO the above expression uses 'a' as where the result is going and 'a' in the argument cell.
	 *        are the scope semantics there left up to the containing cell being bound into?
	 * 
	 * 
	 * 
	 * 
	 * TODO length prefix or streaming?  when sent over a network the cell stream will
	 *      be read as a stream anyway with a reader... (think I decided on length prefix above)
	 * 
	 * 
	 * 
	 * @param cell
	 * @return
	 */
	Channel bind(Cell cell)
	{
		
		
		
		return null;
	}
	
	
	/**
	 * What we do we know about this cell?
	 * Possible named types?  Value ranges?  Other call sites? 
	 *
	 */
	public static class Constraint
	{
		
	}
}
