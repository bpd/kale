package kaygan.atom;

import kaygan.Function;
import kaygan.Type;

public class Num extends Function
{
	private final Number value;
	
	public Num(Number value)
	{
		this.value = value;
		
		set("+", new Function()
		{
			@Override
			public Function bind(Function f)
			{
				if( f instanceof Symbol )
				{
					Symbol symbol = (Symbol)f;
					
					System.out.println("binding " + f);
				}
				// TODO a
				return this;
			}
			
			@Override
			public Function eval()
			{
				return this;
			}
		} );
	}
	
	@Override
	public int hashCode()
	{
		return value.hashCode();
	}
	
	@Override
	public boolean equals(Object other)
	{
		return other instanceof Num
			&& ((Num)other).value.equals(this.value);
	}
	
	public Function eval()
	{
		return this;
	}

	@Override
	public Function bind(Function f)
	{
		return this;
	}
	
	private static final Type TYPE = new Type("<Num>");
	
	@Override
	public Type getType()
	{
		return TYPE;
	}
	
	@Override
	public String toString()
	{
		return value.toString();
	}
}
