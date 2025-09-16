/**
 * Builders and factories for constructing various types of bars.
 *
 * <p>Provides concrete {@link org.ta4j.core.BarBuilder} implementations and
 * their corresponding {@link org.ta4j.core.BarBuilderFactory} producers:
 * <ul>
 * <li>{@link org.ta4j.core.bars.TimeBarBuilder} - Fixed-duration bars</li>
 * <li>{@link org.ta4j.core.bars.TickBarBuilder} - Bars by number of ticks</li>
 * <li>{@link org.ta4j.core.bars.VolumeBarBuilder} - Bars by traded volume</li>
 * <li>{@link org.ta4j.core.bars.AmountBarBuilder} - Bars by traded notional (price × volume)</li>
 * <li>{@link org.ta4j.core.bars.HeikinAshiBarBuilder} - Heikin-Ashi transformation</li>
 * </ul>
 *
 * <p>Factories ensure numeric precision compatibility and often cache builders
 * to carry incremental state across calls when building threshold-based bars.
 */
package org.ta4j.core.bars;
