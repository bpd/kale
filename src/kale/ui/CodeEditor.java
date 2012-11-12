package kale.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.StringReader;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

import kale.Parser;
import kale.Scope;
import kale.ast.CompilationUnit;

public class CodeEditor extends JPanel
{
	private static final long serialVersionUID = 1L;

	private final CodePane text;
	
	private final CodeConsole repl = new CodeConsole()
	{
		private static final long serialVersionUID = 1L;

		@Override
		public void onRequestEval(String input)
		{
			CodeEditor.this.eval(input);
		}
	};
	
	final CodeOutline outline;
	
	private transient Scope scope = CompilationUnit.newRootScope();
	
	private long lastParseTime = 0;
	
	/** in milliseconds */
	private static final long PARSE_INTERVAL = 500;
	
	private volatile boolean dirty = true;
	
	public CodeEditor()
	{
		setLayout( new BorderLayout() );
		
		text  = new CodePane();
		outline = new CodeOutline(text);
		
		// GO button
		JButton goButton = new JButton("Run (F5)");
		goButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				execute();
			}	
		});
		add( goButton, BorderLayout.NORTH );

		outline.setPreferredSize( new Dimension(200, 400) );
		
		// text editor
		text.setPreferredSize( new Dimension(600, 500) );
		
		// REPL
		//repl.setPreferredSize( new Dimension(400, 200) );
		
		JScrollPane scrollText = new JScrollPane(text);
		TextLineNumber tln = new TextLineNumber(text);
		scrollText.setRowHeaderView( tln );
		
		JSplitPane browsableCode = new JSplitPane( 
								JSplitPane.HORIZONTAL_SPLIT,
								outline,
								scrollText
								 );
		
		
		JSplitPane codeWithOutput = new JSplitPane( 
							JSplitPane.VERTICAL_SPLIT, 
							browsableCode, 
							repl );
		
		add( codeWithOutput, BorderLayout.CENTER );
		
		
		// listeners
		text.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				int c = e.getKeyCode();
				if( c == KeyEvent.VK_F5 )
				{
					execute();
				}
				else if( c != KeyEvent.VK_LEFT
						&& c != KeyEvent.VK_RIGHT
						&& c != KeyEvent.VK_UP
						&& c != KeyEvent.VK_DOWN
						&& c != KeyEvent.VK_HOME
						&& c != KeyEvent.VK_END
						&& c != KeyEvent.VK_PAGE_UP
						&& c != KeyEvent.VK_PAGE_DOWN )
				{
					dirty = true;
				}
			}	
		});
		
		text.requestFocusInWindow();
		text.setCaretPosition( 0 ); //text.getStyledDocument().getLength() - 1 );
		
		// schedule the parser
	    new Timer().scheduleAtFixedRate(new TimerTask()
	    {
			@Override
			public void run()
			{
				long nextParseTime = lastParseTime + PARSE_INTERVAL;
				
				if( dirty 
					&& System.currentTimeMillis() > nextParseTime )
				{
					dirty = false;			

					parse( text.getText() );
				}
			}
	    	
	    }, 0, 500);
	}
	
	protected void execute()
	{
		CompilationUnit program = parse( text.getText() );
		
		if( program != null )
		{
			scope = CompilationUnit.newRootScope();
			
			repl.reset();
			
			eval( program );
			
			lastParseTime = System.currentTimeMillis();
		}
		
		repl.validate();
		repl.repaint();
	}
	
	protected void eval( String input )
	{
		CompilationUnit program = parse(input);
		
		if( program != null )
		{
			eval(program);
		}
	}
	
	protected void eval( CompilationUnit program )
	{
		try
		{
			// interpret the program AST within the scope
			Object result = program.execute();
			if( result != null )
			{
				repl.addResult( result );
			}
		}
		catch(RuntimeException re)
		{
			repl.addError( "Error: " + re.getMessage() );
			for( StackTraceElement element : re.getStackTrace() )
			{
				repl.addError( element.toString() );
			}
			
			re.printStackTrace();
			
		}
	}
	
	public synchronized CompilationUnit parse( String text )
	{
		Parser parser = new Parser( new StringReader( text ) );
		try
		{
			// first reset all style
			CompilationUnit program = parser.parse();
			
			if( program != null )
			{
				this.text.setTokens( parser.getTokenList() );
				this.text.setBadTokens( parser.getBadTokens() );
				
				Scope scope = this.scope.newSubScope();
				
				program.inferType(scope);
				
				// hand the AST over to the code pane to highlight
				this.text.setAST(program);
				
				this.outline.setAST(program);
			}
			
			return program;
		}
		catch(Parser.ParseException pe)
		{
			// still try to highlight whatever tokens we parsed
			this.text.setTokens(parser.getTokenList());
			
			// parsing probably failed because the person was
			// in the middle of typing
			
			System.out.println("Exception occurred parsing");
			pe.printStackTrace();

			
		}
		catch(RuntimeException e)
		{
			System.out.println("Exception occurred parsing");
			e.printStackTrace();
		}
		
		
		return null;
	}
	
	
	static
	{
		// Install the look and feel
		try
		{
		    UIManager.setLookAndFeel(
		    		UIManager.getSystemLookAndFeelClassName() );
		}
		catch (Throwable e)
		{
			// can't do anything
		}
	}
	
	public static void main(String[] args)
	{
		java.awt.EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				JFrame frame = new JFrame("Kale Editor");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);				
				
				CodeEditor editor = new CodeEditor();
				
				frame.add( editor );
				
				frame.pack();
				
				editor.text.requestFocusInWindow();
				
				frame.setVisible(true);
				
			}
		});
	}
}
