package kaygan;

import java.util.ArrayList;
import java.util.List;

public class Sequence
{
	private final List<Object> elements = new ArrayList<Object>();
	
	public void add(Object o)
	{
		elements.add( o );
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
}
