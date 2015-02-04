/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package it.unibo.alchemist.modelchecker.interfaces;

import it.unibo.alchemist.model.interfaces.IEnvironment;

import java.io.Serializable;

/**
 * @author Danilo Pianini
 * 
 * @param <K>
 *            Observation result type
 * @param <T>
 *            Concentration type
 */
public interface Observation<K, T> extends Serializable, Cloneable {

	/**
	 * @return false if the value of this observation cannot change anymore,
	 *         regardless how long the simulation will run. Used for
	 *         optimization purposes.
	 */
	boolean canChange();

	/**
	 * @return a clone of this observation
	 */
	Observation<K, T> clone();

	/**
	 * @param env
	 *            the environment to observe
	 * @return the value for this observation
	 */
	K observe(IEnvironment<T> env);

}
