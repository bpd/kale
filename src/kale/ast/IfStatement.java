package kale.ast;

import kale.Scope;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import org.objectweb.asm.Opcodes;

public class IfStatement extends Expression
{
	final Expression condition;
	
	/** statements to execute if condition is true */
	final Expression[] ifBlock;
	
	// Note: either 'nestedIf' or 'elseBlock' should be non-null
	
	/** 'else if' */
	final Expression nestedIf;
	
	/** 'else' */
	final Expression[] elseBlock;
	
	public IfStatement(	Expression condition,
						Expression[] ifBlock,
						Expression nestedIf,
						Expression[] elseBlock )
	{
		this.condition = condition;
		this.ifBlock = ifBlock;
		this.nestedIf = nestedIf;
		this.elseBlock = elseBlock;
		
		addChild( condition );
		addChildren( ifBlock );
		
		if( nestedIf != null )
		{
			addChild( nestedIf );
		}
		
		if( elseBlock != null )
		{
			addChildren( elseBlock );
		}
	}
	
	@Override
	public void inferType(Scope scope)
	{
		condition.inferType(scope);
		
		Scope ifBlockScope = scope.newFrameSubScope();
		for( Expression statement : ifBlock )
		{
			statement.inferType( ifBlockScope );
		}
		
		if( nestedIf != null )
		{
			Scope nestedIfScope = scope.newFrameSubScope();
			nestedIf.inferType( nestedIfScope );
		}
		
		if( elseBlock != null )
		{
			Scope elseBlockScope = scope.newFrameSubScope();
			for( Expression statement : elseBlock )
			{
				statement.inferType( elseBlockScope );
			}
		}
	}
	
	@Override
	public void emitBytecode(MethodVisitor mv)
	{
		// it's assumed the condition will push a boolean onto
		// the stack indicating its result
		condition.emitBytecode(mv);
		
		Label l0 = new Label();
		
		mv.visitJumpInsn(Opcodes.IFEQ, l0);
		
		// emit bytecode for 'true' condition
		for( Expression statement : ifBlock )
		{
			statement.emitBytecode(mv);
		}
		
		// emit bytecode for 'false' condition
		mv.visitLabel(l0);
		
		if( nestedIf != null )
		{
			// if 'condition' is false, this should be called
			nestedIf.emitBytecode(mv);
		}
		else if( elseBlock != null )
		{
			// if 'condition' is false, this should be branched to
			for( Expression statement : elseBlock )
			{
				statement.emitBytecode(mv);
			}
		}
	}



	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("if ").append( this.condition ).append(" {\n");
		for( Expression block : ifBlock )
		{
			sb.append( block );
		}
		sb.append("\n}\n");
		
		if( this.nestedIf != null )
		{
			sb.append("else ");
			sb.append( this.nestedIf );
		}
		else if( this.elseBlock != null )
		{
			sb.append("else {\n");
			for( Expression block : elseBlock )
			{
				sb.append( block );
			}
			sb.append("\n}\n");
		}
		return sb.toString();
	}
	
}
