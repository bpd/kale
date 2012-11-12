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

public class IntType extends TypeDecl
{
	public IntType()
	{
		super(
			new Id("int"),
			new FieldDecl[0],
			new FunctionDecl[]
			{
				new FunctionDecl(
						new Id("+"),
						new Signature(
								true,
								new ParamDecl[]
								{
									new ParamDecl(new Id("a"), new Id("int") ),
									new ParamDecl(new Id("b"), new Id("int") )
								},
								new Id("int")
						),
						new Expression[]
						{
							new Expression()
							{
								@Override
								public void emitBytecode(MethodVisitor mv)
								{
									mv.visitVarInsn(Opcodes.ILOAD, 0);
									mv.visitVarInsn(Opcodes.ILOAD, 1);
									mv.visitInsn(Opcodes.IADD);
									mv.visitInsn(Opcodes.IRETURN);
								}
							}
						}
				),
				new FunctionDecl(
						new Id("-"),
						new Signature(
								true,
								new ParamDecl[]
								{
									new ParamDecl(new Id("a"), new Id("int") ),
									new ParamDecl(new Id("b"), new Id("int") )
								},
								new Id("int")
						),
						new Expression[]
						{
							new Expression()
							{
								@Override
								public void emitBytecode(MethodVisitor mv)
								{
									mv.visitVarInsn(Opcodes.ILOAD, 0);
									mv.visitVarInsn(Opcodes.ILOAD, 1);
									mv.visitInsn(Opcodes.ISUB);
									mv.visitInsn(Opcodes.IRETURN);
								}
							}
						}
				),
				new FunctionDecl(
						new Id("*"),
						new Signature(
								true,
								new ParamDecl[]
								{
									new ParamDecl(new Id("a"), new Id("int") ),
									new ParamDecl(new Id("b"), new Id("int") )
								},
								new Id("int")
						),
						new Expression[]
						{
							new Expression()
							{
								@Override
								public void emitBytecode(MethodVisitor mv)
								{
									mv.visitVarInsn(Opcodes.ILOAD, 0);
									mv.visitVarInsn(Opcodes.ILOAD, 1);
									mv.visitInsn(Opcodes.IMUL);
									mv.visitInsn(Opcodes.IRETURN);
								}
							}
						}
				),
				new FunctionDecl(
						new Id("/"),
						new Signature(
								true,
								new ParamDecl[]
								{
									new ParamDecl(new Id("a"), new Id("int") ),
									new ParamDecl(new Id("b"), new Id("int") )
								},
								new Id("int")
						),
						new Expression[]
						{
							new Expression()
							{
								@Override
								public void emitBytecode(MethodVisitor mv)
								{
									mv.visitVarInsn(Opcodes.ILOAD, 0);
									mv.visitVarInsn(Opcodes.ILOAD, 1);
									mv.visitInsn(Opcodes.IDIV);
									mv.visitInsn(Opcodes.IRETURN);
								}
							}
						}
				),
				new FunctionDecl(
						new Id("|"),
						new Signature(
								true,
								new ParamDecl[]
								{
									new ParamDecl(new Id("a"), new Id("int") ),
									new ParamDecl(new Id("b"), new Id("int") )
								},
								new Id("int")
						),
						new Expression[]
						{
							new Expression()
							{
								@Override
								public void emitBytecode(MethodVisitor mv)
								{
									mv.visitVarInsn(Opcodes.ILOAD, 0);
									mv.visitVarInsn(Opcodes.ILOAD, 1);
									mv.visitInsn(Opcodes.IOR);
									mv.visitInsn(Opcodes.IRETURN);
								}
							}
						}
				),
				new FunctionDecl(
						new Id("^"),
						new Signature(
								true,
								new ParamDecl[]
								{
									new ParamDecl(new Id("a"), new Id("int") ),
									new ParamDecl(new Id("b"), new Id("int") )
								},
								new Id("int")
						),
						new Expression[]
						{
							new Expression()
							{
								@Override
								public void emitBytecode(MethodVisitor mv)
								{
									mv.visitVarInsn(Opcodes.ILOAD, 0);
									mv.visitVarInsn(Opcodes.ILOAD, 1);
									mv.visitInsn(Opcodes.IXOR);
									mv.visitInsn(Opcodes.IRETURN);
								}
							}
						}
				),
				new FunctionDecl(
						new Id("&"),
						new Signature(
								true,
								new ParamDecl[]
								{
									new ParamDecl(new Id("a"), new Id("int") ),
									new ParamDecl(new Id("b"), new Id("int") )
								},
								new Id("int")
						),
						new Expression[]
						{
							new Expression()
							{
								@Override
								public void emitBytecode(MethodVisitor mv)
								{
									mv.visitVarInsn(Opcodes.ILOAD, 0);
									mv.visitVarInsn(Opcodes.ILOAD, 1);
									mv.visitInsn(Opcodes.IAND);
									mv.visitInsn(Opcodes.IRETURN);
								}
							}
						}
				),
				// TODO: Java class format doesn't like names with '<' or '>'
				//       so need to do *per-character* conversion,
				//       so not just a special group of ops get converted
				new FunctionDecl(
						new Id("<"),
						new Signature(
								true,
								new ParamDecl[]
								{
									new ParamDecl(new Id("a"), new Id("int") ),
									new ParamDecl(new Id("b"), new Id("int") )
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
									mv.visitVarInsn(Opcodes.ILOAD, 0);
									mv.visitVarInsn(Opcodes.ILOAD, 1);
									Label l0 = new Label();
									mv.visitJumpInsn(Opcodes.IF_ICMPGE, l0);
									mv.visitInsn(Opcodes.ICONST_1);
									mv.visitInsn(Opcodes.IRETURN);
									mv.visitLabel(l0);
									mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
									mv.visitInsn(Opcodes.ICONST_0);
									mv.visitInsn(Opcodes.IRETURN);
								}
							}
						}
				),
				new FunctionDecl(
						new Id("<="),
						new Signature(
								true,
								new ParamDecl[]
								{
									new ParamDecl(new Id("a"), new Id("int") ),
									new ParamDecl(new Id("b"), new Id("int") )
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
									mv.visitVarInsn(Opcodes.ILOAD, 0);
									mv.visitVarInsn(Opcodes.ILOAD, 1);
									Label l0 = new Label();
									mv.visitJumpInsn(Opcodes.IF_ICMPGT, l0);
									mv.visitInsn(Opcodes.ICONST_1);
									mv.visitInsn(Opcodes.IRETURN);
									mv.visitLabel(l0);
									mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
									mv.visitInsn(Opcodes.ICONST_0);
									mv.visitInsn(Opcodes.IRETURN);
								}
							}
						}
				),
				new FunctionDecl(
						new Id(">"),
						new Signature(
								true,
								new ParamDecl[]
								{
									new ParamDecl(new Id("a"), new Id("int") ),
									new ParamDecl(new Id("b"), new Id("int") )
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
									mv.visitVarInsn(Opcodes.ILOAD, 0);
									mv.visitVarInsn(Opcodes.ILOAD, 1);
									Label l0 = new Label();
									mv.visitJumpInsn(Opcodes.IF_ICMPLE, l0);
									mv.visitInsn(Opcodes.ICONST_1);
									mv.visitInsn(Opcodes.IRETURN);
									mv.visitLabel(l0);
									mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
									mv.visitInsn(Opcodes.ICONST_0);
									mv.visitInsn(Opcodes.IRETURN);
								}
							}
						}
				),
				new FunctionDecl(
						new Id(">="),
						new Signature(
								true,
								new ParamDecl[]
								{
									new ParamDecl(new Id("a"), new Id("int") ),
									new ParamDecl(new Id("b"), new Id("int") )
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
									mv.visitVarInsn(Opcodes.ILOAD, 0);
									mv.visitVarInsn(Opcodes.ILOAD, 1);
									Label l0 = new Label();
									mv.visitJumpInsn(Opcodes.IF_ICMPLT, l0);
									mv.visitInsn(Opcodes.ICONST_1);
									mv.visitInsn(Opcodes.IRETURN);
									mv.visitLabel(l0);
									mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
									mv.visitInsn(Opcodes.ICONST_0);
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
									new ParamDecl(new Id("a"), new Id("int") ),
									new ParamDecl(new Id("b"), new Id("int") )
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
									mv.visitVarInsn(Opcodes.ILOAD, 0);
									mv.visitVarInsn(Opcodes.ILOAD, 1);
									Label l0 = new Label();
									mv.visitJumpInsn(Opcodes.IF_ICMPNE, l0);
									mv.visitInsn(Opcodes.ICONST_1);
									mv.visitInsn(Opcodes.IRETURN);
									mv.visitLabel(l0);
									mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
									mv.visitInsn(Opcodes.ICONST_0);
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
		return "I";
	}
}
