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
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.HighestValueIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.LowestValueIndicator;
import org.ta4j.core.num.Num;

/**
 * Williams %R indicator.
 * 
 * <p>Williams %R is a momentum indicator developed by Larry Williams that measures the level of 
 * the close relative to the highest high for a lookback period. It's an oscillator that moves 
 * between -100 and 0, with readings closer to -100 indicating oversold conditions and readings 
 * closer to 0 indicating overbought conditions.
 * 
 * <h2>Calculation</h2>
 * <p>Williams %R = [(Highest High - Close) / (Highest High - Lowest Low)] × -100
 * <br>Where:
 * <ul>
 * <li><strong>Highest High:</strong> Highest high price over the lookback period</li>
 * <li><strong>Lowest Low:</strong> Lowest low price over the lookback period</li>
 * <li><strong>Close:</strong> Current closing price</li>
 * <li><strong>Lookback Period:</strong> Typically 14 periods</li>
 * </ul>
 * 
 * <h2>Interpretation</h2>
 * <p>Williams %R oscillates between -100 and 0:
 * <ul>
 * <li><strong>Overbought:</strong> %R > -20 (near zero indicates strong buying)</li>
 * <li><strong>Oversold:</strong> %R < -80 (near -100 indicates strong selling)</li>
 * <li><strong>Neutral Zone:</strong> -80 to -20 (normal trading range)</li>
 * <li><strong>Extreme Values:</strong> Above -10 or below -90 indicate very strong momentum</li>
 * </ul>
 * 
 * <h2>Trading Signals</h2>
 * <ul>
 * <li><strong>Overbought/Oversold:</strong>
 *     <ul>
 *     <li>Buy when %R crosses above -80 from below (oversold recovery)</li>
 *     <li>Sell when %R crosses below -20 from above (overbought decline)</li>
 *     </ul>
 * </li>
 * <li><strong>Momentum Signals:</strong>
 *     <ul>
 *     <li>Buy when %R moves from below -50 to above -50 (bullish momentum)</li>
 *     <li>Sell when %R moves from above -50 to below -50 (bearish momentum)</li>
 *     </ul>
 * </li>
 * <li><strong>Divergence:</strong> Price and %R moving in opposite directions</li>
 * <li><strong>Failure Swings:</strong> %R fails to reach previous extreme levels</li>
 * </ul>
 * 
 * <h2>Relationship to Stochastic</h2>
 * <p>Williams %R is closely related to the Stochastic Oscillator:
 * <ul>
 * <li>Williams %R = Stochastic %K - 100</li>
 * <li>Both measure the same concept but with different scales</li>
 * <li>Williams %R: -100 to 0 scale</li>
 * <li>Stochastic %K: 0 to 100 scale</li>
 * </ul>
 * 
 * <h2>Advantages</h2>
 * <ul>
 * <li>Fast-moving oscillator that provides timely signals</li>
 * <li>Effective for identifying short-term reversals</li>
 * <li>Works well in trending and ranging markets</li>
 * <li>Simple calculation and interpretation</li>
 * <li>Can be applied to any timeframe</li>
 * </ul>
 * 
 * <h2>Limitations</h2>
 * <ul>
 * <li>Can remain in overbought/oversold zones for extended periods</li>
 * <li>Generates false signals in strong trending markets</li>
 * <li>Requires confirmation from other indicators</li>
 * <li>Sensitive to the chosen lookback period</li>
 * </ul>
 * 
 * <h2>Common Periods</h2>
 * <ul>
 * <li><strong>Standard:</strong> 14 periods (most common setting)</li>
 * <li><strong>Fast:</strong> 9 periods (more sensitive, more signals)</li>
 * <li><strong>Slow:</strong> 21 periods (smoother, fewer false signals)</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Create 14-period Williams %R
 * WilliamsRIndicator williamsR = new WilliamsRIndicator(series, 14);
 * 
 * // Overbought/oversold rules
 * Rule overbought = new OverIndicatorRule(williamsR, -20);
 * Rule oversold = new UnderIndicatorRule(williamsR, -80);
 * 
 * // Momentum rules
 * Rule bullishMomentum = new CrossedUpIndicatorRule(williamsR, -50);
 * Rule bearishMomentum = new CrossedDownIndicatorRule(williamsR, -50);
 * 
 * // Mean reversion strategy
 * Rule entryRule = oversold; // Enter when oversold
 * Rule exitRule = overbought; // Exit when overbought
 * 
 * // Trend following strategy
 * Rule trendEntryRule = bullishMomentum.and(new OverIndicatorRule(closePrice, sma50));
 * Rule trendExitRule = bearishMomentum.or(new UnderIndicatorRule(closePrice, sma50));
 * 
 * // Custom Williams %R with different price sources
 * TypicalPriceIndicator typicalPrice = new TypicalPriceIndicator(series);
 * WilliamsRIndicator customWR = new WilliamsRIndicator(
 *     new ClosePriceIndicator(series), 14,
 *     new HighPriceIndicator(series), new LowPriceIndicator(series));
 * }</pre>
 * 
 * @see StochasticOscillatorKIndicator
 * @see org.ta4j.core.indicators.helpers.HighestValueIndicator
 * @see org.ta4j.core.indicators.helpers.LowestValueIndicator
 * @see <a href="https://www.investopedia.com/terms/w/williamsr.asp">Investopedia - Williams %R</a>
 * @see <a href="https://school.stockcharts.com/doku.php?id=technical_indicators:williams_r">StockCharts - Williams %R</a>
 * @since 0.1
 */
public class WilliamsRIndicator extends CachedIndicator<Num> {

    private final Indicator<Num> closePriceIndicator;
    private final int barCount;
    private final HighPriceIndicator highPriceIndicator;
    private final LowPriceIndicator lowPriceIndicator;
    private final Num multiplier;

    /**
     * Creates a Williams %R indicator using standard price components.
     * 
     * <p>This constructor creates the most common Williams %R configuration using:
     * <ul>
     * <li><strong>Close Price:</strong> For current position in the range</li>
     * <li><strong>High Price:</strong> For maximum values over the period</li>
     * <li><strong>Low Price:</strong> For minimum values over the period</li>
     * </ul>
     * 
     * <p>This follows Larry Williams' original specification and matches the
     * implementation found in most trading platforms.
     *
     * @param barSeries the bar series to calculate Williams %R over (must not be null)
     * @param barCount  the lookback period, typically 14 (must be > 0)
     * @throws IllegalArgumentException if barSeries is null or barCount <= 0
     */
    public WilliamsRIndicator(BarSeries barSeries, int barCount) {
        this(new ClosePriceIndicator(barSeries), barCount, new HighPriceIndicator(barSeries),
                new LowPriceIndicator(barSeries));
    }

    /**
     * Creates a Williams %R indicator with custom price indicators.
     * 
     * <p>This constructor allows customization of the price sources used in the calculation.
     * While typically used with close, high, and low prices, it can be applied to any
     * price indicators for specialized analysis.
     * 
     * <p><strong>Advanced Usage:</strong>
     * <ul>
     * <li>Apply Williams %R to smoothed prices (e.g., typical price)</li>
     * <li>Use different price transformations</li>
     * <li>Create Williams %R variants with custom ranges</li>
     * </ul>
     *
     * @param closePriceIndicator the indicator for current price level (must not be null)
     * @param barCount            the lookback period for high/low calculations (must be > 0)
     * @param highPriceIndicator  the indicator for high values (must not be null)
     * @param lowPriceIndicator   the indicator for low values (must not be null)
     * @throws IllegalArgumentException if any indicator is null or barCount <= 0
     */
    public WilliamsRIndicator(ClosePriceIndicator closePriceIndicator, int barCount,
            HighPriceIndicator highPriceIndicator, LowPriceIndicator lowPriceIndicator) {
        super(closePriceIndicator);
        this.closePriceIndicator = closePriceIndicator;
        this.barCount = barCount;
        this.highPriceIndicator = highPriceIndicator;
        this.lowPriceIndicator = lowPriceIndicator;
        this.multiplier = getBarSeries().numFactory().numOf(-100);
    }

    @Override
    protected Num calculate(int index) {
        HighestValueIndicator highestHigh = new HighestValueIndicator(highPriceIndicator, barCount);
        LowestValueIndicator lowestMin = new LowestValueIndicator(lowPriceIndicator, barCount);

        Num highestHighPrice = highestHigh.getValue(index);
        Num lowestLowPrice = lowestMin.getValue(index);

        return ((highestHighPrice.minus(closePriceIndicator.getValue(index)))
                .dividedBy(highestHighPrice.minus(lowestLowPrice))).multipliedBy(multiplier);
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
