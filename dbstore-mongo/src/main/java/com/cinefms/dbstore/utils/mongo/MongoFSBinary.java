package com.cinefms.dbstore.utils.mongo;

import com.cinefms.dbstore.api.DBStoreBinary;
import com.mongodb.gridfs.GridFSDBFile;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class MongoFSBinary implements DBStoreBinary {

	private GridFSDBFile f;

	public MongoFSBinary(GridFSDBFile f) {
		super();
		this.f = f;
	}

	public long getLength() {
		return f.getLength();
	}

	public InputStream getInputStream() {
		return f.getInputStream();
	}

	public Map<String, Object> getMetaData() {
		Map<String, Object> md = new HashMap<>();
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
