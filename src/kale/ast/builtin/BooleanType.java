package kale.ast.builtin;

import kale.ast.Expression;
import kale.ast.FieldDecl;
import kale.ast.FunctionDecl;
import kale.ast.Id;
import kale.ast.ParamDecl;
import kale.ast.Signature;
import kale.ast.TypeDecl;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class BooleanType extends TypeDecl
{
	public BooleanType()
	{
		super(
			new Id("boolean"),
			new FieldDecl[0],
			new FunctionDecl[]
			{
				new FunctionDecl(
					new Id("&&"),
					new Signature(
							true,
							new ParamDecl[]
							{
								new ParamDecl(new Id("a"), new Id("boolean") ),
								new ParamDecl(new Id("b"), new Id("boolean") )
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
								// load 'a' and 'b'
								mv.visitVarInsn(Opcodes.ILOAD, 0);
								mv.visitVarInsn(Opcodes.ILOAD, 1);
								
								mv.visitInsn(Opcodes.IAND);
								
								mv.visitInsn(Opcodes.IRETURN);
							}
						}
					}
				),
				
				new FunctionDecl(
					new Id("||"),
					new Signature(
							true,
							new ParamDecl[]
							{
								new ParamDecl(new Id("a"), new Id("boolean") ),
								new ParamDecl(new Id("b"), new Id("boolean") )
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
								// load 'a' and 'b'
								mv.visitVarInsn(Opcodes.ILOAD, 0);
								mv.visitVarInsn(Opcodes.ILOAD, 1);
								
								mv.visitInsn(Opcodes.IOR);
								
								mv.visitInsn(Opcodes.IRETURN);
							}
						}
					}
				),
				
				new FunctionDecl(
					new Id("=="),
					new Signature(
							true,
							new ParamDecl[]
							{
								new ParamDecl(new Id("a"), new Id("boolean") ),
								new ParamDecl(new Id("b"), new Id("boolean") )
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
								// load 'a' and 'b'
								mv.visitVarInsn(Opcodes.ILOAD, 0);
								mv.visitVarInsn(Opcodes.ILOAD, 1);
								
								Label l0 = new Label();
								mv.visitJumpInsn(Opcodes.IFEQ, l0);
								mv.visitInsn(Opcodes.ICONST_0);
								mv.visitInsn(Opcodes.IRETURN);
								
								// l0:
								mv.visitLabel(l0);
								mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
								mv.visitInsn(Opcodes.ICONST_1);
								mv.visitInsn(Opcodes.IRETURN);
							}
						}
					}
				)
			}
		);
		
		setPackageName("kl");
	}
	
	@Override
	public String toDescriptor()
	{
		return "Z";
	}
};
