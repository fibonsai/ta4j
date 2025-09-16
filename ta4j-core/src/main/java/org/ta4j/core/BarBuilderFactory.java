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
package org.ta4j.core;

/**
 * Factory for producing {@link BarBuilder} instances compatible with a given
 * {@link BarSeries}.
 *
 * <p>The returned builder will generate {@link Bar} objects that use the same
 * numerical precision (Num implementation) as the target series. This
 * abstraction allows the core to support multiple numeric backends while
 * keeping bar construction consistent for indicators, rules, and strategies.
 *
 * <h2>Relationship to other components</h2>
 * <ul>
 * <li>Used by {@link BarSeries} implementations (e.g., {@link BaseBarSeries})
 *     to expose a series-compatible {@link BarBuilder}</li>
 * <li>Implementations live under {@code org.ta4j.core.bars.*} (e.g.,
 *     TimeBarBuilderFactory)</li>
 * </ul>
 */
public interface BarBuilderFactory {

    /**
     * Constructor.
     *
     * @param series the bar series to which the created bar should be added
     * @return the bar builder
     */
    BarBuilder createBarBuilder(BarSeries series);
}
