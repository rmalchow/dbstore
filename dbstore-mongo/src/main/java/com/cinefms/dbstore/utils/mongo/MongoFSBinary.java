package com.cinefms.dbstore.utils.mongo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.cinefms.dbstore.api.DBStoreBinary;
import com.mongodb.gridfs.GridFSDBFile;

public class MongoFSBinary implements DBStoreBinary {

	private GridFSDBFile f;

	public MongoFSBinary(GridFSDBFile f) {
		super();
		this.f = f;
	}

	public int getLength() {
		return (int) f.getLength();
	}

	public InputStream getInputStream() {
		return f.getInputStream();
	}

	public Map<String, Object> getMetaData() {
		Map<String, Object> md = new HashMap<String, Object>();
		for (String s : f.getMetaData().keySet()) {
			md.put(s, f.getMetaData().get(s));
		}
		return md;
	}

	public String getId() {
		return f.getFilename();
	}

	public void writeTo(OutputStream os) throws IOException {
		IOUtils.copy(getInputStream(), os);
	}

}
