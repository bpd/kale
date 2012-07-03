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
	private final List<Function> elements = new ArrayList<Function>();
	
	private final Map<Object, Function> bindings = new HashMap<Object, Function>();
	
//	public void add(Function element)
//	{
//		if( element instanceof Pair )
//		{
//			Pair pair = (Pair)element;
//			scope.set( pair.symbol, pair.value );
//		}
//		
//		elements.add( element );
//	}
	
	public int size()
	{
		return elements.size();
	}
	

	@Override
	public Function bind(Function f)
	{
		
		
		if( f instanceof Symbol )
		{
			// bind the calling symbol to the Bindable
			// the symbol refers to in this scope
			final Symbol symbol = (Symbol)f;
			return new Function()
			{
				@Override
				public Function bind(Function f)
				{
					System.out.println("binding " + f + " to symbol " + symbol);
					return symbol.bind(f);
				}
				
				@Override
				public Function eval()
				{
					Function f = bindings.get(symbol);
					if( f == null )
					{
						throw new RuntimeException("Unresolved symbol: " + symbol);
					}
					System.out.println("resolved symbol " + symbol + " to " + f);
					return f.eval();
				}
				
				@Override
				public String toString()
				{
					return "<SymbolRef '"+symbol+"'>";
				}
			};
			//return bindings.get( symbol );
		}
		else if( f instanceof Pair )
		{
			Pair pair = (Pair)f;
			
			System.out.println("binding symbol " + pair.symbol + " to " + pair.value);
			
			bindings.put( pair.symbol, pair.value );
		}
		
		if( f != null )
		{
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
			// FIXME this scope or the parent scope?
			System.out.println("binding: " + element);
			sequence = sequence.bind( element.eval() );
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
