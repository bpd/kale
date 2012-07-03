package kaygan;

public class Error implements Function
{
	private final String message;
	
	public Error(String message)
	{
		this.message = message;
	}
	
	@Override
	public Function bind(Function f)
	{
		return this;
	}
	
	@Override
	public Function eval()
	{
		throw new RuntimeException(this.message);
	}
}
