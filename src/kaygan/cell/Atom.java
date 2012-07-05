package kaygan.cell;

public class Atom implements Cell
{
	private Object value;
	
	private Cell type;
	
	public Atom(Object value, Cell type)
	{
		this.value = value;
		this.type = type;
	}
	
	@Override
	public Cell getType()
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
	
	public static final Atom Nil = new Atom("Nil", null);
	static
	{
		// circular Nil type TODO is this a good idea?
		Nil.type = Nil;
	}
	
	// Types
	public static final Atom Type = new Atom("<Type>", Atom.Nil);
	
	public static final Atom OrType = new Atom("<OrType>", Type);
	
	// Atom Types
	public static final Atom Num = new Atom("<Num>", Type);
	
	public static final Atom Str = new Atom("<Str>", Type);
	
	public static final Atom Symbol = new Atom("<Symbol>", Type);
	
	// List Types
	public static final Atom Sequence = new Atom("<Sequence>", Type);
	
	public static final Atom Chain = new Atom("<Chain>", Type);
	
	
	
	
}
