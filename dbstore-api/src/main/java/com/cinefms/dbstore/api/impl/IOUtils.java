package com.cinefms.dbstore.api.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class IOUtils {
	
	public static long copy(InputStream is, OutputStream os) throws IOException {
		byte[] buff = new byte[1024*1024];
		int a = 0;
		long o = 0;
		while((a = is.read(buff)) > -1) {
			os.write(buff,0,a);
			o = o + a;
		}
		os.flush();
		return o;
	}
	
	public static byte[] getBytes(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		copy(is,baos);
		return baos.toByteArray();
	}

	public static byte[] getBytes(File f) throws IOException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			return getBytes(fis);
		} catch (Exception e) {
			throw new IOException(e);
		} finally {
			try {
				fis.close();
			} catch (Exception e2) {
			}
		}
	}

	
	

}
