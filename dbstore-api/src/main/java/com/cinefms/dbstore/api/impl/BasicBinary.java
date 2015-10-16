package com.cinefms.dbstore.api.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.cinefms.dbstore.api.DBStoreBinary;

public class BasicBinary implements DBStoreBinary {

	private String id;
	private long length = -1;
	private InputStream is;
	private Map<String,Object> metaData = new HashMap<String, Object>();
	
	public BasicBinary(String id, byte[] data) {
		this(id,new ByteArrayInputStream(data),data.length);
	}
	
	public BasicBinary(String id, byte[] data, Map<String,Object> metaData) {
		this(id,new ByteArrayInputStream(data),data.length,metaData);
	}
	
	public BasicBinary(String id, InputStream is, long length) {
		this(id,is,-1,null);
	}
	
	public BasicBinary(String id, InputStream is, long length, Map<String,Object> metaData) {
		this.id = id;
		this.is = is;
		this.length = length;
		this.metaData = metaData;
	}
	
	
	public String getId() {
		return id;
	}

	
	public long getLength() {
		return length;
	}

	
	public InputStream getInputStream() {
		return is;
	}

	
	public Map<String, Object> getMetaData() {
		return metaData;
	}
	
	
	public void writeTo(OutputStream os) throws IOException {
		IOUtils.copy(getInputStream(), os);
	}

}
