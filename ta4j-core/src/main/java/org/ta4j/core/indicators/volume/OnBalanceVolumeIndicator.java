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
package org.ta4j.core.indicators.volume;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.RecursiveCachedIndicator;
import org.ta4j.core.num.Num;

/**
 * On-Balance Volume (OBV) indicator.
 * 
 * <p>On-Balance Volume is a momentum indicator developed by Joe Granville that uses
 * volume flow to predict changes in stock price. It operates on the principle that
 * volume precedes price movement, making it a leading indicator for potential price
 * direction changes.
 * 
 * <h2>Calculation</h2>
 * <p>OBV calculation follows these rules:
 * <ol>
 * <li><strong>If Close > Previous Close:</strong> OBV = Previous OBV + Volume</li>
 * <li><strong>If Close < Previous Close:</strong> OBV = Previous OBV - Volume</li>
 * <li><strong>If Close = Previous Close:</strong> OBV = Previous OBV (unchanged)</li>
 * </ol>
 * 
 * <p>The OBV line starts at zero and accumulates volume based on price direction,
 * creating a running total that shows the cumulative buying and selling pressure.
 * 
 * <h2>Interpretation</h2>
 * <ul>
 * <li><strong>Rising OBV:</strong> Suggests accumulation (buying pressure)</li>
 * <li><strong>Falling OBV:</strong> Suggests distribution (selling pressure)</li>
 * <li><strong>Flat OBV:</strong> Suggests balance between buying and selling</li>
 * <li><strong>OBV Trend:</strong> Should confirm the price trend for validation</li>
 * </ul>
 * 
 * <h2>Trading Signals</h2>
 * <ul>
 * <li><strong>Trend Confirmation:</strong>
 *     <ul>
 *     <li>Rising prices with rising OBV = Strong uptrend</li>
 *     <li>Falling prices with falling OBV = Strong downtrend</li>
 *     </ul>
 * </li>
 * <li><strong>Divergence Analysis:</strong>
 *     <ul>
 *     <li>Bullish Divergence: Price makes lower lows, OBV makes higher lows</li>
 *     <li>Bearish Divergence: Price makes higher highs, OBV makes lower highs</li>
 *     </ul>
 * </li>
 * <li><strong>Breakout Confirmation:</strong>
 *     <ul>
 *     <li>Price breakout accompanied by OBV breakout = Valid breakout</li>
 *     <li>Price breakout without OBV confirmation = Weak breakout</li>
 *     </ul>
 * </li>
 * </ul>
 * 
 * <h2>Advantages</h2>
 * <ul>
 * <li>Leading indicator that can predict price movements</li>
 * <li>Simple to calculate and interpret</li>
 * <li>Effective for confirming trends and breakouts</li>
 * <li>Works well across different timeframes</li>
 * <li>Helps identify accumulation and distribution phases</li>
 * </ul>
 * 
 * <h2>Limitations</h2>
 * <ul>
 * <li>Can generate false signals in volatile markets</li>
 * <li>Absolute OBV values are less meaningful than trends</li>
 * <li>Does not account for the magnitude of price changes</li>
 * <li>May lag in very fast-moving markets</li>
 * <li>Requires volume data (not available for all instruments)</li>
 * </ul>
 * 
 * <h2>Analysis Techniques</h2>
 * <ul>
 * <li><strong>Trend Analysis:</strong> Use moving averages of OBV for smoother trends</li>
 * <li><strong>Support/Resistance:</strong> OBV can have its own support and resistance levels</li>
 * <li><strong>Percentage Analysis:</strong> Compare OBV percentage changes with price changes</li>
 * <li><strong>Pattern Recognition:</strong> Look for chart patterns in OBV line</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Create On-Balance Volume indicator
 * OnBalanceVolumeIndicator obv = new OnBalanceVolumeIndicator(series);
 * 
 * // Get current OBV value
 * Num currentOBV = obv.getValue(series.getEndIndex());
 * 
 * // Create OBV moving average for trend analysis
 * SMAIndicator obvSMA = new SMAIndicator(obv, 20);
 * 
 * // OBV trend rules
 * Rule obvRising = new CrossedUpIndicatorRule(obv, obvSMA);
 * Rule obvFalling = new CrossedDownIndicatorRule(obv, obvSMA);
 * 
 * // Combine with price action
 * ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
 * SMAIndicator priceSMA = new SMAIndicator(closePrice, 20);
 * Rule priceUptrend = new OverIndicatorRule(closePrice, priceSMA);
 * 
 * // Trend confirmation strategy
 * Rule strongUptrend = priceUptrend.and(obvRising);
 * Rule trendWeakening = priceUptrend.and(obvFalling); // Potential divergence
 * 
 * // Volume breakout confirmation
 * Rule volumeConfirmedBreakout = new OverIndicatorRule(closePrice, resistance)
 *     .and(new OverIndicatorRule(obv, obvResistance));
 * }</pre>
 * 
 * @see org.ta4j.core.indicators.averages.SMAIndicator
 * @see org.ta4j.core.rules.CrossedUpIndicatorRule
 * @see org.ta4j.core.rules.CrossedDownIndicatorRule
 * @see <a href="https://www.investopedia.com/terms/o/onbalancevolume.asp">Investopedia - On-Balance Volume</a>
 * @see <a href="https://school.stockcharts.com/doku.php?id=technical_indicators:on_balance_volume_obv">StockCharts - OBV</a>
 * @since 0.1
 */
public class OnBalanceVolumeIndicator extends RecursiveCachedIndicator<Num> {

    /**
     * Creates an On-Balance Volume indicator.
     * 
     * <p>The OBV calculation uses the close prices and volume from each bar to determine
     * the cumulative volume flow. The indicator starts at zero and builds a running
     * total based on whether each bar closes higher, lower, or unchanged from the previous bar.
     * 
     * <p><strong>Note:</strong> This indicator is immediately stable as it doesn't require
     * a warm-up period, though the first value will always be zero since there's no
     * previous bar for comparison.
     *
     * @param series the bar series containing price and volume data (must not be null)
     * @throws IllegalArgumentException if series is null
     * @throws IllegalStateException if the series doesn't contain volume data
     */
    public OnBalanceVolumeIndicator(BarSeries series) {
        super(series);
    }

    @Override
    protected Num calculate(int index) {
        if (index == 0) {
            return getBarSeries().numFactory().zero();
        }
        final Num prevClose = getBarSeries().getBar(index - 1).getClosePrice();
        final Num currentClose = getBarSeries().getBar(index).getClosePrice();

        final Num obvPrev = getValue(index - 1);
        if (prevClose.isGreaterThan(currentClose)) {
            return obvPrev.minus(getBarSeries().getBar(index).getVolume());
        } else if (prevClose.isLessThan(currentClose)) {
            return obvPrev.plus(getBarSeries().getBar(index).getVolume());
        } else {
            return obvPrev;
        }
    }

    @Override
    public int getCountOfUnstableBars() {
        return 0;
    }
}
