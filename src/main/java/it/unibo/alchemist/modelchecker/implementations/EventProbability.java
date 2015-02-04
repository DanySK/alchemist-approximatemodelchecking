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
import it.unibo.alchemist.modelchecker.interfaces.PropertyAggregator;

import java.util.List;

/**
 * @author Danilo Pianini
 * 
 */
public class EventProbability implements PropertyAggregator<Double, Boolean> {

	private static final long serialVersionUID = -1080032955845002876L;

	@Override
	public Double aggregate(final List<? extends Property<?, ?, Boolean>> pList) {
		int count = 0;
		for (final Property<?, ?, Boolean> p : pList) {
			if (p.getResult()) {
				count++;
			}
		}
		return ((double) count) / ((double) pList.size());
	}

}
