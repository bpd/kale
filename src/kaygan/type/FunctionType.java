package kaygan.type;

import java.util.Arrays;

public class FunctionType extends Type
{
	private static final Type TYPE = new NamedType("Type<Function>");
	
	private final Type[] argTypes;
	
	private final Type retType;
	
	public FunctionType( Type[] argsTypes, Type retType )
	{
		this.argTypes = argsTypes;
		this.retType = retType;
		this.type = TYPE;
	}
	
	public Type[] getArgTypes()
	{
		return argTypes;
	}
	
	public Type getRetType()
	{
		return retType;
	}


	@Override
	public String toString()
	{
		return new StringBuilder()
			.append('(')
			.append(Arrays.toString(argTypes)).append("=>").append(retType)
			.append(')')
			.toString();
	}
}
