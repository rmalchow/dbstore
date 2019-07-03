package com.cinefms.dbstore.api.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {

	public static long copy(InputStream is, OutputStream os) throws IOException {
		byte[] buff = new byte[1024 * 1024];
		int avail;
		long size = 0;
		while ((avail = is.read(buff)) > -1) {
			os.write(buff, 0, avail);
			size += avail;
		}
		os.flush();
		return size;
	}

}
