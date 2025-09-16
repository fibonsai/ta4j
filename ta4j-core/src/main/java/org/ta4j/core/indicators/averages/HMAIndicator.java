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
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.numeric.BinaryOperation;
import org.ta4j.core.num.Num;

/**
 * Hull Moving Average (HMA) indicator.
 * 
 * <p>The Hull Moving Average is an advanced moving average developed by Alan Hull that aims to
 * reduce the lag inherent in traditional moving averages while maintaining smoothness. It achieves
 * this by using weighted moving averages and square root calculations to create a more responsive
 * indicator that closely follows price movements without excessive noise.
 * 
 * <h2>Calculation</h2>
 * <p>The HMA is calculated in three steps:
 * <ol>
 * <li><strong>Half Period WMA:</strong> WMA(price, period/2)</li>
 * <li><strong>Full Period WMA:</strong> WMA(price, period)</li>
 * <li><strong>Raw HMA:</strong> (2 × Half Period WMA) - Full Period WMA</li>
 * <li><strong>Final HMA:</strong> WMA(Raw HMA, √period)</li>
 * </ol>
 * 
 * <p>Where WMA is the Weighted Moving Average and √period is the square root of the period.
 * 
 * <h2>Key Characteristics</h2>
 * <ul>
 * <li><strong>Reduced Lag:</strong> Responds faster to price changes than SMA or EMA</li>
 * <li><strong>Smoothness:</strong> Maintains smoothness despite reduced lag</li>
 * <li><strong>Noise Reduction:</strong> Filters out market noise better than simple averages</li>
 * <li><strong>Directional Clarity:</strong> Provides clear trend direction signals</li>
 * </ul>
 * 
 * <h2>Interpretation</h2>
 * <ul>
 * <li><strong>Trend Direction:</strong> Rising HMA indicates uptrend, falling HMA indicates downtrend</li>
 * <li><strong>Price Position:</strong> Price above HMA suggests bullish momentum, below suggests bearish</li>
 * <li><strong>Slope Analysis:</strong> Steeper slope indicates stronger trend momentum</li>
 * <li><strong>Color Changes:</strong> HMA direction changes often coincide with trend reversals</li>
 * </ul>
 * 
 * <h2>Trading Applications</h2>
 * <ul>
 * <li><strong>Trend Following:</strong>
 *     <ul>
 *     <li>Buy when price crosses above rising HMA</li>
 *     <li>Sell when price crosses below falling HMA</li>
 *     </ul>
 * </li>
 * <li><strong>Support/Resistance:</strong>
 *     <ul>
 *     <li>HMA acts as dynamic support in uptrends</li>
 *     <li>HMA acts as dynamic resistance in downtrends</li>
 *     </ul>
 * </li>
 * <li><strong>Multiple Timeframes:</strong>
 *     <ul>
 *     <li>Short HMA for entry signals</li>
 *     <li>Long HMA for trend confirmation</li>
 *     </ul>
 * </li>
 * </ul>
 * 
 * <h2>Advantages over Traditional MAs</h2>
 * <ul>
 * <li>Significantly reduced lag compared to SMA and EMA</li>
 * <li>Better signal-to-noise ratio</li>
 * <li>More accurate trend change detection</li>
 * <li>Smoother appearance with fewer false signals</li>
 * <li>Responsive to short-term price movements</li>
 * </ul>
 * 
 * <h2>Limitations</h2>
 * <ul>
 * <li>More complex calculation than simple moving averages</li>
 * <li>Can be sensitive to sudden price spikes</li>
 * <li>May generate false signals in highly volatile markets</li>
 * <li>Requires understanding of multi-stage calculation process</li>
 * </ul>
 * 
 * <h2>Common Periods</h2>
 * <ul>
 * <li><strong>Short-term:</strong> 9-16 periods (day trading)</li>
 * <li><strong>Medium-term:</strong> 20-21 periods (swing trading)</li>
 * <li><strong>Long-term:</strong> 55-100 periods (position trading)</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Create 21-period Hull Moving Average
 * ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
 * HMAIndicator hma21 = new HMAIndicator(closePrice, 21);
 * 
 * // Trend following rules
 * Rule priceAboveHMA = new OverIndicatorRule(closePrice, hma21);
 * Rule hmaTrendUp = new IsRisingRule(hma21, 3); // HMA rising for 3 bars
 * 
 * // Entry and exit rules
 * Rule entryRule = new CrossedUpIndicatorRule(closePrice, hma21).and(hmaTrendUp);
 * Rule exitRule = new CrossedDownIndicatorRule(closePrice, hma21);
 * 
 * // Multi-timeframe approach
 * HMAIndicator hmaShort = new HMAIndicator(closePrice, 16);
 * HMAIndicator hmaLong = new HMAIndicator(closePrice, 55);
 * Rule trendAlignment = new OverIndicatorRule(hmaShort, hmaLong); // Short above long
 * 
 * // Complete strategy
 * Rule completeEntry = entryRule.and(trendAlignment);
 * Strategy hmaStrategy = new BaseStrategy("HMA Trend", completeEntry, exitRule);
 * 
 * // Apply to different price sources
 * TypicalPriceIndicator typicalPrice = new TypicalPriceIndicator(series);
 * HMAIndicator hmaTypical = new HMAIndicator(typicalPrice, 21);
 * }</pre>
 * 
 * @see WMAIndicator
 * @see SMAIndicator
 * @see EMAIndicator
 * @see org.ta4j.core.indicators.helpers.TypicalPriceIndicator
 * @see <a href="http://alanhull.com/hull-moving-average">Alan Hull - Hull Moving Average</a>
 * @see <a href="https://www.tradingview.com/wiki/Hull_Moving_Average_(HMA)">TradingView - HMA Guide</a>
 * @since 0.1
 */
public class HMAIndicator extends CachedIndicator<Num> {

    private final int barCount;
    private final WMAIndicator sqrtWma;

    /**
     * Creates a Hull Moving Average indicator.
     * 
     * <p>The HMA calculation involves multiple stages of weighted moving averages and
     * square root operations to achieve reduced lag while maintaining smoothness.
     * The final period used for the outer WMA is the square root of the input period,
     * which is a key innovation of Hull's algorithm.
     * 
     * <p><strong>Calculation Process:</strong>
     * <ol>
     * <li>Create WMA with half period (barCount/2)</li>
     * <li>Create WMA with full period (barCount)</li>
     * <li>Calculate intermediate: (2 × half WMA) - full WMA</li>
     * <li>Apply final WMA with √barCount period to the intermediate result</li>
     * </ol>
     *
     * @param indicator the price indicator to calculate HMA for (must not be null)
     * @param barCount  the period for the HMA calculation, typically 9-100 (must be > 0)
     * @throws IllegalArgumentException if indicator is null or barCount <= 0
     */
    public HMAIndicator(Indicator<Num> indicator, int barCount) {
        super(indicator);
        this.barCount = barCount;

        final var halfWma = new WMAIndicator(indicator, barCount / 2);
        final var origWma = new WMAIndicator(indicator, barCount);

        final var indicatorForSqrtWma = BinaryOperation.difference(BinaryOperation.product(halfWma, 2), origWma);
        this.sqrtWma = new WMAIndicator(indicatorForSqrtWma,
                getBarSeries().numFactory().numOf(barCount).sqrt().intValue());
    }

    @Override
    protected Num calculate(int index) {
        return sqrtWma.getValue(index);
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
