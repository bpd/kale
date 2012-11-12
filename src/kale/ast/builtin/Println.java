package kale.ast.builtin;

import kale.ast.Expression;
import kale.ast.FunctionDecl;
import kale.ast.Id;
import kale.ast.ParamDecl;
import kale.ast.Signature;

import org.objectweb.asm.MethodVisitor;

import org.objectweb.asm.Opcodes;

public class Println extends FunctionDecl
{
	public Println()
	{
		super(
			new Id("println"),
			new Signature(
					false,
					new ParamDecl[]
					{
						new ParamDecl(new Id("a"), new Id("int") )
					},
					new Id("void")
			),
			new Expression[]
			{
				new Expression()
				{
					@Override
					public void emitBytecode(MethodVisitor mv)
					{
						mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
						mv.visitVarInsn(Opcodes.ILOAD, 0);
						mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V");
						mv.visitInsn(Opcodes.RETURN);
					}
				}
			}
		);
	}
}
