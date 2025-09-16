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

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

/**
 * Volume indicator for measuring trading activity and liquidity.
 * 
 * <p>The Volume indicator provides essential information about trading activity by measuring
 * the number of shares, contracts, or units traded during a specified period. Volume is
 * a fundamental component of technical analysis, offering insights into market participation,
 * trend strength, and price movement validity. It serves as the foundation for many
 * volume-based indicators and trading strategies.
 * 
 * <h2>Volume Analysis Fundamentals</h2>
 * <p>Volume represents the total trading activity and provides crucial context for price movements:
 * <ul>
 * <li><strong>Market Participation:</strong> Higher volume indicates greater market interest</li>
 * <li><strong>Trend Confirmation:</strong> Volume should increase in the direction of the trend</li>
 * <li><strong>Breakout Validation:</strong> Price breakouts with high volume are more reliable</li>
 * <li><strong>Reversal Signals:</strong> Extreme volume often coincides with trend reversals</li>
 * </ul>
 * 
 * <h2>Volume Types and Applications</h2>
 * <ul>
 * <li><strong>Single Period (barCount = 1):</strong>
 *     <ul>
 *     <li>Raw volume data for each individual bar</li>
 *     <li>Spike detection and unusual activity identification</li>
 *     <li>Volume-price relationship analysis</li>
 *     </ul>
 * </li>
 * <li><strong>Cumulative Volume (barCount > 1):</strong>
 *     <ul>
 *     <li>Total volume over multiple periods</li>
 *     <li>Smoothed volume trends and moving averages</li>
 *     <li>Volume accumulation/distribution patterns</li>
 *     </ul>
 * </li>
 * </ul>
 * 
 * <h2>Trading Applications</h2>
 * <ul>
 * <li><strong>Trend Confirmation:</strong>
 *     <ul>
 *     <li>Rising prices with increasing volume confirm uptrends</li>
 *     <li>Falling prices with increasing volume confirm downtrends</li>
 *     <li>Price moves without volume support are suspect</li>
 *     </ul>
 * </li>
 * <li><strong>Breakout Analysis:</strong>
 *     <ul>
 *     <li>High volume breakouts are more likely to continue</li>
 *     <li>Low volume breakouts often result in false signals</li>
 *     <li>Volume expansion indicates institutional participation</li>
 *     </ul>
 * </li>
 * <li><strong>Reversal Identification:</strong>
 *     <ul>
 *     <li>Climax volume often marks trend exhaustion</li>
 *     <li>Volume spikes at extremes suggest capitulation</li>
 *     <li>Declining volume in trends suggests weakening momentum</li>
 *     </ul>
 * </li>
 * <li><strong>Support/Resistance:</strong>
 *     <ul>
 *     <li>High volume at key levels validates support/resistance</li>
 *     <li>Volume confirmation of level breaks</li>
 *     <li>Institutional accumulation/distribution zones</li>
 *     </ul>
 * </li>
 * </ul>
 * 
 * <h2>Volume-Based Indicators</h2>
 * <ul>
 * <li><strong>Volume Moving Averages:</strong> Smooth volume trends and identify relative levels</li>
 * <li><strong>VWAP:</strong> Volume-weighted average price for institutional benchmarking</li>
 * <li><strong>OBV:</strong> On-Balance Volume for accumulation/distribution analysis</li>
 * <li><strong>MFI:</strong> Money Flow Index combining price and volume momentum</li>
 * <li><strong>Volume Rate of Change:</strong> Momentum of volume changes</li>
 * </ul>
 * 
 * <h2>Market Context Interpretation</h2>
 * <ul>
 * <li><strong>Accumulation Phase:</strong> Gradual volume increase with sideways price action</li>
 * <li><strong>Markup Phase:</strong> Rising prices with consistently high volume</li>
 * <li><strong>Distribution Phase:</strong> High volume with sideways or declining prices</li>
 * <li><strong>Markdown Phase:</strong> Declining prices with increasing volume</li>
 * </ul>
 * 
 * <h2>Volume Patterns and Signals</h2>
 * <ul>
 * <li><strong>Volume Spike:</strong> 2x+ average volume often signals important events</li>
 * <li><strong>Volume Dry-up:</strong> Extremely low volume may precede breakouts</li>
 * <li><strong>Volume Climax:</strong> Extreme volume often marks trend exhaustion</li>
 * <li><strong>Volume Divergence:</strong> Price/volume disagreement suggests reversal</li>
 * </ul>
 * 
 * <h2>Limitations and Considerations</h2>
 * <ul>
 * <li><strong>Market Hours:</strong> Volume varies significantly during different trading sessions</li>
 * <li><strong>Asset Class:</strong> Volume interpretation differs across stocks, forex, crypto</li>
 * <li><strong>Market Cap:</strong> Large cap vs small cap volume characteristics</li>
 * <li><strong>News Events:</strong> Volume spikes may be news-driven rather than technical</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Single period volume (raw volume data)
 * VolumeIndicator volume = new VolumeIndicator(series);
 * 
 * // 20-period cumulative volume
 * VolumeIndicator cumulativeVolume = new VolumeIndicator(series, 20);
 * 
 * // Volume moving average for comparison
 * SMAIndicator avgVolume = new SMAIndicator(volume, 50);
 * 
 * // High volume detection
 * Rule highVolume = new OverIndicatorRule(volume, avgVolume.multipliedBy(2.0));
 * Rule lowVolume = new UnderIndicatorRule(volume, avgVolume.multipliedBy(0.5));
 * 
 * // Volume-confirmed breakouts
 * ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
 * SMAIndicator sma20 = new SMAIndicator(closePrice, 20);
 * Rule priceBreakout = new CrossedUpIndicatorRule(closePrice, sma20);
 * Rule volumeBreakout = priceBreakout.and(highVolume);
 * 
 * // Volume trend analysis
 * SMAIndicator volumeMA10 = new SMAIndicator(volume, 10);
 * SMAIndicator volumeMA50 = new SMAIndicator(volume, 50);
 * Rule increasingVolumeTrend = new OverIndicatorRule(volumeMA10, volumeMA50);
 * 
 * // Volume-price divergence detection
 * HighestValueIndicator priceHigh = new HighestValueIndicator(closePrice, 20);
 * HighestValueIndicator volumeHigh = new HighestValueIndicator(volume, 20);
 * Rule newPriceHigh = new OverIndicatorRule(closePrice, priceHigh);
 * Rule lowerVolumeHigh = new UnderIndicatorRule(volume, volumeHigh.multipliedBy(0.8));
 * Rule bearishDivergence = newPriceHigh.and(lowerVolumeHigh);
 * 
 * // Accumulation/Distribution analysis
 * OnBalanceVolumeIndicator obv = new OnBalanceVolumeIndicator(series);
 * SMAIndicator obvMA = new SMAIndicator(obv, 20);
 * Rule accumulation = new OverIndicatorRule(obv, obvMA);
 * 
 * // Volume spike with price support
 * LowPriceIndicator lowPrice = new LowPriceIndicator(series);
 * LowestValueIndicator supportLevel = new LowestValueIndicator(lowPrice, 50);
 * Rule volumeSpike = new OverIndicatorRule(volume, avgVolume.multipliedBy(3.0));
 * Rule nearSupport = new UnderIndicatorRule(closePrice, supportLevel.multipliedBy(1.02));
 * Rule buyingClimax = volumeSpike.and(nearSupport);
 * 
 * // VWAP and volume relationship
 * VWAPIndicator vwap = new VWAPIndicator(series, 20);
 * Rule institutionalSupport = new OverIndicatorRule(closePrice, vwap).and(highVolume);
 * }</pre>
 * 
 * @see org.ta4j.core.indicators.volume.OnBalanceVolumeIndicator
 * @see org.ta4j.core.indicators.volume.VWAPIndicator
 * @see org.ta4j.core.indicators.volume.MoneyFlowIndexIndicator
 * @see org.ta4j.core.indicators.averages.SMAIndicator
 * @since 0.1
 */
public class VolumeIndicator extends CachedIndicator<Num> {

    private final int barCount;

    /**
     * Creates a Volume indicator for single-period (raw) volume data.
     * 
     * <p>This constructor creates a volume indicator that returns the trading volume
     * for each individual bar without any aggregation. This is the most common usage
     * for volume analysis, providing the raw volume data that can be used for:
     * <ul>
     * <li>Volume spike detection</li>
     * <li>Volume-price relationship analysis</li>
     * <li>Input to other volume-based indicators</li>
     * <li>Trend confirmation analysis</li>
     * </ul>
     *
     * @param series the bar series containing volume data (must not be null)
     * @throws IllegalArgumentException if series is null
     */
    public VolumeIndicator(BarSeries series) {
        this(series, 1);
    }

    /**
     * Creates a Volume indicator for cumulative volume over multiple periods.
     * 
     * <p>This constructor creates a volume indicator that sums the trading volume
     * over the specified number of periods. This cumulative approach is useful for:
     * <ul>
     * <li>Volume moving averages and smoothed trends</li>
     * <li>Total volume analysis over specific timeframes</li>
     * <li>Volume accumulation/distribution patterns</li>
     * <li>Reducing volume noise in analysis</li>
     * </ul>
     * 
     * <p><strong>Performance Note:</strong> The current implementation could be optimized
     * using partial sums for better performance in high-frequency scenarios.
     *
     * @param series   the bar series containing volume data (must not be null)
     * @param barCount the number of periods to sum volume over (must be > 0)
     * @throws IllegalArgumentException if series is null or barCount <= 0
     */
    public VolumeIndicator(BarSeries series, int barCount) {
        super(series);
        this.barCount = barCount;
    }

    @Override
    protected Num calculate(int index) {
        // TODO use partial sums
        int startIndex = Math.max(0, index - barCount + 1);
        Num sumOfVolume = getBarSeries().numFactory().zero();
        for (int i = startIndex; i <= index; i++) {
            sumOfVolume = sumOfVolume.plus(getBarSeries().getBar(i).getVolume());
        }
        return sumOfVolume;
    }

    /**
     * Returns the number of unstable bars required for this indicator.
     * 
     * <p>For single-period volume (barCount=1), the indicator is immediately stable.
     * For cumulative volume (barCount>1), the indicator requires barCount periods
     * to establish a complete sum window.
     * 
     * <p>During the initial unstable period, the indicator will sum available
     * volume data with progressively larger windows until the full period is reached.
     *
     * @return the number of periods required for stable calculation (equal to barCount)
     */
    @Override
    public int getCountOfUnstableBars() {
        return barCount;
    }
}
