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
 * Property defaults to false, becomes true and stops simulation as all
 * Observations are true.
 * 
 * @author Davide Ensini
 * 
 * @param <T>
 *            Concentration type
 */
public class AndEventually<T> extends GenericProperty<T, Boolean, Boolean> {

	private static final long serialVersionUID = 4933685673641141011L;
	private boolean mayChange = true;
	private boolean res;

	/**
	 * Default constructor.
	 */
	public AndEventually() {
		super();
	};

	/**
	 * Builds a new property, copying all the observations from another existing
	 * property.
	 * 
	 * @param o
	 *            the property whose observations will be used
	 */
	protected AndEventually(final AndEventually<T> o) {
		super(o);
		mayChange = o.mayChange;
	}

	@Override
	public AndEventually<T> clone() {
		return new AndEventually<>(this);
	}

	@Override
	public boolean canChange() {
		return mayChange;
	}

	@Override
	public Boolean getResult() {
		return res;
	}

	@Override
	public void stepDone(final IEnvironment<T> env, final IReaction<T> r, final ITime time, final long step) {
		if (!mayChange) {
			return;
		}
		boolean all = true;
		for (final Observation<Boolean, T> o : getObservations()) {
			all = all && o.observe(env);
			if (!all) {
				break;
			}
		}
		if (all) {
			res = true;
			mayChange = false;
		}
		checkForStop(env);
	}

	@Override
	public void initialized(final IEnvironment<T> env) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finished(final IEnvironment<T> env, final ITime time, final long step) {
		// TODO Auto-generated method stub
		
	}
}