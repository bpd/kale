package kaygan.type;

import java.util.Map;

public class OrType extends Type
{
	private final Type left;
	
	private final Type right;
	
	public OrType( Type left, Type right )
	{
		this.left = left;
		this.right = right;
	}
	
	@Override
	public boolean accept(Type type)
	{
		return left.accept(type) || right.accept(type);
	}
	
	@Override
	public Type substitute(Map<Type, Type> substitutions)
	{
		return new OrType(	left.substitute(substitutions),
							right.substitute(substitutions) );
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append(left.toString());
		sb.append(" | ");
		sb.append(right.toString());
		sb.append(')');
		return sb.toString();
	}
}
