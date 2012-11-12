package kale.ast;

import kale.Scope;

import org.objectweb.asm.MethodVisitor;

public class Operation extends Expression
{
	final Id operator;
	
	final Expression left;
	
	final Expression right;
	
	// set by type inference
	FunctionDecl target;
	
	public Operation(Id operator, Expression left, Expression right)
	{
		this.operator = operator;
		this.left = left;
		this.right = right;
		
		addChild( left );
		addChild( operator );
		if( right != null )
		{
			addChild( right );
		}
	}
	
	public Id getOperator()
	{
		return operator;
	}
	
	public Expression getLeft()
	{
		return left;
	}
	
	public Expression getRight()
	{
		return right;
	}
	
	
	@Override
	public void inferType(Scope scope)
	{
		left.inferType(scope);
		if( right == null )
		{
			error("Invalid right side of operator");
			return;
		}
		right.inferType(scope);
		
		if( right.hasErrors() )
		{
			// if the right side has problems, we can't continue
			return;
		}
		
		// lookup the type of 'left'
		//   it should be a TypeDecl
		if( left.getType() instanceof TypeDecl )
		{
			TypeDecl leftType = (TypeDecl)left.getType();
			
			FunctionDecl opFun = leftType.getMethod( operator.id );
			if( opFun != null )
			{
				ParamDecl[] params = opFun.getSignature().getParams();
				
				if( params.length == 2 )
				{
					// the 'left' and 'right' types have to agree with each other
					if( leftType.isEquivalent( right.getType() ) )
					{
						// and they both have to agree with the parameters of the operator
						// (TypeDecl verifies operator params are equivalent)
						if( leftType.isEquivalent(params[0].getType()) )
						{
							target = opFun;
							setType(opFun.getSignature().getReturnType().getType());
						}
						else
						{
							error("Argument doesn't match parameter");
						}
					}
					else
					{
						error("Operator arguments must be of the same type");
					}
				}
				else
				{
					error("Bad operator signature: two parameters expected");
				}
			}
			else
			{
				error("No method " + operator.id + " found on type " + leftType);
			}
			
		}
		else
		{
			error("Expecting left side to be a TypeDecl, instead found " + left.getType());
			return;
		}
		
		
		// lookup 'operator' in 'left'
		//   it should be a function with two arguments
		
		// lookup the type of 'right'
		//   it should match the argument on the operator function
		
		// verify function arguments with types of 'left' and 'right'
		
		
		
	}



	@Override
	public void emitBytecode(MethodVisitor mv)
	{
		// does this just turn into a method (operation) lookup
		// on the 'left' expression result type and then an invocation?
		
		// this.getType() should be a FunctionDecl that takes the results
		// of 'left' and 'right' expressions as parameters (in that order)
		
		left.emitBytecode(mv);
		right.emitBytecode(mv);
		
		Invocation.emitInvocation(target, mv);
		
	}



	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		sb.append( left.toString() );
		sb.append(' ');
		sb.append( operator.toString() );
		sb.append(' ');
		sb.append( right.toString() );
		sb.append(']');
		return sb.toString();
	}
}
