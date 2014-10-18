package com.cinefms.dbstore.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.cinefms.dbstore.api.Entity;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Reference {

	public Class<Entity> value();
	
}
