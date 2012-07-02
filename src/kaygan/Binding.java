package kaygan;

public abstract class Binding implements Bindable
{
	public abstract Object invoke(Scope scope);
	
	public Binding bind(Bindable parent)
	{
		return null;
	}
}
