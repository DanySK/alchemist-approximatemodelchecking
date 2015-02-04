/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package it.unibo.alchemist.modelchecker.interfaces;

import java.io.Serializable;
import java.util.List;

/**
 * @author Danilo Pianini
 * 
 * @param <Data>
 *            Property result type
 * @param <Result>
 *            Final result type
 */
public interface PropertyAggregator<Result, Data> extends Serializable {

	/**
	 * Aggregates the data of multiple properties, belonging to multiple runs.
	 * 
	 * @param p
	 *            the list of properties to aggregate
	 * @return the result of the aggregation
	 */
	Result aggregate(List<? extends Property<?, ?, Data>> p);

}
