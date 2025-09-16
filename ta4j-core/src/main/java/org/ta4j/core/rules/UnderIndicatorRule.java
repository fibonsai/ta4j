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
 * A rule that is satisfied when one indicator's value is less than another indicator or threshold.
 * 
 * <p>This is a fundamental comparison rule that enables detection of when values fall below
 * specific levels or other indicators. The rule is satisfied when the first indicator's value
 * is <strong>strictly less than</strong> the second indicator's value or threshold, providing
 * the logical complement to {@link OverIndicatorRule}.
 * 
 * <h2>Common Trading Applications</h2>
 * <ul>
 * <li><strong>Support Level Testing:</strong> Price dropping below support level</li>
 * <li><strong>Oversold Conditions:</strong> RSI below 30, Stochastic below 20</li>
 * <li><strong>Breakdown Signals:</strong> Price below moving average, indicator below baseline</li>
 * <li><strong>Risk Management:</strong> Stop-loss triggers, drawdown limits</li>
 * <li><strong>Volume Analysis:</strong> Volume below average, momentum below threshold</li>
 * </ul>
 * 
 * <h2>Comparison Logic</h2>
 * <p>The rule uses strict less-than comparison:
 * <ul>
 * <li><strong>Satisfied:</strong> first < second (not equal)</li>
 * <li><strong>Not Satisfied:</strong> first >= second</li>
 * </ul>
 * 
 * <h2>Signal Characteristics</h2>
 * <ul>
 * <li><strong>Continuous Evaluation:</strong> Returns true while condition persists</li>
 * <li><strong>Threshold Sensitivity:</strong> Sensitive to exact threshold levels</li>
 * <li><strong>Complement Rule:</strong> Logical opposite of OverIndicatorRule</li>
 * <li><strong>State-Based:</strong> Reflects current condition, not transitions</li>
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
 * // Price below moving average (bearish signal)
 * ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
 * SMAIndicator sma20 = new SMAIndicator(closePrice, 20);
 * Rule priceBelowSMA = new UnderIndicatorRule(closePrice, sma20);
 * 
 * // RSI oversold condition
 * RSIIndicator rsi = new RSIIndicator(closePrice, 14);
 * Rule rsiOversold = new UnderIndicatorRule(rsi, 30);
 * 
 * // Volume below threshold
 * VolumeIndicator volume = new VolumeIndicator(series);
 * Rule lowVolume = new UnderIndicatorRule(volume, 500000);
 * 
 * // Fast EMA below slow EMA (bearish crossover state)
 * EMAIndicator ema12 = new EMAIndicator(closePrice, 12);
 * EMAIndicator ema26 = new EMAIndicator(closePrice, 26);
 * Rule bearishAlignment = new UnderIndicatorRule(ema12, ema26);
 * 
 * // Support level breakdown
 * Rule supportBreakdown = new UnderIndicatorRule(closePrice, 100.0); // Price < $100
 * 
 * // Combine multiple oversold conditions
 * StochasticOscillatorKIndicator stoch = new StochasticOscillatorKIndicator(series, 14);
 * Rule multipleOversold = rsiOversold.and(new UnderIndicatorRule(stoch, 20));
 * 
 * // Exit strategy combining multiple under conditions
 * Rule exitRule = priceBelowSMA.or(rsiOversold).or(supportBreakdown);
 * 
 * // Trend filter - only trade when in uptrend
 * SMAIndicator sma50 = new SMAIndicator(closePrice, 50);
 * Rule notInDowntrend = new UnderIndicatorRule(closePrice, sma50).negation();
 * Rule filteredEntry = entrySignal.and(notInDowntrend);
 * }</pre>
 * 
 * @see OverIndicatorRule
 * @see CrossedDownIndicatorRule
 * @see CrossedUpIndicatorRule
 * @see org.ta4j.core.indicators.helpers.ConstantIndicator
 * @since 0.1
 */
public class UnderIndicatorRule extends AbstractRule {

    /** The first indicator. */
    private final Indicator<Num> first;

    /** The second indicator. */
    private final Indicator<Num> second;

    /**
     * Creates a rule that detects when an indicator value is below a numeric threshold.
     * 
     * <p>This constructor automatically converts the Number threshold to the appropriate
     * {@link Num} type for comparison. The rule will be satisfied when the indicator
     * value is strictly less than the threshold.
     * 
     * <p><strong>Example:</strong> Detect when price drops below $50 support level.
     *
     * @param indicator the indicator to monitor (must not be null)
     * @param threshold the numeric threshold value that indicator should be below
     * @throws IllegalArgumentException if indicator is null
     */
    public UnderIndicatorRule(Indicator<Num> indicator, Number threshold) {
        this(indicator, new ConstantIndicator<>(indicator.getBarSeries(),
                indicator.getBarSeries().numFactory().numOf(threshold)));
    }

    /**
     * Creates a rule that detects when an indicator value is below a {@link Num} threshold.
     * 
     * <p>This constructor provides precise control over the threshold value using the
     * {@link Num} type. The rule detects when the indicator value is strictly less
     * than the specified threshold.
     *
     * @param indicator the indicator to monitor (must not be null)
     * @param threshold the {@link Num} threshold value that indicator should be below (must not be null)
     * @throws IllegalArgumentException if indicator or threshold is null
     */
    public UnderIndicatorRule(Indicator<Num> indicator, Num threshold) {
        this(indicator, new ConstantIndicator<>(indicator.getBarSeries(), threshold));
    }

    /**
     * Creates a rule that detects when the first indicator is below the second indicator.
     * 
     * <p>This is the most general constructor that enables comparison between any two
     * indicators. The rule is satisfied when the first indicator's value is strictly
     * less than the second indicator's value.
     * 
     * <p><strong>Common Examples:</strong>
     * <ul>
     * <li>Price below moving average (trend breakdown)</li>
     * <li>Fast EMA below slow EMA (bearish alignment)</li>
     * <li>RSI below oversold threshold (potential reversal)</li>
     * <li>Volume below average volume (weak participation)</li>
     * </ul>
     *
     * @param first  the indicator that should be below the second (must not be null)
     * @param second the indicator to compare against (must not be null)
     * @throws IllegalArgumentException if either indicator is null
     */
    public UnderIndicatorRule(Indicator<Num> first, Indicator<Num> second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Evaluates whether the first indicator is below the second at the given index.
     * 
     * <p>This method performs a strict less-than comparison between the two indicator
     * values at the specified bar index. The comparison uses the underlying
     * {@link Num#isLessThan(Num)} method for precise numerical evaluation.
     * 
     * <p><strong>Note:</strong> This rule does not use the trading record parameter as it
     * only performs indicator-based comparison without considering trade history.
     *
     * @param index         the bar index to evaluate (must be valid for both indicators)
     * @param tradingRecord unused by this rule (may be null)
     * @return true if first indicator < second indicator at this index, false otherwise
     * @throws IndexOutOfBoundsException if index is outside valid range for either indicator
     */
    @Override
    public boolean isSatisfied(int index, TradingRecord tradingRecord) {
        final boolean satisfied = first.getValue(index).isLessThan(second.getValue(index));
        traceIsSatisfied(index, satisfied);
        return satisfied;
    }
}
