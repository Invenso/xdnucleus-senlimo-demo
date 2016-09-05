package com.invenso.xperido.senlimo.resources;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import java.util.HashMap;

/**
 * Wrapper class around EntityManager(Factory) to allow try-with-resources
 */
public class Database implements AutoCloseable {

	private final EntityManagerFactory entityManagerFactory;
	private final EntityManager entityManager;

	private Database(EntityManagerFactory entityManagerFactory, EntityManager entityManager) {
		this.entityManagerFactory = entityManagerFactory;
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * Close the entitymanagers
	 */
	@Override
	public void close() {
		try {
			entityManager.close();
		} finally {
			entityManagerFactory.close();
		}
	}

	/**
	 * Initialize the database from the settings in persistence.xml (persistence unit SenlimoDemo)
	 *
	 * @return the initialized database
	 */
	public static Database initialize() {
		// Hibernate uses JBoss logging, this redirects it to slf4j, which is used by xperido-client
		System.setProperty("org.jboss.logging.provider", "slf4j");

		// as defined in /src/main/resources/persistence.xml
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("SenlimoDemo", new HashMap<>());
		try {
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			return new Database(entityManagerFactory, entityManager);
		} catch (PersistenceException e) {
			entityManagerFactory.close();
			throw e;
		}
	}
}
