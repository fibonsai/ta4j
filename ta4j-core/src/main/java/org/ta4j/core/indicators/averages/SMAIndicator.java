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
package org.ta4j.core.indicators.averages;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.helpers.RunningTotalIndicator;
import org.ta4j.core.num.Num;

/**
 * Simple Moving Average (SMA) indicator.
 * 
 * <p>The Simple Moving Average is one of the most basic and widely used technical indicators.
 * It calculates the arithmetic mean of prices over a specified period, providing a smoothed
 * trend line that filters out short-term price fluctuations.
 * 
 * <h2>Calculation</h2>
 * <p>SMA = (P₁ + P₂ + ... + Pₙ) / n
 * <br>Where:
 * <ul>
 * <li>P₁, P₂, ... Pₙ are the price values over n periods</li>
 * <li>n is the number of periods (barCount)</li>
 * </ul>
 * 
 * <h2>Interpretation</h2>
 * <ul>
 * <li><strong>Trend Direction:</strong> Rising SMA indicates uptrend, falling SMA indicates downtrend</li>
 * <li><strong>Support/Resistance:</strong> Price often finds support above or resistance below the SMA</li>
 * <li><strong>Signal Generation:</strong> Price crossing above/below SMA can generate buy/sell signals</li>
 * <li><strong>Momentum:</strong> Distance between price and SMA indicates momentum strength</li>
 * </ul>
 * 
 * <h2>Characteristics</h2>
 * <ul>
 * <li><strong>Lag:</strong> SMAs lag price movements; longer periods create more lag</li>
 * <li><strong>Smoothing:</strong> Longer periods provide more smoothing but less responsiveness</li>
 * <li><strong>Equal Weighting:</strong> All values in the period have equal weight</li>
 * </ul>
 * 
 * <h2>Common Periods</h2>
 * <ul>
 * <li><strong>Short-term:</strong> 5, 10, 20 days</li>
 * <li><strong>Medium-term:</strong> 50, 100 days</li>
 * <li><strong>Long-term:</strong> 200 days</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Create 20-period SMA on close prices
 * ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
 * SMAIndicator sma20 = new SMAIndicator(closePrice, 20);
 * 
 * // Get current SMA value
 * Num currentSMA = sma20.getValue(series.getEndIndex());
 * 
 * // Create crossover strategy
 * Rule entryRule = new CrossedUpIndicatorRule(closePrice, sma20);
 * Rule exitRule = new CrossedDownIndicatorRule(closePrice, sma20);
 * }</pre>
 * 
 * @see EMAIndicator
 * @see org.ta4j.core.rules.CrossedUpIndicatorRule
 * @see org.ta4j.core.rules.CrossedDownIndicatorRule
 * @see <a href="https://www.investopedia.com/terms/s/sma.asp">Investopedia - Simple Moving Average</a>
 * @since 0.1
 */
public class SMAIndicator extends CachedIndicator<Num> {

    private final int barCount;
    private RunningTotalIndicator previousSum;

    /**
     * Creates a Simple Moving Average indicator.
     * 
     * <p>The SMA will be calculated over the specified number of bars from the provided indicator.
     * Typically used with price indicators (close, high, low, etc.) but can be applied to any
     * numeric indicator to smooth its values.
     *
     * @param indicator the source indicator to calculate the moving average over (must not be null)
     * @param barCount  the number of bars to include in the moving average calculation (must be > 0)
     * @throws IllegalArgumentException if indicator is null or barCount <= 0
     */
    public SMAIndicator(Indicator<Num> indicator, int barCount) {
        super(indicator);
        this.previousSum = new RunningTotalIndicator(indicator, barCount);
        this.barCount = barCount;
    }

    @Override
    protected Num calculate(int index) {
        final int realBarCount = Math.min(barCount, index + 1);
        final var sum = partialSum(index);
        return sum.dividedBy(getBarSeries().numFactory().numOf(realBarCount));
    }

    private Num partialSum(int index) {
        return this.previousSum.getValue(index);
    }

    /**
     * Returns the number of bars required for this SMA to produce stable values.
     * 
     * <p>The SMA needs exactly {@code barCount} bars to calculate a true moving average.
     * Values calculated with fewer bars represent partial averages and may not be reliable
     * for trading decisions.
     * 
     * @return the bar count used for this SMA calculation
     */
    @Override
    public int getCountOfUnstableBars() {
        return barCount;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " barCount: " + barCount;
    }

}
