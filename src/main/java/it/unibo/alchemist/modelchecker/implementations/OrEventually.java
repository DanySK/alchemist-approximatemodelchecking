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
 * Property defaults to false, becomes true and stops simulation as at least one
 * Observation is true.
 * 
 * @author Danilo Pianini
 * 
 * @param <T>
 *            Concentration type
 */
public final class OrEventually<T> extends GenericProperty<T, Boolean, Boolean> {

	private static final long serialVersionUID = -5850197669176723902L;
	private boolean res = false;

	/**
	 * Default constructor.
	 */
	public OrEventually() {
		super();
	};

	/**
	 * Builds a new property, copying all the observations from another existing
	 * property.
	 * 
	 * @param o
	 *            the property whose observations will be used
	 */
	protected OrEventually(final OrEventually<T> o) {
		super(o);
		res = o.res;
	}

	@Override
	public boolean canChange() {
		return !res;
	}

	@Override
	public OrEventually<T> clone() {
		return new OrEventually<>(this);
	}

	@Override
	public Boolean getResult() {
		return res;
	}

	@Override
	public void stepDone(final IEnvironment<T> env, final IReaction<T> r, final ITime time, final long step) {
		for (final Observation<Boolean, T> o : getObservations()) {
			res |= o.observe(env);
		}
		checkForStop(env);
	}

	@Override
	public void initialized(final IEnvironment<T> env) {
	}

	@Override
	public void finished(final IEnvironment<T> env, final ITime time, final long step) {
	}

}
