package kale.ast;


import kale.Scope;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ReturnStatement extends Expression
{
	final Expression expression;
	
	public ReturnStatement( Expression expression )
	{
		if( expression == null )
		{
			throw new IllegalArgumentException("Expression cannot be null");
		}
		this.expression = expression;
		
		addChild( expression );
	}
	
	public Expression getExpression()
	{
		return expression;
	}
	
	@Override
	public void inferType(Scope scope)
	{
		expression.inferType(scope);
		
		setType( expression.getType() );
	}

	@Override
	public void emitBytecode(MethodVisitor mv)
	{
		// push expression result onto stack
		expression.emitBytecode(mv);
		
		Type retType = expression.getType();
		String descriptor = retType.toDescriptor();
		
		if( descriptor.equals("I") || descriptor.equals("Z") )
		{
			mv.visitInsn(Opcodes.IRETURN);
		}
		else if( descriptor.equals("V") )
		{
			mv.visitInsn(Opcodes.RETURN);
		}
		else
		{
			// assume reference
			mv.visitInsn(Opcodes.ARETURN);
		}
		
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("return ");
		sb.append(expression);
		sb.append(';');
		return sb.toString();
	}
}
