package kale.ast;

import kale.Scope;
import kale.Token;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class BooleanLiteral extends Expression
{
	final Token token;
	
	final boolean value;
	
	public BooleanLiteral( Token token )
	{
		this.token = token;
		
		this.value = Boolean.parseBoolean(token.value);
	}
	
	public boolean getValue()
	{
		return value;
	}
	
	@Override
	public void inferType(Scope scope)
	{
		ASTNode node = scope.get("boolean");
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
		mv.visitInsn( value ? Opcodes.ICONST_1 : Opcodes.ICONST_0 );
	}
	
	@Override
	public String toString()
	{
		return String.valueOf(value);
	}
}
