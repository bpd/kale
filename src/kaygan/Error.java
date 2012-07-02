package kaygan;

public class Error extends Binding
{
	private final String message;
	
	public Error(String message)
	{
		this.message = message;
	}
	
	public Object invoke(Scope scope)
	{
		throw new RuntimeException(this.message);
	}
}
