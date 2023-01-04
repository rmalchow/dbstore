**DBSTORE**
=================

this is a simple abstraction / utility layer on top of mongo db.  from version 0.9, these are the minimum requirements:

- Spring Framework 5.3 (Spring Boot 2.5)
- MongoDB Driver 4.2
- Mongojack 4.2
- MongoDB 4.4

from version 0.10 added compatibility for Spring Framework 6.0 (Spring Boot 3.0).

it contains a few basic classes that might help, specifically:

***DbQuery***

this is an interface for a builder style class to create queries. a basic implementation called "BasicDBQuery" is provided. simple usage:

	DbQuery q = BasicQuery.createQuery();
	q = q.eq("fieldname","value");
	q = q.gte("fieldname2","value2");
	[...]


​
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

#### Annotations

this package contains a few annotations that can come in handy, most importantly:

``` asd
@Indexes({
		@Index(name = "firstNameIdx", fields = "firstName"),
		@Index(name = "lastNameIdx", fields = "lastName"),
		@Index(name = "fullNameIdx", fields = {"firstName", "lastName"}, unique = true)
})
public class EntityWithIndexes extends BaseDBStoreEntity {
		...
}
```

for creating indexes on collections of entities.

also:

```
@CollectionName(CollectionName.USE_CLASS_NAME)
public class Entity extends BaseDBStoreEntity {
		...
}
```

for configuring how collections are named.



***How To Use This In A Maven Project***

to use this in a maven project, add our repo to your classpath:

	<repositories>
		<repository>
			<id>dbstore-releases</id>
			<url>https://raw.githubusercontent.com/rmalchow/maven/master/</url>
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
