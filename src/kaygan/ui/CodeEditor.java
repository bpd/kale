package kaygan.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import kaygan.Interpreter;
import kaygan.Parser;

public class CodeEditor extends JPanel
{
	private static final long serialVersionUID = 1L;

	private final CodePane text = new CodePane();
	
	private final ReadEvalPrintPane repl = new ReadEvalPrintPane();
	
	public CodeEditor()
	{
		setLayout( new BorderLayout() );
		
		// GO button
		JButton goButton = new JButton("Run (F5)");
		goButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				go();
			}	
		});
		add( goButton, BorderLayout.NORTH );
		
		// text editor
		text.setPreferredSize( new Dimension(400, 400) );
		
		JScrollPane scrollText = new JScrollPane(text);
		
		add( scrollText, BorderLayout.CENTER );
		
		// REPL
		repl.setPreferredSize( new Dimension(400, 200) );
		add( repl, BorderLayout.SOUTH );
		
		// listeners
		text.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if( e.getKeyCode() == KeyEvent.VK_F5 )
				{
					go();
				}
				else
				{
					text.dirty();
				}
			}	
		});
		
		text.requestFocusInWindow();
		text.setCaretPosition( text.getStyledDocument().getLength() - 1 );
		
		
	}
	
	protected void go()
	{
		eval( text.getText() );
		
		text.highlight();
		
		repl.validate();
		repl.repaint();
	}
	
	protected void eval( String input )
	{
		try
		{
			repl.addInfo("Load");
			repl.reset();
			repl.eval(input);
		}
		catch(Parser.ParseException pe)
		{
			if( pe.token != null )
			{
				text.error(	pe.getMessage(), 
							pe.token.beginOffset, 
							pe.token.endOffset - pe.token.beginOffset );
			}
			repl.addError( "Error: " + pe.getMessage() );
			
		}
		catch(Interpreter.InterpretException ie)
		{
			text.error(	ie.getMessage(), 
						ie.astNode.getOffset(), 
						ie.astNode.getLength() );

			repl.addError( "Error: " + ie.getMessage() );
		}
		catch(RuntimeException re)
		{
			repl.addError( "Error: " + re.getMessage() );
		}
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
				JFrame frame = new JFrame("Kaygan Editor");
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
