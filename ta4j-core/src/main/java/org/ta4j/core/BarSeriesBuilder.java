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
 * Builder interface for creating {@link BarSeries} instances.
 *
 * <p>Concrete builders (e.g., {@link BaseBarSeriesBuilder}) encapsulate the
 * configuration required to assemble a bar series, including the underlying
 * {@link org.ta4j.core.num.NumFactory} and the {@link BarBuilderFactory} used to
 * create compatible {@link Bar} instances. Using a builder ensures that all
 * interrelated components are wired consistently for subsequent indicator and
 * strategy calculations.
 */
public interface BarSeriesBuilder {

    /**
     * Builds the bar series with corresponding parameters.
     *
     * @return bar series
     */
    BarSeries build();
}
