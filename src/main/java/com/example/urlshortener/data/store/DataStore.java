package com.example.urlshortener.data.store;

/**
 * Common API for in-memory data store that will keep application entities.
 *
 * @param <T> for specific application entity
 */
public interface DataStore<T> {
    /**
     * Inserts passed entity to data store and immediately returns it.
     *
     * @param entity specific application entity
     * @return inserted entity
     */
    T insert(T entity);

    /**
     * Checks existence of specific entity by passed identifier.
     *
     * @param id entity identifier
     * @return {@code true} if entity exist in data store {@code false} otherwise
     */
    boolean exists(String id);

    /**
     * Finds and returns specific entity by passed identifier.
     *
     * @param id entity identifier
     * @return specific entity
     */
    T find(String id);

}
