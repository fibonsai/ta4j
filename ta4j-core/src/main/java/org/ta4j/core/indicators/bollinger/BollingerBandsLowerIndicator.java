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
package org.ta4j.core.indicators.bollinger;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

/**
 * Bollinger Bands Lower Band indicator.
 *
 * <p>The lower Bollinger Band represents the lower boundary of the price channel
 * and is calculated by subtracting a multiple of standard deviation from the middle band
 * (typically a simple moving average). It acts as dynamic support and helps
 * identify oversold conditions and potential reversal points.
 *
 * <h2>Calculation</h2>
 * <p>Lower Band = Middle Band - (k × Standard Deviation)
 * <br>Where:
 * <ul>
 * <li><strong>Middle Band:</strong> Typically a 20-period Simple Moving Average</li>
 * <li><strong>k:</strong> Multiplier, typically 2.0</li>
 * <li><strong>Standard Deviation:</strong> Of the same period as the middle band</li>
 * </ul>
 *
 * <h2>Interpretation</h2>
 * <ul>
 * <li><strong>Support Level:</strong> Lower band often acts as dynamic support</li>
 * <li><strong>Oversold Signal:</strong> Price touching or dropping below lower band may indicate oversold conditions</li>
 * <li><strong>Volatility Measure:</strong> Distance from middle band reflects current volatility</li>
 * <li><strong>Trend Strength:</strong> Price consistently near lower band suggests strong downtrend</li>
 * </ul>
 *
 * <h2>Trading Applications</h2>
 * <ul>
 * <li><strong>Mean Reversion:</strong> Buy when price touches lower band (expecting return to middle)</li>
 * <li><strong>Breakdown Confirmation:</strong> Sell when price closes below lower band with volume</li>
 * <li><strong>Support Testing:</strong> Watch for price reaction at lower band for trend continuation</li>
 * <li><strong>Squeeze Analysis:</strong> Narrow bands often precede significant moves</li>
 * </ul>
 *
 * <h2>Bollinger Band Strategies</h2>
 * <ul>
 * <li><strong>Mean Reversion:</strong>
 *     <ul>
 *     <li>Buy when price touches lower band in ranging markets</li>
 *     <li>Target middle band or upper band for exits</li>
 *     </ul>
 * </li>
 * <li><strong>Trend Following:</strong>
 *     <ul>
 *     <li>Sell when price breaks below lower band in downtrends</li>
 *     <li>Look for continuation patterns below the band</li>
 *     </ul>
 * </li>
 * <li><strong>Volatility Breakouts:</strong>
 *     <ul>
 *     <li>Trade breakouts when bands are unusually narrow</li>
 *     <li>Use band width as volatility filter</li>
 *     </ul>
 * </li>
 * </ul>
 *
 * <h2>Common Configurations</h2>
 * <ul>
 * <li><strong>Standard:</strong> 20-period SMA with 2.0 standard deviations</li>
 * <li><strong>Conservative:</strong> 20-period SMA with 2.5 standard deviations (wider bands)</li>
 * <li><strong>Aggressive:</strong> 20-period SMA with 1.5 standard deviations (tighter bands)</li>
 * <li><strong>Short-term:</strong> 10-period SMA with 2.0 standard deviations</li>
 * </ul>
 *
 * <h2>Market Context Considerations</h2>
 * <ul>
 * <li><strong>Trending Markets:</strong> Price can "walk the bands" for extended periods</li>
 * <li><strong>Ranging Markets:</strong> Bands act as dynamic support/resistance levels</li>
 * <li><strong>High Volatility:</strong> Bands expand, providing wider trading ranges</li>
 * <li><strong>Low Volatility:</strong> Bands contract, often preceding breakouts</li>
 * </ul>
 *
 * <h2>Limitations</h2>
 * <ul>
 * <li>Price can remain near bands longer than expected in strong trends</li>
 * <li>May generate false signals in trending markets</li>
 * <li>Should be combined with other indicators for confirmation</li>
 * <li>Not predictive - describes current market conditions</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Create standard Bollinger Bands (20-period, 2.0 std dev)
 * ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
 * BollingerBandsMiddleIndicator bbMiddle = new BollingerBandsMiddleIndicator(
 *     new SMAIndicator(closePrice, 20));
 * StandardDeviationIndicator stdDev = new StandardDeviationIndicator(closePrice, 20);
 * BollingerBandsLowerIndicator bbLower = new BollingerBandsLowerIndicator(bbMiddle, stdDev, 2.0);
 *
 * // Trading rules
 * Rule oversoldRule = new UnderIndicatorRule(closePrice, bbLower); // Price below lower band
 * Rule meanReversionBuy = new CrossedDownIndicatorRule(closePrice, bbLower); // Price crosses below lower
 * Rule breakdownSell = new UnderIndicatorRule(closePrice, bbLower); // Price stays below lower
 *
 * // Band squeeze detection
 * NumericIndicator bandWidth = NumericIndicator.of(bbUpper).minus(bbLower);
 * Rule lowVolatility = new UnderIndicatorRule(bandWidth, bbMiddle.multipliedBy(0.1));
 * Rule squeezeBuy = meanReversionBuy.and(lowVolatility);
 *
 * // Combined with RSI for oversold confirmation
 * RSIIndicator rsi = new RSIIndicator(closePrice, 14);
 * Rule doubleOversold = oversoldRule.and(new UnderIndicatorRule(rsi, 30));
 *
 * // Multi-band strategy
 * Rule entryRule = doubleOversold;
 * Rule exitRule = new CrossedUpIndicatorRule(closePrice, bbMiddle); // Exit at middle band
 * }</pre>
 *
 * @see BollingerBandsMiddleIndicator
 * @see BollingerBandsUpperIndicator
 * @see org.ta4j.core.indicators.statistics.StandardDeviationIndicator
 * @see org.ta4j.core.indicators.averages.SMAIndicator
 * @see <a href="https://www.investopedia.com/terms/b/bollingerbands.asp">Investopedia - Bollinger Bands</a>
 * @see <a href="https://school.stockcharts.com/doku.php?id=technical_indicators:bollinger_bands">StockCharts - Bollinger Bands</a>
 * @since 0.1
 */
public class BollingerBandsLowerIndicator extends CachedIndicator<Num> {

    private final BollingerBandsMiddleIndicator bbm;
    private final Indicator<Num> indicator;
    private final Num k;

    /**
     * Creates a Bollinger Bands Lower Band indicator with default multiplier of 2.0.
     * 
     * <p>This constructor uses the standard Bollinger Band configuration with a multiplier
     * of 2.0, which results in approximately 95% of price action occurring within the bands
     * under normal market conditions (assuming normal distribution).
     * 
     * <p>The lower band provides dynamic support levels and oversold signal generation
     * for mean reversion and trend analysis strategies.
     *
     * @param bbm       the middle band indicator, typically a 20-period SMA (must not be null)
     * @param indicator the standard deviation indicator for band calculation (must not be null)
     * @throws IllegalArgumentException if bbm or indicator is null
     */
    public BollingerBandsLowerIndicator(BollingerBandsMiddleIndicator bbm, Indicator<Num> indicator) {
        this(bbm, indicator, bbm.getBarSeries().numFactory().numOf(2));
    }

    /**
     * Creates a Bollinger Bands Lower Band indicator with custom multiplier.
     * 
     * <p>This constructor allows customization of the standard deviation multiplier,
     * enabling fine-tuning of the band sensitivity:
     * <ul>
     * <li><strong>k = 1.5:</strong> Tighter bands, more sensitive (68% coverage)</li>
     * <li><strong>k = 2.0:</strong> Standard bands (95% coverage)</li>
     * <li><strong>k = 2.5:</strong> Wider bands, less sensitive (99% coverage)</li>
     * </ul>
     * 
     * <p>Lower multipliers create more trading signals but higher false positive rates,
     * while higher multipliers create fewer but potentially more reliable signals.
     *
     * @param bbm       the middle band indicator, typically a SMA (must not be null)
     * @param indicator the standard deviation indicator (must not be null)
     * @param k         the standard deviation multiplier, typically 1.5-2.5 (must be > 0)
     * @throws IllegalArgumentException if bbm or indicator is null, or k <= 0
     */
    public BollingerBandsLowerIndicator(BollingerBandsMiddleIndicator bbm, Indicator<Num> indicator, Num k) {
        super(indicator);
        this.bbm = bbm;
        this.indicator = indicator;
        this.k = k;
    }

    @Override
    protected Num calculate(int index) {
        return bbm.getValue(index).minus(indicator.getValue(index).multipliedBy(k));
    }

    @Override
    public int getCountOfUnstableBars() {
        return 0;
    }

    /**
     * Returns the standard deviation multiplier used for band calculation.
     * 
     * <p>The multiplier determines the distance of the lower band from the middle band
     * in terms of standard deviations. Common values are:
     * <ul>
     * <li>1.5 for aggressive/tight bands</li>
     * <li>2.0 for standard bands (most common)</li>
     * <li>2.5 for conservative/wide bands</li>
     * </ul>
     *
     * @return the standard deviation multiplier (k factor)
     */
    public Num getK() {
        return k;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "k: " + k + "deviation: " + indicator + "series: " + bbm;
    }
}
