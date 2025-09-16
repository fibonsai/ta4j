/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2025 Ta4j Organization & respective
 * authors (see AUTHORS)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
/**
 * The core module of the Ta4j library - a comprehensive technical analysis library for Java.
 * 
 * <p>Ta4j provides a cohesive set of abstractions for building trading systems:
 * consistent time series, indicators, rules, strategies, execution records, and
 * evaluation metrics. The classes in this package form the backbone of the
 * library and are intentionally lightweight and composable.
 * 
 * <h2>Core Components</h2>
 * <ul>
 * <li><strong>{@link org.ta4j.core.Bar}</strong> - Immutable OHLCV snapshot over a
 *     time interval</li>
 * <li><strong>{@link org.ta4j.core.BarSeries}</strong> - Ordered collection of bars;
 *     the canonical data source</li>
 * <li><strong>{@link org.ta4j.core.Indicator}</strong> - Analytics computed over a
 *     series (e.g., moving averages, oscillators)</li>
 * <li><strong>{@link org.ta4j.core.Rule}</strong> - Boolean conditions for trading
 *     decisions; combinable via logic operators</li>
 * <li><strong>{@link org.ta4j.core.Strategy}</strong> - Entry/exit rules plus
 *     unstable (warm-up) handling</li>
 * <li><strong>{@link org.ta4j.core.Trade}</strong> - Atomic BUY/SELL operation</li>
 * <li><strong>{@link org.ta4j.core.Position}</strong> - Round-trip trade (entry +
 *     exit)</li>
 * <li><strong>{@link org.ta4j.core.TradingRecord}</strong> - Execution log and
 *     position ledger</li>
 * <li><strong>{@link org.ta4j.core.AnalysisCriterion}</strong> - Metric for
 *     evaluating performance</li>
 * </ul>
 * 
 * <h2>Data Flow</h2>
 * <ol>
 * <li>Construct a {@link org.ta4j.core.BarSeries} and populate it with
 *     {@link org.ta4j.core.Bar} data</li>
 * <li>Build {@link org.ta4j.core.Indicator} instances over the series</li>
 * <li>Combine indicators into {@link org.ta4j.core.Rule} objects</li>
 * <li>Assemble a {@link org.ta4j.core.Strategy} from entry and exit rules</li>
 * <li>Execute the strategy to produce {@link org.ta4j.core.Trade} events and
 *     {@link org.ta4j.core.Position} objects recorded in a
 *     {@link org.ta4j.core.TradingRecord}</li>
 * <li>Evaluate outcomes with {@link org.ta4j.core.AnalysisCriterion}</li>
 * </ol>
 * 
 * <h2>Thread Safety</h2>
 * Most classes in this package are <strong>not thread-safe</strong> unless explicitly documented otherwise.
 * Synchronization is the responsibility of the calling code when sharing instances across threads.
 * 
 * <h2>Numerical Precision</h2>
 * Ta4j supports different numerical implementations through the {@link org.ta4j.core.num.Num} interface,
 * allowing you to choose between performance ({@link org.ta4j.core.num.DoubleNum}) and precision 
 * ({@link org.ta4j.core.num.DecimalNum}). Builders (e.g.,
 * {@link org.ta4j.core.BarSeries#barBuilder()}) ensure type-compatible bar construction.
 * 
 * @see <a href="https://github.com/ta4j/ta4j">Ta4j GitHub Repository</a>
 * @see <a href="https://ta4j.github.io/ta4j-wiki/">Ta4j Documentation</a>
 * @since 0.1
 */
package org.ta4j.core;