package kale.ast.builtin;

import kale.ast.Expression;
import kale.ast.FieldDecl;
import kale.ast.FunctionDecl;
import kale.ast.Id;
import kale.ast.ParamDecl;
import kale.ast.Signature;
import kale.ast.TypeDecl;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class StringType extends TypeDecl
{
	public StringType()
	{
		super(	new Id("string"),
				new FieldDecl[0],
				new FunctionDecl[]
				{
					new FunctionDecl(
							new Id("=="),
							new Signature(
									true,
									new ParamDecl[]
									{
										new ParamDecl(new Id("a"), new Id("string") ),
										new ParamDecl(new Id("b"), new Id("string") )
									},
									new Id("boolean")
							),
							new Expression[]
							{
								new Expression()
								{
									@Override
									public void emitBytecode(MethodVisitor mv)
									{
										mv.visitVarInsn(Opcodes.ALOAD, 0);
										mv.visitVarInsn(Opcodes.ALOAD, 1);
										mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
										mv.visitInsn(Opcodes.IRETURN);
									}
								}
							}
					),
					new FunctionDecl(
							new Id("!="),
							new Signature(
									true,
									new ParamDecl[]
									{
										new ParamDecl(new Id("a"), new Id("string") ),
										new ParamDecl(new Id("b"), new Id("string") )
									},
									new Id("boolean")
							),
							new Expression[]
							{
								new Expression()
								{
									@Override
									public void emitBytecode(MethodVisitor mv)
									{
										mv.visitVarInsn(Opcodes.ALOAD, 0);
										mv.visitVarInsn(Opcodes.ALOAD, 1);
										mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
										mv.visitLdcInsn(1);
										mv.visitInsn(Opcodes.IXOR);
										mv.visitInsn(Opcodes.IRETURN);
									}
								}
							}
					),
					new FunctionDecl(
							new Id("+"),
							new Signature(
									true,
									new ParamDecl[]
									{
										new ParamDecl(new Id("a"), new Id("string") ),
										new ParamDecl(new Id("b"), new Id("string") )
									},
									new Id("string")
							),
							new Expression[]
							{
								new Expression()
								{
									@Override
									public void emitBytecode(MethodVisitor mv)
									{
										mv.visitVarInsn(Opcodes.ALOAD, 0);
										mv.visitVarInsn(Opcodes.ALOAD, 1);
										mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "concat", "(Ljava/lang/String;)Ljava/lang/String;");
										mv.visitInsn(Opcodes.ARETURN);
									}
								}
							}
					)
				});
		
		setPackageName("kl");
	}
	
	@Override
	public String toDescriptor()
	{
		return "Ljava/lang/String;";
	}
};
