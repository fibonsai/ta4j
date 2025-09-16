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

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.ta4j.core.num.Num;

/**
 * An indicator over a {@link BarSeries bar series} that provides calculated values for technical analysis.
 *
 * <p>An indicator computes analytical values (such as moving averages, oscillators, or boolean signals)
 * based on historical price data from a bar series. Each indicator returns a value of type {@code T}
 * for each index position in the associated bar series.
 *
 * <h2>Types of Indicators</h2>
 * <ul>
 * <li><strong>Numerical Indicators</strong> ({@code Indicator<Num>}) - Return numeric values like
 *     moving averages, RSI, MACD, etc.</li>
 * <li><strong>Boolean Indicators</strong> ({@code Indicator<Boolean>}) - Return true/false signals
 *     for pattern recognition or threshold crossing</li>
 * </ul>
 *
 * <h2>Stability and Warm-up Period</h2>
 * <p>Many indicators require a minimum number of data points before they can produce reliable values.
 * During this "warm-up" period, indicators may return {@link org.ta4j.core.num.NaN} or unreliable values.
 * Use {@link #isStable()} to check if the indicator has enough data for accurate calculations.
 *
 * <h2>Performance Considerations</h2>
 * <p>Most indicator implementations cache calculated values to avoid recomputation. However,
 * be aware that indicators maintain references to their underlying bar series and may consume
 * memory proportional to the series length.
 *
 * <h2>Thread Safety</h2>
 * <p>Indicators are <strong>not thread-safe</strong>. If you need to access an indicator from
 * multiple threads, external synchronization is required.
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Create a bar series with market data
 * BarSeries series = new BaseBarSeriesBuilder().withName("AAPL").build();
 * // ... add bars to series
 *
 * // Create a simple moving average indicator
 * Indicator<Num> sma = new SMAIndicator(new ClosePriceIndicator(series), 14);
 *
 * // Get the latest SMA value
 * Num currentSMA = sma.getValue(series.getEndIndex());
 *
 * // Check if indicator is stable (has enough data)
 * if (sma.isStable()) {
 *     System.out.println("SMA(14): " + currentSMA);
 * }
 * }</pre>
 *
 * @param <T> the type of the returned value (typically {@link org.ta4j.core.num.Num} for numerical
 *            indicators or {@link Boolean} for signal indicators)
 *
 * @see BarSeries
 * @see org.ta4j.core.indicators.AbstractIndicator
 * @see org.ta4j.core.indicators.CachedIndicator
 * @since 0.1
 */
public interface Indicator<T> {

    /**
     * Returns the indicator value at the specified bar index.
     * 
     * <p>This is the core method of any indicator. It calculates and returns the indicator's 
     * value for the given bar index. The returned value may be cached for performance.
     * 
     * <p><strong>Important:</strong> If the indicator is not stable at the given index
     * (i.e., there are insufficient data points), the returned value may be unreliable
     * or {@link org.ta4j.core.num.NaN NaN} for numerical indicators.
     *
     * @param index the bar index (must be >= 0 and <= series.getEndIndex())
     * @return the calculated indicator value at the specified index
     * @throws IndexOutOfBoundsException if the index is outside the valid range
     * @see #isStable()
     * @see #getCountOfUnstableBars()
     */
    T getValue(int index);

    /**
     * Returns {@code true} once {@code this} indicator has enough bars to
     * accurately calculate its value. Otherwise, {@code false} will be returned,
     * which means the indicator will give incorrect values ​​due to insufficient
     * data. This method determines stability using the formula:
     *
     * <pre>
     * isStable = {@link BarSeries#getBarCount()} >= {@link #getCountOfUnstableBars()}
     * </pre>
     *
     * @return true if the calculated indicator value is correct
     */
    default boolean isStable() {
        return getBarSeries().getBarCount() >= getCountOfUnstableBars();
    }

    /**
     * Returns the number of bars required before this indicator produces stable/reliable values.
     * 
     * <p>This is the "warm-up" period during which the indicator may return inaccurate results
     * due to insufficient historical data. For example, a 14-period moving average needs 14 bars
     * to produce a fully accurate value.
     * 
     * <p>Values calculated during the unstable period should be used with caution as they
     * may not reflect the true indicator behavior.
     *
     * @return the number of bars required for stable calculations (>= 0)
     * @see #isStable()
     */
    int getCountOfUnstableBars();

    /**
     * Returns the bar series that this indicator is calculated over.
     * 
     * <p>This is the source data for all indicator calculations. The indicator will
     * always reference the same bar series instance that was provided during construction.
     *
     * @return the associated bar series (never null)
     */
    BarSeries getBarSeries();

    /**
     * Returns a stream of all indicator values over the valid index range of the bar series.
     * 
     * <p>This is a convenience method that creates a stream containing all indicator values
     * from {@link BarSeries#getBeginIndex()} to {@link BarSeries#getEndIndex()} inclusive.
     * This can be useful for batch processing or functional-style operations on indicator data.
     * 
     * <p><strong>Performance Note:</strong> This method may trigger calculation of all
     * indicator values if they haven't been computed yet. For large series, consider
     * processing values individually instead.
     *
     * @return a stream of all indicator values in index order
     * @see #getValue(int)
     */
    default Stream<T> stream() {
        return IntStream.range(getBarSeries().getBeginIndex(), getBarSeries().getEndIndex() + 1)
                .mapToObj(this::getValue);
    }

    /**
     * Converts a numeric indicator's values to a Double array for the specified range.
     * 
     * <p>This utility method extracts values from a {@link Num}-based indicator and converts
     * them to Java Double objects. The extraction starts from {@code index - barCount + 1}
     * and includes {@code barCount} values ending at {@code index}.
     * 
     * <p><strong>Precision Warning:</strong> Converting from {@link org.ta4j.core.num.DecimalNum}
     * to Double may result in precision loss. Use this method only when working with external
     * APIs that require Double arrays or when precision loss is acceptable.
     * 
     * <p><strong>Example:</strong> To get the last 5 SMA values as doubles:
     * <pre>{@code
     * Indicator<Num> sma = new SMAIndicator(closePrices, 14);
     * Double[] lastFiveValues = Indicator.toDouble(sma, series.getEndIndex(), 5);
     * }</pre>
     *
     * @param ref      the numeric indicator to extract values from (must not be null)
     * @param index    the ending index (inclusive) for value extraction
     * @param barCount the number of values to extract (must be > 0)
     * @return array of Double values in chronological order
     * @throws IllegalArgumentException if barCount <= 0 or ref is null
     * @throws IndexOutOfBoundsException if the calculated range is invalid
     * @since 0.1
     */
    static Double[] toDouble(Indicator<Num> ref, int index, int barCount) {
        int startIndex = Math.max(0, index - barCount + 1);
        return IntStream.range(startIndex, startIndex + barCount)
                .mapToObj(ref::getValue)
                .map(Num::doubleValue)
                .toArray(Double[]::new);
    }
}
