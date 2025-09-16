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
import org.ta4j.core.indicators.helpers.*;
import org.ta4j.core.num.Num;

/**
 * Stochastic Oscillator %K indicator.
 * 
 * <p>The Stochastic Oscillator is a momentum indicator developed by George Lane that compares
 * a security's closing price to its price range over a given time period. It consists of two
 * lines: %K (the fast line) and %D (the slow line, which is a moving average of %K).
 * This class implements the %K line calculation.
 * 
 * <h2>Calculation</h2>
 * <p>%K = [(Current Close - Lowest Low) / (Highest High - Lowest Low)] × 100
 * <br>Where:
 * <ul>
 * <li><strong>Current Close:</strong> Most recent closing price</li>
 * <li><strong>Lowest Low:</strong> Lowest low price over the lookback period</li>
 * <li><strong>Highest High:</strong> Highest high price over the lookback period</li>
 * <li><strong>Lookback Period:</strong> Typically 14 periods</li>
 * </ul>
 * 
 * <h2>Interpretation</h2>
 * <p>The Stochastic Oscillator oscillates between 0 and 100:
 * <ul>
 * <li><strong>Overbought:</strong> %K > 80 (traditional threshold)</li>
 * <li><strong>Oversold:</strong> %K < 20 (traditional threshold)</li>
 * <li><strong>Bullish:</strong> %K > 50 or %K crosses above %D</li>
 * <li><strong>Bearish:</strong> %K < 50 or %K crosses below %D</li>
 * </ul>
 * 
 * <h2>Trading Signals</h2>
 * <ul>
 * <li><strong>Overbought/Oversold:</strong>
 *     <ul>
 *     <li>Buy when %K crosses above 20 from below (oversold recovery)</li>
 *     <li>Sell when %K crosses below 80 from above (overbought decline)</li>
 *     </ul>
 * </li>
 * <li><strong>%K/%D Crossover:</strong>
 *     <ul>
 *     <li>Buy when %K crosses above %D</li>
 *     <li>Sell when %K crosses below %D</li>
 *     </ul>
 * </li>
 * <li><strong>Divergence:</strong> Price and %K moving in opposite directions</li>
 * <li><strong>Extreme Readings:</strong> Very high/low %K values may indicate reversal</li>
 * </ul>
 * 
 * <h2>Advantages</h2>
 * <ul>
 * <li>Effective in ranging markets for identifying turning points</li>
 * <li>Provides early warning of potential reversals</li>
 * <li>Works well with other momentum indicators</li>
 * <li>Bounded between 0-100 making interpretation straightforward</li>
 * </ul>
 * 
 * <h2>Limitations</h2>
 * <ul>
 * <li>Can remain overbought/oversold for extended periods in trending markets</li>
 * <li>Generates false signals in strong trending conditions</li>
 * <li>Should be used with trend-following indicators for confirmation</li>
 * <li>Sensitive to the chosen period parameter</li>
 * </ul>
 * 
 * <h2>Common Periods</h2>
 * <ul>
 * <li><strong>Standard:</strong> 14 periods (Lane's original recommendation)</li>
 * <li><strong>Fast:</strong> 5-9 periods (more sensitive, more signals)</li>
 * <li><strong>Slow:</strong> 21 periods (smoother, fewer false signals)</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Create 14-period Stochastic %K
 * StochasticOscillatorKIndicator stochK = new StochasticOscillatorKIndicator(series, 14);
 * 
 * // Create %D line (3-period SMA of %K)
 * StochasticOscillatorDIndicator stochD = new StochasticOscillatorDIndicator(stochK, 3);
 * 
 * // Overbought/oversold rules
 * Rule overbought = new OverIndicatorRule(stochK, 80);
 * Rule oversold = new UnderIndicatorRule(stochK, 20);
 * 
 * // Crossover rules
 * Rule bullishCrossover = new CrossedUpIndicatorRule(stochK, stochD);
 * Rule bearishCrossover = new CrossedDownIndicatorRule(stochK, stochD);
 * 
 * // Combined strategy
 * Rule entryRule = oversold.and(bullishCrossover);
 * Rule exitRule = overbought.or(bearishCrossover);
 * 
 * // Custom price-based Stochastic (using different price indicator)
 * TypicalPriceIndicator typicalPrice = new TypicalPriceIndicator(series);
 * StochasticOscillatorKIndicator customStoch = new StochasticOscillatorKIndicator(
 *     typicalPrice, 14, new HighPriceIndicator(series), new LowPriceIndicator(series));
 * }</pre>
 * 
 * @see StochasticOscillatorDIndicator
 * @see org.ta4j.core.indicators.helpers.HighestValueIndicator
 * @see org.ta4j.core.indicators.helpers.LowestValueIndicator
 * @see <a href="https://www.investopedia.com/terms/s/stochasticoscillator.asp">Investopedia - Stochastic Oscillator</a>
 * @see <a href="https://school.stockcharts.com/doku.php?id=technical_indicators:stochastic_oscillator_fast_slow_and_full">StockCharts - Stochastic</a>
 * @since 0.1
 */
public class StochasticOscillatorKIndicator extends CachedIndicator<Num> {

    private final Indicator<Num> indicator;
    private final Indicator<Num> highPriceIndicator;
    private final Indicator<Num> lowPriceIndicator;
    private final int barCount;

    /**
     * Creates a standard Stochastic %K indicator using close, high, and low prices.
     * 
     * <p>This constructor creates the most common Stochastic configuration:
     * <ul>
     * <li><strong>Price Source:</strong> Close price for current position calculation</li>
     * <li><strong>High Source:</strong> High prices for range maximum</li>
     * <li><strong>Low Source:</strong> Low prices for range minimum</li>
     * </ul>
     * 
     * <p>This is the standard implementation used in most trading platforms and
     * follows George Lane's original Stochastic Oscillator specification.
     *
     * @param barSeries the bar series to calculate Stochastic over (must not be null)
     * @param barCount  the lookback period for highest high and lowest low, typically 14 (must be > 0)
     * @throws IllegalArgumentException if barSeries is null or barCount <= 0
     */
    public StochasticOscillatorKIndicator(BarSeries barSeries, int barCount) {
        this(new ClosePriceIndicator(barSeries), barCount, new HighPriceIndicator(barSeries),
                new LowPriceIndicator(barSeries));
    }

    /**
     * Creates a custom Stochastic %K indicator with specified price sources.
     * 
     * <p>This constructor allows customization of the price sources used in the calculation:
     * <ul>
     * <li><strong>Current Value:</strong> Any indicator for "current" position (typically close price)</li>
     * <li><strong>High Source:</strong> Any indicator for range maximum calculation</li>
     * <li><strong>Low Source:</strong> Any indicator for range minimum calculation</li>
     * </ul>
     * 
     * <p><strong>Example Use Cases:</strong>
     * <ul>
     * <li>Use typical price instead of close price</li>
     * <li>Apply Stochastic to other indicators (RSI, MACD)</li>
     * <li>Custom price transformations</li>
     * </ul>
     *
     * @param indicator          the indicator for current value calculation (must not be null)
     * @param barCount           the lookback period, typically 14 (must be > 0)
     * @param highPriceIndicator the indicator for high values (must not be null)
     * @param lowPriceIndicator  the indicator for low values (must not be null)
     * @throws IllegalArgumentException if any indicator is null or barCount <= 0
     */
    public StochasticOscillatorKIndicator(Indicator<Num> indicator, int barCount, Indicator<Num> highPriceIndicator,
            Indicator<Num> lowPriceIndicator) {
        super(indicator);
        this.indicator = indicator;
        this.barCount = barCount;
        this.highPriceIndicator = highPriceIndicator;
        this.lowPriceIndicator = lowPriceIndicator;
    }

    @Override
    protected Num calculate(int index) {
        HighestValueIndicator highestHigh = new HighestValueIndicator(highPriceIndicator, barCount);
        LowestValueIndicator lowestMin = new LowestValueIndicator(lowPriceIndicator, barCount);

        Num highestHighPrice = highestHigh.getValue(index);
        Num lowestLowPrice = lowestMin.getValue(index);

        return indicator.getValue(index)
                .minus(lowestLowPrice)
                .dividedBy(highestHighPrice.minus(lowestLowPrice))
                .multipliedBy(getBarSeries().numFactory().hundred());
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
