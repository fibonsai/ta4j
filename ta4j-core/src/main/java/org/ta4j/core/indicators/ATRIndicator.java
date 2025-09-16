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
package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.averages.MMAIndicator;
import org.ta4j.core.indicators.helpers.TRIndicator;
import org.ta4j.core.num.Num;

/**
 * Average True Range (ATR) indicator.
 * 
 * <p>The Average True Range is a volatility indicator developed by J. Welles Wilder Jr.
 * It measures market volatility by calculating the average of true ranges over a specified period.
 * ATR does not indicate price direction but rather the degree of price movement or volatility.
 * 
 * <h2>Calculation</h2>
 * <p>ATR is calculated in two steps:
 * <ol>
 * <li><strong>True Range (TR):</strong> The greatest of:
 *     <ul>
 *     <li>Current High - Current Low</li>
 *     <li>|Current High - Previous Close|</li>
 *     <li>|Current Low - Previous Close|</li>
 *     </ul>
 * </li>
 * <li><strong>ATR:</strong> Modified Moving Average (MMA) of True Range over n periods</li>
 * </ol>
 * 
 * <h2>Interpretation</h2>
 * <ul>
 * <li><strong>High ATR:</strong> High volatility, large price movements</li>
 * <li><strong>Low ATR:</strong> Low volatility, small price movements</li>
 * <li><strong>Rising ATR:</strong> Increasing volatility</li>
 * <li><strong>Falling ATR:</strong> Decreasing volatility</li>
 * </ul>
 * 
 * <h2>Trading Applications</h2>
 * <ul>
 * <li><strong>Stop Loss Placement:</strong> Set stops at multiples of ATR (e.g., 2×ATR)</li>
 * <li><strong>Position Sizing:</strong> Adjust position size based on volatility</li>
 * <li><strong>Breakout Confirmation:</strong> Higher ATR confirms significant breakouts</li>
 * <li><strong>Market State:</strong> Identify trending vs. ranging markets</li>
 * <li><strong>Risk Management:</strong> Measure and manage portfolio risk</li>
 * </ul>
 * 
 * <h2>Advantages</h2>
 * <ul>
 * <li>Provides objective volatility measurement</li>
 * <li>Adapts to current market conditions</li>
 * <li>Useful across all timeframes and markets</li>
 * <li>Foundation for many risk management techniques</li>
 * </ul>
 * 
 * <h2>Limitations</h2>
 * <ul>
 * <li>Does not indicate price direction</li>
 * <li>Can remain high/low for extended periods</li>
 * <li>Requires sufficient historical data for accuracy</li>
 * <li>May lag sudden volatility changes</li>
 * </ul>
 * 
 * <h2>Common Periods</h2>
 * <ul>
 * <li><strong>Standard:</strong> 14 periods (Wilder's original recommendation)</li>
 * <li><strong>Short-term:</strong> 7-10 periods (more sensitive)</li>
 * <li><strong>Long-term:</strong> 20-50 periods (more stable)</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Create 14-period ATR
 * ATRIndicator atr = new ATRIndicator(series, 14);
 * 
 * // Use for stop loss placement (2x ATR below entry)
 * ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
 * NumericIndicator stopLevel = NumericIndicator.of(closePrice).minus(
 *     NumericIndicator.of(atr).multipliedBy(2));
 * 
 * // Use for position sizing
 * Num currentATR = atr.getValue(series.getEndIndex());
 * Num riskPerUnit = currentATR.multipliedBy(2); // 2x ATR risk
 * 
 * // Volatility-based entry rule
 * Rule highVolatility = new OverIndicatorRule(atr, atr.getValue(series.getEndIndex()).multipliedBy(1.5));
 * }</pre>
 * 
 * @see TRIndicator
 * @see org.ta4j.core.indicators.averages.MMAIndicator
 * @see org.ta4j.core.rules.AverageTrueRangeStopLossRule
 * @see <a href="https://www.investopedia.com/terms/a/atr.asp">Investopedia - Average True Range</a>
 * @see <a href="https://school.stockcharts.com/doku.php?id=technical_indicators:average_true_range_atr">StockCharts - ATR</a>
 * @since 0.1
 */
public class ATRIndicator extends AbstractIndicator<Num> {

    private final TRIndicator trIndicator;
    private final MMAIndicator averageTrueRangeIndicator;

    /**
     * Creates an Average True Range indicator for the specified bar series.
     * 
     * <p>This constructor creates a new {@link TRIndicator} internally and calculates
     * the ATR using Wilder's Modified Moving Average method over the specified period.
     * This is the most common way to create an ATR indicator.
     *
     * @param series   the bar series to calculate ATR for (must not be null)
     * @param barCount the number of periods for ATR calculation, typically 14 (must be > 0)
     * @throws IllegalArgumentException if series is null or barCount <= 0
     */
    public ATRIndicator(BarSeries series, int barCount) {
        this(new TRIndicator(series), barCount);
    }

    /**
     * Creates an Average True Range indicator using an existing True Range indicator.
     * 
     * <p>This constructor allows you to reuse an existing {@link TRIndicator} instance,
     * which can be useful for performance optimization when multiple indicators need
     * the same True Range calculation.
     *
     * @param tr       the True Range indicator to calculate ATR from (must not be null)
     * @param barCount the number of periods for ATR calculation, typically 14 (must be > 0)
     * @throws IllegalArgumentException if tr is null or barCount <= 0
     */
    public ATRIndicator(TRIndicator tr, int barCount) {
        super(tr.getBarSeries());
        this.trIndicator = tr;
        this.averageTrueRangeIndicator = new MMAIndicator(tr, barCount);
    }

    @Override
    public Num getValue(int index) {
        return averageTrueRangeIndicator.getValue(index);
    }

    @Override
    public int getCountOfUnstableBars() {
        return getBarCount();
    }

    /**
     * Returns the True Range indicator used for ATR calculation.
     * 
     * <p>The True Range indicator calculates the true range for each bar,
     * which is then averaged to produce the ATR. This can be useful for
     * accessing individual TR values or for creating other indicators
     * that depend on True Range.
     *
     * @return the underlying True Range indicator
     */
    public TRIndicator getTRIndicator() {
        return trIndicator;
    }

    /**
     * Returns the number of periods used for ATR calculation.
     * 
     * <p>This is the smoothing period for the Modified Moving Average
     * applied to the True Range values. A typical value is 14 periods,
     * as originally recommended by Wilder.
     *
     * @return the number of periods in the ATR calculation
     */
    public int getBarCount() {
        return averageTrueRangeIndicator.getBarCount();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " barCount: " + getBarCount();
    }
}
