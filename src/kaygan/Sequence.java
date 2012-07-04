package kaygan;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Sequence extends Function implements Iterable<Function>
{
	private final List<Function> elements = new ArrayList<Function>();
	
	private final Set<Type> types = new LinkedHashSet<Type>();

	public int size()
	{
		return elements.size();
	}
	
	public void add(Function element)
	{
		if( element == null )
		{
			throw new RuntimeException("Cannot add null element to sequence");
		}
		element.setParent(this);
		elements.add(element);
	}
	
	@Override
	public Function bindTo(Function function)
	{
		System.out.println("bind " + this + " To " + function);
		
		// TODO separated function abstract structure from bind,
		//      but right now this is doing an in-place update
		//      of the sequence on bindTo... when it really should
		//      be making a copy, but the copy doesn't bind properly...
		
//		Sequence sequence = (Sequence)this.clone( new Sequence() );
//		sequence.setParent(function);
//		
//		for( Function element : elements )
//		{
//			sequence.add( element.bindTo(sequence) );
//		}
		
		setParent(function);
		
		for( int i=0; i<elements.size(); i++ )
		{
			elements.set(i, elements.get(i).bindTo(this) );
		}
		
		return this;
//		return sequence;
	}
	
	@Override
	public Type getType()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<{ ");
		
		for( Type type : types )
		{
			sb.append(type.toString());
			sb.append(' ');
		}
		
		sb.append("}>");
		
		return new Type(sb.toString());
	}
	
	@Override
	public Sequence eval()
	{
		Sequence sequence = new Sequence();
		
		for( Function element : elements )
		{
			sequence.elements.add( element.eval() );
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
