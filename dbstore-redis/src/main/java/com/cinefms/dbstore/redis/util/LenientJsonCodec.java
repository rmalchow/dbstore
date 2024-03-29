package com.cinefms.dbstore.redis.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.redisson.codec.JsonJacksonCodec;

public class LenientJsonCodec extends JsonJacksonCodec {

	private static final Log LOGGER = LogFactory.getLog(LenientJsonCodec.class);

	public LenientJsonCodec() {
		super();
		LOGGER.info("### LENIENT JSON CODEC INSTANTIATED .... ");
	}

	@Override
	protected void init(ObjectMapper mapper) {
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
		LOGGER.info("### INITIALIZED LENIENT JSON CODEC FOR REDIS ... ");
		super.init(mapper);
	}

}
