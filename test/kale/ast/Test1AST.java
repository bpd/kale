package kale.ast;

import junit.framework.TestCase;
import kale.Token;
import kale.TokenType;
import kale.ast.Expression;
import kale.ast.FieldDecl;
import kale.ast.FunctionDecl;
import kale.ast.Id;
import kale.ast.IntLiteral;
import kale.ast.ParamDecl;
import kale.ast.ReturnStatement;
import kale.ast.Signature;
import kale.ast.TypeDecl;
import sun.invoke.anon.AnonymousClassLoader;

public class Test1AST extends TestCase
{
	protected TypeDecl newPersonType()
	{
		TypeDecl type = new TypeDecl( new Id("Person"),
				new FieldDecl[]{
					new FieldDecl(new Id("name"), new Id("string")),
					new FieldDecl(new Id("age"), new Id("int"))
				},
				new FunctionDecl[]{
					new FunctionDecl(	new Id("Age"),
										new Signature(	false,
														new ParamDecl[]{},
														new Id("int") ),
										new Expression[]{
											new ReturnStatement(
												new IntLiteral( new Token(TokenType.Int, "2") )
												)
										} )
				} );
		type.setPackageName("test");
		return type;
	}
	
	public void testSimpleReturn() throws Exception
	{
		// type Person
		TypeDecl type = newPersonType();
		
		byte[] classBytes = type.emitBytecode();
		
		Class<?> clazz = new AnonymousClassLoader().loadClass(classBytes);
		
		Object result = clazz.getDeclaredMethod("Age")
							.invoke( clazz.newInstance() );
		
		assertEquals( 2, result );
	}

}
