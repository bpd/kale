package kaygan.cell;

public interface Cell
{
	Type getType();
	
	Cell eval();
	
	public enum Type implements Cell
	{
		// Base Types
		Nil(0),       // 0000
		Cons(8),      // 1000
		Atom(1),      // 0001
		
		// Atom Types
		Num(3),       // 0011
		Str(7),       // 0111
		Symbol(5),    // 0101
		
		// Cons Types
		Sequence(10), // 1010
		Chain(12)     // 1100
		
		;
		
		private final int mask;
		
		public int mask() { return mask; }
		
		private Type(int mask)
		{
			this.mask = mask;
		}
		
		public boolean isInstance(Type type)
		{
			Integer.toBinaryString(this.mask);
			return (this.mask & type.mask) == type.mask;
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
