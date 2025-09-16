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
 * Aggregation utilities for transforming bars and bar series.
 *
 * <p>Provides interfaces and implementations to convert a list of
 * {@link org.ta4j.core.Bar} instances or a full {@link org.ta4j.core.BarSeries}
 * into aggregated forms:
 * <ul>
 * <li>{@link org.ta4j.core.aggregator.BarAggregator} - low-level list-to-list aggregation</li>
 * <li>{@link org.ta4j.core.aggregator.BarSeriesAggregator} - series-to-series aggregation</li>
 * <li>{@link org.ta4j.core.aggregator.DurationBarAggregator} - time-based upsampling (e.g., 1m → 5m)</li>
 * <li>{@link org.ta4j.core.aggregator.HeikinAshiBarAggregator} - Heikin-Ashi transformation</li>
 * </ul>
 */
package org.ta4j.core.aggregator;