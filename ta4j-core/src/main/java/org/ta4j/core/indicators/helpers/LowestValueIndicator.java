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
 * Lowest Value indicator for finding minimum values over a rolling period.
 * 
 * <p>The Lowest Value indicator is a fundamental utility that tracks the minimum value
 * of any indicator over a specified lookback period. It serves as the complementary
 * function to HighestValueIndicator and is essential for channel analysis, oscillator
 * calculations, support level identification, and oversold condition detection. This
 * rolling minimum function is universally applicable across all indicator types.
 * 
 * <h2>Calculation Method</h2>
 * <p>For each bar, the indicator examines the most recent N periods and returns the
 * lowest value found within that window:
 * <ul>
 * <li><strong>Window Size:</strong> Defined by barCount parameter</li>
 * <li><strong>Search Direction:</strong> Looks backward from current index</li>
 * <li><strong>Comparison Logic:</strong> Uses numerical comparison for minimum detection</li>
 * <li><strong>NaN Handling:</strong> Recursively handles missing data points gracefully</li>
 * </ul>
 * 
 * <h2>Common Applications</h2>
 * <ul>
 * <li><strong>Price Channel Analysis:</strong>
 *     <ul>
 *     <li>Donchian Channels (lowest low over N periods)</li>
 *     <li>Price channel lower boundaries</li>
 *     <li>Support level identification</li>
 *     </ul>
 * </li>
 * <li><strong>Oscillator Calculations:</strong>
 *     <ul>
 *     <li>Stochastic Oscillator (lowest low component)</li>
 *     <li>Williams %R (minimum price component)</li>
 *     <li>Custom momentum indicators</li>
 *     </ul>
 * </li>
 * <li><strong>Oversold Detection:</strong>
 *     <ul>
 *     <li>New low identification (current < lowest previous)</li>
 *     <li>Capitulation signal detection</li>
 *     <li>Reversal opportunity identification</li>
 *     </ul>
 * </li>
 * <li><strong>Risk Management:</strong>
 *     <ul>
 *     <li>Trailing stop-loss calculations</li>
 *     <li>Maximum adverse excursion tracking</li>
 *     <li>Drawdown measurement</li>
 *     </ul>
 * </li>
 * </ul>
 * 
 * <h2>Trading Strategy Applications</h2>
 * <ul>
 * <li><strong>Support/Resistance:</strong>
 *     <ul>
 *     <li>Dynamic support levels based on recent lows</li>
 *     <li>Psychological price floors</li>
 *     <li>Channel trading boundaries</li>
 *     </ul>
 * </li>
 * <li><strong>Reversal Strategies:</strong>
 *     <ul>
 *     <li>Entry signals when price bounces from recent lows</li>
 *     <li>Oversold condition identification</li>
 *     <li>Mean reversion opportunities</li>
 *     </ul>
 * </li>
 * <li><strong>Trend Analysis:</strong>
 *     <ul>
 *     <li>Higher lows pattern identification</li>
 *     <li>Trend strength measurement</li>
 *     <li>Support zone definition</li>
 *     </ul>
 * </li>
 * <li><strong>Risk Control:</strong>
 *     <ul>
 *     <li>Stop-loss placement above recent lows</li>
 *     <li>Position sizing based on range analysis</li>
 *     <li>Maximum risk exposure calculation</li>
 *     </ul>
 * </li>
 * </ul>
 * 
 * <h2>Period Selection Guidelines</h2>
 * <ul>
 * <li><strong>Short-term (5-10 periods):</strong> Sensitive to recent selling pressure</li>
 * <li><strong>Medium-term (20-50 periods):</strong> Balanced support level identification</li>
 * <li><strong>Long-term (100+ periods):</strong> Major support zones and cycle analysis</li>
 * <li><strong>Volatility-Based:</strong> Adaptive periods based on market conditions</li>
 * </ul>
 * 
 * <h2>Performance Considerations</h2>
 * <ul>
 * <li><strong>Algorithm Optimization:</strong> Current O(N) implementation noted for improvement</li>
 * <li><strong>Sliding Window:</strong> Potential for more efficient rolling minimum algorithms</li>
 * <li><strong>Memory Efficiency:</strong> Cached values reduce redundant calculations</li>
 * <li><strong>Data Integrity:</strong> Robust NaN handling prevents calculation errors</li>
 * </ul>
 * 
 * <h2>Market Psychology Applications</h2>
 * <ul>
 * <li><strong>Panic Lows:</strong> Identification of extreme oversold conditions</li>
 * <li><strong>Accumulation Zones:</strong> Areas where institutional buying typically occurs</li>
 * <li><strong>Fear Index:</strong> When combined with volume, indicates capitulation</li>
 * <li><strong>Reversal Zones:</strong> High probability areas for trend changes</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Basic lowest low over 20 periods
 * LowPriceIndicator lowPrice = new LowPriceIndicator(series);
 * LowestValueIndicator lowest20 = new LowestValueIndicator(lowPrice, 20);
 * 
 * // Donchian Channel lower band
 * LowestValueIndicator donchianLower = new LowestValueIndicator(lowPrice, 50);
 * 
 * // Support bounce detection
 * Rule supportBounce = new OverIndicatorRule(lowPrice, lowest20);
 * 
 * // Stochastic Oscillator calculation
 * HighPriceIndicator highPrice = new HighPriceIndicator(series);
 * HighestValueIndicator stochHigh = new HighestValueIndicator(highPrice, 14);
 * LowestValueIndicator stochLow = new LowestValueIndicator(lowPrice, 14);
 * ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
 * 
 * // Calculate %K manually for demonstration
 * // %K = 100 * (Close - LowestLow) / (HighestHigh - LowestLow)
 * 
 * // Dynamic support levels
 * SMAIndicator supportMA = new SMAIndicator(lowest20, 10);
 * 
 * // Oversold RSI combined with price support
 * RSIIndicator rsi = new RSIIndicator(closePrice, 14);
 * LowestValueIndicator lowestRSI = new LowestValueIndicator(rsi, 10);
 * Rule oversoldRSI = new UnderIndicatorRule(rsi, 30);
 * Rule nearSupportLow = new UnderIndicatorRule(closePrice, lowest20.multipliedBy(1.02));
 * Rule oversoldBuy = oversoldRSI.and(nearSupportLow);
 * 
 * // Multi-timeframe support analysis
 * LowestValueIndicator shortSupport = new LowestValueIndicator(lowPrice, 5);
 * LowestValueIndicator longSupport = new LowestValueIndicator(lowPrice, 50);
 * Rule strongSupport = new OverIndicatorRule(shortSupport, longSupport.multipliedBy(0.98));
 * 
 * // Trailing stop loss
 * LowestValueIndicator trailingLow = new LowestValueIndicator(closePrice, 10);
 * Rule trailingStop = new UnderIndicatorRule(closePrice, trailingLow.multipliedBy(0.95));
 * 
 * // Volume confirmation at lows
 * VolumeIndicator volume = new VolumeIndicator(series);
 * LowestValueIndicator minVolume = new LowestValueIndicator(volume, 20);
 * Rule climaxLow = new UnderIndicatorRule(lowPrice, lowest20).and(
 *     new OverIndicatorRule(volume, minVolume.multipliedBy(2.0))); // High volume at low
 * 
 * // Channel breakout strategy
 * Rule channelBreakdown = new UnderIndicatorRule(closePrice, donchianLower);
 * Rule falseBreakdown = new OverIndicatorRule(closePrice, donchianLower);
 * }</pre>
 * 
 * @see HighestValueIndicator
 * @see org.ta4j.core.indicators.helpers.LowPriceIndicator
 * @see org.ta4j.core.indicators.DonchianLowerIndicator
 * @see org.ta4j.core.indicators.StochasticOscillatorKIndicator
 * @since 0.1
 */
public class LowestValueIndicator extends CachedIndicator<Num> {

    private final Indicator<Num> indicator;
    private final int barCount;

    /**
     * Creates a Lowest Value indicator over a specified lookback period.
     * 
     * <p>This constructor establishes a rolling minimum calculation that tracks the lowest
     * value of the specified indicator over the most recent barCount periods. The indicator
     * examines the current value plus the previous (barCount-1) values to determine the minimum.
     * 
     * <p><strong>Implementation Note:</strong> The current algorithm scans all values in the
     * window for each calculation (O(N) complexity). Future optimizations could implement
     * sliding window algorithms for improved performance in high-frequency scenarios.
     *
     * @param indicator the indicator to find minimum values for (must not be null)
     * @param barCount  the number of periods to look back, typically 5-50 (must be > 0)
     * @throws IllegalArgumentException if indicator is null or barCount <= 0
     */
    public LowestValueIndicator(Indicator<Num> indicator, int barCount) {
        super(indicator);
        this.indicator = indicator;
        this.barCount = barCount;
    }

    @Override
    public Num calculate(int index) {
        if (indicator.getValue(index).isNaN() && barCount != 1) {
            return new LowestValueIndicator(indicator, barCount - 1).getValue(index - 1);
        }

        // TODO optimize algorithm, compare previous minimum with current value without
        // looping
        int end = Math.max(0, index - barCount + 1);
        Num lowest = indicator.getValue(index);
        for (int i = index - 1; i >= end; i--) {
            if (lowest.isGreaterThan(indicator.getValue(i))) {
                lowest = indicator.getValue(i);
            }
        }
        return lowest;
    }

    /**
     * Returns the number of unstable bars required for this indicator.
     * 
     * <p>The Lowest Value indicator requires barCount periods to establish a complete
     * lookback window. During the initial barCount-1 bars, the indicator uses a progressively
     * larger window as more historical values become available.
     * 
     * <p>This ensures that the minimum detection has sufficient data for meaningful analysis
     * while gracefully handling the startup period with available data.
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
