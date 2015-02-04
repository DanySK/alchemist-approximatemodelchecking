/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package it.unibo.alchemist.modelchecker.implementations;

import it.unibo.alchemist.model.interfaces.IEnvironment;
import it.unibo.alchemist.model.interfaces.IReaction;
import it.unibo.alchemist.model.interfaces.ITime;
import it.unibo.alchemist.modelchecker.interfaces.Observation;

/**
 * Property is true as all Observations are true, becomes false and stops as at
 * least one Observation is false.
 * 
 * @author Davide Ensini
 * 
 * @param <T>
 *            Concentration type
 */
public class FinalTime<T> extends GenericProperty<T, Boolean, Double> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5895359665610366704L;
	private Double res = 0.0;
	private boolean canChange = true;

	/**
	 * Default constructor.
	 */
	public FinalTime() {
		super();
	}

	/**
	 * Builds a new property, copying all the observations from another existing
	 * property.
	 * 
	 * 
	 * @param o
	 *            the property whose observations will be used
	 */
	protected FinalTime(final FinalTime<T> o) {
		super(o);
		res = o.res;
	}

	@Override
	public boolean canChange() {
		return canChange;
	}

	@Override
	public Double getResult() {
		return res;
	}

	@Override
	public void stepDone(final IEnvironment<T> env, final IReaction<T> r, final ITime time, final long step) {
		res = time.toDouble();
		if (!canChange) {
			return;
		}
		for (final Observation<Boolean, T> o : getObservations()) {
			if (o.canChange() && !o.observe(env)) {
				return;
			}
		}
		canChange = false;
		checkForStop(env);

	}

	@Override
	public FinalTime<T> clone() {
		return new FinalTime<>(this);
	}

	@Override
	public void initialized(final IEnvironment<T> env) {
	}

	@Override
	public void finished(final IEnvironment<T> env, final ITime time, final long step) {
	}

}
