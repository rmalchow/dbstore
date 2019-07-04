/*
 * Copyright 2011 VZ Netzwerke Ltd
 * Copyright 2014 devbliss GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mongojack;

import com.mongodb.DBObject;
import com.mongodb.MongoException;
import org.mongojack.internal.stream.JacksonDBObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class lets you access the results of the previous write. if you have
 * STRICT mode on, this just stores the result of that getLastError call if you
 * don't, then this will actually do the getlasterror call. if another operation
 * has been done on this connection in the interim, calls will fail
 * <p>
 * This class also gives you access to the saved objects, for the purposes of
 * finding out the objects ids, if they were generated by MongoDB.
 *
 * @author James Roper
 * @since 1.0
 */
public class WriteResult<T, K> {
	private final JacksonDBCollection<T, K> jacksonDBCollection;
	private final DBObject[] dbObjects;
	private final com.mongodb.WriteResult writeResult;
	private List<T> objects;

	protected WriteResult(JacksonDBCollection<T, K> jacksonDBCollection,
						  com.mongodb.WriteResult writeResult, DBObject... dbObjects) {
		this.jacksonDBCollection = jacksonDBCollection;
		this.writeResult = writeResult;
		this.dbObjects = dbObjects;
	}

	/**
	 * Get the object that was saved. This will contain the updated ID if the ID
	 * was generated.
	 * <p>
	 * Note, this operation is a little expensive because it has to deserialise the object. If you just want the ID,
	 * call getSavedId() instead.
	 *
	 * @return The saved object
	 * @throws MongoException If no objects were saved
	 */
	public T getSavedObject() {
		if (dbObjects.length == 0) {
			throw new MongoException("No objects to return");
		}
		return getSavedObjects().get(0);
	}

	/**
	 * Get the objects that were saved. These will contain the updated IDs if
	 * the IDs were generated.
	 * <p>
	 * This operation only works if object serialization is used. If stream serialization is used, the IDs are generated
	 * by the database, and cannot be known.
	 * <p>
	 * Note, this operation is a little expensive because it has to deserialise the objects. If you just want the IDs,
	 * call getSavedIds() instead.
	 *
	 * @return The saved objects
	 */
	public List<T> getSavedObjects() {
		// Lazily generate the object, in case it's not needed.
		if (objects == null) {
			if (dbObjects.length > 0) {
				if (dbObjects[0] instanceof JacksonDBObject) {
					throw new UnsupportedOperationException(
							"Saved object retrieval not supported when using stream serialization");
				}
			}
			objects = jacksonDBCollection.convertFromDbObjects(dbObjects);
		}
		return objects;
	}

	/**
	 * Get the saved ID. This may be useful for finding out the ID that was
	 * generated by MongoDB if no ID was supplied.
	 *
	 * @return The saved ID
	 * @throws MongoException If no objects were saved
	 */
	public K getSavedId() {
		if (dbObjects.length == 0) {
			throw new MongoException("No objects to return");
		}
		if (dbObjects[0] instanceof JacksonDBObject) {
			throw new UnsupportedOperationException(
					"Generated _id retrieval not supported when using stream serialization");
		}
		return jacksonDBCollection.convertFromDbId(dbObjects[0].get("_id"));
	}

	/**
	 * Get the saved IDs. This may be useful for finding out the IDs that were
	 * generated by MongoDB if no IDs were supplied.
	 *
	 * @return The saved IDs
	 */

	public List<K> getSavedIds() {
		if (dbObjects.length > 0 && dbObjects[0] instanceof JacksonDBObject) {
			throw new UnsupportedOperationException(
					"Generated _id retrieval not supported when using stream serialization");
		}

		List<K> ids = new ArrayList<>();
		for (DBObject dbObject : dbObjects) {
			ids.add(jacksonDBCollection.convertFromDbId(dbObject.get("_id")));
		}

		return ids;
	}

	/**
	 * Get the underlying DBObject that was serialised before it was saved. This
	 * will contain the updated ID if an ID was generated.
	 *
	 * @return The underlying DBObject
	 * @throws MongoException If no objects were saved
	 */
	public DBObject getDbObject() {
		if (dbObjects.length == 0) {
			throw new MongoException("No objects to return");
		}
		return dbObjects[0];
	}

	/**
	 * Get the underlying DBObjects that were serialised before they were saved.
	 * These will contain the updated IDs if IDs were generated.
	 *
	 * @return The underlying DBObjects
	 */
	public DBObject[] getDbObjects() {
		return dbObjects;
	}

	/**
	 * The underlying write result
	 *
	 * @return Get the underlying MongoDB write result
	 */
	public com.mongodb.WriteResult getWriteResult() {
		return writeResult;
	}

	/**
	 * Gets the "n" field, which contains the number of documents affected in
	 * the write operation.
	 *
	 * @return The n field
	 */
	public int getN() {
		return writeResult.getN();
	}

	/**
	 * Gets the _id value of an upserted document that resulted from this write.  Note that for MongoDB servers prior to version 2.6,
	 * this method will return null unless the _id of the upserted document was of type ObjectId.
	 *
	 * @return the value of the _id of an upserted document
	 */
	public Object getUpsertedId() {
		return writeResult.getUpsertedId();
	}


	/**
	 * Returns true if this write resulted in an update of an existing document.
	 *
	 * @return whether the write resulted in an update of an existing document.
	 */
	public boolean isUpdateOfExisting() {
		return writeResult.isUpdateOfExisting();
	}

	@Override
	public String toString() {
		return writeResult.toString();
	}

}
