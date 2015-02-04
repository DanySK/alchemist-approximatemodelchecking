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
import it.unibo.alchemist.modelchecker.interfaces.Observation;

/**
 * Checks whether some nodes are inside a given rectangular area.
 * 
 * @author Davide Ensini
 * @param <T>
 *            Concentration type
 */
public class NodesInAreaObservation<T> implements Observation<Boolean, T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3909644384674703156L;
	private final double[] ne;
	private final double[] sw;
	private final int[] nodes;
	private final double nodesRatio;
	private boolean canItChange = true;

	/**
	 * Returns a clone of the given Observation.
	 * 
	 * @param p
	 *            The Observation to clone
	 */
	public NodesInAreaObservation(final NodesInAreaObservation<T> p) {
		sw = p.sw.clone();
		ne = p.ne.clone();
		nodes = p.nodes.clone();
		canItChange = p.canItChange;
		nodesRatio = p.nodesRatio;
	}

	/**
	 * Creates the observation given coordinates and some contiguous nodes.
	 * 
	 * @param northEast
	 *            The North-East point of the rectangle, coordinates
	 * @param southWest
	 *            The South-West point of the rectangle, coordinates
	 * @param nNodes
	 *            The number of nodes to consider
	 * @param initialNode
	 *            The nodeId of the first node
	 * @param ratio
	 *            The ratio of well positioned nodes that makes the observation
	 *            true.
	 */
	public NodesInAreaObservation(final double[] northEast, final double[] southWest, final int nNodes, final int initialNode, final double ratio) {
		this(northEast, southWest, getNodesIdS(initialNode, nNodes), ratio);
	}

	/**
	 * Creates the observation given coordinates and some nodes.
	 * 
	 * @param northEast
	 *            The North-East point of the rectangle, coordinates
	 * @param southWest
	 *            The South-West point of the rectangle, coordinates
	 * @param nodeArray
	 *            An array of node IDs
	 * @param ratio
	 *            The ratio of well positioned nodes that makes the observation
	 *            true.
	 */
	public NodesInAreaObservation(final double[] northEast, final double[] southWest, final int[] nodeArray, final double ratio) {
		this.ne = northEast.clone();
		this.sw = southWest.clone();
		this.nodes = nodeArray.clone();
		this.nodesRatio = ratio;
	}

	@Override
	public boolean canChange() {
		return canItChange;
	}

	@Override
	public Boolean observe(final IEnvironment<T> env) {
		if (!canItChange) {
			return true;
		}
		int count = 0;
		for (final int code : nodes) {
			final double[] coords = env.getPosition(env.getNodeByID(code)).getCartesianCoordinates();
			if (coords[0] < ne[0] && coords[0] > sw[0] && coords[1] < ne[1] && coords[1] > sw[1]) {
				count++;
			}
		}
		if ((double) count / nodes.length < nodesRatio) {
			return false;
		}
		canItChange = false;
		return true;
	}

	@Override
	public NodesInAreaObservation<T> clone() {
		return new NodesInAreaObservation<>(this);
	}

	private static int[] getNodesIdS(final int initial, final int nNodes) {
		int[] seq = new int[nNodes];
		for (int i = 0; i < nNodes; i++) {
			seq[i] = i + initial;
		}
		return seq;
	}
}