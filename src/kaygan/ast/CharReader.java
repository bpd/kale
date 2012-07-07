package kaygan.ast;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

public class CharReader extends FilterReader
{
    /** Pushback buffer */
    private char[] buf;

    /** Current position in buffer */
    private int pos;
    
    /** Offset from the beginning of the stream */
    private int streamOffset;

    public CharReader(Reader in, int size)
    {
    	super(in);
        if (size <= 0) {
            throw new IllegalArgumentException("size <= 0");
        }
        this.buf = new char[size];
        this.pos = size;
    }

    /**
     * Creates a new pushback reader with a one-character pushback buffer.
     *
     * @param   in  The reader from which characters will be read
     */
    public CharReader(Reader in)
    {
    	this(in, 1);
    }
    
    public int getOffset()
    {
    	return streamOffset;
    }

    /**
     * Reads a single character.
     *
     * @return     The character read, or -1 if the end of the stream has been
     *             reached
     *
     * @exception  IOException  If an I/O error occurs
     */
    @Override
    public int read() throws IOException
    {
    	streamOffset++;
    	
	    if (pos < buf.length)
	    {
	    	return buf[pos++];
	    }
	    else
	    {
	    	return super.read();
	    }
    }
    
    public int peek() throws IOException
    {
    	int c = read();
    	unread(c);
    	return c;
    }

    /**
     * Pushes back a single character by copying it to the front of the
     * pushback buffer. After this method returns, the next character to be read
     * will have the value <code>(char)c</code>.
     *
     * @param  c  The int value representing a character to be pushed back
     *
     * @exception  IOException  If the pushback buffer is full,
     *                          or if some other I/O error occurs
     */
    public void unread(int c) throws IOException
    {
	    if (pos == 0)
	    	throw new IOException("Pushback buffer overflow");
	    buf[--pos] = (char) c;
	    
	    streamOffset--;
    }

    /**
     * Tells whether this stream is ready to be read.
     *
     * @exception  IOException  If an I/O error occurs
     */
    @Override
    public boolean ready() throws IOException
    {
	    return (pos < buf.length) || super.ready();
    }

    /**
     * Marks the present position in the stream. The <code>mark</code>
     * for class <code>PushbackReader</code> always throws an exception.
     *
     * @exception  IOException  Always, since mark is not supported
     */
    @Override
    public void mark(int readAheadLimit) throws IOException
    {
    	throw new IOException("mark/reset not supported");
    }

    /**
     * Resets the stream. The <code>reset</code> method of 
     * <code>PushbackReader</code> always throws an exception.
     *
     * @exception  IOException  Always, since reset is not supported
     */
    @Override
    public void reset() throws IOException
    {
    	throw new IOException("mark/reset not supported");
    }

    /**
     * Tells whether this stream supports the mark() operation, which it does
     * not.
     */
    @Override
    public boolean markSupported()
    {
    	return false;
    }

    /**
     * Closes the stream and releases any system resources associated with
     * it. Once the stream has been closed, further read(),
     * unread(), ready(), or skip() invocations will throw an IOException.
     * Closing a previously closed stream has no effect.
     *
     * @exception  IOException  If an I/O error occurs
     */
    @Override
    public void close() throws IOException
    {
    	super.close();
    	buf = null;
    }

    /**
     * Skips characters.  This method will block until some characters are
     * available, an I/O error occurs, or the end of the stream is reached.
     *
     * @param  n  The number of characters to skip
     *
     * @return    The number of characters actually skipped
     *
     * @exception  IllegalArgumentException  If <code>n</code> is negative.
     * @exception  IOException  If an I/O error occurs
     */
    @Override
    public long skip(long n) throws IOException
    {
        if (n < 0L)
            throw new IllegalArgumentException("skip value is negative");
        
        int avail = buf.length - pos;
        if (avail > 0) {
            if (n <= avail) {
                pos += n;
                
                streamOffset += n;
                return n;
            } else {
                pos = buf.length;
                n -= avail;
            }
        }
        
        long skipped = avail + super.skip(n);
        streamOffset += skipped;
        
        return skipped;
    }
}
