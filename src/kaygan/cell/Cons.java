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
	
	public Cell type = Atom.Nil;
	
	@Override
	public Cell getType()
	{
		if( type == Atom.Nil )
		{
			type = deriveType();
		}
		return type;
	}
	
	public void setType(Cell type)
	{
		this.type = type;
	}
	
	protected Cell deriveType()
	{
		if( left.getType() == Atom.Symbol )
		{
			return right.getType();
		}
		else
		{
			return new Atom( left.getType() + " | " + right.getType(), Atom.OrType );
		}
	}
	
	@Override
	public Cell eval()
	{
		return this;
	}
	
	
	@Override
	public String toString()
	{
		if( right == Atom.Nil )
		{
			return left.toString();
		}
		else
		{
			StringBuilder sb = new StringBuilder();
			
			if( getType() == Atom.Sequence )
			{
				sb.append('[');
				sb.append(left.toString());
				sb.append(' ');
				sb.append(right.toString());
				sb.append(']');
			}
			else if( getType() == Atom.Chain )
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
		
		if( right != Atom.Nil )
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
