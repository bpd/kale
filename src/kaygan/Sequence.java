package kaygan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kaygan.atom.Pair;

public class Sequence implements Iterable<Function>, Bindable, Function
{
	private final List<Function> elements = new ArrayList<Function>();
	
	protected final Scope scope = new Scope();
	
	public void add(Function element)
	{
		if( element instanceof Pair )
		{
			Pair pair = (Pair)element;
			scope.set( pair.symbol, pair.value );
		}
		
		elements.add( element );
	}
	
	public int size()
	{
		return elements.size();
	}
	
	public Function eval(Scope scope)
	{
		Sequence sequence = new Sequence();
		
		for( Function element : elements )
		{
			// FIXME this scope or the parent scope?
			sequence.add( element.eval(this.scope) );
		}
		
		return sequence;
	}
	
	public Function bind(Scope scope)
	{
		// replace the current sequence with a sequence of functions
		return null;
	}
	
	public Binding bind(Bindable parent)
	{
		
//		if( caller instanceof Symbol )
//		{
//			// bind the calling symbol to the Bindable
//			// the symbol refers to in this scope
//			Symbol symbol = (Symbol)caller;
//			return bindings.get( symbol ).bind( caller );
//		}
		
		
		// default: 
		return null; //caller.bind( EMPTY );
		
		
//		if( elements.size() == 0 )
//		{
//			// empty chain evaluates to itself
//			//return this;
//			return null;
//		}
//		
//		Object head = elements.get(0);
//		if( head instanceof Sequence )
//		{
//			// head is a sequence, this is a function
//			
//			Sequence arguments = (Sequence)head;
//			
//			for( Object argument : arguments )
//			{
//				// each argument should be a symbol (String) or Pair
//				//   (anything else doesn't make sense as an argument)
//				
//				if( argument instanceof String )
//				{
//					// null binding, tracking the argument but
//					// annotating that it is not bound to anything
//					bindings.put( (String)argument, null );
//				}
//				else if( argument instanceof Pair )
//				{
//					Pair arg = (Pair)argument;
//					bindings.put( arg.symbol, arg.value );
//				}
//			}
//			
//		}
//		else
//		{
//			// not a function, so evaluate each element of the sequence
//			// in the context of its Bindable parent
//			
//			for( Bindable element : elements )
//			{
//				element.bind(this);
//			}
//			
//		}
//		
//		return null;
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
