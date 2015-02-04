/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package it.unibo.alchemist.modelchecker.implementations;

import it.unibo.alchemist.core.implementations.Simulation;
import it.unibo.alchemist.model.interfaces.IEnvironment;
import it.unibo.alchemist.modelchecker.interfaces.Observation;
import it.unibo.alchemist.modelchecker.interfaces.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Danilo Pianini
 * 
 * @param <T>
 *            Concentration type
 * @param <K>
 *            Observation result type
 * @param <R>
 *            Property result type
 */
public abstract class GenericProperty<T, K, R> implements Property<T, K, R> {

	private static final long serialVersionUID = -5079010184833195005L;
	private final List<Observation<K, T>> observations = new ArrayList<>();

	/**
	 * Default constructor.
	 */
	protected GenericProperty() {
	}

	/**
	 * Builds a new property, copying all the observations from another existing
	 * property.
	 * 
	 * @param g
	 *            the property whose observations will be used
	 */
	protected GenericProperty(final GenericProperty<T, K, R> g) {
		for (final Observation<K, T> o : g.observations) {
			observations.add(o.clone());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.unibo.alchemist.modelchecker.interfaces.Property#addObservation(it
	 * .unibo .alchemist.modelchecker.interfaces.Observation)
	 */
	@Override
	public void addObservation(final Observation<K, T> obs) {
		observations.add(obs);
	}

	/**
	 * Checks whether it makes sense or not to continue executing this
	 * simulation in order to evaluate this property. If not, stops the
	 * simulation flow.
	 * 
	 * @param env
	 *            the environment
	 */
	protected void checkForStop(final IEnvironment<?> env) {
		if (!canChange()) {
			Simulation.stop(env);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public abstract GenericProperty<T, K, R> clone();

	/**
	 * @return the list of observations
	 */
	protected List<Observation<K, T>> getObservations() {
		return observations;
	}

}
