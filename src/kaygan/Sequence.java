package kaygan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kaygan.atom.Pair;
import kaygan.atom.Symbol;

public class Sequence implements Iterable<Function>,  Function
{
	private final Sequence parent;
	
	private final List<Function> elements = new ArrayList<Function>();
	
	private final Map<Object, Function> bindings = new HashMap<Object, Function>();
	
	public Sequence( Sequence parent )
	{
		this.parent = parent;
	}
	
	public Sequence()
	{
		this( null );
	}
	
	public int size()
	{
		return elements.size();
	}
	
	public Function resolve(Symbol symbol)
	{
		Sequence sequence = this;
		while( sequence != null )
		{
			Function f = sequence.bindings.get(symbol);
			if( f != null )
			{
				// we found a binding for the symbol
				return f;
			}
			
			// traverse up parent scopes
			sequence = sequence.parent;
		}
		throw new RuntimeException("Unresolved symbol: " + symbol);
	}

	@Override
	public Function bind(Function f)
	{
		
		if( f instanceof Sequence )
		{
			// sequence or chain
			elements.add( f );
			
		}
		else if( f instanceof Symbol )
		{
			// bind the calling symbol to the Bindable
			// the symbol refers to in this scope			
			
			final Symbol symbol = (Symbol)f;
			
			Function binding = resolve( symbol );
			
			elements.add( binding );
			
		}
		else if( f instanceof Pair )
		{
			Pair pair = (Pair)f;
			
			bindings.put( pair.symbol, pair.value );
			
			elements.add( pair );
		}
		else
		{
			// catch-all for Int, Num, etc.
			
			elements.add( f );
		}
		
		
		return this;
	}
	
	@Override
	public Function eval()
	{
		Function sequence = new Sequence();
		
		for( Function element : elements )
		{
			if( element instanceof Pair )
			{
				// pairs have already done their job in the bind phase
				// by binding the named element
				sequence = sequence.bind( element );
			}
			else
			{
				sequence = sequence.bind( element.eval() );
			}
		}
		
		return sequence;
	}
	
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for( Object element : elements )
		{
			sb.append(' ');
			sb.append(element);
		}
		sb.append(' ');
		sb.append(']');
		
		return sb.toString();
	}

	@Override
	public Iterator<Function> iterator() 
	{
		return elements.iterator();
	}
}
