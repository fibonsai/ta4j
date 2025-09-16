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
package org.ta4j.core.indicators.averages;

import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

/**
 * Exponential Moving Average (EMA) indicator.
 * 
 * <p>The Exponential Moving Average is a type of moving average that gives more weight to recent prices,
 * making it more responsive to new information compared to a Simple Moving Average (SMA).
 * Unlike SMA which weights all values equally, EMA applies exponentially decreasing weights
 * to older data points.
 * 
 * <h2>Calculation</h2>
 * <p>EMA calculation uses a smoothing factor (α) derived from the period:
 * <ul>
 * <li>α = 2 / (n + 1), where n is the number of periods</li>
 * <li>EMA(today) = α × Price(today) + (1 - α) × EMA(yesterday)</li>
 * <li>Initial EMA = First price value</li>
 * </ul>
 * 
 * <h2>Characteristics</h2>
 * <ul>
 * <li><strong>Responsiveness:</strong> More responsive to recent price changes than SMA</li>
 * <li><strong>Smoothing:</strong> Still provides smoothing while reducing lag</li>
 * <li><strong>Weighting:</strong> Recent prices have exponentially higher influence</li>
 * <li><strong>Continuity:</strong> Uses all historical data (theoretically infinite memory)</li>
 * </ul>
 * 
 * <h2>Advantages over SMA</h2>
 * <ul>
 * <li>Faster reaction to trend changes</li>
 * <li>Better for momentum strategies</li>
 * <li>Reduces lag while maintaining smoothing</li>
 * <li>More suitable for volatile markets</li>
 * </ul>
 * 
 * <h2>Common Applications</h2>
 * <ul>
 * <li><strong>Trend Following:</strong> Price crossing above/below EMA signals</li>
 * <li><strong>Support/Resistance:</strong> EMA acts as dynamic support/resistance</li>
 * <li><strong>MACD:</strong> Foundation for MACD oscillator calculation</li>
 * <li><strong>Multiple EMAs:</strong> Crossover strategies using different periods</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Create 12-period EMA on close prices
 * ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
 * EMAIndicator ema12 = new EMAIndicator(closePrice, 12);
 * 
 * // Create EMA crossover strategy (12/26)
 * EMAIndicator ema26 = new EMAIndicator(closePrice, 26);
 * Rule entryRule = new CrossedUpIndicatorRule(ema12, ema26);
 * Rule exitRule = new CrossedDownIndicatorRule(ema12, ema26);
 * 
 * // Use in MACD calculation
 * MACDIndicator macd = new MACDIndicator(closePrice, 12, 26);
 * }</pre>
 * 
 * @see SMAIndicator
 * @see AbstractEMAIndicator
 * @see MACDIndicator
 * @see <a href="https://www.investopedia.com/terms/e/ema.asp">Investopedia - Exponential Moving Average</a>
 * @since 0.1
 */
public class EMAIndicator extends AbstractEMAIndicator {

    /**
     * Creates an Exponential Moving Average indicator.
     * 
     * <p>This constructor creates an EMA with the standard smoothing factor of 2/(barCount+1).
     * This is the most commonly used EMA calculation method and matches the default
     * implementation found in most trading platforms and technical analysis software.
     *
     * @param indicator the source indicator to calculate the EMA over (must not be null)
     * @param barCount  the number of periods for the EMA calculation (must be > 0)
     * @throws IllegalArgumentException if indicator is null or barCount <= 0
     */
    public EMAIndicator(Indicator<Num> indicator, int barCount) {
        super(indicator, barCount, (2.0 / (barCount + 1)));
    }

    @Override
    public int getCountOfUnstableBars() {
        return getBarCount();
    }
}
