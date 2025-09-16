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

import org.ta4j.core.rules.AndRule;
import org.ta4j.core.rules.NotRule;
import org.ta4j.core.rules.OrRule;
import org.ta4j.core.rules.XorRule;

/**
 * A trading rule that evaluates market conditions to generate boolean signals for strategy decisions.
 * 
 * <p>Rules are the fundamental building blocks of trading strategies in Ta4j. They analyze
 * market data and return {@code true} when specific conditions are met, and {@code false} otherwise.
 * Rules can be simple (e.g., "price > moving average") or complex combinations of multiple conditions.
 * 
 * <h2>Types of Rules</h2>
 * <ul>
 * <li><strong>Indicator Rules</strong> - Based on technical indicator values and thresholds</li>
 * <li><strong>Pattern Rules</strong> - Detect specific price patterns or candlestick formations</li>
 * <li><strong>Time Rules</strong> - Based on time conditions (day of week, time ranges, etc.)</li>
 * <li><strong>Composite Rules</strong> - Logical combinations of other rules using AND, OR, NOT, XOR</li>
 * </ul>
 * 
 * <h2>Rule Combination</h2>
 * <p>Rules can be combined using logical operators to create complex trading conditions:</p>
 * <ul>
 * <li>{@link #and(Rule)} - Both rules must be satisfied</li>
 * <li>{@link #or(Rule)} - Either rule must be satisfied</li>
 * <li>{@link #xor(Rule)} - Exactly one rule must be satisfied</li>
 * <li>{@link #negation()} - Inverts the rule result</li>
 * </ul>
 * 
 * <h2>Trading Context</h2>
 * <p>Rules can optionally consider trading history when making decisions. This allows for
 * more sophisticated strategies that consider factors like:</p>
 * <ul>
 * <li>Time since last trade</li>
 * <li>Number of consecutive wins/losses</li>
 * <li>Current position size or profit/loss</li>
 * </ul>
 * 
 * @see Strategy
 * @see org.ta4j.core.rules
 * @see org.ta4j.core.TradingRecord
 * @since 0.1
 */
public interface Rule {

    /**
     * Creates a new rule that is the logical AND combination of this rule and another.
     * 
     * <p>The resulting rule will only be satisfied when <strong>both</strong> this rule
     * and the provided rule are satisfied. This is useful for creating compound conditions
     * where multiple criteria must be met simultaneously.
     * 
     * <p><strong>Example:</strong> Entry only when price is above SMA AND volume is high:
     * <pre>{@code
     * Rule priceRule = new OverIndicatorRule(closePrice, sma);
     * Rule volumeRule = new OverIndicatorRule(volume, threshold);
     * Rule entryRule = priceRule.and(volumeRule);
     * }</pre>
     *
     * @param rule another trading rule to combine with (must not be null)
     * @return a new rule representing the AND combination
     * @throws IllegalArgumentException if rule is null
     * @see AndRule
     */
    default Rule and(Rule rule) {
        return new AndRule(this, rule);
    }

    /**
     * Creates a new rule that is the logical OR combination of this rule and another.
     * 
     * <p>The resulting rule will be satisfied when <strong>either</strong> this rule
     * or the provided rule (or both) are satisfied. This is useful for creating
     * alternative entry/exit conditions.
     * 
     * <p><strong>Example:</strong> Exit when either stop loss OR take profit is hit:
     * <pre>{@code
     * Rule stopLoss = new StopLossRule(closePrice, 0.05);
     * Rule takeProfit = new StopGainRule(closePrice, 0.10);
     * Rule exitRule = stopLoss.or(takeProfit);
     * }</pre>
     *
     * @param rule another trading rule to combine with (must not be null)
     * @return a new rule representing the OR combination
     * @throws IllegalArgumentException if rule is null
     * @see OrRule
     */
    default Rule or(Rule rule) {
        return new OrRule(this, rule);
    }

    /**
     * Creates a new rule that is the logical XOR (exclusive OR) combination of this rule and another.
     * 
     * <p>The resulting rule will be satisfied when <strong>exactly one</strong> of the two rules
     * is satisfied, but not both. This is useful for creating mutually exclusive conditions
     * or for detecting divergence patterns.
     * 
     * <p><strong>Example:</strong> Signal when price trend and momentum disagree:
     * <pre>{@code
     * Rule priceUp = new CrossedUpIndicatorRule(price, sma);
     * Rule momentumUp = new OverIndicatorRule(rsi, 50);
     * Rule divergence = priceUp.xor(momentumUp);
     * }</pre>
     *
     * @param rule another trading rule to combine with (must not be null)
     * @return a new rule representing the XOR combination
     * @throws IllegalArgumentException if rule is null
     * @see XorRule
     */
    default Rule xor(Rule rule) {
        return new XorRule(this, rule);
    }

    /**
     * Creates a new rule that is the logical negation (NOT) of this rule.
     * 
     * <p>The resulting rule will be satisfied when this rule is <strong>not</strong> satisfied,
     * and vice versa. This is useful for creating opposite conditions or for implementing
     * contrarian strategies.
     * 
     * <p><strong>Example:</strong> Exit when price is no longer above moving average:
     * <pre>{@code
     * Rule priceAboveSMA = new OverIndicatorRule(closePrice, sma);
     * Rule exitRule = priceAboveSMA.negation(); // Price below or equal to SMA
     * }</pre>
     *
     * @return a new rule representing the logical negation
     * @see NotRule
     */
    default Rule negation() {
        return new NotRule(this);
    }

    /**
     * Evaluates whether this rule is satisfied at the given bar index.
     * 
     * <p>This is a convenience method that calls {@link #isSatisfied(int, TradingRecord)}
     * with a null trading record. Use this when the rule doesn't need access to
     * trading history for its evaluation.
     *
     * @param index the bar index to evaluate (must be >= 0)
     * @return true if the rule is satisfied at this index, false otherwise
     * @throws IndexOutOfBoundsException if index is outside the valid range
     * @see #isSatisfied(int, TradingRecord)
     */
    default boolean isSatisfied(int index) {
        return isSatisfied(index, null);
    }

    /**
     * Evaluates whether this rule is satisfied at the given bar index, with access to trading history.
     * 
     * <p>This is the core evaluation method for rules. It determines if the rule's condition
     * is met at the specified bar index. Some rules may use the trading record to access
     * information about previous trades, current position, or other historical context.
     * 
     * <p><strong>Implementation Note:</strong> Rules should be deterministic - calling this
     * method multiple times with the same parameters should return the same result.
     *
     * @param index         the bar index to evaluate (must be >= 0)
     * @param tradingRecord the trading history for context (may be null if not needed)
     * @return true if the rule is satisfied at this index, false otherwise
     * @throws IndexOutOfBoundsException if index is outside the valid range
     * @see TradingRecord
     */
    boolean isSatisfied(int index, TradingRecord tradingRecord);
}
