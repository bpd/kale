package kale.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import kale.ast.ASTNode;
import kale.ast.CompilationUnit;
import kale.ast.FieldDecl;
import kale.ast.FunctionDecl;
import kale.ast.FunctionSignature;
import kale.ast.InterfaceDecl;
import kale.ast.PackageDecl;
import kale.ast.TypeDecl;

public class CodeOutline extends JPanel implements TreeSelectionListener
{
	private static final long serialVersionUID = 1L;
	
	final JTree tree;
	
	final DefaultMutableTreeNode root;
	
	final DefaultTreeModel model;
	
	final CodePane codePane;
	
	public CodeOutline( CodePane codePane )
	{
		this.codePane = codePane;
		
		setLayout( new BorderLayout() );
		
		setPreferredSize( new Dimension(100, -1) );
		
		root = new DefaultMutableTreeNode("");
		
		tree = new JTree( root )
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			public String convertValueToText(Object value, boolean selected,
					boolean expanded, boolean leaf, int row, boolean hasFocus)
			{
				if( value instanceof DefaultMutableTreeNode )
				{
					value = ((DefaultMutableTreeNode)value).getUserObject();
				}

				if( value instanceof ASTNode )
				{
					ASTNode node = (ASTNode)value;
					
					if( node instanceof PackageDecl )
					{
						return ((PackageDecl) node).getName();
					}
					else if( node instanceof TypeDecl )
					{
						return ((TypeDecl) node).getName();
					}
					else if( node instanceof InterfaceDecl )
					{
						return ((InterfaceDecl) node).getName();
					}
					else if( node instanceof FunctionDecl )
					{
						FunctionDecl func = (FunctionDecl)node;
						return func.getName() + " : " 
								+ func.getSignature().getReturnType()
									.getType().getName();
					}
					else if( node instanceof FieldDecl )
					{
						FieldDecl field = (FieldDecl)node;
						return field.getName() + " : " 
								+ field.getType().getName();
					}
				}
				return value.toString();
			}
		};
		tree.addTreeSelectionListener(this);
		
		model = (DefaultTreeModel)tree.getModel();
		
		add( tree, BorderLayout.CENTER );
	}
	
	public void setAST( CompilationUnit unit )
	{
		root.removeAllChildren();
		
		PackageDecl packageDecl = unit.getPackageDecl();
		
		root.setUserObject( packageDecl );
		
		for( TypeDecl type : packageDecl.getTypes() )
		{
			DefaultMutableTreeNode typeNode 
							= new DefaultMutableTreeNode( type );

			root.add( typeNode );
			
			for( FieldDecl field : type.getFields() )
			{
				typeNode.add( new DefaultMutableTreeNode( field ) );
			}
			
			for( FunctionDecl method : type.getMethods() )
			{
				typeNode.add( new DefaultMutableTreeNode( method ) );
			}
		}
		
		for( InterfaceDecl iface : packageDecl.getInterfaces() )
		{
			DefaultMutableTreeNode ifaceNode 
						= new DefaultMutableTreeNode( iface );
			
			root.add( ifaceNode );
			
			tree.expandPath( new TreePath(ifaceNode.getPath()) );
			
			for( FunctionSignature method : iface.getMethods() )
			{
				ifaceNode.add( new DefaultMutableTreeNode( method ) );
			}
		}
		
		for( FunctionDecl function : packageDecl.getFunctions() )
		{
			root.add( new DefaultMutableTreeNode( function ) );
		}
		
		model.reload(root);
		
		DefaultMutableTreeNode currentNode = root.getNextNode();
		do
		{
			tree.expandPath( new TreePath( currentNode.getPath() ) );
			
			currentNode = currentNode.getNextNode();
		}
		while (currentNode != null);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e)
	{
		TreePath path = e.getNewLeadSelectionPath();
		if( path == null )
		{
			return;
		}
		Object o = path.getLastPathComponent();

		if( o instanceof DefaultMutableTreeNode )
		{
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)o;
			
			if( treeNode.getUserObject() instanceof ASTNode )
			{
				ASTNode astNode = (ASTNode)treeNode.getUserObject();
				
				codePane.setCaretPosition( astNode.getBeginOffset() );
			}
		}
		
		//model.reload(root);
	}
}
