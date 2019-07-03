package com.cinefms.dbstore.api;

import javax.persistence.Id;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public interface DBStoreBinary {

	@Id
	String getId();

	long getLength();

	InputStream getInputStream();

	Map<String, Object> getMetaData();

	void writeTo(OutputStream os) throws IOException;

}
