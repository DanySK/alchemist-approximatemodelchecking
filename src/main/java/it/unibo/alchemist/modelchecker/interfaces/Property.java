/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package it.unibo.alchemist.modelchecker.interfaces;

import it.unibo.alchemist.boundary.interfaces.OutputMonitor;

/**
 * @author Danilo Pianini
 * 
 * @param <T>
 *            Concentration type
 * @param <K>
 *            Observation result type
 * @param <R>
 *            Property result type
 */
public interface Property<T, K, R> extends OutputMonitor<T>, Cloneable {

	/**
	 * Adds an observation to the pool of the observations to consider.
	 * 
	 * @param obs
	 *            the observation to add
	 */
	void addObservation(Observation<K, T> obs);

	/**
	 * @return false if the value of this property cannot change anymore,
	 *         regardless how long the simulation will run. Used for
	 *         optimization purposes.
	 */
	boolean canChange();

	/**
	 * @return a clone of this property
	 */
	Property<T, K, R> clone();

	/**
	 * @return the current value of this property
	 */
	R getResult();

}
