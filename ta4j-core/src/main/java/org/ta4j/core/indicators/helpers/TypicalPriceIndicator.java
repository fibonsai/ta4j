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

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

/**
 * Typical Price indicator.
 * 
 * <p>The Typical Price is a fundamental price indicator that represents the average
 * of the high, low, and closing prices for each bar. It provides a more balanced view
 * of price action than using the closing price alone and is widely used as the basis
 * for other technical indicators and volume-weighted calculations.
 * 
 * <h2>Calculation</h2>
 * <p>Typical Price = (High + Low + Close) / 3
 * <br>This simple arithmetic mean of the three key price points gives equal weight
 * to each component, providing a central tendency measure that reflects the overall
 * price action during each period.
 * 
 * <h2>Characteristics</h2>
 * <ul>
 * <li><strong>Balanced Representation:</strong> Incorporates range (high/low) and closing sentiment</li>
 * <li><strong>Smooth Price Action:</strong> Less volatile than close price alone</li>
 * <li><strong>Volume Calculations:</strong> Standard input for volume-weighted indicators</li>
 * <li><strong>Universal Application:</strong> Works across all timeframes and markets</li>
 * <li><strong>Immediate Stability:</strong> No warm-up period required</li>
 * </ul>
 * 
 * <h2>Advantages over Single Price Points</h2>
 * <ul>
 * <li><strong>vs. Close Price:</strong> Includes intrabar range information</li>
 * <li><strong>vs. High/Low:</strong> Dampened by closing price sentiment</li>
 * <li><strong>vs. Open Price:</strong> Reflects actual trading activity, not just start</li>
 * <li><strong>vs. OHLC Average:</strong> Emphasizes close price (3/4 weight total)</li>
 * </ul>
 * 
 * <h2>Common Applications</h2>
 * <ul>
 * <li><strong>Volume-Weighted Indicators:</strong>
 *     <ul>
 *     <li>VWAP (Volume Weighted Average Price)</li>
 *     <li>Money Flow Index (MFI)</li>
 *     <li>Volume Rate of Change</li>
 *     </ul>
 * </li>
 * <li><strong>Trend Analysis:</strong>
 *     <ul>
 *     <li>Moving averages with reduced noise</li>
 *     <li>Support and resistance level identification</li>
 *     <li>Channel and band calculations</li>
 *     </ul>
 * </li>
 * <li><strong>Oscillator Calculations:</strong>
 *     <ul>
 *     <li>CCI (Commodity Channel Index)</li>
 *     <li>Williams %R variations</li>
 *     <li>Custom momentum indicators</li>
 *     </ul>
 * </li>
 * </ul>
 * 
 * <h2>Alternative Price Formulations</h2>
 * <ul>
 * <li><strong>Typical Price:</strong> (H + L + C) / 3 (this indicator)</li>
 * <li><strong>Weighted Close:</strong> (H + L + 2C) / 4 (emphasizes close)</li>
 * <li><strong>Median Price:</strong> (H + L) / 2 (ignores close)</li>
 * <li><strong>OHLC Average:</strong> (O + H + L + C) / 4 (includes open)</li>
 * </ul>
 * 
 * <h2>Market Context Usage</h2>
 * <ul>
 * <li><strong>High Volatility:</strong> Provides stability compared to individual prices</li>
 * <li><strong>Gap Markets:</strong> Better represents trading range than close alone</li>
 * <li><strong>Volume Analysis:</strong> Essential for volume-price relationship studies</li>
 * <li><strong>Smooth Indicators:</strong> Creates less noisy moving averages and oscillators</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Create Typical Price indicator
 * TypicalPriceIndicator typicalPrice = new TypicalPriceIndicator(series);
 * 
 * // Use in moving averages
 * SMAIndicator typicalSMA = new SMAIndicator(typicalPrice, 20);
 * EMAIndicator typicalEMA = new EMAIndicator(typicalPrice, 12);
 * 
 * // Volume-weighted applications
 * VWAPIndicator vwap = new VWAPIndicator(series, 20); // Uses typical price internally
 * MFIIndicator mfi = new MFIIndicator(series, 14); // Money Flow Index
 * 
 * // Custom oscillators
 * RSIIndicator typicalRSI = new RSIIndicator(typicalPrice, 14);
 * StochasticOscillatorKIndicator stochTypical = new StochasticOscillatorKIndicator(
 *     typicalPrice, 14, new HighPriceIndicator(series), new LowPriceIndicator(series));
 * 
 * // Bollinger Bands on typical price
 * BollingerBandsMiddleIndicator bbMiddle = new BollingerBandsMiddleIndicator(
 *     new SMAIndicator(typicalPrice, 20));
 * 
 * // Trading rules with typical price
 * SMAIndicator tpSMA = new SMAIndicator(typicalPrice, 50);
 * Rule trendUp = new OverIndicatorRule(typicalPrice, tpSMA);
 * 
 * // Compare typical price to close price for sentiment
 * ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
 * Rule bullishSentiment = new OverIndicatorRule(closePrice, typicalPrice);
 * }</pre>
 * 
 * @see org.ta4j.core.indicators.helpers.ClosePriceIndicator
 * @see org.ta4j.core.indicators.helpers.HighPriceIndicator
 * @see org.ta4j.core.indicators.helpers.LowPriceIndicator
 * @see org.ta4j.core.indicators.helpers.MedianPriceIndicator
 * @see org.ta4j.core.indicators.helpers.WeightedCloseIndicator
 * @see org.ta4j.core.indicators.volume.VWAPIndicator
 * @since 0.1
 */
public class TypicalPriceIndicator extends CachedIndicator<Num> {

    /**
     * Creates a Typical Price indicator.
     * 
     * <p>The typical price calculation extracts the high, low, and close prices
     * from each bar in the series and computes their arithmetic mean. This provides
     * a balanced price representation that includes both range and closing information.
     * 
     * <p>This indicator is immediately stable and produces valid values from the first bar,
     * making it suitable for use as input to other indicators without warm-up considerations.
     *
     * @param series the bar series containing OHLC price data (must not be null)
     * @throws IllegalArgumentException if series is null
     */
    public TypicalPriceIndicator(BarSeries series) {
        super(series);
    }

    @Override
    protected Num calculate(int index) {
        final Bar bar = getBarSeries().getBar(index);
        final Num highPrice = bar.getHighPrice();
        final Num lowPrice = bar.getLowPrice();
        final Num closePrice = bar.getClosePrice();
        return highPrice.plus(lowPrice).plus(closePrice).dividedBy(getBarSeries().numFactory().three());
    }

    /**
     * Returns the number of unstable bars for this indicator.
     * 
     * <p>The Typical Price indicator is immediately stable as it performs a simple
     * arithmetic calculation on current bar data without requiring historical context
     * or warm-up periods.
     *
     * @return {@code 0} (immediately stable)
     */
    @Override
    public int getCountOfUnstableBars() {
        return 0;
    }
}
