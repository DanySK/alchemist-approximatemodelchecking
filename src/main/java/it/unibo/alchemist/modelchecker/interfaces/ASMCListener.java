/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package it.unibo.alchemist.modelchecker.interfaces;

/**
 * @author Davide Enisni
 * 
 */
public interface ASMCListener {
	/**
	 * Passes on simulation results.
	 * 
	 * @param values
	 *            An ordered array of values. Failing to pass an ordered array
	 *            results in unpredictable behavior.
	 */
	void batchDone(Double[] values);
}
