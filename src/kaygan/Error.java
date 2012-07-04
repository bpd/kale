package kaygan;

public class Error implements Function
{
	private final String message;
	
	
	private final String trace;
	
	public Error(String message)
	{
		this.message = message;
		
		this.trace = "<Error: " + this.message + ">";
	}
	
	@Override
	public Function bind(Function f)
	{
		return this;
	}
	
	@Override
	public Function eval()
	{
		return this; //();
	}
	
	@Override
	public String toString()
	{
		return trace;
	}
}
