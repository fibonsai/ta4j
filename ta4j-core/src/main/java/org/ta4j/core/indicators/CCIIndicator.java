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
import org.ta4j.core.indicators.averages.SMAIndicator;
import org.ta4j.core.indicators.helpers.TypicalPriceIndicator;
import org.ta4j.core.indicators.statistics.MeanDeviationIndicator;
import org.ta4j.core.num.Num;

/**
 * Commodity Channel Index (CCI) indicator.
 * 
 * <p>The Commodity Channel Index is a momentum-based oscillator developed by Donald Lambert
 * that measures the variation of a security's price from its statistical mean. Despite its name,
 * CCI can be applied to any financial instrument, not just commodities. It helps identify
 * cyclical trends and potential reversal points.
 * 
 * <h2>Calculation</h2>
 * <p>CCI is calculated using the following formula:
 * <ol>
 * <li><strong>Typical Price:</strong> (High + Low + Close) / 3</li>
 * <li><strong>SMA:</strong> Simple Moving Average of Typical Price over n periods</li>
 * <li><strong>Mean Deviation:</strong> Average of absolute deviations from SMA</li>
 * <li><strong>CCI:</strong> (Typical Price - SMA) / (0.015 × Mean Deviation)</li>
 * </ol>
 * 
 * <p>The constant 0.015 ensures that approximately 70-80% of CCI values fall between -100 and +100.
 * 
 * <h2>Interpretation</h2>
 * <ul>
 * <li><strong>Overbought:</strong> CCI > +100 (traditionally)</li>
 * <li><strong>Oversold:</strong> CCI < -100 (traditionally)</li>
 * <li><strong>Normal Range:</strong> -100 to +100 (70-80% of values)</li>
 * <li><strong>Extreme Values:</strong> Above +200 or below -200 indicate very strong momentum</li>
 * </ul>
 * 
 * <h2>Trading Signals</h2>
 * <ul>
 * <li><strong>Overbought/Oversold:</strong> 
 *     <ul>
 *     <li>Buy when CCI crosses below -100 and then back above -100</li>
 *     <li>Sell when CCI crosses above +100 and then back below +100</li>
 *     </ul>
 * </li>
 * <li><strong>Zero Line Crossover:</strong>
 *     <ul>
 *     <li>Buy when CCI crosses above 0 (bullish momentum)</li>
 *     <li>Sell when CCI crosses below 0 (bearish momentum)</li>
 *     </ul>
 * </li>
 * <li><strong>Divergence:</strong> Price and CCI moving in opposite directions</li>
 * </ul>
 * 
 * <h2>Advantages</h2>
 * <ul>
 * <li>Identifies both trending and ranging market conditions</li>
 * <li>Provides early signals of potential reversals</li>
 * <li>Works well in conjunction with other indicators</li>
 * <li>Can be applied across different timeframes</li>
 * </ul>
 * 
 * <h2>Limitations</h2>
 * <ul>
 * <li>Can generate false signals in sideways markets</li>
 * <li>Extreme readings can persist longer than expected</li>
 * <li>Should be used with trend-following indicators for confirmation</li>
 * <li>Sensitive to the chosen period parameter</li>
 * </ul>
 * 
 * <h2>Common Periods</h2>
 * <ul>
 * <li><strong>Standard:</strong> 20 periods (Lambert's original recommendation)</li>
 * <li><strong>Short-term:</strong> 14 periods (more sensitive)</li>
 * <li><strong>Long-term:</strong> 30 periods (smoother, fewer signals)</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Create 20-period CCI
 * CCIIndicator cci = new CCIIndicator(series, 20);
 * 
 * // Overbought/oversold rules
 * Rule overbought = new OverIndicatorRule(cci, 100);
 * Rule oversold = new UnderIndicatorRule(cci, -100);
 * 
 * // Zero line crossover strategy
 * Rule bullishMomentum = new CrossedUpIndicatorRule(cci, 0);
 * Rule bearishMomentum = new CrossedDownIndicatorRule(cci, 0);
 * 
 * // Mean reversion strategy
 * Rule entryRule = oversold; // Enter when oversold
 * Rule exitRule = overbought; // Exit when overbought
 * 
 * // Trend following strategy
 * Rule trendEntryRule = bullishMomentum;
 * Rule trendExitRule = bearishMomentum;
 * }</pre>
 * 
 * @see org.ta4j.core.indicators.helpers.TypicalPriceIndicator
 * @see org.ta4j.core.indicators.averages.SMAIndicator
 * @see org.ta4j.core.indicators.statistics.MeanDeviationIndicator
 * @see <a href="https://www.investopedia.com/terms/c/commoditychannelindex.asp">Investopedia - CCI</a>
 * @see <a href="http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:commodity_channel_in">StockCharts - CCI</a>
 * @since 0.1
 */
public class CCIIndicator extends CachedIndicator<Num> {

    private final Num factor;
    private final TypicalPriceIndicator typicalPriceInd;
    private final SMAIndicator smaInd;
    private final MeanDeviationIndicator meanDeviationInd;
    private final int barCount;

    /**
     * Creates a Commodity Channel Index indicator.
     * 
     * <p>This implementation uses the standard CCI calculation with:
     * <ul>
     * <li>Typical Price as the base price indicator</li>
     * <li>Simple Moving Average for the central tendency</li>
     * <li>Mean Deviation for measuring price dispersion</li>
     * <li>0.015 as the scaling constant (Lambert's original factor)</li>
     * </ul>
     *
     * @param series   the bar series to calculate CCI for (must not be null)
     * @param barCount the number of periods for calculation, typically 20 (must be > 0)
     * @throws IllegalArgumentException if series is null or barCount <= 0
     */
    public CCIIndicator(BarSeries series, int barCount) {
        super(series);
        this.factor = getBarSeries().numFactory().numOf(0.015);
        this.typicalPriceInd = new TypicalPriceIndicator(series);
        this.smaInd = new SMAIndicator(typicalPriceInd, barCount);
        this.meanDeviationInd = new MeanDeviationIndicator(typicalPriceInd, barCount);
        this.barCount = barCount;
    }

    @Override
    protected Num calculate(int index) {
        final Num typicalPrice = typicalPriceInd.getValue(index);
        final Num typicalPriceAvg = smaInd.getValue(index);
        final Num meanDeviation = meanDeviationInd.getValue(index);
        if (meanDeviation.isZero()) {
            return getBarSeries().numFactory().zero();
        }
        return (typicalPrice.minus(typicalPriceAvg)).dividedBy(meanDeviation.multipliedBy(factor));
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
