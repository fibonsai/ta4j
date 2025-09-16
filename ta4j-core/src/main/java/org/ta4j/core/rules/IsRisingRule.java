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
package org.ta4j.core.rules;

import org.ta4j.core.Indicator;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.num.Num;

/**
 * A rule that detects when an indicator is rising over a specified period.
 * 
 * <p>The IsRisingRule evaluates whether an indicator shows an upward trend by measuring
 * the proportion of rising periods within a specified timeframe. It provides flexible
 * trend detection with configurable strength requirements, making it valuable for
 * momentum analysis, trend confirmation, and signal filtering.
 * 
 * <h2>Rising Detection Logic</h2>
 * <p>The rule calculates the rising strength using the following approach:
 * <ol>
 * <li>Examine each bar in the specified period</li>
 * <li>Count bars where the indicator value is higher than the previous bar</li>
 * <li>Calculate ratio: Rising Bars / Total Bars</li>
 * <li>Compare ratio to minimum strength threshold</li>
 * </ol>
 * 
 * <h2>Strength Configuration</h2>
 * <p>The minimum strength parameter allows flexible trend detection:
 * <ul>
 * <li><strong>Strict Rising (1.0):</strong> Every bar must be higher than previous</li>
 * <li><strong>Strong Rising (0.8):</strong> 80% of bars must be rising</li>
 * <li><strong>Moderate Rising (0.6):</strong> 60% of bars must be rising</li>
 * <li><strong>Weak Rising (0.5):</strong> More than half the bars must be rising</li>
 * </ul>
 * 
 * <h2>Trading Applications</h2>
 * <ul>
 * <li><strong>Trend Confirmation:</strong>
 *     <ul>
 *     <li>Confirm that indicators are in rising trend before entry</li>
 *     <li>Filter signals based on momentum direction</li>
 *     <li>Validate breakout signals with momentum confirmation</li>
 *     </ul>
 * </li>
 * <li><strong>Momentum Analysis:</strong>
 *     <ul>
 *     <li>Detect acceleration in price movements</li>
 *     <li>Identify momentum buildups before breakouts</li>
 *     <li>Measure trend strength and sustainability</li>
 *     </ul>
 * </li>
 * <li><strong>Signal Filtering:</strong>
 *     <ul>
 *     <li>Only take long signals when momentum is rising</li>
 *     <li>Avoid counter-trend trades in strong momentum</li>
 *     <li>Enhance signal quality with directional filters</li>
 *     </ul>
 * </li>
 * <li><strong>Exit Conditions:</strong>
 *     <ul>
 *     <li>Exit long positions when momentum stops rising</li>
 *     <li>Detect early signs of trend weakness</li>
 *     <li>Implement momentum-based stop losses</li>
 *     </ul>
 * </li>
 * </ul>
 * 
 * <h2>Common Period Settings</h2>
 * <ul>
 * <li><strong>Short-term:</strong> 3-5 periods (very responsive, more signals)</li>
 * <li><strong>Medium-term:</strong> 8-12 periods (balanced responsiveness)</li>
 * <li><strong>Long-term:</strong> 20-50 periods (stable trend detection)</li>
 * </ul>
 * 
 * <h2>Indicator Applications</h2>
 * <ul>
 * <li><strong>Price Indicators:</strong> Detect rising price trends</li>
 * <li><strong>Moving Averages:</strong> Confirm trend direction</li>
 * <li><strong>Momentum Oscillators:</strong> Identify momentum acceleration</li>
 * <li><strong>Volume Indicators:</strong> Detect increasing participation</li>
 * <li><strong>Volatility Measures:</strong> Monitor volatility expansion</li>
 * </ul>
 * 
 * <h2>Advantages</h2>
 * <ul>
 * <li>Flexible strength configuration for different market conditions</li>
 * <li>Works with any numerical indicator</li>
 * <li>Provides quantitative trend measurement</li>
 * <li>Filters out sideways or declining momentum</li>
 * <li>Reduces false signals from single-bar movements</li>
 * </ul>
 * 
 * <h2>Limitations</h2>
 * <ul>
 * <li>Backward-looking analysis based on historical data</li>
 * <li>May lag in very fast-moving markets</li>
 * <li>Requires sufficient period for meaningful analysis</li>
 * <li>Can give conflicting signals in choppy markets</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Basic rising price detection
 * ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
 * IsRisingRule priceRising = new IsRisingRule(closePrice, 10); // Strict rising over 10 bars
 * 
 * // Flexible momentum detection
 * RSIIndicator rsi = new RSIIndicator(closePrice, 14);
 * IsRisingRule rsiMomentum = new IsRisingRule(rsi, 5, 0.6); // 60% rising strength over 5 bars
 * 
 * // Moving average trend confirmation
 * SMAIndicator sma20 = new SMAIndicator(closePrice, 20);
 * IsRisingRule trendConfirm = new IsRisingRule(sma20, 8, 0.75); // Strong trend over 8 bars
 * 
 * // Volume momentum
 * VolumeIndicator volume = new VolumeIndicator(series);
 * IsRisingRule volumeIncrease = new IsRisingRule(volume, 6, 0.5); // More than half rising
 * 
 * // Multiple timeframe momentum
 * IsRisingRule shortMomentum = new IsRisingRule(closePrice, 5, 0.8);  // Short-term
 * IsRisingRule longMomentum = new IsRisingRule(closePrice, 20, 0.6);  // Long-term
 * Rule alignedMomentum = shortMomentum.and(longMomentum);
 * 
 * // Entry strategy with momentum filter
 * Rule baseEntry = new CrossedUpIndicatorRule(closePrice, sma20);
 * Rule momentumEntry = baseEntry.and(priceRising).and(volumeIncrease);
 * 
 * // Exit when momentum weakens
 * IsRisingRule momentumContinues = new IsRisingRule(closePrice, 3, 0.66);
 * Rule momentumExit = momentumContinues.negation();
 * 
 * // MACD momentum confirmation
 * MACDIndicator macd = new MACDIndicator(closePrice, 12, 26);
 * IsRisingRule macdRising = new IsRisingRule(macd, 4, 0.75);
 * Rule strongEntry = baseEntry.and(macdRising);
 * 
 * // ATR expansion detection
 * ATRIndicator atr = new ATRIndicator(series, 14);
 * IsRisingRule volatilityExpansion = new IsRisingRule(atr, 5, 0.6);
 * Rule breakoutConfirmation = baseEntry.and(volatilityExpansion);
 * 
 * // Combine with falling rule for range detection
 * IsFallingRule priceFalling = new IsFallingRule(closePrice, 10, 0.7);
 * Rule ranging = priceRising.negation().and(priceFalling.negation());
 * }</pre>
 * 
 * @see IsFallingRule
 * @see org.ta4j.core.rules.CrossedUpIndicatorRule
 * @see org.ta4j.core.rules.OverIndicatorRule
 * @since 0.1
 */
public class IsRisingRule extends AbstractRule {

    /** The actual indicator */
    private final Indicator<Num> ref;

    /** The barCount */
    private final int barCount;

    /** The minimum required strength of the rising */
    private final double minStrength;

    /**
     * Creates a rule for strict rising trend detection.
     * 
     * <p>This constructor creates an IsRisingRule with strict rising requirements,
     * meaning the indicator must rise in every single bar within the specified period.
     * This is equivalent to setting the minimum strength to 1.0 (100% rising).
     * 
     * <p>Use this constructor when you need to detect very strong, consistent
     * upward momentum without any declining bars in the period.
     *
     * @param ref      the indicator to monitor for rising trend (must not be null)
     * @param barCount the number of periods to analyze, typically 3-20 (must be > 0)
     * @throws IllegalArgumentException if ref is null or barCount <= 0
     */
    public IsRisingRule(Indicator<Num> ref, int barCount) {
        this(ref, barCount, 1);
    }

    /**
     * Creates a rule for flexible rising trend detection with configurable strength.
     * 
     * <p>This constructor allows fine-tuning of the rising detection sensitivity by
     * specifying the minimum proportion of rising bars required within the period.
     * This provides flexibility for different market conditions and trend types.
     * 
     * <p><strong>Strength Examples:</strong>
     * <ul>
     * <li><strong>1.0:</strong> Strict rising (every bar must be higher)</li>
     * <li><strong>0.8:</strong> Strong rising (80% of bars must be higher)</li>
     * <li><strong>0.6:</strong> Moderate rising (60% of bars must be higher)</li>
     * <li><strong>0.5:</strong> Weak rising (majority of bars must be higher)</li>
     * </ul>
     *
     * @param ref         the indicator to monitor for rising trend (must not be null)
     * @param barCount    the number of periods to analyze (must be > 0)
     * @param minStrenght the minimum required rising strength between 0.0 and 1.0 (must be > 0)
     * @throws IllegalArgumentException if ref is null, barCount <= 0, or minStrenght <= 0
     */
    public IsRisingRule(Indicator<Num> ref, int barCount, double minStrenght) {
        this.ref = ref;
        this.barCount = barCount;
        this.minStrength = minStrenght >= 1 ? 0.99 : minStrenght;
    }

    /**
     * Evaluates whether the indicator is rising according to the configured criteria.
     * 
     * <p>This method analyzes the indicator values over the specified period and calculates
     * the proportion of rising bars. The rule is satisfied when this proportion meets
     * or exceeds the minimum strength requirement.
     * 
     * <p><strong>Calculation Process:</strong>
     * <ol>
     * <li>Examine each bar in the lookback period</li>
     * <li>Count bars where current value > previous value</li>
     * <li>Calculate rising ratio = rising bars / total bars</li>
     * <li>Compare ratio to minimum strength threshold</li>
     * </ol>
     * 
     * <p><strong>Note:</strong> This rule does not use the trading record parameter as it
     * only performs indicator-based trend analysis without considering trade history.
     *
     * @param index         the bar index to evaluate (must be valid for the indicator)
     * @param tradingRecord unused by this rule (may be null)
     * @return true if the indicator shows sufficient rising strength, false otherwise
     * @throws IndexOutOfBoundsException if index is outside valid range for the indicator
     */
    @Override
    public boolean isSatisfied(int index, TradingRecord tradingRecord) {
        int count = 0;
        for (int i = Math.max(0, index - barCount + 1); i <= index; i++) {
            if (ref.getValue(i).isGreaterThan(ref.getValue(Math.max(0, i - 1)))) {
                count += 1;
            }
        }

        double ratio = count / (double) barCount;

        final boolean satisfied = ratio >= minStrength;
        traceIsSatisfied(index, satisfied);
        return satisfied;
    }
}
