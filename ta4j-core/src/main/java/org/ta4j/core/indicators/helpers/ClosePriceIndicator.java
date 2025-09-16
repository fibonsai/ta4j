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
package org.ta4j.core.indicators.helpers;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.AbstractIndicator;
import org.ta4j.core.num.Num;

/**
 * Close Price indicator that extracts the closing price from each bar in a series.
 * 
 * <p>This is one of the most fundamental indicators in technical analysis, providing
 * access to the closing price of each bar. The close price is typically the most
 * important price point as it represents the final agreed-upon value between buyers
 * and sellers at the end of each trading period.
 * 
 * <h2>Characteristics</h2>
 * <ul>
 * <li><strong>Stable:</strong> Always stable (no warm-up period required)</li>
 * <li><strong>Performance:</strong> Direct access to bar data with no calculations</li>
 * <li><strong>Foundation:</strong> Base for most other technical indicators</li>
 * <li><strong>Universal:</strong> Available for all market data types</li>
 * </ul>
 * 
 * <h2>Common Usage</h2>
 * <ul>
 * <li><strong>Moving Averages:</strong> Calculate SMA, EMA, etc. on close prices</li>
 * <li><strong>Oscillators:</strong> RSI, MACD, Stochastic calculations</li>
 * <li><strong>Price Rules:</strong> Compare close price with indicators or thresholds</li>
 * <li><strong>Trend Analysis:</strong> Identify price trends and patterns</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Create close price indicator
 * ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
 * 
 * // Use with moving averages
 * SMAIndicator sma20 = new SMAIndicator(closePrice, 20);
 * EMAIndicator ema12 = new EMAIndicator(closePrice, 12);
 * 
 * // Use with oscillators
 * RSIIndicator rsi = new RSIIndicator(closePrice, 14);
 * 
 * // Use in trading rules
 * Rule priceAboveSMA = new OverIndicatorRule(closePrice, sma20);
 * }</pre>
 * 
 * @see org.ta4j.core.indicators.helpers.OpenPriceIndicator
 * @see org.ta4j.core.indicators.helpers.HighPriceIndicator
 * @see org.ta4j.core.indicators.helpers.LowPriceIndicator
 * @see org.ta4j.core.Bar#getClosePrice()
 * @since 0.1
 */
public class ClosePriceIndicator extends AbstractIndicator<Num> {

    /**
     * Creates a Close Price indicator for the specified bar series.
     * 
     * <p>This indicator provides direct access to the closing price of each bar
     * in the series without any calculations or transformations.
     *
     * @param series the bar series to extract close prices from (must not be null)
     * @throws IllegalArgumentException if series is null
     */
    public ClosePriceIndicator(BarSeries series) {
        super(series);
    }

    @Override
    public Num getValue(int index) {
        return getBarSeries().getBar(index).getClosePrice();
    }

    /**
     * Returns the number of unstable bars for this indicator.
     * 
     * <p>The close price indicator is always stable as it requires no calculations
     * or historical data - it simply returns the close price of the requested bar.
     * 
     * @return always returns 0 (immediately stable)
     */
    @Override
    public int getCountOfUnstableBars() {
        return 0;
    }
}
