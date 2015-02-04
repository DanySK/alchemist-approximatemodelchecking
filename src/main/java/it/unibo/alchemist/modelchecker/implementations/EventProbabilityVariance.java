/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package it.unibo.alchemist.modelchecker.implementations;

import java.util.List;

import it.unibo.alchemist.modelchecker.interfaces.Property;
import it.unibo.alchemist.modelchecker.interfaces.PropertyAggregatorVariance;

/**
 * @author Davide Ensini
 * 
 */
public class EventProbabilityVariance extends EventProbability implements PropertyAggregatorVariance<Double, Boolean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1767827513849586746L;

	@Override
	public double getS(final List<? extends Property<?, ?, Boolean>> pList) {
		double count = 0;
		for (final Property<?, ?, Boolean> p : pList) {
			if (p.getResult()) {
				count++;
			}
		}
		final double squareSum = count * count / pList.size();
		return Math.sqrt((count - squareSum) / (pList.size() - 1));
	}

}
