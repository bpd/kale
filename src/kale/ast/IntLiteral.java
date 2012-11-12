package kale.ast;

import kale.Scope;
import kale.Token;

import org.objectweb.asm.MethodVisitor;

public class IntLiteral	extends Expression
{
	final Token token;
	
	final int value;
	
	public IntLiteral( Token token )
	{
		this.token = token;
		
		this.value = Integer.parseInt(token.value);
	}
	
	public int getValue()
	{
		return value;
	}
	
	@Override
	public void inferType(Scope scope)
	{
		ASTNode node = scope.get("int");
		if( node instanceof TypeDecl )
		{
			setType( (TypeDecl)node );
		}
		else
		{
			error("Expected TypeDecl");
		}
	}
	
	@Override
	public int getBeginOffset()
	{
		return token.beginOffset;
	}

	@Override
	public int getEndOffset()
	{
		return token.endOffset;
	}
	
	@Override
	public void emitBytecode(MethodVisitor mv)
	{
		// load value onto stack
		mv.visitLdcInsn( value );
	}
	
	@Override
	public String toString()
	{
		return String.valueOf(value);
	}
	
	
}
