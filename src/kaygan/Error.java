package kaygan;

public class Error extends Function
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
	
	private static final Type TYPE = new Type("<Error>");
	
	@Override
	public Type getType()
	{
		return TYPE;
	}
	
	@Override
	public String toString()
	{
		return trace;
	}
}
