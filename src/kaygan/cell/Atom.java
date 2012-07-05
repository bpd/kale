package kaygan.cell;

public class Atom implements Cell
{
	private Object value;
	
	private Type type;
	
	public Atom(Object value, Type type)
	{
		this.value = value;
		this.type = type;
	}
	
	@Override
	public Cell.Type getType()
	{
		return type;
	}
	
	@Override
	public Cell eval()
	{
		return this;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if( o == this )
		{
			return true;
		}
		if( o instanceof Atom )
		{
			Atom other = (Atom)o;
			return other.type.equals(this.type) && other.value.equals(this.value);
		}
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return value.hashCode() * 13 * (type != null ? type.hashCode() : 1);
	}
	
	@Override
	public String toString()
	{
		return value == null ? "Nil" : value.toString();
	}

}
