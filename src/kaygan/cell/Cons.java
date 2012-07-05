package kaygan.cell;

public class Cons implements Cell
{
	public Cell left;
	
	public Cell right;
	
	public Cons(Cell left, Cell next)
	{
		this.left = left;
		this.right = next;
	}
	
	public Type type = Type.Nil;
	
	@Override
	public Type getType()
	{
		return type;
	}
	
	public void setType(Type type)
	{
		this.type = type;
	}
	
	@Override
	public Cell eval()
	{
		return this;
	}
	
	
	@Override
	public String toString()
	{
		if( right == Type.Nil )
		{
			return left.toString();
		}
		else
		{
			StringBuilder sb = new StringBuilder();
			
			if( getType() == Type.Sequence )
			{
				sb.append('[');
				sb.append(left.toString());
				sb.append(' ');
				sb.append(right.toString());
				sb.append(']');
			}
			else if( getType() == Type.Chain )
			{
				sb.append('(');
				sb.append(left.toString());
				sb.append(' ');
				sb.append(right.toString());
				sb.append(')');
			}
			else
			{
				sb.append(left.toString());
				sb.append(' ');
				sb.append(right.toString());
			}

			return sb.toString();
		}
	}
	
	public String toCellString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		sb.append(' ');
		
		if( left instanceof Cons )
		{
			sb.append(((Cons) left).toCellString());
		}
		else
		{
			sb.append(left.toString());
		}
		
		if( right != Type.Nil )
		{
			if( right instanceof Cons )
			{
				sb.append(' ');
				sb.append(((Cons) right).toCellString());
			}
			else
			{
				sb.append(" . ");
				sb.append(right.toString());
			}
		}
		
		sb.append(' ');
		sb.append('}');
		return sb.toString();
	}
}
