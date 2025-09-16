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

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.averages.EMAIndicator;
import org.ta4j.core.indicators.numeric.NumericIndicator;
import org.ta4j.core.num.Num;

/**
 * Moving Average Convergence Divergence (MACD) indicator.
 * 
 * <p>MACD is one of the most popular momentum oscillators used in technical analysis.
 * It shows the relationship between two moving averages of an asset's price and is used
 * to identify trend changes, momentum shifts, and generate buy/sell signals.
 * 
 * <h2>Calculation</h2>
 * <p>MACD consists of three components:
 * <ol>
 * <li><strong>MACD Line:</strong> Difference between fast EMA and slow EMA</li>
 * <li><strong>Signal Line:</strong> EMA of the MACD line (typically 9 periods)</li>
 * <li><strong>Histogram:</strong> Difference between MACD line and signal line</li>
 * </ol>
 * 
 * <p>Standard MACD calculation:
 * <ul>
 * <li>MACD Line = EMA(12) - EMA(26)</li>
 * <li>Signal Line = EMA(9) of MACD Line</li>
 * <li>Histogram = MACD Line - Signal Line</li>
 * </ul>
 * 
 * <h2>Interpretation</h2>
 * <ul>
 * <li><strong>Bullish Signals:</strong>
 *     <ul>
 *     <li>MACD crosses above signal line</li>
 *     <li>MACD crosses above zero line</li>
 *     <li>Bullish divergence (price makes lower lows, MACD makes higher lows)</li>
 *     </ul>
 * </li>
 * <li><strong>Bearish Signals:</strong>
 *     <ul>
 *     <li>MACD crosses below signal line</li>
 *     <li>MACD crosses below zero line</li>
 *     <li>Bearish divergence (price makes higher highs, MACD makes lower highs)</li>
 *     </ul>
 * </li>
 * </ul>
 * 
 * <h2>Trading Applications</h2>
 * <ul>
 * <li><strong>Trend Following:</strong> Use zero-line crossovers to identify trend direction</li>
 * <li><strong>Momentum:</strong> Histogram shows strengthening or weakening momentum</li>
 * <li><strong>Divergence:</strong> Price and MACD divergence signals potential reversals</li>
 * <li><strong>Overbought/Oversold:</strong> Extreme MACD values may indicate reversal points</li>
 * </ul>
 * 
 * <h2>Limitations</h2>
 * <ul>
 * <li>Lagging indicator due to use of moving averages</li>
 * <li>Can generate false signals in sideways markets</li>
 * <li>Best used in trending markets</li>
 * <li>Should be combined with other indicators for confirmation</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Create standard MACD (12, 26, 9)
 * ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
 * MACDIndicator macd = new MACDIndicator(closePrice, 12, 26);
 * EMAIndicator signalLine = macd.getSignalLine(9);
 * NumericIndicator histogram = macd.getHistogram(9);
 * 
 * // Create trading rules
 * Rule bullishCrossover = new CrossedUpIndicatorRule(macd, signalLine);
 * Rule bearishCrossover = new CrossedDownIndicatorRule(macd, signalLine);
 * 
 * // Zero line crossover
 * Rule bullishTrend = new CrossedUpIndicatorRule(macd, 0);
 * Rule bearishTrend = new CrossedDownIndicatorRule(macd, 0);
 * }</pre>
 * 
 * @see EMAIndicator
 * @see org.ta4j.core.indicators.numeric.NumericIndicator
 * @see org.ta4j.core.rules.CrossedUpIndicatorRule
 * @see org.ta4j.core.rules.CrossedDownIndicatorRule
 * @see <a href="https://www.investopedia.com/terms/m/macd.asp">Investopedia - MACD</a>
 * @see <a href="http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:moving_average_convergence_divergence_macd">StockCharts - MACD</a>
 * @since 0.1
 */
public class MACDIndicator extends CachedIndicator<Num> {

    private final EMAIndicator shortTermEma;
    private final EMAIndicator longTermEma;

    /**
     * Creates a MACD indicator with standard parameters (12, 26).
     * 
     * <p>This is the most commonly used MACD configuration:
     * <ul>
     * <li>Fast EMA: 12 periods</li>
     * <li>Slow EMA: 26 periods</li>
     * </ul>
     * 
     * <p>For a complete MACD analysis, you'll typically also want to create
     * a 9-period signal line using {@link #getSignalLine(int)}.
     *
     * @param indicator the source indicator, typically close price (must not be null)
     * @throws IllegalArgumentException if indicator is null
     */
    public MACDIndicator(Indicator<Num> indicator) {
        this(indicator, 12, 26);
    }

    /**
     * Creates a MACD indicator with custom EMA periods.
     * 
     * <p>This constructor allows you to customize the fast and slow EMA periods.
     * The short period must be less than the long period for the MACD to work correctly.
     * Common combinations include 5/35, 8/21, or the standard 12/26.
     *
     * @param indicator     the source indicator, typically close price (must not be null)
     * @param shortBarCount the fast EMA period, must be < longBarCount (must be > 0)
     * @param longBarCount  the slow EMA period, must be > shortBarCount (must be > 0)
     * @throws IllegalArgumentException if indicator is null, periods are <= 0, or shortBarCount >= longBarCount
     */
    public MACDIndicator(Indicator<Num> indicator, int shortBarCount, int longBarCount) {
        super(indicator);
        if (shortBarCount > longBarCount) {
            throw new IllegalArgumentException("Long term period count must be greater than short term period count");
        }
        this.shortTermEma = new EMAIndicator(indicator, shortBarCount);
        this.longTermEma = new EMAIndicator(indicator, longBarCount);
    }

    /**
     * Returns the fast (short-term) EMA used in the MACD calculation.
     * 
     * <p>This is the faster-moving average that reacts more quickly to price changes.
     * In the standard MACD, this is typically a 12-period EMA.
     *
     * @return the fast EMA indicator component
     */
    public EMAIndicator getShortTermEma() {
        return shortTermEma;
    }

    /**
     * Returns the slow (long-term) EMA used in the MACD calculation.
     * 
     * <p>This is the slower-moving average that provides stability to the MACD.
     * In the standard MACD, this is typically a 26-period EMA.
     *
     * @return the slow EMA indicator component
     */
    public EMAIndicator getLongTermEma() {
        return longTermEma;
    }

    /**
     * Creates the MACD signal line, which is an EMA of the MACD line.
     * 
     * <p>The signal line is used to generate buy/sell signals when the MACD line
     * crosses above or below it. The standard signal line uses a 9-period EMA.
     * Crossovers between the MACD line and signal line are among the most common
     * MACD trading signals.
     *
     * @param barCount the number of periods for the signal line EMA, typically 9 (must be > 0)
     * @return a new EMA indicator representing the signal line
     * @throws IllegalArgumentException if barCount <= 0
     */
    public EMAIndicator getSignalLine(int barCount) {
        return new EMAIndicator(this, barCount);
    }

    /**
     * Creates the MACD histogram, which shows the difference between MACD and signal lines.
     * 
     * <p>The histogram provides insight into the momentum of the MACD:
     * <ul>
     * <li>Positive histogram: MACD is above signal line (bullish momentum)</li>
     * <li>Negative histogram: MACD is below signal line (bearish momentum)</li>
     * <li>Growing histogram: Strengthening momentum</li>
     * <li>Shrinking histogram: Weakening momentum</li>
     * </ul>
     * 
     * <p>Histogram peaks often precede MACD line crossovers, making it useful
     * for early signal detection.
     *
     * @param barCount the signal line period used for histogram calculation, typically 9 (must be > 0)
     * @return a numeric indicator representing the MACD histogram
     * @throws IllegalArgumentException if barCount <= 0
     */
    public NumericIndicator getHistogram(int barCount) {
        return NumericIndicator.of(this).minus(getSignalLine(barCount));
    }

    @Override
    protected Num calculate(int index) {
        return shortTermEma.getValue(index).minus(longTermEma.getValue(index));
    }

    @Override
    public int getCountOfUnstableBars() {
        return 0;
    }
}
