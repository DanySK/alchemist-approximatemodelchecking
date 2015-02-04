/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package it.unibo.alchemist.modelchecker;

import it.unibo.alchemist.modelchecker.interfaces.Property;
import it.unibo.alchemist.modelchecker.interfaces.PropertyAggregatorVariance;

/**
 * 
 * Approximate Model Checker. See Alchemist Manual for statistical reference.
 * Use AlchemistASMCNumeric to deal with numbers.
 * 
 * @author Danilo Pianini
 * @author Davide Ensini
 * 
 * @param <T>
 *            Concentration type
 * @param <D>
 *            Property result type
 */

public class AlchemistASMCNumeric<T, D extends Number> extends AlchemistASMC<T, D, Double> {

	/**
	 * Construct an instance with given parameters.
	 * 
	 * @param delta
	 *            approximation
	 * @param alpha
	 *            confidence
	 * @param p
	 *            property to verify
	 * @param pa
	 *            property aggregator to use
	 */
	public AlchemistASMCNumeric(final double delta, final double alpha, final Property<T, ?, D> p, final PropertyAggregatorVariance<Double, D> pa) {
		super(delta, alpha, p, pa);
	}

	/**
	 * Construct an instance with given parameters.
	 * 
	 * @param delta
	 *            approximation
	 * @param alpha
	 *            confidence
	 * @param p
	 *            property to verify
	 * @param pa
	 *            property aggregator to use
	 * @param min
	 *            minimum sample size
	 */
	public AlchemistASMCNumeric(final double delta, final double alpha, final Property<T, ?, D> p, final PropertyAggregatorVariance<Double, D> pa, final int min) {
		super(delta, alpha, p, pa, min);
	}

	@Override
	protected boolean stopCondition() {
		return intervalSizeReached();
	}

	@Override
	protected void notifyASMCListeners() {
		// TODO should be implemented
	}

}
