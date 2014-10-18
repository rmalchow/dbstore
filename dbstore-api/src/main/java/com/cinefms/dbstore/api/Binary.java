package com.cinefms.dbstore.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public interface Binary {

	public String getId();
	public int getLength();
	public InputStream getInputStream();
	public Map<String,Object> getMetaData();

	public void writeTo(OutputStream os) throws IOException;
	
}
