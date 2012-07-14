package kaygan.type;

import java.util.LinkedHashSet;
import java.util.Set;

public class ListType extends Type
{
	private static final Type TYPE = new NamedType("Type<List>");
	
	private final Set<Type> types = new LinkedHashSet<Type>();
	
	public ListType()
	{
		this.type = TYPE;
	}
	
	public void add(Type type)
	{
		types.add( type );
	}
	
	@Override
	public boolean accept(Type type)
	{
		if( type instanceof ListType )
		{
			ListType listType = (ListType)type;
			for( Type innerType : listType.types )
			{
				if( !this.types.contains(innerType) )
				{
					return false;
				}
			}
			return true;
		}
		return types.contains( type.toString() );
	}
	
	@Override
	public Type substitute(Type from, Type to)
	{
		ListType newTypes = new ListType();
		
		for( Type type : this.types )
		{
			newTypes.add( type.substitute(from, to) );
		}
		
		return newTypes;
	}

	@Override
	public String toString()
	{
		StringBuilder sb =  new StringBuilder();
		sb.append("list of ").append(types);
		return sb.toString();
	}
	
	
}
