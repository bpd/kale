package kaygan.cell;

public interface Cell
{
	Type getType();
	
	Cell eval();
	
	public enum Type implements Cell
	{
		// Base Types
		Cons(0),      // 0000
		Atom(1),      // 0001
		
		// Atom Types
		Nil(3),       // 0011
		Num(5),       // 0101
		Str(7),       // 0111
		Symbol(9),    // 1001
		
		// Cons Types
		Sequence(2),  // 0010
		Chain(4)      // 0100
		
		;
		
		private final int mask;
		
		public int mask() { return mask; }
		
		private Type(int mask)
		{
			this.mask = mask;
		}
		
		public boolean isInstance(Type type)
		{
			return (this.mask & type.mask) == this.mask;
		}
		
		public Type getType()
		{
			return this;
		}
		
		public Cell eval()
		{
			return this;
		}
	}	
}
