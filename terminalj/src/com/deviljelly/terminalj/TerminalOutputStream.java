package com.deviljelly.terminalj;

import java.io.IOException;
import java.io.OutputStream;

public class TerminalOutputStream extends OutputStream {

	public static TerminalOutputStream DEFAULT_TERMINAL_OUTPUT_STREAM = new TerminalOutputStream(System.out);

	
	private OutputStream underlyingOutputStream;
	
	
	public TerminalOutputStream(OutputStream underlyingOutputStream) {
		this.underlyingOutputStream = underlyingOutputStream;
	}
	
	
	
	public static TerminalOutputStream getDefault() {
		return DEFAULT_TERMINAL_OUTPUT_STREAM;
	}
	
	
	@Override
	public void write(int b) throws IOException {

		underlyingOutputStream.write(b);
		
	}


	@Override
	public void write(byte[] b) throws IOException {

		underlyingOutputStream.write(b);
		
	}


	@Override
	public void write(byte[] b, int off, int len) throws IOException {

		underlyingOutputStream.write(b, off, len);

	}


	@Override
	public void flush() throws IOException {

		underlyingOutputStream.flush();

	}


	@Override
	public void close() throws IOException {

		underlyingOutputStream.close();

	}
	
	

}
