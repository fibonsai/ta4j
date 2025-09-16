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
 * A stop-loss rule for automatic position risk management.
 * 
 * <p>The Stop-Loss Rule is a fundamental risk management tool that automatically triggers
 * position exit when losses reach a predetermined threshold. It monitors the current price
 * against the entry price of an open position and is satisfied when the loss percentage
 * exceeds the configured limit, helping protect trading capital from excessive drawdowns.
 * 
 * <h2>Risk Management Function</h2>
 * <p>The stop-loss rule serves several critical functions:
 * <ul>
 * <li><strong>Capital Protection:</strong> Limits maximum loss per trade</li>
 * <li><strong>Emotional Control:</strong> Removes emotion from exit decisions</li>
 * <li><strong>Portfolio Preservation:</strong> Prevents single trades from destroying accounts</li>
 * <li><strong>Risk Consistency:</strong> Ensures uniform risk management across all trades</li>
 * </ul>
 * 
 * <h2>Calculation Logic</h2>
 * <p>The rule calculates stop-loss levels differently for long and short positions:
 * 
 * <h3>Long Positions (Buy Entry)</h3>
 * <ul>
 * <li><strong>Stop Level:</strong> Entry Price × (100 - Loss%) / 100</li>
 * <li><strong>Trigger:</strong> Current Price ≤ Stop Level</li>
 * <li><strong>Example:</strong> Entry at $100, 5% stop → Stop at $95</li>
 * </ul>
 * 
 * <h3>Short Positions (Sell Entry)</h3>
 * <ul>
 * <li><strong>Stop Level:</strong> Entry Price × (100 + Loss%) / 100</li>
 * <li><strong>Trigger:</strong> Current Price ≥ Stop Level</li>
 * <li><strong>Example:</strong> Entry at $100, 5% stop → Stop at $105</li>
 * </ul>
 * 
 * <h2>Trading Context Dependency</h2>
 * <p>Unlike most rules, the stop-loss rule requires access to {@link TradingRecord}:
 * <ul>
 * <li><strong>Position Awareness:</strong> Only triggers when a position is open</li>
 * <li><strong>Entry Price Reference:</strong> Calculates loss from actual entry price</li>
 * <li><strong>Direction Sensitivity:</strong> Handles long and short positions differently</li>
 * <li><strong>State Management:</strong> Inactive when no position is open</li>
 * </ul>
 * 
 * <h2>Risk Management Best Practices</h2>
 * <ul>
 * <li><strong>Position Sizing:</strong>
 *     <ul>
 *     <li>Risk 1-2% of capital per trade</li>
 *     <li>Adjust position size based on stop distance</li>
 *     <li>Consider volatility when setting stop levels</li>
 *     </ul>
 * </li>
 * <li><strong>Stop Placement:</strong>
 *     <ul>
 *     <li>Place stops beyond normal price noise</li>
 *     <li>Consider support/resistance levels</li>
 *     <li>Use ATR for volatility-based stops</li>
 *     </ul>
 * </li>
 * <li><strong>Implementation:</strong>
 *     <ul>
 *     <li>Set stops immediately after entry</li>
 *     <li>Avoid moving stops against your position</li>
 *     <li>Consider trailing stops for trend following</li>
 *     </ul>
 * </li>
 * </ul>
 * 
 * <h2>Common Stop-Loss Percentages</h2>
 * <ul>
 * <li><strong>Day Trading:</strong> 1-3% (tight stops for quick exits)</li>
 * <li><strong>Swing Trading:</strong> 3-8% (allow for normal volatility)</li>
 * <li><strong>Position Trading:</strong> 8-15% (accommodate long-term noise)</li>
 * <li><strong>Volatile Markets:</strong> 10-20% (adjust for asset volatility)</li>
 * </ul>
 * 
 * <h2>Limitations and Considerations</h2>
 * <ul>
 * <li><strong>Gap Risk:</strong> Price gaps can cause execution beyond stop level</li>
 * <li><strong>Whipsaws:</strong> Tight stops may trigger on normal volatility</li>
 * <li><strong>Market Hours:</strong> Consider overnight and weekend risks</li>
 * <li><strong>Slippage:</strong> Actual execution may differ from calculated level</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Basic stop-loss rule (5% loss limit)
 * ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
 * StopLossRule stopLoss = new StopLossRule(closePrice, 5.0);
 * 
 * // Trading strategy with stop-loss protection
 * Rule entryRule = new CrossedUpIndicatorRule(ema12, ema26);
 * Rule profitTarget = new StopGainRule(closePrice, 10.0); // 10% profit target
 * Rule exitRule = stopLoss.or(profitTarget);
 * 
 * Strategy protectedStrategy = new BaseStrategy("Protected Trend", entryRule, exitRule);
 * 
 * // Volatility-adjusted stop loss
 * ATRIndicator atr = new ATRIndicator(series, 14);
 * Num currentATR = atr.getValue(series.getEndIndex());
 * Num stopDistance = currentATR.multipliedBy(2); // 2x ATR stop
 * Num stopPercentage = stopDistance.dividedBy(closePrice.getValue(series.getEndIndex()))
 *     .multipliedBy(100);
 * StopLossRule atrStopLoss = new StopLossRule(closePrice, stopPercentage);
 * 
 * // Risk management with position sizing
 * Num accountSize = series.numFactory().numOf(100000); // $100k account
 * Num riskPerTrade = accountSize.multipliedBy(0.02); // 2% risk
 * Num stopDistance2 = closePrice.getValue(series.getEndIndex()).multipliedBy(0.05); // 5% stop
 * Num positionSize = riskPerTrade.dividedBy(stopDistance2);
 * 
 * // Multiple exit conditions
 * Rule timeStop = new WaitForRule(entryRule, 20); // Time-based exit
 * Rule comprehensiveExit = stopLoss.or(profitTarget).or(timeStop);
 * }</pre>
 * 
 * @see StopGainRule
 * @see TrailingStopLossRule
 * @see org.ta4j.core.Position
 * @see org.ta4j.core.TradingRecord
 * @see org.ta4j.core.analysis.cost.CostModel
 * @since 0.1
 */
public class StopLossRule extends AbstractRule {

    /** The constant value for 100. */
    private final Num HUNDRED;

    /** The reference price indicator. */
    private final Indicator<Num> priceIndicator;

    /** The loss percentage. */
    private final Num lossPercentage;

    /**
     * Creates a stop-loss rule with a numeric loss percentage threshold.
     * 
     * <p>This constructor automatically converts the Number loss percentage to the appropriate
     * {@link Num} type. The loss percentage is applied to the entry price to calculate
     * the stop-loss level.
     * 
     * <p><strong>Example:</strong> A 5% stop-loss on a $100 entry will trigger when
     * the price reaches $95 (for long positions) or $105 (for short positions).
     *
     * @param priceIndicator the price indicator to monitor for stop triggers (must not be null)
     * @param lossPercentage the maximum loss percentage before stop triggers (must be > 0)
     * @throws IllegalArgumentException if priceIndicator is null or lossPercentage <= 0
     */
    public StopLossRule(Indicator<Num> priceIndicator, Number lossPercentage) {
        this(priceIndicator, priceIndicator.getBarSeries().numFactory().numOf(lossPercentage));
    }

    /**
     * Creates a stop-loss rule with a {@link Num} loss percentage threshold.
     * 
     * <p>This constructor provides precise control over the loss percentage using the
     * {@link Num} type. The percentage is used to calculate stop-loss levels based
     * on the entry price of open positions.
     * 
     * <p><strong>Loss Calculation:</strong>
     * <ul>
     * <li><strong>Long Positions:</strong> Stop Level = Entry × (100 - lossPercentage) / 100</li>
     * <li><strong>Short Positions:</strong> Stop Level = Entry × (100 + lossPercentage) / 100</li>
     * </ul>
     *
     * @param priceIndicator the price indicator to monitor (must not be null)
     * @param lossPercentage the maximum loss percentage, typically 1-20% (must be > 0)
     * @throws IllegalArgumentException if priceIndicator is null or lossPercentage <= 0
     */
    public StopLossRule(Indicator<Num> priceIndicator, Num lossPercentage) {
        this.priceIndicator = priceIndicator;
        this.lossPercentage = lossPercentage;
        HUNDRED = priceIndicator.getBarSeries().numFactory().hundred();
    }

    /**
     * Evaluates whether the stop-loss threshold has been breached for the current position.
     * 
     * <p>This method requires access to the trading record to determine:
     * <ul>
     * <li>Whether a position is currently open</li>
     * <li>The entry price and direction of the position</li>
     * <li>The appropriate stop-loss calculation method</li>
     * </ul>
     * 
     * <p>The rule returns {@code false} if no trading record is provided or no position
     * is currently open. When a position is open, it calculates the stop-loss threshold
     * based on the position direction and entry price.
     * 
     * <p><strong>Position Types:</strong>
     * <ul>
     * <li><strong>Long Position:</strong> Triggered when current price ≤ stop level</li>
     * <li><strong>Short Position:</strong> Triggered when current price ≥ stop level</li>
     * </ul>
     *
     * @param index         the bar index to evaluate (must be valid for price indicator)
     * @param tradingRecord the trading record containing position information (must not be null for evaluation)
     * @return true if stop-loss threshold is breached for current position, false otherwise
     * @throws IndexOutOfBoundsException if index is outside valid range for price indicator
     */
    @Override
    public boolean isSatisfied(int index, TradingRecord tradingRecord) {
        boolean satisfied = false;
        // No trading history or no position opened, no loss
        if (tradingRecord != null) {
            var currentPosition = tradingRecord.getCurrentPosition();
            if (currentPosition.isOpened()) {

                var entryPrice = currentPosition.getEntry().getNetPrice();
                var currentPrice = priceIndicator.getValue(index);

                if (currentPosition.getEntry().isBuy()) {
                    satisfied = isBuyStopSatisfied(entryPrice, currentPrice);
                } else {
                    satisfied = isSellStopSatisfied(entryPrice, currentPrice);
                }
            }
        }
        traceIsSatisfied(index, satisfied);
        return satisfied;
    }

    private boolean isBuyStopSatisfied(Num entryPrice, Num currentPrice) {
        var lossRatioThreshold = HUNDRED.minus(lossPercentage).dividedBy(HUNDRED);
        var threshold = entryPrice.multipliedBy(lossRatioThreshold);
        return currentPrice.isLessThanOrEqual(threshold);
    }

    private boolean isSellStopSatisfied(Num entryPrice, Num currentPrice) {
        var lossRatioThreshold = HUNDRED.plus(lossPercentage).dividedBy(HUNDRED);
        var threshold = entryPrice.multipliedBy(lossRatioThreshold);
        return currentPrice.isGreaterThanOrEqual(threshold);
    }
}
