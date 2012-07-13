package kaygan.type;

import java.util.LinkedHashSet;
import java.util.Set;

public class ListType extends Type
{
	private final Set<String> types = new LinkedHashSet<String>();
	
	public void add(Type type)
	{
		types.add( type.toString() );
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb =  new StringBuilder();
		sb.append("list of ").append(types);
		return sb.toString();
	}
	
	
}
