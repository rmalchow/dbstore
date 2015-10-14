package com.cinefms.dbstore.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CollectionName {

	public static final String USE_PACKAGE_AND_CLASS_NAME = "USE_PACKAGE_AND_CLASS_NAME";
	public static final String USE_CLASS_NAME = "USE_CLASS_NAME";
	
	public String value() default USE_PACKAGE_AND_CLASS_NAME;
	
}
