package kaygan.type;

import java.util.Arrays;

import kaygan.Scope;

public class FunctionType extends Type
{
	private final Type[] argTypes;
	
	private final Type retType;
	
	public FunctionType( Type[] argsTypes, Type retType )
	{
		this.argTypes = argsTypes;
		this.retType = retType;
	}
	
	private static final Type TYPE = new NamedType("Type<Function>");
	
	@Override
	public Type inferType(Scope scope)
	{
		return TYPE;
	}

	@Override
	public Type getType()
	{
		return TYPE;
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
