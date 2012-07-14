package kaygan.ast;

import java.util.Arrays;

import kaygan.Scope;
import kaygan.Token;
import kaygan.type.FunctionType;
import kaygan.type.NamedType;
import kaygan.type.Type;

public class Function extends Block
{
	public final Token open;
	public final Token close;
	
	public final Exp[] args;
	
	// TODO this really needs to be moved somewhere else, after
	//      the interpreter is more fleshed out
	public Scope scope;
	
	public Function(Token open, Token close, Exp[] args, Exp[] exps)
	{
		super( exps );
		
		this.open = open;
		this.close = close;
		this.args = args;
	}
	
	@Override
	public int getOffset()
	{
		return open.beginOffset;
	}

	@Override
	public int getLength()
	{
		return close.endOffset - open.beginOffset;
	}
	
	@Override
	public ASTNode findNode(int offset)
	{
		for( Exp exp : args )
		{
			if( exp.overlaps(offset) )
			{
				return exp.findNode(offset);
			}
		}
		for( Exp exp : exps )
		{
			if( exp.overlaps(offset) )
			{
				return exp.findNode(offset);
			}
		}
		return overlaps(offset) ? this : null;
	}
	
	@Override
	public void link(Scope scope)
	{
		Scope functionScope = scope.newSubScope();
		
		// link arguments
		for( Exp arg : this.args )
		{
			if( arg instanceof Bind )
			{
				arg.link( functionScope );
			}
			else if( arg instanceof Symbol )
			{
				Symbol symbol = (Symbol)arg;
				String key = symbol.symbol();
				
				if( functionScope.getLocal(key) != null )
				{
					symbol.error("Duplicate argument name");
					symbol.ref = Type.ERROR;
				}
				else
				{
					functionScope.set( key, symbol );
					symbol.ref = Type.ANY;
				}
			}
			else
			{
				arg.error(" Expected Bind | Symbol ");
			}
		}
		
		// link contents
		for( Exp exp : this )
		{
			exp.link( functionScope );
		}
	}
	
	@Override
	public Type inferType()
	{
		super.inferType();
		
		if( this.type != null )
		{
			return this.type;
		}
		
		Type[] argTypes = new Type[this.args.length];
		for( int i=0; i<argTypes.length; i++ )
		{
			Exp arg = this.args[i];
			
			if( arg instanceof Symbol )
			{
				Symbol symbolArg = (Symbol)arg;
				
				symbolArg.type = new NamedType(
									"Type<"+symbolArg.symbol()+">")
				{
					// generated type for argument
					@Override
					public boolean accept(Type type)
					{
						return true;
					}
				};
				symbolArg.ref = symbolArg.type;
			}
			else if( arg instanceof Bind )
			{
				arg.inferType();
			}
			else
			{
				arg.error("Expected Symbol | Bind");
				arg.type = Type.ERROR;
			}
			
			argTypes[i] = arg.type;
		}
		
		for( Exp e : this )
		{
			e.inferType();
		}
		
		Type retType = Type.ANY;
		if( this.size() > 0 )
		{
			Exp last = this.exps[ this.exps.length -1 ];
			
			retType = last.type;
		}
		
		this.type = new FunctionType( argTypes, retType );
		
		return this.type;
	}


	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{Function: ");
		sb.append("args:").append(args);
		sb.append(' ').append(" exps:").append(Arrays.toString(exps));
		sb.append('}');
		return sb.toString();
	}
	
}
