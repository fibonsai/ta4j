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
package org.ta4j.core.aggregator;

import java.util.List;

import org.ta4j.core.Bar;

/**
 * Strategy interface for transforming a list of {@link Bar} into an aggregated set of bars.
 *
 * <p>Implementations define how lower-granularity bars are combined into
 * higher-level bars (e.g., 1-minute into 5-minute) or alternative bar types
 * (e.g., Heikin-Ashi). The output may contain one or more aggregated bars
 * depending on the implementation and input size.
 *
 * <h2>Typical usage</h2>
 * <ul>
 * <li>Combine with {@link BaseBarSeriesAggregator} to aggregate entire
 * {@link org.ta4j.core.BarSeries}</li>
 * <li>Use {@link DurationBarAggregator} for time-based upsampling</li>
 * <li>Use {@link HeikinAshiBarAggregator} to derive Heikin-Ashi bars</li>
 * </ul>
 */
public interface BarAggregator {

    /**
     * Aggregates the input bars producing one or more output bars.
     *
     * @param bars the source bars (chronologically ordered)
     * @return the aggregated bars (chronologically ordered)
     */
    List<Bar> aggregate(List<Bar> bars);
}
