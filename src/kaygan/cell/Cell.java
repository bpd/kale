package kaygan.cell;

public class Cell
{
	public Object left;
	
	public Object right;
	
	public Cell(Object left, Object next)
	{
		this.left = left;
		this.right = next;
	}
	
	@Override
	public String toString()
	{
		if( right == null )
		{
			return left.toString();
		}
		else
		{
			StringBuilder sb = new StringBuilder();
			
			sb.append(left.toString());
			sb.append(' ');
			sb.append(right.toString());
			
			if( left.equals("[") )
			{
				sb.append(" ]");
			}
			else if( left.equals("(") )
			{
				sb.append(" )");
			}

			return sb.toString();
		}
	}
	
	public String toCellString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		sb.append(' ');
		
		if( left instanceof Cell )
		{
			sb.append(((Cell) left).toCellString());
		}
		else
		{
			sb.append(left.toString());
		}
		
		if( right != null )
		{
			if( right instanceof Cell )
			{
				sb.append(' ');
				sb.append(((Cell) right).toCellString());
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
