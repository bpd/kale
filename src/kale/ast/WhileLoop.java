package kale.ast;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import kale.Scope;

public class WhileLoop extends Expression
{
	final Expression condition;
	
	final Expression[] block;
	
	public WhileLoop( Expression condition, Expression[] block )
	{
		this.condition = condition;
		this.block = block;
		
		addChild( condition );
		addChildren( block );
	}
	
	@Override
	public void inferType(Scope scope)
	{
		condition.inferType( scope );
		
		if( condition.getType() == null 
			|| !condition.getType().getName().equals("boolean") )
		{
			condition.error("While loop requires boolean condition");
		}
		
		Scope whileBlockScope = scope.newFrameSubScope();
		
		for( Expression statement : block )
		{
			statement.inferType( whileBlockScope );
		}

	}
	
	@Override
	public void emitBytecode(MethodVisitor mv)
	{
		Label begin = new Label();
		Label end = new Label();
		
		// begin:
		mv.visitLabel(begin);
		
		// calculate condition, push boolean result onto stack
		condition.emitBytecode(mv);
		
		// if the condition is false (zero), jump to the end
		mv.visitJumpInsn(Opcodes.IFEQ, end);
		
		// do the work of the while loop...
		for( Expression exp : block )
		{
			exp.emitBytecode(mv);
			
			if( exp instanceof Invocation )
			{
				if( !((Invocation)exp).getType().toDescriptor().equals("V") )
				{
					mv.visitInsn(Opcodes.POP);
				}
			}
		}
		
		// then go back to the beginning to check the condition
		mv.visitJumpInsn(Opcodes.GOTO, begin);
		
		// end:
		mv.visitLabel(end);
	}
}
