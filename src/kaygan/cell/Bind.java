package kaygan.cell;

public class Bind
{
	private final String value;
	
	private final String displayValue;
	
	public Bind(String value)
	{
		this.value = value;
		
		this.displayValue = value + ':';
	}
	
	public String getValue()
	{
		return value;
	}
	
	@Override
	public String toString()
	{
		return displayValue;
	}
}
