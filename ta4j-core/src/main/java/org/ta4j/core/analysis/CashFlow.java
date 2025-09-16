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
package org.ta4j.core.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.Position;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.num.Num;

/**
 * Cash Flow indicator for tracking portfolio value evolution over time during strategy execution.
 * 
 * <p>The CashFlow class calculates and tracks the cumulative monetary performance of trading
 * strategies by monitoring how invested capital changes with each position and price movement.
 * It provides a time-series view of portfolio value that is essential for performance analysis,
 * risk assessment, and strategy evaluation.
 * 
 * <h2>Cash Flow Calculation</h2>
 * <p>The cash flow tracks portfolio value changes through:
 * <ul>
 * <li><strong>Initial Capital:</strong> Normalized to 1.0 (100% of starting capital)</li>
 * <li><strong>Position Returns:</strong> Multiplicative returns based on entry and exit prices</li>
 * <li><strong>Intermediate Values:</strong> Unrealized P&L during open positions</li>
 * <li><strong>Transaction Costs:</strong> Inclusion of trading costs and holding costs</li>
 * </ul>
 * 
 * <h2>Types of Cash Flow Analysis</h2>
 * <ul>
 * <li><strong>Single Position:</strong> Performance of an individual closed position</li>
 * <li><strong>Trading Record:</strong> Cumulative performance across all closed positions</li>
 * <li><strong>Real-time:</strong> Including unrealized P&L of currently open positions</li>
 * <li><strong>Historical:</strong> Performance analysis up to a specific point in time</li>
 * </ul>
 * 
 * <h2>Value Calculation Logic</h2>
 * 
 * <h3>Long Positions</h3>
 * <ul>
 * <li><strong>Return Ratio:</strong> Current Price / Entry Price</li>
 * <li><strong>Portfolio Value:</strong> Previous Value × Return Ratio</li>
 * <li><strong>Example:</strong> Entry at $100, current at $110 → 10% gain</li>
 * </ul>
 * 
 * <h3>Short Positions</h3>
 * <ul>
 * <li><strong>Return Ratio:</strong> 2 - (Current Price / Entry Price)</li>
 * <li><strong>Portfolio Value:</strong> Previous Value × Return Ratio</li>
 * <li><strong>Example:</strong> Entry at $100, current at $90 → 10% gain</li>
 * </ul>
 * 
 * <h2>Cost Integration</h2>
 * <p>The cash flow incorporates various trading costs:
 * <ul>
 * <li><strong>Transaction Costs:</strong> Entry and exit commissions, spreads, slippage</li>
 * <li><strong>Holding Costs:</strong> Financing costs, borrowing fees, storage costs</li>
 * <li><strong>Cost Distribution:</strong> Holding costs are distributed proportionally over the position duration</li>
 * </ul>
 * 
 * <h2>Performance Analysis Applications</h2>
 * <ul>
 * <li><strong>Equity Curve Analysis:</strong> Visual representation of strategy performance</li>
 * <li><strong>Drawdown Calculation:</strong> Maximum peak-to-trough decline measurement</li>
 * <li><strong>Return Metrics:</strong> Total return, annualized return, volatility calculation</li>
 * <li><strong>Risk Assessment:</strong> Sharpe ratio, maximum drawdown, volatility analysis</li>
 * <li><strong>Strategy Comparison:</strong> Relative performance between different strategies</li>
 * </ul>
 * 
 * <h2>Real-time Monitoring</h2>
 * <p>For live trading and strategy monitoring:
 * <ul>
 * <li><strong>Unrealized P&L:</strong> Current value of open positions</li>
 * <li><strong>Portfolio Value:</strong> Combined closed and unrealized returns</li>
 * <li><strong>Running Performance:</strong> Continuous performance tracking</li>
 * <li><strong>Risk Metrics:</strong> Real-time risk assessment</li>
 * </ul>
 * 
 * <h2>Limitations and Considerations</h2>
 * <ul>
 * <li><strong>Market Impact:</strong> Does not account for market impact of large orders</li>
 * <li><strong>Liquidity:</strong> Assumes perfect liquidity for position sizing</li>
 * <li><strong>Slippage:</strong> Basic cost modeling may not reflect actual execution</li>
 * <li><strong>Gaps:</strong> Price gaps can cause execution differences from theoretical values</li>
 * </ul>
 * 
 * <h2>Usage Examples</h2>
 * <pre>{@code
 * // Single position cash flow analysis
 * Position position = createAndClosePosition(); // Entry and exit trades
 * CashFlow positionCashFlow = new CashFlow(series, position);
 * 
 * // Get final portfolio value after position
 * Num finalValue = positionCashFlow.getValue(series.getEndIndex());
 * Num totalReturn = finalValue.minus(series.numFactory().one()); // -1 for percentage
 * 
 * // Trading record cash flow (multiple positions)
 * TradingRecord record = backtestStrategy(strategy, series);
 * CashFlow strategyCashFlow = new CashFlow(series, record);
 * 
 * // Calculate performance metrics
 * List<Num> cashFlowValues = new ArrayList<>();
 * for (int i = 0; i <= series.getEndIndex(); i++) {
 *     cashFlowValues.add(strategyCashFlow.getValue(i));
 * }
 * 
 * // Calculate maximum drawdown
 * Num maxValue = series.numFactory().one();
 * Num maxDrawdown = series.numFactory().zero();
 * for (Num value : cashFlowValues) {
 *     if (value.isGreaterThan(maxValue)) {
 *         maxValue = value;
 *     }
 *     Num drawdown = maxValue.minus(value).dividedBy(maxValue);
 *     if (drawdown.isGreaterThan(maxDrawdown)) {
 *         maxDrawdown = drawdown;
 *     }
 * }
 * 
 * // Real-time cash flow with open position
 * int currentIndex = series.getEndIndex();
 * CashFlow realTimeCashFlow = new CashFlow(series, record, currentIndex);
 * Num currentPortfolioValue = realTimeCashFlow.getValue(currentIndex);
 * 
 * // Use in analysis criteria
 * TotalProfitCriterion totalProfit = new TotalProfitCriterion();
 * MaximumDrawdownCriterion maxDD = new MaximumDrawdownCriterion();
 * 
 * Num profit = totalProfit.calculate(series, record);
 * Num drawdown = maxDD.calculate(series, record);
 * 
 * // Chart equity curve
 * // cashFlowValues can be plotted to show strategy performance over time
 * }</pre>
 * 
 * @see Position
 * @see TradingRecord
 * @see org.ta4j.core.analysis.cost.CostModel
 * @see org.ta4j.core.analysis.criteria.TotalProfitCriterion
 * @see org.ta4j.core.analysis.criteria.MaximumDrawdownCriterion
 * @since 0.1
 */
public class CashFlow implements Indicator<Num> {

    /** The bar series. */
    private final BarSeries barSeries;

    /** The (accrued) cash flow sequence (without trading costs). */
    private final List<Num> values;

    /**
     * Creates a cash flow indicator for analyzing a single closed position.
     * 
     * <p>This constructor calculates the cash flow evolution for a specific closed position,
     * showing how the portfolio value would change from the position entry to exit.
     * The cash flow starts at 1.0 (representing 100% of initial capital) and tracks
     * the multiplicative returns through the position lifecycle.
     * 
     * <p><strong>Requirements:</strong> The position must be closed (both entry and exit trades present).
     * For open positions, use the constructor with finalIndex parameter.
     *
     * @param barSeries the bar series containing price data (must not be null)
     * @param position  the closed position to analyze (must not be null and must be closed)
     * @throws IllegalArgumentException if position is not closed or barSeries/position is null
     */
    public CashFlow(BarSeries barSeries, Position position) {
        this.barSeries = barSeries;
        values = new ArrayList<>(Collections.singletonList(barSeries.numFactory().one()));

        calculate(position);
        fillToTheEnd(barSeries.getEndIndex());
    }

    /**
     * Creates a cash flow indicator for analyzing all closed positions in a trading record.
     * 
     * <p>This constructor calculates the cumulative cash flow evolution across all closed
     * positions in the trading record. Each position's return is compounded with previous
     * returns to show the overall portfolio performance progression.
     * 
     * <p>Open positions are ignored in this calculation. To include the current unrealized
     * P&L of open positions, use the constructor with finalIndex parameter.
     *
     * @param barSeries     the bar series containing price data (must not be null)
     * @param tradingRecord the trading record with positions to analyze (must not be null)
     * @throws IllegalArgumentException if barSeries or tradingRecord is null
     */
    public CashFlow(BarSeries barSeries, TradingRecord tradingRecord) {
        this(barSeries, tradingRecord, tradingRecord.getEndIndex(barSeries));
    }

    /**
     * Creates a cash flow indicator including unrealized P&L up to a specific index.
     * 
     * <p>This constructor calculates cash flow for all closed positions plus the unrealized
     * profit/loss of any currently open position up to the specified finalIndex. This is
     * essential for real-time portfolio monitoring and strategy analysis during execution.
     * 
     * <p>The cash flow includes:
     * <ul>
     * <li>Realized returns from all closed positions</li>
     * <li>Unrealized returns from open position (if any) up to finalIndex</li>
     * <li>Transaction and holding costs appropriately distributed</li>
     * </ul>
     *
     * @param barSeries     the bar series containing price data (must not be null)
     * @param tradingRecord the trading record with positions (must not be null)
     * @param finalIndex    the index up to which to calculate unrealized P&L (must be valid)
     * @throws IllegalArgumentException if barSeries/tradingRecord is null or finalIndex is invalid
     */
    public CashFlow(BarSeries barSeries, TradingRecord tradingRecord, int finalIndex) {
        this.barSeries = barSeries;
        values = new ArrayList<>(Collections.singletonList(getBarSeries().numFactory().one()));

        calculate(tradingRecord, finalIndex);
        fillToTheEnd(finalIndex);
    }

    /**
     * @param index the bar index
     * @return the cash flow value at the index-th position
     */
    @Override
    public Num getValue(int index) {
        return values.get(index);
    }

    @Override
    public int getCountOfUnstableBars() {
        return 0;
    }

    @Override
    public BarSeries getBarSeries() {
        return barSeries;
    }

    /**
     * @return the size of the bar series
     */
    public int getSize() {
        return barSeries.getBarCount();
    }

    /**
     * Calculates the cash flow for a single closed position.
     *
     * @param position a single position
     */
    private void calculate(Position position) {
        if (position.isOpened()) {
            throw new IllegalArgumentException(
                    "Position is not closed. Final index of observation needs to be provided.");
        }
        calculate(position, position.getExit().getIndex());
    }

    /**
     * Calculates the cash flow for a single position (including accrued cashflow
     * for open positions).
     *
     * @param position   a single position
     * @param finalIndex index up until cash flow of open positions is considered
     */
    private void calculate(Position position, int finalIndex) {
        boolean isLongTrade = position.getEntry().isBuy();
        int endIndex = determineEndIndex(position, finalIndex, barSeries.getEndIndex());
        final int entryIndex = position.getEntry().getIndex();
        int begin = entryIndex + 1;
        if (begin > values.size()) {
            Num lastValue = values.get(values.size() - 1);
            values.addAll(Collections.nCopies(begin - values.size(), lastValue));
        }
        // Trade is not valid if net balance at the entryIndex is negative
        if (values.get(values.size() - 1).isGreaterThan(values.get(0).getNumFactory().numOf(0))) {
            int startingIndex = Math.max(begin, 1);

            int nPeriods = endIndex - entryIndex;
            Num holdingCost = position.getHoldingCost(endIndex);
            Num avgCost = holdingCost.dividedBy(holdingCost.getNumFactory().numOf(nPeriods));

            // Add intermediate cash flows during position
            Num netEntryPrice = position.getEntry().getNetPrice();
            for (int i = startingIndex; i < endIndex; i++) {
                Num intermediateNetPrice = addCost(barSeries.getBar(i).getClosePrice(), avgCost, isLongTrade);
                Num ratio = getIntermediateRatio(isLongTrade, netEntryPrice, intermediateNetPrice);
                values.add(values.get(entryIndex).multipliedBy(ratio));
            }

            // add net cash flow at exit position
            Num exitPrice;
            if (position.getExit() != null) {
                exitPrice = position.getExit().getNetPrice();
            } else {
                exitPrice = barSeries.getBar(endIndex).getClosePrice();
            }
            Num ratio = getIntermediateRatio(isLongTrade, netEntryPrice, addCost(exitPrice, avgCost, isLongTrade));
            values.add(values.get(entryIndex).multipliedBy(ratio));
        }
    }

    /**
     * Calculates the ratio of intermediate prices.
     *
     * @param isLongTrade true, if the entry trade type is BUY
     * @param entryPrice  price ratio denominator
     * @param exitPrice   price ratio numerator
     */
    private static Num getIntermediateRatio(boolean isLongTrade, Num entryPrice, Num exitPrice) {
        Num ratio;
        if (isLongTrade) {
            ratio = exitPrice.dividedBy(entryPrice);
        } else {
            ratio = entryPrice.getNumFactory().numOf(2).minus(exitPrice.dividedBy(entryPrice));
        }

        return ratio;
    }

    /**
     * Calculates the cash flow for the closed positions of a trading record.
     *
     * @param tradingRecord the trading record
     */
    private void calculate(TradingRecord tradingRecord) {
        // For each position...
        tradingRecord.getPositions().forEach(this::calculate);
    }

    /**
     * Calculates the cash flow for all positions of a trading record, including
     * accrued cash flow of an open position.
     *
     * @param tradingRecord the trading record
     * @param finalIndex    index up until cash flows of open positions are
     *                      considered
     */
    private void calculate(TradingRecord tradingRecord, int finalIndex) {
        calculate(tradingRecord);

        // Add accrued cash flow of open position
        if (tradingRecord.getCurrentPosition().isOpened()) {
            calculate(tradingRecord.getCurrentPosition(), finalIndex);
        }
    }

    /**
     * Adjusts (intermediate) price to incorporate trading costs.
     *
     * @param rawPrice    the gross asset price
     * @param holdingCost share of the holding cost per period
     * @param isLongTrade true, if the entry trade type is BUY
     */
    static Num addCost(Num rawPrice, Num holdingCost, boolean isLongTrade) {
        Num netPrice;
        if (isLongTrade) {
            netPrice = rawPrice.minus(holdingCost);
        } else {
            netPrice = rawPrice.plus(holdingCost);
        }
        return netPrice;
    }

    /**
     * Pads {@link #values} with its last value up until {@code endIndex}.
     *
     * @param endIndex the end index
     */
    private void fillToTheEnd(int endIndex) {
        if (endIndex >= values.size()) {
            Num lastValue = values.get(values.size() - 1);
            values.addAll(Collections.nCopies(barSeries.getEndIndex() - values.size() + 1, lastValue));
        }
    }

    /**
     * Determines the valid final index to be considered.
     *
     * @param position   the position
     * @param finalIndex index up until cash flows of open positions are considered
     * @param maxIndex   maximal valid index
     */
    static int determineEndIndex(Position position, int finalIndex, int maxIndex) {
        int idx = finalIndex;
        // After closing of position, no further accrual necessary
        if (position.getExit() != null) {
            idx = Math.min(position.getExit().getIndex(), finalIndex);
        }
        // Accrual at most until maximal index of asset data
        if (idx > maxIndex) {
            idx = maxIndex;
        }
        return idx;
    }
}
