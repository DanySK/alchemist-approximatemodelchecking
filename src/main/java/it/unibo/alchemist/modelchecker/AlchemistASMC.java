/*
 * Copyright (C) 2010-2014, Danilo Pianini and contributors
 * listed in the project's pom.xml file.
 * 
 * This file is part of Alchemist, and is distributed under the terms of
 * the GNU General Public License, with a linking exception, as described
 * in the file LICENSE in the Alchemist distribution's top directory.
 */
package it.unibo.alchemist.modelchecker;

import it.unibo.alchemist.core.executors.MultithreadedExecutor;
import it.unibo.alchemist.core.interfaces.ISimulation;
import it.unibo.alchemist.external.cern.jet.random.engine.MersenneTwister;
import it.unibo.alchemist.external.cern.jet.random.engine.RandomEngine;
import it.unibo.alchemist.language.EnvironmentBuilder;
import it.unibo.alchemist.modelchecker.interfaces.ASMCListener;
import it.unibo.alchemist.modelchecker.interfaces.Property;
import it.unibo.alchemist.modelchecker.interfaces.PropertyAggregator;
import it.unibo.alchemist.modelchecker.interfaces.PropertyAggregatorVariance;
import it.unibo.alchemist.utils.L;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BisectionSolver;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.util.FastMath;
import org.xml.sax.SAXException;

/**
 * 
 * Approximate Model Checker. See Alchemist Manual for statistical reference.
 * This is an abstract class. Use AlchemistASMCBoolean to deal with boolean
 * values, or AlchemistASMCNumeric to deal with numbers.
 * 
 * @author Danilo Pianini
 * @author Davide Ensini
 * 
 * 
 * @param <T>
 *            Concentration type
 * @param <D>
 *            Property result type
 * @param <R>
 *            Final result type
 */
public abstract class AlchemistASMC<T, D, R> {

	
	
	private static final double LOG_MUL = 0.5;
	private final PropertyAggregator<R, D> aggregator;
	private final double a;
	private final double d;
	private final Semaphore exec = new Semaphore(0);
	private final int minN;
	private final int maxN;
	private static final int SAMPLESTEP = 30;
	private final MultithreadedExecutor<T> mx = new MultithreadedExecutor<T>() {
		@Override
		protected void configureSimulation(final ISimulation<T> s) {
			final Property<T, ?, D> pclone = property.clone();
			s.addOutputMonitor(pclone);
			getpList().add(pclone);
		}
	};
	private int nr;
	private final List<Property<T, ?, D>> pList;
	private final Property<T, ?, D> property;
	private final List<ASMCListener> listeners = new LinkedList<>();

	/**
	 * Given the approximation and the number of runs to execute, computes a
	 * lower bound for confidence (see manual).
	 * 
	 * @param delta
	 *            approximation
	 * @param n
	 *            number of runs
	 * @return confidence
	 */
	public static double computeAlphaUB(final double delta, final int n) {
		return 2 * Math.exp(-delta * delta * n / LOG_MUL);
	}

	/**
	 * Given the runs and the confidence, computes an upper bound for the
	 * approximation (see manual).
	 * 
	 * @param n
	 *            number of runs
	 * @param alpha
	 *            confidence
	 * @return the approximation
	 */
	public static double computeDeltaUB(final int n, final double alpha) {
		return Math.sqrt((LOG_MUL * Math.log(2 / alpha)) / n);
	}

	/**
	 * Given the approximation and the confidence, computes an upper bound for
	 * the number of runs to execute (see manual).
	 * 
	 * @param delta
	 *            approximation
	 * @param alpha
	 *            confidence
	 * @return number of runs
	 */
	public static int computeSampleSizeUB(final double delta, final double alpha) {
		return (int) Math.ceil(LOG_MUL * Math.log(2 / alpha) / (delta * delta));
	}

	/**
	 * Construct an instance with given parameters.
	 * 
	 * @param delta
	 *            approximation
	 * @param alpha
	 *            confidence
	 * @param p
	 *            property to verify
	 * @param pa
	 *            property aggregator to use
	 */
	protected AlchemistASMC(final double delta, final double alpha, final Property<T, ?, D> p, final PropertyAggregator<R, D> pa) {
		this(delta, alpha, p, pa, Math.min(computeMinimum(delta, alpha), computeSampleSizeUB(delta, alpha)));
	}

	/**
	 * Construct an instance with given parameters, specifiyng minimum sample
	 * size.
	 * 
	 * @param delta
	 *            approximation
	 * @param alpha
	 *            confidence
	 * @param p
	 *            property to verify
	 * @param pa
	 *            property aggregator to use
	 * @param min
	 *            Minimum sample size
	 */
	protected AlchemistASMC(final double delta, final double alpha, final Property<T, ?, D> p, final PropertyAggregator<R, D> pa, final int min) {
		d = delta;
		a = alpha;
		minN = min;
		maxN = computeSampleSizeUB(delta, alpha);
		property = p;
		aggregator = pa;
		pList = Collections.synchronizedList(new ArrayList<Property<T, ?, D>>(min));
	}

	/**
	 * @param xmlFilePath
	 *            Alchemist XML specification to execute
	 * @param steps
	 *            maximum length of the simulation in steps
	 * @param finalTime
	 *            maximum length of the simulation in simulated time units
	 */
	public void execute(final String xmlFilePath, final long steps, final double finalTime) {
		final ExecutorService ex = Executors.newSingleThreadExecutor();
		ex.execute(new Runnable() {
			public void run() {
				try {
					nr = minN;
					final byte[] ba = mx.serializedEnvironment(xmlFilePath);
					EnvironmentBuilder<T> ebo;
					ebo = new EnvironmentBuilder<>(xmlFilePath);
					ebo.buildEnvironment();
					final RandomEngine random = new MersenneTwister();
					random.setSeed(ebo.getRandomEngine().getSeed());

					mx.addJob(ba, random, minN, steps, finalTime);
					mx.waitForCompletion();
					notifyASMCListeners();
					while (!stopCondition()) {
						nr += SAMPLESTEP;
						// pList.ensureCapacity(INCREASE);
						mx.addJob(ba, random, SAMPLESTEP, steps, finalTime);
						mx.waitForCompletion();
						notifyASMCListeners();
					}
					mx.destroy();
					exec.release();
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException | SAXException | IOException | ParserConfigurationException e) {
					L.error(e);
				}
			}

		});

	}

	/**
	 * @return the confidence
	 */
	public double getAlpha() {
		return a;
	}

	/**
	 * @return the approximation
	 */
	public double getDelta() {
		return d;
	}

	/**
	 * @return sample size (so far)
	 */
	public int getN() {
		return nr;
	}

	/**
	 * Waits for all the simulations to finish and returns the aggregated
	 * result.
	 * 
	 * @return the final aggregated result
	 */
	public R getResult() {
		waitForCompletion();
		return aggregator.aggregate(getpList());
	}

	/**
	 * Waits until all the queued jobs are completed.
	 */
	public void waitForCompletion() {
		try {
			exec.acquire();
		} catch (InterruptedException e) {
			L.error(e);
		}
	}

	private static int computeMinimum(final double interval, final double confidence) {
		final UnivariateFunction f = new UnivariateFunction() {
			@Override
			public double value(final double n) {
				double t;
				if (Math.ceil(n) == FastMath.floor(n)) {
					t = new TDistribution((int) n).inverseCumulativeProbability(1 - confidence / 2);
				} else {
					double t1 = new TDistribution((int) FastMath.ceil(n)).inverseCumulativeProbability((1 - confidence / 2)) * (n - Math.floor(n));
					double t2 = new TDistribution((int) FastMath.floor(n)).inverseCumulativeProbability((1 - confidence / 2)) * (Math.ceil(n) - n);
					t = t1 + t2;
				}
				double value = 2 * t / n;
				return value - interval;
			}
		};
		final BisectionSolver bs = new BisectionSolver();
		return (int) Math.ceil(bs.solve(Integer.MAX_VALUE, f, 1, Integer.MAX_VALUE));
	}

	/**
	 * Checks whether obtained confidence interval size meets requirement.
	 * 
	 * @return True iff interval size is little enough
	 */
	protected boolean intervalSizeReached() {
		final double s = ((PropertyAggregatorVariance<R, D>) aggregator).getS(getpList());
		final double d0 = computeDeltaDynamic(s, nr, a);
		return d0 < d;
	}

	/**
	 * Computes interval size according to dynamic criterion (see manual). A
	 * lower size interval could be obtained with computeDeltaStatic.
	 * 
	 * @param sPar
	 *            Value of variance function s (see manual)
	 * @param sampleSize
	 *            The number of simulated instances
	 * @param alpha
	 *            confidence
	 * @return Interval size
	 */
	public static double computeDeltaDynamic(final double sPar, final int sampleSize, final double alpha) {
		final double t = new TDistribution(sampleSize - 1).inverseCumulativeProbability(1 - alpha / 2);
		return 2 * t * sPar / Math.sqrt(sampleSize);
	}

	/**
	 * Computes interval size according to static criterion (see manual). A
	 * lower size interval could be obtained with computeDeltaDynamic.
	 * 
	 * @param sampleSize
	 *            The number of simulated instances
	 * @param alpha
	 *            confidence
	 * @return Interval size
	 */
	public static double computeDeltaStatic(final int sampleSize, final double alpha) {
		return computeDeltaUB(sampleSize, alpha);
	}

	/**
	 * @return true when there's no need to execute more simulations
	 */
	protected abstract boolean stopCondition();

	/**
	 * @return minimum sample size, wheter computed or imposed by user
	 */
	public int getMinN() {
		return minN;
	}

	/**
	 * @return maximum sample size from the upper bound
	 */
	public int getMaxN() {
		return maxN;
	}

	/**
	 * @return the property aggregator in use
	 */
	public PropertyAggregator<R, D> getAggregator() {
		return aggregator;
	}

	/**
	 * @param l
	 *            The ASMCListener to register
	 */
	public void addASMCListener(final ASMCListener l) {
		getListeners().add(l);
	}

	/**
	 * 
	 */
	protected abstract void notifyASMCListeners();

	/**
	 * @return the pList
	 */
	protected List<Property<T, ?, D>> getpList() {
		return pList;
	}

	/**
	 * @return the listeners
	 */
	protected List<ASMCListener> getListeners() {
		return listeners;
	}
}