**DBSTORE**
=================


this is a simple abstraction layer for datastores. mostly, for mongo db. 

it contains a few basic classes that might help in working with mongodb, specifically:

***DbQuery***

this is an interface for a builder style class to create queries. a basic implementation called "BasicDBQuery" is provided. simple usage:

	DbQuery q = BasicQuery.createQuery();
	q = q.eq("fieldname","value");
	q = q.gte("fieldname2","value2");
	[...]
	
	
***DataStore***

an interface for a data store that can be queried with a DbQuery. an implementation for mongo (along with the necessary translator to translate DbQuery into an actual mongo query is provided in dbstore-mongo and dbstore-query-mongo). simple usage:


	DbQuery q = BasicQuery.createQuery();
	q = q.eq("fieldname","value");
	q = q.gte("fieldname2","value2");
	[...]
	DataStore ds = getDataStore();
	return ds.findObjects(MyClass.class,q);
	
and, for saving: 

	ds.saveObject("my_db",object);
	
note that your objects have to extend `DBStoreEntity`. 
	
	
***DbStoreListener***

this is an interface for anything that's interested in receiving notifications on the persistence of specific objects. in a spring context (if your data store is a bean in your spring context), these will be autowired. a basic adaptor called `"DBStoreListenerAdapter"` is provided.

***How To Use This In A Maven Project***

to use this in a maven project, add our repo to your classpath:

	<repositories>
		<repository>
			<id>skjlls-oss-releases</id>
			<url>https://infra.skjlls.com/nexus/content/repositories/releases-oss/</url>
		</repository>
	</repositories>

and then depend on the API artifacts:

	<dependency>
		<groupId>com.cinefms.dbstore</groupId>
		<artifactId>dbstore-api</artifactId>
		<version>${dbstore.version}</version>
	</dependency>
	<dependency>
		<groupId>com.cinefms.dbstore</groupId>
		<artifactId>dbstore-query-api</artifactId>
		<version>${dbstore.version}</version>
	</dependency>

and possibly the implementations:

	<dependency>
		<groupId>com.cinefms.dbstore</groupId>
		<artifactId>dbstore-mongo</artifactId>
		<version>${dbstore.version}</version>
	</dependency>

this is meant to be used with spring, and it contains a couple of classes to do autoconfig in a spring context, so that (apart from some env variables) everything should be wired up correctly out of the box.

questions? ask me! i'll try to help.