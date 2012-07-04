package kaygan;

import java.util.HashMap;
import java.util.Map;

import kaygan.atom.Pair;
import kaygan.atom.Symbol;

public abstract class Function
{
	// TODO this needs to be an immutable constructor argument for all Functions
	protected volatile Function parent;
	
	public void setParent(Function parent)
	{
		this.parent = parent;
	}
	
	public Function getParent()
	{
		return parent;
	}
	
	private final Map<Object, Function> bindings = new HashMap<Object, Function>();
	
	public void bind(Object key, Function value)
	{
		bindings.put(key, value);
	}
	
	/**
	 * 
	 * In a Sequence:
	 * 
	 *   a: [ 1 2 3 b c d ]
	 *   
	 *   a = sequence.bind(1).bind(2).bind(3).bind('b').bind('c').bind('d')
	 *   
	 *     the Sequence returns itself each with with the new Function added
	 * 
	 * In a Chain:
	 * 
	 *   f: ( a b c )
	 *   g: ([a b] a + b)
	 *   
	 *   f = chain.bind('a').bind('b').bind('c')
	 *   g = chain.bind( sequence.bind('a').bind('b') ).bind('a').bind('+').bind('b')
	 *   
	 *     the Chain returns the result of binding the 'left' to the 'right' Function
	 *       ( whatever is returned by left.bind(right) )
	 * 
	 * 
	 * @param f
	 * @return
	 */
	public Function bind(Function f)
	{
		System.out.println("bind(): " + f);
		
		if( f instanceof Pair )
		{
			Pair pair = (Pair)f;
			
			Function bound = bind(pair.value);
			
			bindings.put( pair.symbol, bound );
			
			System.out.println(pair.symbol + " => " + bound);
			
			return pair;
			
			// TODO adjust type signature
		}
		else if( f instanceof Symbol )
		{
			// bind the calling symbol to the Bindable
			// the symbol refers to in this scope			
			
			final Symbol symbol = (Symbol)f;
			
			Function resolved = bindings.get(symbol);
			if( resolved == null && parent != null )
			{
				resolved = parent.bind(symbol);
			}
			
			if( resolved == null )
			{
				throw new RuntimeException("Unresolved symbol: " + symbol);
			}
			
			return resolved;
		}
		
		return f;
	}
	
	public abstract Type getType();
	
	public abstract Function eval();
	
	//private final List<String> errors = new ArrayList<String>();
	
	public void error(String message)
	{
		//errors.add(message);
		throw new RuntimeException(message);
	}
}
