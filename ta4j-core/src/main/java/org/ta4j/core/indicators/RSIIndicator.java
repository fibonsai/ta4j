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

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.averages.MMAIndicator;
import org.ta4j.core.indicators.helpers.GainIndicator;
import org.ta4j.core.indicators.helpers.LossIndicator;
import org.ta4j.core.num.Num;

import static org.ta4j.core.num.NaN.NaN;

/**
 * Relative Strength Index (RSI) oscillator indicator.
 * 
 * <p>The RSI is a momentum oscillator that measures the speed and change of price movements,
 * developed by J. Welles Wilder Jr. It oscillates between 0 and 100 and is primarily used
 * to identify overbought and oversold conditions in a market.
 * 
 * <h2>Calculation</h2>
 * <p>The RSI is calculated using Wilder's original formula:
 * <ol>
 * <li>Calculate average gains and losses over the specified period</li>
 * <li>RS (Relative Strength) = Average Gain / Average Loss</li>
 * <li>RSI = 100 - (100 / (1 + RS))</li>
 * </ol>
 * 
 * <p>Where average gains and losses are calculated using Wilder's smoothing method 
 * (Modified Moving Average), making the RSI more stable than using simple averages.
 * 
 * <h2>Interpretation</h2>
 * <ul>
 * <li><strong>Overbought:</strong> RSI > 70 (traditional threshold)</li>
 * <li><strong>Oversold:</strong> RSI < 30 (traditional threshold)</li>
 * <li><strong>Neutral:</strong> RSI between 30-70</li>
 * <li><strong>Trend Strength:</strong> RSI > 50 suggests uptrend, RSI < 50 suggests downtrend</li>
 * </ul>
 * 
 * <h2>Trading Signals</h2>
 * <ul>
 * <li><strong>Divergence:</strong> Price and RSI moving in opposite directions</li>
 * <li><strong>Overbought/Oversold:</strong> RSI crossing threshold levels</li>
 * <li><strong>Centerline:</strong> RSI crossing above/below 50</li>
 * <li><strong>Failure Swings:</strong> RSI fails to exceed previous high/low</li>
 * </ul>
 * 
 * <h2>Limitations</h2>
 * <ul>
 * <li>Can remain overbought/oversold for extended periods in strong trends</li>
 * <li>Threshold levels may need adjustment for different markets/timeframes</li>
 * <li>Works best in sideways or moderately trending markets</li>
 * <li>Should be used with other indicators for confirmation</li>
 * </ul>
 * 
 * <h2>Common Periods</h2>
 * <ul>
 * <li><strong>Standard:</strong> 14 periods (Wilder's original recommendation)</li>
 * <li><strong>Short-term:</strong> 9 periods (more sensitive)</li>
 * <li><strong>Long-term:</strong> 21 or 25 periods (more stable)</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Create 14-period RSI on close prices
 * ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
 * RSIIndicator rsi = new RSIIndicator(closePrice, 14);
 * 
 * // Create overbought/oversold rules
 * Rule overbought = new OverIndicatorRule(rsi, 70);
 * Rule oversold = new UnderIndicatorRule(rsi, 30);
 * 
 * // Create strategy
 * Rule entryRule = oversold; // Buy when oversold
 * Rule exitRule = overbought; // Sell when overbought
 * }</pre>
 * 
 * @see org.ta4j.core.indicators.helpers.GainIndicator
 * @see org.ta4j.core.indicators.helpers.LossIndicator
 * @see org.ta4j.core.indicators.averages.MMAIndicator
 * @see <a href="https://www.investopedia.com/terms/r/rsi.asp">Investopedia - RSI</a>
 * @see <a href="https://school.stockcharts.com/doku.php?id=technical_indicators:relative_strength_index_rsi">StockCharts - RSI</a>
 * @since 0.1
 */
public class RSIIndicator extends CachedIndicator<Num> {

    private final MMAIndicator averageGainIndicator;
    private final MMAIndicator averageLossIndicator;
    private final int barCount;

    /**
     * Creates a Relative Strength Index indicator.
     * 
     * <p>This implementation follows Wilder's original RSI calculation using Modified Moving Average
     * (MMA) for smoothing gains and losses. The RSI will be calculated over the specified period
     * and will return values between 0 and 100.
     * 
     * <p><strong>Note:</strong> The indicator requires a warm-up period equal to the barCount
     * before producing reliable values. During this period, it may return {@link org.ta4j.core.num.NaN}.
     *
     * @param indicator the source indicator to calculate RSI over, typically a price indicator (must not be null)
     * @param barCount  the number of periods for RSI calculation, commonly 14 (must be > 0)
     * @throws IllegalArgumentException if indicator is null or barCount <= 0
     */
    public RSIIndicator(Indicator<Num> indicator, int barCount) {
        super(indicator);
        this.averageGainIndicator = new MMAIndicator(new GainIndicator(indicator), barCount);
        this.averageLossIndicator = new MMAIndicator(new LossIndicator(indicator), barCount);
        this.barCount = barCount;
    }

    @Override
    protected Num calculate(int index) {
        if (index < getCountOfUnstableBars()) {
            return NaN;
        }
        // compute relative strength
        Num averageGain = averageGainIndicator.getValue(index);
        Num averageLoss = averageLossIndicator.getValue(index);
        final var numFactory = getBarSeries().numFactory();
        if (averageLoss.isZero()) {
            return averageGain.isZero() ? numFactory.zero() : numFactory.hundred();
        }
        Num relativeStrength = averageGain.dividedBy(averageLoss);
        // compute relative strength index
        return numFactory.hundred().minus(numFactory.hundred().dividedBy(numFactory.one().plus(relativeStrength)));
    }

    @Override
    public int getCountOfUnstableBars() {
        return this.barCount;
    }
}
