package kaygan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kaygan.atom.Pair;
import kaygan.atom.Ref;
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
			
			System.out.println("symbol " + symbol + " not found in: " + sequence);
			
			// traverse up parent scopes
			sequence = sequence.parent;
		}
		//throw new RuntimeException("Unresolved symbol: " + symbol);
		return new Error("Unresolved symbol: " + symbol);
	}
	
	public void add(Function f)
	{
		if( f instanceof Pair )
		{
			Pair pair = (Pair)f;
			bindings.put( pair.symbol, bind(pair.value) );
		}
		else
		{
			elements.add( f );
		}
	}
	
	public void bind()
	{
//		for(int i=0; i<elements.size(); i++ )
//		{
//			Function f = elements.get(i);
//			
//			elements.set(i, bind(f));
//		}
		bindTo(this);
	}
	
	public void bindTo(Sequence sequence)
	{
		for(int i=0; i<elements.size(); i++ )
		{
			Function f = elements.get(i);
			
			Function bound = sequence.bind(f);
			
			elements.set(i, bound);
		}
	}

	@Override
	public Function bind(Function f)
	{
		Function bound = f;
		
		if( f instanceof Sequence )
		{
			System.out.println("found sequence");
			
			// sequence or chain
			((Sequence)f).bindTo(this);			
		}
		else if( f instanceof Symbol )
		{
			// bind the calling symbol to the Bindable
			// the symbol refers to in this scope			
			
			final Symbol symbol = (Symbol)f;
			
			bound = new Ref(this, symbol);
		}
		
		System.out.println("bound " + f + " => " + bound);

		return bound;
	}
	
	@Override
	public Sequence eval()
	{
		System.out.println("eval()");
		
		Sequence sequence = new Sequence();
		
		for( Function element : elements )
		{
			Function evalResult = element.eval();
			
			System.out.println("evalResult: " + evalResult);
			
			sequence.elements.add( evalResult );
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
