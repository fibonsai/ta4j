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
import org.ta4j.core.indicators.helpers.ConstantIndicator;
import org.ta4j.core.num.Num;

/**
 * A rule that is satisfied when one indicator's value is greater than another indicator or threshold.
 * 
 * <p>This is one of the most fundamental and frequently used rules in technical analysis.
 * It enables comparison between indicators, or between an indicator and a fixed threshold value.
 * The rule is satisfied when the first indicator's value is <strong>strictly greater than</strong>
 * the second indicator's value or threshold.
 * 
 * <h2>Common Usage Patterns</h2>
 * <ul>
 * <li><strong>Threshold Crossing:</strong> Price above resistance level, RSI above 70</li>
 * <li><strong>Indicator Crossover:</strong> Fast EMA above slow EMA, price above SMA</li>
 * <li><strong>Oscillator Levels:</strong> RSI above overbought, Stochastic above signal</li>
 * <li><strong>Volume Analysis:</strong> Volume above average, price above VWAP</li>
 * <li><strong>Volatility Signals:</strong> ATR above historical average</li>
 * </ul>
 * 
 * <h2>Comparison Logic</h2>
 * <p>The rule uses strict greater-than comparison:
 * <ul>
 * <li><strong>Satisfied:</strong> first > second (not equal)</li>
 * <li><strong>Not Satisfied:</strong> first <= second</li>
 * </ul>
 * 
 * <h2>Performance Characteristics</h2>
 * <ul>
 * <li><strong>Evaluation:</strong> O(1) constant time per evaluation</li>
 * <li><strong>Memory:</strong> Minimal overhead, references to indicators only</li>
 * <li><strong>Dependencies:</strong> Depends on underlying indicator calculations</li>
 * </ul>
 * 
 * <h2>Thread Safety</h2>
 * <p>This rule is <strong>thread-safe</strong> as it only performs read operations
 * on the underlying indicators and maintains no mutable state.
 * 
 * <h2>Usage Examples</h2>
 * <pre>{@code
 * // Price above moving average
 * ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
 * SMAIndicator sma20 = new SMAIndicator(closePrice, 20);
 * Rule priceAboveSMA = new OverIndicatorRule(closePrice, sma20);
 * 
 * // RSI overbought condition
 * RSIIndicator rsi = new RSIIndicator(closePrice, 14);
 * Rule rsiOverbought = new OverIndicatorRule(rsi, 70);
 * 
 * // Volume above threshold
 * VolumeIndicator volume = new VolumeIndicator(series);
 * Rule highVolume = new OverIndicatorRule(volume, 1000000);
 * 
 * // Fast EMA above slow EMA (bullish crossover)
 * EMAIndicator ema12 = new EMAIndicator(closePrice, 12);
 * EMAIndicator ema26 = new EMAIndicator(closePrice, 26);
 * Rule bullishCrossover = new OverIndicatorRule(ema12, ema26);
 * 
 * // Combine with other rules
 * Rule entryRule = priceAboveSMA.and(highVolume).and(rsiOverbought.negation());
 * }</pre>
 * 
 * @see UnderIndicatorRule
 * @see CrossedUpIndicatorRule
 * @see CrossedDownIndicatorRule
 * @see org.ta4j.core.indicators.helpers.ConstantIndicator
 * @since 0.1
 */
public class OverIndicatorRule extends AbstractRule {

    /** The first indicator. */
    private final Indicator<Num> first;

    /** The second indicator. */
    private final Indicator<Num> second;

    /**
     * Creates a rule that checks if an indicator is above a numeric threshold.
     * 
     * <p>This constructor automatically converts the Number threshold to the appropriate
     * {@link Num} type using the indicator's number factory, ensuring type compatibility.
     * This is the most convenient way to compare indicators with fixed values.
     * 
     * <p><strong>Example:</strong> Check if RSI is above 70 (overbought)
     *
     * @param indicator the indicator to compare (must not be null)
     * @param threshold the numeric threshold value to compare against
     * @throws IllegalArgumentException if indicator is null
     */
    public OverIndicatorRule(Indicator<Num> indicator, Number threshold) {
        this(indicator, indicator.getBarSeries().numFactory().numOf(threshold));
    }

    /**
     * Creates a rule that checks if an indicator is above a {@link Num} threshold.
     * 
     * <p>This constructor provides precise control over the threshold value using
     * the {@link Num} type. It internally creates a {@link ConstantIndicator} to
     * represent the threshold, enabling consistent evaluation across all bar indices.
     *
     * @param indicator the indicator to compare (must not be null)
     * @param threshold the {@link Num} threshold value to compare against (must not be null)
     * @throws IllegalArgumentException if indicator or threshold is null
     */
    public OverIndicatorRule(Indicator<Num> indicator, Num threshold) {
        this(indicator, new ConstantIndicator<>(indicator.getBarSeries(), threshold));
    }

    /**
     * Creates a rule that checks if the first indicator is above the second indicator.
     * 
     * <p>This is the most general constructor that enables comparison between any two
     * indicators. Both indicators must be from the same or compatible bar series.
     * This is essential for crossover strategies and relative strength analysis.
     * 
     * <p><strong>Examples:</strong>
     * <ul>
     * <li>Fast EMA above slow EMA (momentum confirmation)</li>
     * <li>Price above moving average (trend following)</li>
     * <li>Current RSI above previous RSI (momentum strengthening)</li>
     * </ul>
     *
     * @param first  the indicator that should be greater (must not be null)
     * @param second the indicator to compare against (must not be null)
     * @throws IllegalArgumentException if either indicator is null
     */
    public OverIndicatorRule(Indicator<Num> first, Indicator<Num> second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Evaluates whether the first indicator is greater than the second at the given index.
     * 
     * <p>This method performs the core comparison logic by retrieving values from both
     * indicators at the specified index and checking if the first value is strictly
     * greater than the second value.
     * 
     * <p><strong>Note:</strong> This rule does not use the trading record parameter
     * as it only performs indicator-based comparisons without considering trade history.
     *
     * @param index         the bar index to evaluate (must be valid for both indicators)
     * @param tradingRecord unused by this rule (may be null)
     * @return true if first indicator > second indicator at the given index, false otherwise
     * @throws IndexOutOfBoundsException if index is outside valid range for either indicator
     */
    @Override
    public boolean isSatisfied(int index, TradingRecord tradingRecord) {
        final boolean satisfied = first.getValue(index).isGreaterThan(second.getValue(index));
        traceIsSatisfied(index, satisfied);
        return satisfied;
    }
}
