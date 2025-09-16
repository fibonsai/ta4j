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
 * Median Price indicator.
 * 
 * <p>The Median Price indicator represents the midpoint of each bar's trading range
 * by calculating the average of the high and low prices. It provides a central
 * reference point that captures the middle of the price action and is less influenced
 * by closing sentiment compared to other price indicators.
 * 
 * <h2>Calculation</h2>
 * <p>Median Price = (High + Low) / 2
 * <br>This simple arithmetic mean of the high and low prices provides the midpoint
 * of each bar's trading range, regardless of opening or closing price levels.
 * 
 * <h2>Characteristics</h2>
 * <ul>
 * <li><strong>Range-Based:</strong> Focuses purely on the trading range (high to low)</li>
 * <li><strong>Neutral Sentiment:</strong> Not influenced by opening or closing bias</li>
 * <li><strong>Stability:</strong> Less volatile than close-based indicators</li>
 * <li><strong>Central Tendency:</strong> Represents the average price level within each bar</li>
 * <li><strong>Immediate Availability:</strong> No warm-up period required</li>
 * </ul>
 * 
 * <h2>Advantages over Other Price Indicators</h2>
 * <ul>
 * <li><strong>vs. Close Price:</strong> Not affected by last-minute price movements or sentiment</li>
 * <li><strong>vs. Typical Price:</strong> Simpler calculation, focuses on range rather than close</li>
 * <li><strong>vs. Weighted Close:</strong> Equal weighting of high and low, no close bias</li>
 * <li><strong>vs. OHLC Average:</strong> Excludes open price, focuses on intrabar range</li>
 * </ul>
 * 
 * <h2>Trading Applications</h2>
 * <ul>
 * <li><strong>Support and Resistance:</strong>
 *     <ul>
 *     <li>Identify key price levels based on range midpoints</li>
 *     <li>Median price levels often act as psychological support/resistance</li>
 *     </ul>
 * </li>
 * <li><strong>Trend Analysis:</strong>
 *     <ul>
 *     <li>Smoother trend lines using median price instead of close</li>
 *     <li>Less noisy moving averages and technical indicators</li>
 *     </ul>
 * </li>
 * <li><strong>Range Analysis:</strong>
 *     <ul>
 *     <li>Study price behavior relative to range midpoints</li>
 *     <li>Identify range expansion and contraction patterns</li>
 *     </ul>
 * </li>
 * <li><strong>Momentum Indicators:</strong>
 *     <ul>
 *     <li>Alternative input for RSI, MACD, and other oscillators</li>
 *     <li>Reduces noise in momentum calculations</li>
 *     </ul>
 * </li>
 * </ul>
 * 
 * <h2>Market Context Usage</h2>
 * <ul>
 * <li><strong>Range-Bound Markets:</strong> Excellent for identifying range midpoints and reversals</li>
 * <li><strong>Trending Markets:</strong> Provides smoother trend analysis without close price noise</li>
 * <li><strong>Gap Markets:</strong> Not affected by gap openings, focuses on actual trading range</li>
 * <li><strong>Volatile Markets:</strong> Offers stability by averaging high and low extremes</li>
 * </ul>
 * 
 * <h2>Alternative Price Formulations</h2>
 * <ul>
 * <li><strong>Median Price:</strong> (H + L) / 2 (this indicator)</li>
 * <li><strong>Typical Price:</strong> (H + L + C) / 3 (includes close)</li>
 * <li><strong>Weighted Close:</strong> (H + L + 2C) / 4 (emphasizes close)</li>
 * <li><strong>OHLC Average:</strong> (O + H + L + C) / 4 (includes all prices)</li>
 * </ul>
 * 
 * <h2>Limitations</h2>
 * <ul>
 * <li>Ignores opening and closing price information</li>
 * <li>May not capture intraday sentiment or bias</li>
 * <li>Less suitable for strategies requiring close price behavior</li>
 * <li>May smooth out important closing action signals</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Create Median Price indicator
 * MedianPriceIndicator medianPrice = new MedianPriceIndicator(series);
 * 
 * // Use in moving averages for smoother trends
 * SMAIndicator medianSMA = new SMAIndicator(medianPrice, 20);
 * EMAIndicator medianEMA = new EMAIndicator(medianPrice, 12);
 * 
 * // Range-based analysis
 * ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
 * Rule priceAboveMedian = new OverIndicatorRule(closePrice, medianPrice); // Close above range midpoint
 * Rule priceBelowMedian = new UnderIndicatorRule(closePrice, medianPrice); // Close below range midpoint
 * 
 * // Median price oscillators
 * RSIIndicator medianRSI = new RSIIndicator(medianPrice, 14);
 * MACDIndicator medianMACD = new MACDIndicator(medianPrice, 12, 26);
 * 
 * // Support/resistance based on median price levels
 * SMAIndicator medianSupport = new SMAIndicator(medianPrice, 50);
 * Rule supportTest = new UnderIndicatorRule(medianPrice, medianSupport);
 * 
 * // Range expansion detection
 * HighPriceIndicator highPrice = new HighPriceIndicator(series);
 * LowPriceIndicator lowPrice = new LowPriceIndicator(series);
 * NumericIndicator rangeSize = NumericIndicator.of(highPrice).minus(lowPrice);
 * NumericIndicator rangeFromMedian = NumericIndicator.of(closePrice).minus(medianPrice).abs();
 * 
 * Rule rangeExpansion = new OverIndicatorRule(rangeFromMedian, 
 *     new SMAIndicator(rangeFromMedian, 20).multipliedBy(1.5));
 * 
 * // Compare with other price measures
 * TypicalPriceIndicator typicalPrice = new TypicalPriceIndicator(series);
 * Rule medianVsTypical = new OverIndicatorRule(medianPrice, typicalPrice);
 * 
 * // Median price trend strategy
 * Rule entryRule = new CrossedUpIndicatorRule(closePrice, medianSMA);
 * Rule exitRule = new CrossedDownIndicatorRule(closePrice, medianSMA);
 * }</pre>
 * 
 * @see org.ta4j.core.indicators.helpers.TypicalPriceIndicator
 * @see org.ta4j.core.indicators.helpers.ClosePriceIndicator
 * @see org.ta4j.core.indicators.helpers.HighPriceIndicator
 * @see org.ta4j.core.indicators.helpers.LowPriceIndicator
 * @see org.ta4j.core.indicators.helpers.WeightedCloseIndicator
 * @since 0.1
 */
public class MedianPriceIndicator extends CachedIndicator<Num> {

    /**
     * Creates a Median Price indicator.
     * 
     * <p>The median price calculation extracts the high and low prices from each bar
     * and computes their arithmetic mean, providing the midpoint of the trading range.
     * This offers a price reference that focuses purely on the range without any bias
     * toward opening or closing sentiment.
     * 
     * <p>This indicator is immediately stable and produces valid values from the first bar,
     * making it suitable for use as input to other indicators without warm-up considerations.
     *
     * @param series the bar series containing OHLC price data (must not be null)
     * @throws IllegalArgumentException if series is null
     */
    public MedianPriceIndicator(BarSeries series) {
        super(series);
    }

    @Override
    protected Num calculate(int index) {
        final Bar bar = getBarSeries().getBar(index);
        return bar.getHighPrice().plus(bar.getLowPrice()).dividedBy(getBarSeries().numFactory().two());
    }

    /**
     * Returns the number of unstable bars for this indicator.
     * 
     * <p>The Median Price indicator is immediately stable as it performs a simple
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
