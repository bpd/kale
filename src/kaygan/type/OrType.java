package kaygan.type;

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
