/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package it.unibo.alchemist.modelchecker.interfaces;

import java.util.List;

/**
 * @author Davide Ensini
 * 
 * @param <Data>
 *            Property result type
 * @param <Result>
 *            Final result type
 */
public interface PropertyAggregatorVariance<Result, Data> extends PropertyAggregator<Result, Data> {

	/**
	 * Computes sample variance.
	 * 
	 * @param p
	 *            the list of properties to aggregate
	 * @return the variance of data.
	 */
	double getS(List<? extends Property<?, ?, Data>> p);

}
