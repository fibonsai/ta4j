/**
 * Backtesting utilities for executing strategies over bar series.
 *
 * <p>Provides the orchestration and execution models required to simulate
 * trading decisions made by a {@link org.ta4j.core.Strategy} on a
 * {@link org.ta4j.core.BarSeries}. Execution models specify how trades are
 * placed in time and at which price, enabling different assumptions (e.g.,
 * current close vs next open).
 *
 * <h2>Key classes</h2>
 * <ul>
 * <li>{@link org.ta4j.core.backtest.BarSeriesManager} - Coordinates a strategy
 *     run over a series</li>
 * <li>{@link org.ta4j.core.backtest.TradeExecutionModel} - Strategy for
 *     simulating execution timing and price</li>
 * <li>{@link org.ta4j.core.backtest.TradeOnCurrentCloseModel} - Execute at
 *     current bar close</li>
 * <li>{@link org.ta4j.core.backtest.TradeOnNextOpenModel} - Execute at next bar
 *     open</li>
 * <li>{@link org.ta4j.core.backtest.BacktestExecutor} - High-level runner that
 *     produces {@link org.ta4j.core.reports.TradingStatement} reports</li>
 * </ul>
 */
package org.ta4j.core.backtest;
