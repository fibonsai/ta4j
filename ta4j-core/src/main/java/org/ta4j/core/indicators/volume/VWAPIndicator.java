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
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.helpers.TypicalPriceIndicator;
import org.ta4j.core.indicators.helpers.VolumeIndicator;
import org.ta4j.core.num.Num;

/**
 * Volume-Weighted Average Price (VWAP) indicator.
 * 
 * <p>VWAP is a trading benchmark used by institutional traders that represents the average price
 * a security has traded at throughout the day, weighted by both volume and price. It provides
 * insight into both trend direction and value, making it one of the most important indicators
 * for professional trading and algorithmic execution.
 * 
 * <h2>Calculation</h2>
 * <p>VWAP = Σ(Typical Price × Volume) / Σ(Volume)
 * <br>Where:
 * <ul>
 * <li><strong>Typical Price:</strong> (High + Low + Close) / 3</li>
 * <li><strong>Volume:</strong> Number of shares/contracts traded in the period</li>
 * <li><strong>Σ:</strong> Sum over the specified time period</li>
 * </ul>
 * 
 * <h2>Key Characteristics</h2>
 * <ul>
 * <li><strong>Volume-Weighted:</strong> Gives more weight to prices where more volume occurred</li>
 * <li><strong>Fair Value Representation:</strong> Shows the "true" average price based on trading activity</li>
 * <li><strong>Institutional Benchmark:</strong> Used by funds to evaluate execution quality</li>
 * <li><strong>Dynamic Support/Resistance:</strong> Acts as intraday support and resistance levels</li>
 * </ul>
 * 
 * <h2>Trading Applications</h2>
 * <ul>
 * <li><strong>Execution Benchmark:</strong>
 *     <ul>
 *     <li>Institutional traders aim to beat VWAP in their executions</li>
 *     <li>Buying below VWAP or selling above VWAP indicates good execution</li>
 *     </ul>
 * </li>
 * <li><strong>Trend Analysis:</strong>
 *     <ul>
 *     <li>Price above VWAP suggests bullish sentiment</li>
 *     <li>Price below VWAP suggests bearish sentiment</li>
 *     </ul>
 * </li>
 * <li><strong>Support/Resistance:</strong>
 *     <ul>
 *     <li>VWAP often acts as dynamic support in uptrends</li>
 *     <li>VWAP often acts as dynamic resistance in downtrends</li>
 *     </ul>
 * </li>
 * <li><strong>Mean Reversion:</strong>
 *     <ul>
 *     <li>Price tends to revert toward VWAP during the trading session</li>
 *     <li>Extreme deviations from VWAP often reverse</li>
 *     </ul>
 * </li>
 * </ul>
 * 
 * <h2>Institutional Usage</h2>
 * <ul>
 * <li><strong>Algorithmic Trading:</strong> VWAP strategies aim to match or beat the benchmark</li>
 * <li><strong>Performance Measurement:</strong> Evaluate execution quality against VWAP</li>
 * <li><strong>Market Impact:</strong> Minimize market impact by trading around VWAP</li>
 * <li><strong>Order Timing:</strong> Spread large orders throughout the day to achieve VWAP</li>
 * </ul>
 * 
 * <h2>Time Period Considerations</h2>
 * <ul>
 * <li><strong>Intraday VWAP:</strong> Reset daily, most common for day trading</li>
 * <li><strong>Rolling VWAP:</strong> Calculated over a specific number of periods</li>
 * <li><strong>Session VWAP:</strong> Calculated for specific trading sessions</li>
 * <li><strong>Multi-day VWAP:</strong> Extended periods for longer-term analysis</li>
 * </ul>
 * 
 * <h2>Advantages</h2>
 * <ul>
 * <li>Reflects actual trading activity and liquidity</li>
 * <li>Provides objective execution benchmark</li>
 * <li>Combines price and volume information effectively</li>
 * <li>Useful across all asset classes and timeframes</li>
 * <li>Widely accepted institutional standard</li>
 * </ul>
 * 
 * <h2>Limitations</h2>
 * <ul>
 * <li>Lagging indicator based on historical data</li>
 * <li>Less effective in low-volume periods</li>
 * <li>Can be manipulated in thin markets</li>
 * <li>Resets daily, limiting longer-term analysis</li>
 * <li>May not reflect current market sentiment in fast-moving markets</li>
 * </ul>
 * 
 * <h2>Common Period Settings</h2>
 * <ul>
 * <li><strong>Intraday:</strong> Full trading session (6.5 hours = 390 minutes for US markets)</li>
 * <li><strong>Rolling 20:</strong> 20-period rolling VWAP for shorter-term analysis</li>
 * <li><strong>Rolling 50:</strong> 50-period rolling VWAP for medium-term trends</li>
 * <li><strong>Custom Periods:</strong> Based on specific trading strategies or market hours</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Create 20-period rolling VWAP
 * VWAPIndicator vwap20 = new VWAPIndicator(series, 20);
 * 
 * // VWAP-based trading rules
 * ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
 * Rule priceAboveVWAP = new OverIndicatorRule(closePrice, vwap20);
 * Rule priceBelowVWAP = new UnderIndicatorRule(closePrice, vwap20);
 * 
 * // VWAP mean reversion strategy
 * VolumeIndicator volume = new VolumeIndicator(series);
 * Rule highVolume = new OverIndicatorRule(volume, new SMAIndicator(volume, 20));
 * 
 * Rule meanReversionBuy = priceBelowVWAP.and(highVolume);
 * Rule meanReversionSell = priceAboveVWAP.and(highVolume);
 * 
 * // VWAP trend following
 * Rule trendUpEntry = new CrossedUpIndicatorRule(closePrice, vwap20);
 * Rule trendDownExit = new CrossedDownIndicatorRule(closePrice, vwap20);
 * 
 * // Institutional-style execution rules
 * Rule goodBuyExecution = new UnderIndicatorRule(closePrice, vwap20); // Buy below VWAP
 * Rule goodSellExecution = new OverIndicatorRule(closePrice, vwap20); // Sell above VWAP
 * 
 * // Multi-timeframe VWAP analysis
 * VWAPIndicator vwapShort = new VWAPIndicator(series, 10);
 * VWAPIndicator vwapLong = new VWAPIndicator(series, 50);
 * Rule vwapUptrend = new OverIndicatorRule(vwapShort, vwapLong);
 * 
 * // Deviation from VWAP
 * NumericIndicator vwapDeviation = NumericIndicator.of(closePrice).minus(vwap20)
 *     .dividedBy(vwap20).multipliedBy(100); // Percentage deviation
 * Rule significantDeviation = new OverIndicatorRule(vwapDeviation, 2.0); // 2% above VWAP
 * }</pre>
 * 
 * @see org.ta4j.core.indicators.helpers.TypicalPriceIndicator
 * @see org.ta4j.core.indicators.helpers.VolumeIndicator
 * @see org.ta4j.core.indicators.averages.SMAIndicator
 * @see <a href="https://www.investopedia.com/terms/v/vwap.asp">Investopedia - VWAP</a>
 * @see <a href="https://school.stockcharts.com/doku.php?id=technical_indicators:vwap_intraday">StockCharts - VWAP</a>
 * @since 0.1
 */
public class VWAPIndicator extends CachedIndicator<Num> {

    private final int barCount;
    private final Indicator<Num> typicalPrice;
    private final Indicator<Num> volume;

    /**
     * Creates a Volume-Weighted Average Price indicator over a specified period.
     * 
     * <p>This constructor creates a rolling VWAP that calculates the volume-weighted
     * average price over the most recent barCount periods. For each bar, it considers
     * the typical price weighted by the trading volume to determine the fair value
     * benchmark.
     * 
     * <p><strong>Calculation Process:</strong>
     * <ol>
     * <li>Calculate typical price for each bar: (High + Low + Close) / 3</li>
     * <li>Multiply typical price by volume for each bar</li>
     * <li>Sum all (typical price × volume) values over the period</li>
     * <li>Divide by the sum of all volumes over the period</li>
     * </ol>
     *
     * @param series   the bar series containing price and volume data (must not be null)
     * @param barCount the number of periods for VWAP calculation, typically 20-50 (must be > 0)
     * @throws IllegalArgumentException if series is null or barCount <= 0
     */
    public VWAPIndicator(BarSeries series, int barCount) {
        super(series);
        this.barCount = barCount;
        this.typicalPrice = new TypicalPriceIndicator(series);
        this.volume = new VolumeIndicator(series);
    }

    @Override
    protected Num calculate(int index) {
        if (index <= 0) {
            return typicalPrice.getValue(index);
        }
        int startIndex = Math.max(0, index - barCount + 1);
        final var zero = getBarSeries().numFactory().zero();
        Num cumulativeTPV = zero;
        Num cumulativeVolume = zero;
        for (int i = startIndex; i <= index; i++) {
            Num currentVolume = volume.getValue(i);
            cumulativeTPV = cumulativeTPV.plus(typicalPrice.getValue(i).multipliedBy(currentVolume));
            cumulativeVolume = cumulativeVolume.plus(currentVolume);
        }
        return cumulativeTPV.dividedBy(cumulativeVolume);
    }

    @Override
    public int getCountOfUnstableBars() {
        return barCount;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " barCount: " + barCount;
    }
}
