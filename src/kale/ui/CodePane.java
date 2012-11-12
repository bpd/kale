package kale.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTextPane;
import javax.swing.ToolTipManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import kale.Token;
import kale.TokenType;
import kale.ast.*;

public class CodePane extends JTextPane
{
	private static final long serialVersionUID = 1L;
	
	public static final Font FONT = new Font("Consolas", Font.PLAIN, 14);
	
	public static final Color STRING_COLOR = new Color(0, 162, 232);
	
	public static final Color SYMBOL_COLOR = new Color(75, 60, 255);
	
	public static final Color ARG_COLOR = new Color(255, 128, 70);
	
	public static final Color NUM_COLOR = new Color(35, 175, 75);
	
	public static final Color BOOLEAN_COLOR = new Color(35, 175, 75);
	
	public static final Color COMMENT_COLOR = new Color(150, 150, 150);
	
	
	static SimpleAttributeSet errorStyle = new SimpleAttributeSet();
	
	static SimpleAttributeSet normalStyle = new SimpleAttributeSet();
	
	static SimpleAttributeSet stringStyle = new SimpleAttributeSet();
	
	static SimpleAttributeSet symbolStyle = new SimpleAttributeSet();
	
	static SimpleAttributeSet argStyle = new SimpleAttributeSet();
	
	static SimpleAttributeSet numStyle = new SimpleAttributeSet();
	
	static SimpleAttributeSet booleanStyle = new SimpleAttributeSet();
	
	static SimpleAttributeSet commentStyle = new SimpleAttributeSet();
	
	static SimpleAttributeSet removeStyle = new SimpleAttributeSet();
	
	static
	{
		StyleConstants.setForeground(errorStyle, Color.RED);
	    StyleConstants.setUnderline(errorStyle, true);
	    
	    StyleConstants.setForeground(stringStyle, STRING_COLOR);
	    
	    StyleConstants.setForeground(symbolStyle, SYMBOL_COLOR);

	    StyleConstants.setForeground(argStyle, ARG_COLOR);
	    
	    StyleConstants.setForeground(numStyle, NUM_COLOR);
	    
	    StyleConstants.setForeground(commentStyle, COMMENT_COLOR);
	    
	    StyleConstants.setForeground(booleanStyle, BOOLEAN_COLOR);
	    
	    StyleConstants.setForeground(removeStyle, Color.RED);
	    StyleConstants.setStrikeThrough(removeStyle, true);
	}
	
	static final Map<TokenType, SimpleAttributeSet> TOKEN_STYLES 
		= new HashMap<TokenType, SimpleAttributeSet>();
	
	static
	{
		TOKEN_STYLES.put(TokenType.String, stringStyle);
		TOKEN_STYLES.put(TokenType.Int, numStyle);
		TOKEN_STYLES.put(TokenType.Boolean, booleanStyle);
		TOKEN_STYLES.put(TokenType.Keyword, symbolStyle);
		TOKEN_STYLES.put(TokenType.Comment, commentStyle);
	}
	
	
	private transient CompilationUnit ast;
	
	private transient List<Token> badTokens;

	public CodePane()
	{	
		setFont( FONT );
	    
	    new LinePainter(this, new Color(225, 225, 225));
	    
	    // TODO abstract code loading
	    // load the code from file
	    StringBuilder code = new StringBuilder();
	    try
	    {
		    InputStream is = CodePane.class.getResourceAsStream("/kale/example.lang");
		    if( is != null )
		    {
		    	BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		    	try
		    	{
			    	String line = null;
			    	while( (line = reader.readLine()) != null )
			    	{
			    		code.append( line ).append('\n');
			    	}
		    	}
		    	finally
		    	{
		    		reader.close();
		    	}
		    }
	    }
	    catch(IOException e)
	    {
	    	// eat it
	    }
	    
		
	    // populate initial document content
	    StyledDocument doc = getStyledDocument();
		try
		{
			doc.insertString(0, code.toString(), null);
		}
		catch(BadLocationException e)
		{
			throw new RuntimeException(e);
		}
		
		ToolTipManager.sharedInstance().registerComponent(this);
	}
	
	public void setTokens( List<Token> tokens )
	{
		StyledDocument doc = getStyledDocument();
		
		// first reset style
		//highlight( doc, ast, normalStyle );
		doc.setCharacterAttributes(	0,
									doc.getLength(),
									normalStyle,
									true );

		// highlight all tokens before AST annotations are added
		for( Token token : tokens )
		{
			SimpleAttributeSet style = TOKEN_STYLES.get(token.type);

			if( style == null )
			{
				style = normalStyle;
			}
			highlight(doc, token, style);
		}
	}
	
	public void setBadTokens( List<Token> badTokens )
	{
		StyledDocument doc = getStyledDocument();
		
		this.badTokens = badTokens;
		
		for( Token badToken : badTokens )
		{
			highlight( doc, badToken, removeStyle );
		}
	}
	
	public void setAST(CompilationUnit ast)
	{
		this.ast = ast;
		
		// then recursively highlight AST nodes
		highlight( getStyledDocument(), ast );		
	}
	
	
	@Override
	public String getToolTipText(MouseEvent event)
	{
		int position = viewToModel(event.getPoint());
		
		// even if we constructed an AST, we may have had
		// to perform error correction to get there...
		// so before we walk the AST see if this
		// offset belongs to a bad token
		// TODO binary search, bad tokens should be sorted
		for( int i=0; i < badTokens.size(); i++ )
		{
			Token badToken = badTokens.get(i);
			if( position >= badToken.beginOffset
				&& position < badToken.endOffset )
			{
				return "Bad token, remove";
			}
		}
		
		if( ast != null )
		{
			ASTNode node = ast.findNode(position);
			if( node != null )
			{
				// if this node has errors, display those.
				// otherwise return the inferred type string
				//
				if( node.hasErrors() )
				{
					StringBuilder sb = new StringBuilder();
					sb.append("<html><ul>");
					for( String error : node.getErrors() )
					{
						sb.append("<li>");
						sb.append(error);
						sb.append("</li>");
					}
					sb.append("</ul></html>");
					return sb.toString();
				}

				return node.getClass().getSimpleName();
			}
		}
		
		return null;
	}



	protected void highlight(final StyledDocument doc, ASTNode node)
	{
		if( node.hasLocalErrors() )
		{
			highlight( doc, node, errorStyle );
		}
		
		for( ASTNode child : node.children )
		{
			highlight( doc, child );
		}
		
	}
	
	protected void highlight(	StyledDocument doc, 
								ASTNode node,
								SimpleAttributeSet style )
	{
		doc.setCharacterAttributes(	node.getBeginOffset(),
									node.getEndOffset() - node.getBeginOffset(),
									style,
									true );
	}
	
	protected void highlight(	StyledDocument doc, 
								Token token,
								SimpleAttributeSet style )
	{
		doc.setCharacterAttributes(	token.beginOffset,
									token.endOffset - token.beginOffset,
									style,
									true );
	}
	
	public void error(String message, int offset, int length)
	{
	    StyledDocument doc = getStyledDocument();
	    doc.setCharacterAttributes(offset, length, errorStyle, false);	    
	}
}
