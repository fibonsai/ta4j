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
import org.ta4j.core.indicators.helpers.CrossIndicator;
import org.ta4j.core.num.Num;

/**
 * A rule that detects when one indicator crosses above another indicator or threshold.
 * 
 * <p>This rule is fundamental for crossover trading strategies and is satisfied only at the
 * exact moment when the first indicator's value crosses from below to above the second indicator's
 * value. It detects the transition point rather than the ongoing state of being above.
 * 
 * <h2>Crossover Detection Logic</h2>
 * <p>The rule uses a {@link org.ta4j.core.indicators.helpers.CrossIndicator} internally to detect:
 * <ul>
 * <li><strong>Previous Bar:</strong> First indicator ≤ Second indicator</li>
 * <li><strong>Current Bar:</strong> First indicator > Second indicator</li>
 * <li><strong>Result:</strong> Rule is satisfied (true) only at the crossover moment</li>
 * </ul>
 * 
 * <h2>Common Trading Applications</h2>
 * <ul>
 * <li><strong>Moving Average Crossovers:</strong> Fast EMA crossing above slow EMA (golden cross)</li>
 * <li><strong>Price Breakouts:</strong> Price crossing above resistance level or moving average</li>
 * <li><strong>Oscillator Signals:</strong> RSI crossing above 30 (exit oversold), MACD crossing above signal line</li>
 * <li><strong>Momentum Confirmation:</strong> Stochastic %K crossing above %D line</li>
 * <li><strong>Volume Analysis:</strong> Volume crossing above average volume</li>
 * </ul>
 * 
 * <h2>Signal Characteristics</h2>
 * <ul>
 * <li><strong>Momentary Signal:</strong> Returns true only at the exact crossover bar</li>
 * <li><strong>No Persistence:</strong> Returns false immediately after the crossover bar</li>
 * <li><strong>Precise Timing:</strong> Captures the exact moment of trend change</li>
 * <li><strong>No False Persistence:</strong> Avoids continuous triggering while above</li>
 * </ul>
 * 
 * <h2>Advantages</h2>
 * <ul>
 * <li>Precise crossover detection without false continuation signals</li>
 * <li>Essential for trend-following and momentum strategies</li>
 * <li>Works with any combination of indicators and thresholds</li>
 * <li>Provides clear, unambiguous entry/exit signals</li>
 * <li>Computationally efficient</li>
 * </ul>
 * 
 * <h2>Limitations</h2>
 * <ul>
 * <li>Signals only last one bar, requiring immediate action</li>
 * <li>Can generate false signals in volatile, sideways markets</li>
 * <li>May produce whipsaws during choppy price action</li>
 * <li>Requires additional confirmation in trending markets</li>
 * </ul>
 * 
 * <h2>Best Practices</h2>
 * <ul>
 * <li><strong>Combine with Trend Filters:</strong> Use trend indicators to avoid counter-trend signals</li>
 * <li><strong>Volume Confirmation:</strong> Confirm with volume indicators for stronger signals</li>
 * <li><strong>Multiple Timeframes:</strong> Check higher timeframes for trend direction</li>
 * <li><strong>Signal Persistence:</strong> Use additional rules to maintain positions after crossover</li>
 * </ul>
 * 
 * <h2>Usage Examples</h2>
 * <pre>{@code
 * // Moving average crossover (Golden Cross)
 * ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
 * EMAIndicator ema12 = new EMAIndicator(closePrice, 12);
 * EMAIndicator ema26 = new EMAIndicator(closePrice, 26);
 * Rule goldenCross = new CrossedUpIndicatorRule(ema12, ema26);
 * 
 * // Price breakout above resistance
 * Rule breakoutSignal = new CrossedUpIndicatorRule(closePrice, 150.00);
 * 
 * // RSI exit from oversold
 * RSIIndicator rsi = new RSIIndicator(closePrice, 14);
 * Rule oversoldExit = new CrossedUpIndicatorRule(rsi, 30);
 * 
 * // MACD bullish crossover
 * MACDIndicator macd = new MACDIndicator(closePrice);
 * EMAIndicator signalLine = macd.getSignalLine(9);
 * Rule macdBullish = new CrossedUpIndicatorRule(macd, signalLine);
 * 
 * // Volume spike detection
 * VolumeIndicator volume = new VolumeIndicator(series);
 * SMAIndicator avgVolume = new SMAIndicator(volume, 20);
 * Rule volumeSpike = new CrossedUpIndicatorRule(volume, avgVolume.multipliedBy(1.5));
 * 
 * // Combined strategy with confirmation
 * Rule entryRule = goldenCross.and(new OverIndicatorRule(rsi, 50)).and(volumeSpike);
 * }</pre>
 * 
 * @see CrossedDownIndicatorRule
 * @see OverIndicatorRule
 * @see org.ta4j.core.indicators.helpers.CrossIndicator
 * @see org.ta4j.core.indicators.helpers.ConstantIndicator
 * @since 0.1
 */
public class CrossedUpIndicatorRule extends AbstractRule {

    /** The cross indicator */
    private final CrossIndicator cross;

    /**
     * Creates a rule that detects when an indicator crosses above a numeric threshold.
     * 
     * <p>This constructor automatically converts the Number threshold to the appropriate
     * {@link Num} type for comparison. The rule will be satisfied when the indicator
     * value crosses from at-or-below the threshold to above the threshold.
     * 
     * <p><strong>Example:</strong> Detect when price breaks above $100 resistance level.
     *
     * @param indicator the indicator to monitor for crossover (must not be null)
     * @param threshold the numeric threshold value to cross above
     * @throws IllegalArgumentException if indicator is null
     */
    public CrossedUpIndicatorRule(Indicator<Num> indicator, Number threshold) {
        this(indicator, indicator.getBarSeries().numFactory().numOf(threshold));
    }

    /**
     * Creates a rule that detects when an indicator crosses above a {@link Num} threshold.
     * 
     * <p>This constructor provides precise control over the threshold value using the
     * {@link Num} type. The rule detects the exact moment when the indicator transitions
     * from at-or-below the threshold to above it.
     *
     * @param indicator the indicator to monitor for crossover (must not be null)
     * @param threshold the {@link Num} threshold value to cross above (must not be null)
     * @throws IllegalArgumentException if indicator or threshold is null
     */
    public CrossedUpIndicatorRule(Indicator<Num> indicator, Num threshold) {
        this(indicator, new ConstantIndicator<>(indicator.getBarSeries(), threshold));
    }

    /**
     * Creates a rule that detects when the first indicator crosses above the second indicator.
     * 
     * <p>This is the most general constructor that enables crossover detection between any two
     * indicators. The rule is satisfied when the first indicator transitions from at-or-below
     * the second indicator to above it.
     * 
     * <p><strong>Common Examples:</strong>
     * <ul>
     * <li>Fast EMA crossing above slow EMA (trend reversal)</li>
     * <li>Price crossing above moving average (breakout)</li>
     * <li>MACD crossing above signal line (momentum shift)</li>
     * <li>Stochastic %K crossing above %D (oversold recovery)</li>
     * </ul>
     *
     * @param first  the indicator that should cross above (must not be null)
     * @param second the indicator to cross above (must not be null)
     * @throws IllegalArgumentException if either indicator is null
     */
    public CrossedUpIndicatorRule(Indicator<Num> first, Indicator<Num> second) {
        this.cross = new CrossIndicator(second, first);
    }

    /**
     * Evaluates whether a crossover occurred at the given index.
     * 
     * <p>This method uses the internal {@link org.ta4j.core.indicators.helpers.CrossIndicator}
     * to determine if the first indicator crossed above the second indicator at the specified
     * bar index. The crossover is detected by comparing the current and previous relationships
     * between the two indicators.
     * 
     * <p><strong>Note:</strong> This rule does not use the trading record parameter as it
     * only performs indicator-based crossover detection without considering trade history.
     *
     * @param index         the bar index to evaluate (must be valid for both indicators)
     * @param tradingRecord unused by this rule (may be null)
     * @return true if a crossover occurred at this index, false otherwise
     * @throws IndexOutOfBoundsException if index is outside valid range for either indicator
     */
    @Override
    public boolean isSatisfied(int index, TradingRecord tradingRecord) {
        final boolean satisfied = cross.getValue(index);
        traceIsSatisfied(index, satisfied);
        return satisfied;
    }

    /**
     * Returns the indicator that was initially below (the one being crossed above).
     * 
     * <p>This is the second indicator provided to the constructor, which acts as the
     * reference level or threshold that the first indicator must cross above.
     * In crossover terminology, this is often called the "slow" or "reference" indicator.
     *
     * @return the lower/reference indicator in the crossover relationship
     */
    public Indicator<Num> getLow() {
        return cross.getLow();
    }

    /**
     * Returns the indicator that crosses above (the one doing the crossing).
     * 
     * <p>This is the first indicator provided to the constructor, which is monitored
     * for crossing above the second indicator. In crossover terminology, this is often
     * called the "fast" or "signal" indicator.
     *
     * @return the upper/crossing indicator in the crossover relationship
     */
    public Indicator<Num> getUp() {
        return cross.getUp();
    }
}
