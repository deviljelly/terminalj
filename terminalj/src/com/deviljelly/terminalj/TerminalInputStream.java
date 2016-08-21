package com.deviljelly.terminalj;

import java.io.IOException;
import java.io.InputStream;

public class TerminalInputStream extends InputStream {


	private static TerminalInputStream DEFAULT_TERMINAL_INPUT_STREAM = new TerminalInputStream(System.in);

	InputStream underlyingInputStream;
	
	public TerminalInputStream(InputStream underlyingInputStream) {
		
		this.underlyingInputStream = underlyingInputStream;
		
	}
	
	
	public static TerminalInputStream getDefault() {
		return DEFAULT_TERMINAL_INPUT_STREAM;
	}
	
	
	@Override
	public int read() throws IOException {

		return underlyingInputStream.read();

	}

	@Override
	public int read(byte[] b) throws IOException {

		return underlyingInputStream.read(b);

	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {

		return underlyingInputStream.read(b, off, len);

	}

	@Override
	public long skip(long n) throws IOException {

		return underlyingInputStream.skip(n);

	}

	@Override
	public int available() throws IOException {

		return underlyingInputStream.available();
		
	}

	@Override
	public void close() throws IOException {

		underlyingInputStream.close();

	}

	@Override
	public synchronized void mark(int readlimit) {

		underlyingInputStream.mark(readlimit);

	}

	@Override
	public synchronized void reset() throws IOException {

		underlyingInputStream.reset();

	}

	@Override
	public boolean markSupported() {

		return underlyingInputStream.markSupported();

	}
	
	
	
	
	

}
