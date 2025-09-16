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
package org.ta4j.core.indicators.helpers;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

/**
 * Highest Value indicator for finding maximum values over a rolling period.
 * 
 * <p>The Highest Value indicator is a fundamental utility that tracks the maximum value
 * of any indicator over a specified lookback period. It's essential for many technical
 * analysis calculations including channel analysis, oscillator calculations, support/resistance
 * identification, and breakout detection. This rolling maximum function is universally
 * applicable across all indicator types and timeframes.
 * 
 * <h2>Calculation Method</h2>
 * <p>For each bar, the indicator examines the most recent N periods and returns the
 * highest value found within that window:
 * <ul>
 * <li><strong>Window Size:</strong> Defined by barCount parameter</li>
 * <li><strong>Search Direction:</strong> Looks backward from current index</li>
 * <li><strong>Comparison Logic:</strong> Uses numerical comparison for maximum detection</li>
 * <li><strong>NaN Handling:</strong> Recursively handles missing data points</li>
 * </ul>
 * 
 * <h2>Common Applications</h2>
 * <ul>
 * <li><strong>Price Channel Analysis:</strong>
 *     <ul>
 *     <li>Donchian Channels (highest high over N periods)</li>
 *     <li>Price channel upper boundaries</li>
 *     <li>Resistance level identification</li>
 *     </ul>
 * </li>
 * <li><strong>Oscillator Calculations:</strong>
 *     <ul>
 *     <li>Stochastic Oscillator (highest high component)</li>
 *     <li>Williams %R (maximum price component)</li>
 *     <li>Relative Strength Index variants</li>
 *     </ul>
 * </li>
 * <li><strong>Breakout Detection:</strong>
 *     <ul>
 *     <li>New high identification (current > highest previous)</li>
 *     <li>Breakout confirmation signals</li>
 *     <li>Momentum analysis</li>
 *     </ul>
 * </li>
 * <li><strong>Volatility Analysis:</strong>
 *     <ul>
 *     <li>True Range calculations</li>
 *     <li>ATR (Average True Range) components</li>
 *     <li>Volatility band calculations</li>
 *     </ul>
 * </li>
 * </ul>
 * 
 * <h2>Trading Strategy Applications</h2>
 * <ul>
 * <li><strong>Support/Resistance:</strong>
 *     <ul>
 *     <li>Dynamic resistance levels based on recent highs</li>
 *     <li>Psychological price barriers</li>
 *     <li>Channel trading boundaries</li>
 *     </ul>
 * </li>
 * <li><strong>Breakout Strategies:</strong>
 *     <ul>
 *     <li>Entry signals when price exceeds recent highs</li>
 *     <li>Momentum confirmation</li>
 *     <li>Trend continuation patterns</li>
 *     </ul>
 * </li>
 * <li><strong>Risk Management:</strong>
 *     <ul>
 *     <li>Dynamic stop-loss placement below recent highs</li>
 *     <li>Profit target calculations</li>
 *     <li>Position sizing based on range analysis</li>
 *     </ul>
 * </li>
 * </ul>
 * 
 * <h2>Period Selection Guidelines</h2>
 * <ul>
 * <li><strong>Short-term (5-10 periods):</strong> Responsive to recent price action</li>
 * <li><strong>Medium-term (20-50 periods):</strong> Balanced between sensitivity and stability</li>
 * <li><strong>Long-term (100+ periods):</strong> Major resistance levels and trend analysis</li>
 * <li><strong>Custom Periods:</strong> Based on market volatility and trading style</li>
 * </ul>
 * 
 * <h2>Performance Considerations</h2>
 * <ul>
 * <li><strong>Computational Complexity:</strong> O(N) per calculation where N = barCount</li>
 * <li><strong>Memory Usage:</strong> Cached values optimize repeated access</li>
 * <li><strong>Optimization Opportunities:</strong> Sliding window algorithms for high-frequency use</li>
 * <li><strong>NaN Handling:</strong> Recursive approach manages missing data gracefully</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Basic highest high over 20 periods
 * HighPriceIndicator highPrice = new HighPriceIndicator(series);
 * HighestValueIndicator highest20 = new HighestValueIndicator(highPrice, 20);
 * 
 * // Donchian Channel upper band
 * HighestValueIndicator donchianUpper = new HighestValueIndicator(highPrice, 50);
 * 
 * // Breakout detection
 * Rule breakoutRule = new OverIndicatorRule(highPrice, highest20);
 * 
 * // Stochastic Oscillator component
 * HighestValueIndicator stochHigh = new HighestValueIndicator(highPrice, 14);
 * LowestValueIndicator stochLow = new LowestValueIndicator(lowPrice, 14);
 * 
 * // Dynamic resistance levels
 * SMAIndicator resistanceMA = new SMAIndicator(highest20, 10);
 * 
 * // Custom indicator highest values
 * RSIIndicator rsi = new RSIIndicator(closePrice, 14);
 * HighestValueIndicator highestRSI = new HighestValueIndicator(rsi, 10);
 * 
 * // Multi-timeframe analysis
 * HighestValueIndicator shortTerm = new HighestValueIndicator(highPrice, 5);
 * HighestValueIndicator longTerm = new HighestValueIndicator(highPrice, 50);
 * Rule newHighConfirmation = new OverIndicatorRule(shortTerm, longTerm);
 * 
 * // Volume analysis
 * VolumeIndicator volume = new VolumeIndicator(series);
 * HighestValueIndicator maxVolume = new HighestValueIndicator(volume, 20);
 * Rule highVolumeBreakout = breakoutRule.and(
 *     new OverIndicatorRule(volume, maxVolume.multipliedBy(0.8)));
 * 
 * // Stop loss placement
 * ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
 * HighestValueIndicator recentHigh = new HighestValueIndicator(closePrice, 10);
 * Rule stopLoss = new UnderIndicatorRule(closePrice, recentHigh.multipliedBy(0.95));
 * }</pre>
 * 
 * @see LowestValueIndicator
 * @see org.ta4j.core.indicators.helpers.HighPriceIndicator
 * @see org.ta4j.core.indicators.DonchianUpperIndicator
 * @see org.ta4j.core.indicators.StochasticOscillatorKIndicator
 * @since 0.1
 */
public class HighestValueIndicator extends CachedIndicator<Num> {

    private final Indicator<Num> indicator;
    private final int barCount;

    /**
     * Creates a Highest Value indicator over a specified lookback period.
     * 
     * <p>This constructor sets up a rolling maximum calculation that tracks the highest
     * value of the specified indicator over the most recent barCount periods. The indicator
     * will examine the current value plus the previous (barCount-1) values to determine
     * the maximum.
     * 
     * <p><strong>Performance Note:</strong> Each calculation requires scanning barCount values,
     * resulting in O(N) complexity where N = barCount. For high-frequency applications with
     * large periods, consider caching strategies or algorithm optimizations.
     *
     * @param indicator the indicator to find maximum values for (must not be null)
     * @param barCount  the number of periods to look back, typically 5-50 (must be > 0)
     * @throws IllegalArgumentException if indicator is null or barCount <= 0
     */
    public HighestValueIndicator(Indicator<Num> indicator, int barCount) {
        super(indicator);
        this.indicator = indicator;
        this.barCount = barCount;
    }

    @Override
    public Num calculate(int index) {
        if (indicator.getValue(index).isNaN() && barCount != 1) {
            return new HighestValueIndicator(indicator, barCount - 1).getValue(index - 1);
        }
        int end = Math.max(0, index - barCount + 1);
        Num highest = indicator.getValue(index);
        for (int i = index - 1; i >= end; i--) {
            if (highest.isLessThan(indicator.getValue(i))) {
                highest = indicator.getValue(i);
            }
        }
        return highest;
    }

    /**
     * Returns the number of unstable bars required for this indicator.
     * 
     * <p>The Highest Value indicator requires barCount periods to establish a complete
     * lookback window. During the initial barCount-1 bars, the indicator uses a shorter
     * window as fewer historical values are available.
     * 
     * <p>For example, with barCount=20, the first 19 bars will use progressively larger
     * windows (1, 2, 3, ... 19 periods) before reaching the full 20-period calculation.
     *
     * @return the number of periods required for stable calculation (equal to barCount)
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
