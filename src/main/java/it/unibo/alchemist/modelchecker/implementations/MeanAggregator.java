/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package it.unibo.alchemist.modelchecker.implementations;

import it.unibo.alchemist.modelchecker.interfaces.Property;
import it.unibo.alchemist.modelchecker.interfaces.PropertyAggregatorVariance;

import java.util.List;

/**
 * Aggregates double values returning their mean.
 * 
 * @author Davide Ensini
 * 
 * @param <T>
 *            Concentration type
 */
public class MeanAggregator<T extends Number> implements PropertyAggregatorVariance<Double, T> {

	private static final long serialVersionUID = 102979612426372191L;

	@Override
	public Double aggregate(final List<? extends Property<?, ?, T>> pList) {
		double sum = 0;
		synchronized (pList) {
			for (final Property<?, ?, T> p : pList) {
					sum += p.getResult().doubleValue();
			}
		}
		return ((double) sum) / ((double) pList.size());
	}

	@Override
	public double getS(final List<? extends Property<?, ?, T>> pList) {
		final double mean = aggregate(pList);
		double sum = 0;
		for (final Property<?, ?, T> p : pList) {
			sum += Math.pow(p.getResult().doubleValue(), 2.0);
		}
		return Math.sqrt((sum - mean * mean * pList.size()) / (pList.size() - 1));
	}

}
