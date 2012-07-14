package kaygan.ast;

import java.util.Arrays;

import kaygan.Scope;
import kaygan.Token;
import kaygan.type.FunctionType;
import kaygan.type.Type;

public class Callsite extends Block
{
	public final Token open;
	public final Token close;
	
	public Callsite(Token open, Token close, Exp[] contents)
	{
		super( contents );
		
		if( contents.length < 1 )
		{
			throw new IllegalArgumentException(
					"Callsite must have at least one expression");
		}
		this.open = open;
		this.close = close;
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
		// if this node has errors, we want
		// it to 'cover' its contents
		if( hasErrors() && overlaps(offset) )
		{
			return this;
		}
		
		for( Exp exp : this )
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
		for( Exp e : this )
		{
			e.link(scope);
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
		
		for( Exp exp : this )
		{
			exp.inferType();
		}
		
		Exp first = this.exps[0];
		
		if( first.type instanceof FunctionType )
		{
			FunctionType funcType = (FunctionType)first.type;
			
			System.out.println("function type: " + funcType);
			
			// substitute the arguments
			try
			{
				Type[] argTypes = funcType.getArgTypes();
				
				for( int i=1; i<this.exps.length; i++ )
				{
					Type senderArgType = this.exps[i].type;
					
					Type receiverArgType = argTypes[i-1];
					
					System.out.println("substitution: " + funcType);
					System.out.println("substituting " + receiverArgType + " -> " + senderArgType);
					
					funcType = funcType.substitute(receiverArgType, senderArgType);
					
					System.out.println("substitution result: " + funcType);
				}
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
			
			first.type = funcType;
			this.type = funcType.getRetType();
			
		}
		else
		{
			System.out.println("not a function type: " + first.type);
			this.type = first.type;
		}
		
		return this.type;
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{Callsite: ");
		sb.append( Arrays.toString(this.exps) );
		sb.append('}');
		return sb.toString();
	}
}
