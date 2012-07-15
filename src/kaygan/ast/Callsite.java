package kaygan.ast;

import java.util.Arrays;

import kaygan.Token;
import kaygan.type.DependentType;
import kaygan.type.FunctionType;
import kaygan.type.Type;
import kaygan.type.TypeException;

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
			
			// substitute the arguments
			try
			{
				Type[] argTypes = funcType.getArgTypes();
				
				for( int i=1; i<this.exps.length; i++ )
				{
					Type senderArgType = this.exps[i].type;
					
					Type receiverArgType = argTypes[i-1];
					
					funcType = funcType.substitute(receiverArgType, senderArgType);
				}
				
				//funcType = funcType.substitute( funcType.getRetType(), first.type );
			}
			catch(TypeException e)
			{
				this.error( e.getMessage() );
			}
			
			first.type = funcType;
			this.type = funcType.getRetType();
			
		}
		else if( first instanceof Symbol )
		{
			// if first.type is not a function we must generate
			// a dependent type for when the receiver is known
			//
			Symbol receiver = (Symbol)first;
			
			Type[] argTypes = new Type[ this.exps.length - 1 ];
			
			for( int i=0; i<argTypes.length; i++ )
			{
				Type senderArgType = this.exps[i+1].type;
				
				argTypes[i] = senderArgType;
			}
			
			this.type = new DependentType(receiver.type, argTypes);
		}
		else
		{
			first.error("Unknown dispatch type");
			first.type = Type.ERROR;
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
