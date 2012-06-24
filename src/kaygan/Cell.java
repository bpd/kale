package kaygan;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Cell
{
	private final Map<Object, Object> entries = new LinkedHashMap<Object, Object>();
	
	//private final List<?> members = new ArrayList<Object>();
	
	// Cell Traits:
	//   Key [ Ordered Integer ] => Array   (note: this type notation itself is a cell, except the '=> Array' part)
	//   Value [ Ordered ]
	//   Size
	
	@Override
	public String toString()
	{
		Iterator<Entry<Object, Object>> i = entries.entrySet().iterator();
		if (! i.hasNext())
		{
		    return "[]";
		}

		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (;;)
		{
		    Entry<Object, Object> e = i.next();
		    
		    Object key = e.getKey();
		    Object value = e.getValue();
		    
		    // 
		    if( !(key instanceof Integer) )
		    {
			    sb.append(key   == this ? "(this Cell)" : key);
			    sb.append(':');
		    }
		    sb.append(value == this ? "(this Cell)" : value);
		    
		    if (! i.hasNext())
		    {
		    	return sb.append(']').toString();
		    }
		    sb.append(' ');
		}
	}
	
	public Object get(Object key)
	{
		return entries.get(key);
	}
	
	public void put(Object key, Object value)
	{
		Object previousValue = entries.put(key, value);
		if( previousValue == null )
		{
			// we overwrote an existing key
			
		}
	}
	
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
	 * When the data structure is loaded, as part of a 'linking' step, the 'bind' message
	 *   is sent to 'a', passing the cell `[ name [ substring [ from: b to: 2 ] ] ]` as an argument
	 *   
	 *   a name substring [ from: b to: 2 ]
	 * 
	 * TODO is there enough above to implicitly assume cell boundaries?
	 *      is it safe to assume that subsequent symbols in a sequence are evaluated
	 *        within the scope of the preceding symbol, unless cell boundaries are explicitly defined?
	 * 
	 * If the 'to' key defaulted to the length of the string, it could be omitted:
	 * 
	 *   a name substring from: b
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
	 * - default implementation of Cell, keys are stored in their declaration/set order
	 * - when a key is overwritten in a sub-cell, the original position of the key is maintained
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
	
	// TODO should bind() be an internal call on the data structure itself...
	//      the cell should have access to its parent/environment, and the
	//      
	
	
	/**
	 * ambiguity:
	 * 
	 * a: [ b [c d] ]
	 * 
	 * is the above cell defining a sequence of length two (b and [c d])
	 *   or a cell of length one (the result of [c d] being applied to b)
	 * 
	 * ... or do we leave that up to the environment to decide?
	 * 
	 * #============================
	 * another example:
	 * 
	 * b: [ [e f] e + f ]  c: 2  d: 4
	 * a: [ b [c d] ]
	 * 
	 * # should output [ 6 ]
	 * 
	 * - b is a function that takes two arguments... so should it consume the cell that follows it?
	 * - similarly, if it was a non-function, should it then not consume the next?
	 * - this seems to be far too ambiguous
	 * 
	 * 
	 * 
	 * #============================
	 * b: [ [e f] e + f ]  c: 2  d: 4
	 * a: [ b `[c d] ]
	 * 
	 * # should output [ <#function> [c d] ]
	 * # with the quoted [c d] we know it's not to be evaluated
	 * 
	 * FIXME is that consistent?  need to finalize an evaluation model
	 * 
	 * #============================
	 * 
	 * a: 4 + 2
	 * a: 4[ + [ 2 ] ]
	 *       ^ evaluate '+' within the context of a 4 (it has access to 'this')
	 *        ^ bind '+' to the cell [2]
	 *   ^       the result of '+ [2]' evaluated in the context of 4 is stored in a
	 *   
	 * #============================
	 * 
	 * a: [ 2 4 8 7 ]
	 * a: [ 2 [4 [8 [7 ] ] ] ]
	 * 
	 *   ^ no context, so 2 is evaluated within the context of ( Nil?  The assignment (':')? empty list ([])? )
	 *      ^ this results in a list with 2 in it
	 *        ^ 4 is evaluated within the context of a list with a 2 in it, and by the default list
	 *          implementation that results in a list with a 2 and a 4 in it... etc
	 *      
	 * 
	 * 
	 * 
	 * thinking about this in the context of the evaluation model,
	 * 
	 * b is evaluated first, and evaluates to a functor, since it needs one argument.
	 *   The next chained element is [c d], which is applied to the result of b being evaluated.
	 *     
	 * 
	 * 
	 * 
	 */
	
	
	/**
	 * What we do we know about this cell?
	 * Possible named types?  Value ranges?  Other call sites? 
	 *
	 */
	public static class Constraint
	{
		
	}
}
