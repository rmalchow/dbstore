package com.cinefms.dbstore.cache.api.annotations;

public @interface CollectionName {

	public static final String USE_PACKAGE_AND_CLASS_NAME = "USE_PACKAGE_AND_CLASS_NAME";
	public static final String USE_CLASS_NAME = "USE_CLASS_NAME";
	
	public String value() default USE_PACKAGE_AND_CLASS_NAME;
	
}
