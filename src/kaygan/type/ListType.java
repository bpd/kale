package kaygan.type;

import java.util.LinkedHashSet;
import java.util.Set;

import kaygan.Scope;

public class ListType extends Type
{
	private final Set<String> types = new LinkedHashSet<String>();
	
	public void add(Type type)
	{
		types.add( type.toString() );
	}
	
	private static final Type TYPE = new NamedType("Type<List>");
	
	@Override
	public Type inferType(Scope scope)
	{
		return TYPE;
	}

	@Override
	public Type getType()
	{
		return TYPE;
	}


	@Override
	public String toString()
	{
		StringBuilder sb =  new StringBuilder();
		sb.append("list of ").append(types);
		return sb.toString();
	}
	
	
}
