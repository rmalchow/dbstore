package com.cinefms.dbstore.api.impl;

import com.cinefms.dbstore.api.DBStoreBinary;

import java.io.*;
import java.util.Map;

public class BasicBinary implements DBStoreBinary {

	private String id;
	private byte[] data;
	private Map<String, Object> metaData;

	public BasicBinary(String id, byte[] data) {
		this(id, data, null);
	}

	public BasicBinary(String id, byte[] data, Map<String, Object> metaData) {
		this.id = id;
		this.data = data;
		this.metaData = metaData;
	}

	public BasicBinary(String id, InputStream is) throws IOException {
		this(id, is, null);
	}

	public BasicBinary(String id, InputStream is, Map<String, Object> metaData) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(is, baos);

		this.id = id;
		this.data = baos.toByteArray();
		this.metaData = metaData;
	}

	public String getId() {
		return id;
	}

	public long getLength() {
		return data.length;
	}

	public InputStream getInputStream() {
		return new ByteArrayInputStream(data);
	}

	public Map<String, Object> getMetaData() {
		return metaData;
	}

	public void writeTo(OutputStream os) throws IOException {
		IOUtils.copy(getInputStream(), os);
	}

}
