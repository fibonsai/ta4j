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
 * Bollinger Bands Upper Band indicator.
 * 
 * <p>The upper Bollinger Band represents the upper boundary of the price channel
 * and is calculated by adding a multiple of standard deviation to the middle band
 * (typically a simple moving average). It acts as dynamic resistance and helps
 * identify overbought conditions and potential reversal points.
 * 
 * <h2>Calculation</h2>
 * <p>Upper Band = Middle Band + (k × Standard Deviation)
 * <br>Where:
 * <ul>
 * <li><strong>Middle Band:</strong> Typically a 20-period Simple Moving Average</li>
 * <li><strong>k:</strong> Multiplier, typically 2.0</li>
 * <li><strong>Standard Deviation:</strong> Of the same period as the middle band</li>
 * </ul>
 * 
 * <h2>Interpretation</h2>
 * <ul>
 * <li><strong>Resistance Level:</strong> Upper band often acts as dynamic resistance</li>
 * <li><strong>Overbought Signal:</strong> Price touching or exceeding upper band may indicate overbought conditions</li>
 * <li><strong>Volatility Measure:</strong> Distance from middle band reflects current volatility</li>
 * <li><strong>Trend Strength:</strong> Price consistently near upper band suggests strong uptrend</li>
 * </ul>
 * 
 * <h2>Trading Applications</h2>
 * <ul>
 * <li><strong>Mean Reversion:</strong> Sell when price touches upper band (expecting return to middle)</li>
 * <li><strong>Breakout Confirmation:</strong> Buy when price closes above upper band with volume</li>
 * <li><strong>Volatility Analysis:</strong> Wide bands indicate high volatility, narrow bands low volatility</li>
 * <li><strong>Squeeze Detection:</strong> Very narrow bands often precede significant moves</li>
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
 * <h2>Limitations</h2>
 * <ul>
 * <li>Price can "walk the bands" during strong trends</li>
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
 * BollingerBandsUpperIndicator bbUpper = new BollingerBandsUpperIndicator(bbMiddle, stdDev, 2.0);
 * 
 * // Trading rules
 * Rule overboughtRule = new OverIndicatorRule(closePrice, bbUpper); // Price above upper band
 * Rule meanReversionSell = new CrossedUpIndicatorRule(closePrice, bbUpper); // Price crosses above upper
 * Rule breakoutBuy = new OverIndicatorRule(closePrice, bbUpper); // Price stays above upper
 * 
 * // Volatility analysis
 * NumericIndicator bandWidth = NumericIndicator.of(bbUpper).minus(bbLower);
 * Rule lowVolatility = new UnderIndicatorRule(bandWidth, bbMiddle.multipliedBy(0.1));
 * }</pre>
 * 
 * @see BollingerBandsMiddleIndicator
 * @see BollingerBandsLowerIndicator
 * @see org.ta4j.core.indicators.statistics.StandardDeviationIndicator
 * @see org.ta4j.core.indicators.averages.SMAIndicator
 * @see <a href="https://www.investopedia.com/terms/b/bollingerbands.asp">Investopedia - Bollinger Bands</a>
 * @see <a href="https://school.stockcharts.com/doku.php?id=technical_indicators:bollinger_bands">StockCharts - Bollinger Bands</a>
 * @since 0.1
 */
public class BollingerBandsUpperIndicator extends CachedIndicator<Num> {

    private final BollingerBandsMiddleIndicator bbm;
    private final Indicator<Num> deviation;
    private final Num k;

    /**
     * Creates a Bollinger Bands upper indicator with standard 2.0 multiplier.
     * 
     * <p>This constructor uses the standard configuration with k=2.0, which encompasses
     * approximately 95% of price data within the bands under normal distribution assumptions.
     * This is the most commonly used Bollinger Bands configuration.
     *
     * @param bbm       the middle band indicator, typically a 20-period SMA (must not be null)
     * @param deviation the standard deviation indicator of the same period as middle band (must not be null)
     * @throws IllegalArgumentException if bbm or deviation is null
     */
    public BollingerBandsUpperIndicator(BollingerBandsMiddleIndicator bbm, Indicator<Num> deviation) {
        this(bbm, deviation, bbm.getBarSeries().numFactory().two());
    }

    /**
     * Creates a Bollinger Bands upper indicator with custom multiplier.
     * 
     * <p>This constructor allows customization of the multiplier (k) to adjust band sensitivity:
     * <ul>
     * <li><strong>k > 2.0:</strong> Wider bands, fewer signals, more conservative</li>
     * <li><strong>k < 2.0:</strong> Tighter bands, more signals, more aggressive</li>
     * <li><strong>k = 1.0:</strong> One standard deviation (68% coverage)</li>
     * <li><strong>k = 2.0:</strong> Two standard deviations (95% coverage, standard)</li>
     * <li><strong>k = 2.5:</strong> More conservative approach</li>
     * </ul>
     *
     * @param bbm       the middle band indicator, typically a 20-period SMA (must not be null)
     * @param deviation the standard deviation indicator of the same period (must not be null)
     * @param k         the multiplier for standard deviation, typically 2.0 (must be > 0)
     * @throws IllegalArgumentException if any parameter is null or k <= 0
     */
    public BollingerBandsUpperIndicator(BollingerBandsMiddleIndicator bbm, Indicator<Num> deviation, Num k) {
        super(deviation);
        this.bbm = bbm;
        this.deviation = deviation;
        this.k = k;
    }

    @Override
    protected Num calculate(int index) {
        return bbm.getValue(index).plus(deviation.getValue(index).multipliedBy(k));
    }

    @Override
    public int getCountOfUnstableBars() {
        return 0;
    }

    /**
     * Returns the multiplier (k) used to scale the standard deviation.
     * 
     * <p>This value determines the width of the Bollinger Bands. Higher values
     * create wider bands with fewer signals, while lower values create tighter
     * bands with more frequent signals.
     *
     * @return the k multiplier value used in the calculation
     */
    public Num getK() {
        return k;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "k: " + k + "deviation: " + deviation + "series" + bbm;
    }
}
