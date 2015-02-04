/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package it.unibo.alchemist.modelchecker;

import java.util.Arrays;

import it.unibo.alchemist.modelchecker.interfaces.ASMCListener;
import it.unibo.alchemist.modelchecker.interfaces.Property;
import it.unibo.alchemist.modelchecker.interfaces.PropertyAggregator;

/**
 * 
 * Approximate Model Checker. See Alchemist Manual for statistical reference.
 * Use AlchemistASMCGraphSupport for probability v.s. time experiments that need
 * plotting.
 * 
 * @author Davide Ensini
 * 
 * @param <T>
 *            Concentration type
 */
public class AlchemistASMCGraphSupport<T> extends AlchemistASMC<T, Double, Double> {

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
	public AlchemistASMCGraphSupport(final double delta, final double alpha, final Property<T, ?, Double> p, final PropertyAggregator<Double, Double> pa) {
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
	public AlchemistASMCGraphSupport(final double delta, final double alpha, final Property<T, ?, Double> p, final PropertyAggregator<Double, Double> pa, final int min) {
		super(delta, alpha, p, pa, min);
	}

	@Override
	protected boolean stopCondition() {
		return getN() >= getMaxN();
	}

	@Override
	protected void notifyASMCListeners() {
		Double[] da = new Double[getpList().size()];
		synchronized (getpList()) {
			for (int i = 0; i < da.length; i++) {
				da[i] = (Double) getpList().get(i).getResult();
			}
		}
		Arrays.sort(da);
		for (final ASMCListener l : getListeners()) {
			l.batchDone(da);
		}
	}
}
