package com.deviljelly.terminalj;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;


public class FDTest {

	public static void main(String[] args) {
		
		try {
		
			
			String[] cmd = {"/bin/sh", "-c", "stty raw -echo </dev/tty"};
			Runtime.getRuntime().exec(cmd);
			
			
			
//			Scanner keyboard = new Scanner(System.in);

//			System.out.println("pressr to continue");

//			keyboard.next();
			
//			System.exit(0);
			
//			FileInputStream fis = new FileInputStream(FileDescriptor.in);
			FileInputStream fis = new FileInputStream("/proc/self/fd/0");
			
			
//			FileOutputStream fos = new FileOutputStream(FileDescriptor.out);
			FileOutputStream fos = new FileOutputStream("/proc/self/fd/1");
		
//			Path path = FileSystems.getDefault().getPath("/proc/self/fd/0");
			
//			FileChannel fc = FileChannel.open(path, StandardOpenOption.READ);
			
//			ByteBuffer dst = ByteBuffer.allocate(16);
			
//			fc.read(dst);
			
			
		fos.write('x');
		
		int x = fis.read();
		
		fos.write(x);
		
		} catch(Exception e) {
			System.out.println(e);
		} 
		
		try {
			String[] cmd = {"/bin/sh", "-c", "stty cooked echo </dev/tty"};
			Runtime.getRuntime().exec(cmd);
		} catch(Exception e) {
			
		}
		
		
	}
	
	
}
