package com.cinefms.dbstore.redis.util;

import javax.annotation.PostConstruct;

import org.redisson.codec.JsonJacksonCodec;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class LenientJsonCodec extends JsonJacksonCodec {

	
	private ObjectMapper mapper;
	
	@PostConstruct
	protected void init() {
		if(mapper==null) {
			mapper = new ObjectMapper();
			
		}
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
		mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,false);
		super.init(mapper);
	}



	public ObjectMapper getMapper() {
		return mapper;
	}



	public void setMapper(ObjectMapper mapper) {
		this.mapper = mapper;
	}

}