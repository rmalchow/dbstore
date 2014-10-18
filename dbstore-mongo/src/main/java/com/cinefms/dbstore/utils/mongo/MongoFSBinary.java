package com.cinefms.dbstore.utils.mongo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.mongodb.gridfs.GridFSDBFile;
import com.skjlls.utils.db.SkjllsBinary;

public class MongoFSBinary implements SkjllsBinary {
	
	private GridFSDBFile f;
	
	public MongoFSBinary(GridFSDBFile f) {
		super();
		this.f = f;
	}


	@Override
	public int getLength() {
		return (int)f.getLength();
	}

	@Override
	public InputStream getInputStream() {
		return f.getInputStream();
	}

	@Override
	public Map<String, Object> getMetaData() {
		Map<String,Object> md = new HashMap<String, Object>();
		for(String s : f.getMetaData().keySet()) {
			md.put(s, f.getMetaData().get(s));
		}
		return md;
	}

	@Override
	public String getId() {
		return f.getFilename();
	}


	@Override
	public void writeTo(OutputStream os) throws IOException {
		IOUtils.copy(getInputStream(), os);
	}

}
