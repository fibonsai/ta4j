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
import org.ta4j.core.Position;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.num.Num;

/**
 * A stop-gain rule for automatic profit-taking and position management.
 * 
 * <p>The Stop-Gain Rule (also known as Take-Profit Rule) is a profit management tool
 * that automatically triggers position exit when gains reach a predetermined threshold.
 * It monitors the current price against the entry price of an open position and is
 * satisfied when the profit percentage exceeds the configured target, helping lock in
 * profits and manage risk-reward ratios.
 * 
 * <h2>Profit Management Function</h2>
 * <p>The stop-gain rule serves several important functions:
 * <ul>
 * <li><strong>Profit Protection:</strong> Automatically captures gains at predetermined levels</li>
 * <li><strong>Discipline Enforcement:</strong> Removes emotion from profit-taking decisions</li>
 * <li><strong>Risk-Reward Management:</strong> Ensures consistent risk-reward ratios</li>
 * <li><strong>Systematic Exits:</strong> Provides mechanical exit criteria for winning trades</li>
 * </ul>
 * 
 * <h2>Calculation Logic</h2>
 * <p>The rule calculates profit levels differently for long and short positions:
 * 
 * <h3>Long Positions (Buy Entry)</h3>
 * <ul>
 * <li><strong>Target Level:</strong> Entry Price × (100 + Gain%) / 100</li>
 * <li><strong>Trigger:</strong> Current Price ≥ Target Level</li>
 * <li><strong>Example:</strong> Entry at $100, 10% target → Exit at $110</li>
 * </ul>
 * 
 * <h3>Short Positions (Sell Entry)</h3>
 * <ul>
 * <li><strong>Target Level:</strong> Entry Price × (100 - Gain%) / 100</li>
 * <li><strong>Trigger:</strong> Current Price ≤ Target Level</li>
 * <li><strong>Example:</strong> Entry at $100, 10% target → Exit at $90</li>
 * </ul>
 * 
 * <h2>Trading Strategy Integration</h2>
 * <p>Stop-gain rules are typically used in combination with other exit criteria:
 * <ul>
 * <li><strong>Risk-Reward Ratios:</strong> Common ratios like 1:2, 1:3 (risk 1% to gain 2-3%)</li>
 * <li><strong>Multiple Targets:</strong> Partial position exits at different profit levels</li>
 * <li><strong>Trailing Stops:</strong> Combined with trailing stop-losses for trend following</li>
 * <li><strong>Time Exits:</strong> Maximum holding period regardless of profit/loss</li>
 * </ul>
 * 
 * <h2>Profit-Taking Strategies</h2>
 * <ul>
 * <li><strong>Fixed Targets:</strong>
 *     <ul>
 *     <li>Single profit target for entire position</li>
 *     <li>Simple and mechanical approach</li>
 *     <li>Ensures consistent profit capture</li>
 *     </ul>
 * </li>
 * <li><strong>Scaling Out:</strong>
 *     <ul>
 *     <li>Multiple profit targets at different levels</li>
 *     <li>Partial position exits (e.g., 1/3 at 5%, 1/3 at 10%, 1/3 at 15%)</li>
 *     <li>Balances profit capture with trend participation</li>
 *     </ul>
 * </li>
 * <li><strong>Volatility-Based:</strong>
 *     <ul>
 *     <li>Profit targets based on Average True Range (ATR)</li>
 *     <li>Adapts to market volatility conditions</li>
 *     <li>Examples: 2x ATR, 3x ATR profit targets</li>
 *     </ul>
 * </li>
 * </ul>
 * 
 * <h2>Risk-Reward Optimization</h2>
 * <ul>
 * <li><strong>Minimum Risk-Reward:</strong> Ensure profit targets are at least 1.5-2x the stop-loss distance</li>
 * <li><strong>Win Rate Consideration:</strong> Higher targets reduce win rate but increase average win size</li>
 * <li><strong>Market Conditions:</strong> Adjust targets based on trending vs. ranging markets</li>
 * <li><strong>Backtesting:</strong> Optimize target levels based on historical performance</li>
 * </ul>
 * 
 * <h2>Common Profit Target Percentages</h2>
 * <ul>
 * <li><strong>Day Trading:</strong> 0.5-2% (quick profit capture)</li>
 * <li><strong>Swing Trading:</strong> 5-15% (medium-term holds)</li>
 * <li><strong>Position Trading:</strong> 20-50% (long-term trends)</li>
 * <li><strong>Scalping:</strong> 0.1-0.5% (very short-term profits)</li>
 * </ul>
 * 
 * <h2>Psychological Benefits</h2>
 * <ul>
 * <li><strong>Profit Realization:</strong> Converts unrealized gains to realized profits</li>
 * <li><strong>Regret Minimization:</strong> Reduces regret from not taking available profits</li>
 * <li><strong>Discipline:</strong> Enforces systematic profit-taking behavior</li>
 * <li><strong>Confidence:</strong> Builds trading confidence through consistent wins</li>
 * </ul>
 * 
 * <h2>Limitations and Considerations</h2>
 * <ul>
 * <li><strong>Trend Limitation:</strong> May exit too early in strong trending markets</li>
 * <li><strong>Opportunity Cost:</strong> Missing larger profits from extended trends</li>
 * <li><strong>Market Gaps:</strong> Price gaps can cause execution beyond target levels</li>
 * <li><strong>Static Targets:</strong> Fixed targets may not adapt to changing market conditions</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Basic stop-gain rule (10% profit target)
 * ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
 * StopGainRule stopGain = new StopGainRule(closePrice, 10.0);
 * 
 * // Complete risk management strategy
 * StopLossRule stopLoss = new StopLossRule(closePrice, 5.0); // 5% stop loss
 * Rule exitRule = stopGain.or(stopLoss); // 2:1 risk-reward ratio
 * 
 * Strategy riskManagedStrategy = new BaseStrategy("Risk Managed", entryRule, exitRule);
 * 
 * // Multiple profit targets (scaling out)
 * StopGainRule target1 = new StopGainRule(closePrice, 5.0);  // First 1/3 at 5%
 * StopGainRule target2 = new StopGainRule(closePrice, 10.0); // Second 1/3 at 10%
 * StopGainRule target3 = new StopGainRule(closePrice, 15.0); // Final 1/3 at 15%
 * 
 * // Volatility-based profit target
 * ATRIndicator atr = new ATRIndicator(series, 14);
 * Num currentATR = atr.getValue(series.getEndIndex());
 * Num dynamicTarget = currentATR.multipliedBy(3); // 3x ATR target
 * Num targetPercentage = dynamicTarget.dividedBy(closePrice.getValue(series.getEndIndex()))
 *     .multipliedBy(100);
 * StopGainRule atrStopGain = new StopGainRule(closePrice, targetPercentage);
 * 
 * // Combined with trailing stop
 * TrailingStopLossRule trailingStop = new TrailingStopLossRule(closePrice, 8.0);
 * Rule flexibleExit = stopGain.or(trailingStop);
 * 
 * // Risk-reward validation
 * Num stopLossDistance = closePrice.getValue(series.getEndIndex()).multipliedBy(0.05); // 5%
 * Num profitTarget = closePrice.getValue(series.getEndIndex()).multipliedBy(0.12); // 12%
 * Num riskRewardRatio = profitTarget.dividedBy(stopLossDistance); // Should be > 2.0
 * 
 * // Time-based exit combined with profit target
 * Rule timeStop = new DayOfWeekRule(DayOfWeek.FRIDAY); // Exit on Fridays
 * Rule comprehensiveExit = stopGain.or(stopLoss).or(timeStop);
 * }</pre>
 * 
 * @see StopLossRule
 * @see TrailingStopLossRule
 * @see org.ta4j.core.Position
 * @see org.ta4j.core.TradingRecord
 * @since 0.1
 */
public class StopGainRule extends AbstractRule {

    /** The constant value for 100. */
    private final Num HUNDRED;

    /** The reference price indicator. */
    private final Indicator<Num> priceIndicator;

    /** The gain percentage. */
    private final Num gainPercentage;

    /**
     * Creates a stop-gain rule with a numeric profit percentage threshold.
     * 
     * <p>This constructor automatically converts the Number gain percentage to the appropriate
     * {@link Num} type. The gain percentage is applied to the entry price to calculate
     * the profit target level.
     * 
     * <p><strong>Example:</strong> A 10% profit target on a $100 entry will trigger when
     * the price reaches $110 (for long positions) or $90 (for short positions).
     *
     * @param priceIndicator the price indicator to monitor for profit triggers (must not be null)
     * @param gainPercentage the target profit percentage before exit triggers (must be > 0)
     * @throws IllegalArgumentException if priceIndicator is null or gainPercentage <= 0
     */
    public StopGainRule(Indicator<Num> priceIndicator, Number gainPercentage) {
        this(priceIndicator, priceIndicator.getBarSeries().numFactory().numOf(gainPercentage));
    }

    /**
     * Creates a stop-gain rule with a {@link Num} profit percentage threshold.
     * 
     * <p>This constructor provides precise control over the gain percentage using the
     * {@link Num} type. The percentage is used to calculate profit target levels based
     * on the entry price of open positions.
     * 
     * <p><strong>Profit Calculation:</strong>
     * <ul>
     * <li><strong>Long Positions:</strong> Target Level = Entry × (100 + gainPercentage) / 100</li>
     * <li><strong>Short Positions:</strong> Target Level = Entry × (100 - gainPercentage) / 100</li>
     * </ul>
     *
     * @param priceIndicator the price indicator to monitor (must not be null)
     * @param gainPercentage the target profit percentage, typically 5-50% (must be > 0)
     * @throws IllegalArgumentException if priceIndicator is null or gainPercentage <= 0
     */
    public StopGainRule(Indicator<Num> priceIndicator, Num gainPercentage) {
        this.priceIndicator = priceIndicator;
        this.gainPercentage = gainPercentage;
        HUNDRED = priceIndicator.getBarSeries().numFactory().hundred();
    }

    /**
     * Evaluates whether the profit target threshold has been reached for the current position.
     * 
     * <p>This method requires access to the trading record to determine:
     * <ul>
     * <li>Whether a position is currently open</li>
     * <li>The entry price and direction of the position</li>
     * <li>The appropriate profit target calculation method</li>
     * </ul>
     * 
     * <p>The rule returns {@code false} if no trading record is provided or no position
     * is currently open. When a position is open, it calculates the profit target
     * based on the position direction and entry price.
     * 
     * <p><strong>Position Types:</strong>
     * <ul>
     * <li><strong>Long Position:</strong> Triggered when current price ≥ target level</li>
     * <li><strong>Short Position:</strong> Triggered when current price ≤ target level</li>
     * </ul>
     *
     * @param index         the bar index to evaluate (must be valid for price indicator)
     * @param tradingRecord the trading record containing position information (must not be null for evaluation)
     * @return true if profit target threshold is reached for current position, false otherwise
     * @throws IndexOutOfBoundsException if index is outside valid range for price indicator
     */
    @Override
    public boolean isSatisfied(int index, TradingRecord tradingRecord) {
        var satisfied = false;
        // No trading history or no position opened, no loss
        if (tradingRecord != null) {
            Position currentPosition = tradingRecord.getCurrentPosition();
            if (currentPosition.isOpened()) {

                var entryPrice = currentPosition.getEntry().getNetPrice();
                var currentPrice = priceIndicator.getValue(index);

                if (currentPosition.getEntry().isBuy()) {
                    satisfied = isBuyGainSatisfied(entryPrice, currentPrice);
                } else {
                    satisfied = isSellGainSatisfied(entryPrice, currentPrice);
                }
            }
        }
        traceIsSatisfied(index, satisfied);
        return satisfied;
    }

    private boolean isBuyGainSatisfied(Num entryPrice, Num currentPrice) {
        var lossRatioThreshold = HUNDRED.plus(gainPercentage).dividedBy(HUNDRED);
        var threshold = entryPrice.multipliedBy(lossRatioThreshold);
        return currentPrice.isGreaterThanOrEqual(threshold);
    }

    private boolean isSellGainSatisfied(Num entryPrice, Num currentPrice) {
        var lossRatioThreshold = HUNDRED.minus(gainPercentage).dividedBy(HUNDRED);
        var threshold = entryPrice.multipliedBy(lossRatioThreshold);
        return currentPrice.isLessThanOrEqual(threshold);
    }
}
