package kale.ast;

import org.objectweb.asm.MethodVisitor;

import kale.Scope;
import kale.Token;

public class StringLiteral extends Expression
{
	final Token token;
	
	final String value;
	
	public StringLiteral( Token token )
	{
		this.token = token;
		value = token.value.substring(1, token.value.length()-1);
	}
	
	public String getValue()
	{
		return value;
	}
	
	@Override
	public void inferType(Scope scope)
	{
		ASTNode node = scope.get("string");
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
		mv.visitLdcInsn( this.value );
	}
	
	@Override
	public String toString()
	{
		return token.value;
	}
}
