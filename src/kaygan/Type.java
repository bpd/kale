package kaygan;

public class Type extends Function
{
	private final String signature;
	
	public Type(String signature)
	{
		this.signature = signature;
	}
	
	@Override
	public int hashCode()
	{
		return signature.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if( o instanceof Type )
		{
			return ((Type)o).signature.equals(this.signature);
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		return signature;
	}
}
