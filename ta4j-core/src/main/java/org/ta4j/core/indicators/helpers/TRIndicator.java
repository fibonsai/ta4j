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
 * True Range (TR) indicator for measuring intraday volatility and price gaps.
 * 
 * <p>The True Range indicator, developed by J. Welles Wilder Jr., provides a comprehensive
 * measure of price volatility that accounts for both intraday price movement and overnight
 * gaps. Unlike simple range calculations, True Range captures the full extent of price
 * movement between trading sessions, making it essential for volatility analysis and
 * risk management calculations.
 * 
 * <h2>Calculation Method</h2>
 * <p>True Range = MAX(|High - Low|, |High - Previous Close|, |Previous Close - Low|)
 * 
 * <p>The calculation considers three possible scenarios and takes the maximum:
 * <ol>
 * <li><strong>High - Low:</strong> Normal intraday range (current bar only)</li>
 * <li><strong>High - Previous Close:</strong> Gap up scenario</li>
 * <li><strong>Previous Close - Low:</strong> Gap down scenario</li>
 * </ol>
 * 
 * <h2>Gap Handling Logic</h2>
 * <p>The True Range formula specifically addresses different market opening scenarios:
 * <ul>
 * <li><strong>Normal Trading:</strong> Open within previous day's range → TR = High - Low</li>
 * <li><strong>Gap Up Opening:</strong> Open above previous close → TR includes gap size</li>
 * <li><strong>Gap Down Opening:</strong> Open below previous close → TR includes gap size</li>
 * <li><strong>Limit Moves:</strong> Extreme gaps are fully captured in volatility measure</li>
 * </ul>
 * 
 * <h2>Volatility Components Captured</h2>
 * <ul>
 * <li><strong>Intraday Volatility:</strong> Normal price fluctuation during trading hours</li>
 * <li><strong>Overnight Risk:</strong> Price gaps due to after-hours events</li>
 * <li><strong>Market Discontinuity:</strong> Weekend and holiday gap risk</li>
 * <li><strong>News Impact:</strong> Sudden price jumps from unexpected events</li>
 * </ul>
 * 
 * <h2>Applications in Technical Analysis</h2>
 * <ul>
 * <li><strong>Average True Range (ATR):</strong>
 *     <ul>
 *     <li>Foundation for ATR calculation (smoothed average of TR)</li>
 *     <li>Dynamic position sizing based on volatility</li>
 *     <li>Adaptive stop-loss placement</li>
 *     </ul>
 * </li>
 * <li><strong>Volatility Breakouts:</strong>
 *     <ul>
 *     <li>Identify periods of expanding volatility</li>
 *     <li>Breakout confirmation signals</li>
 *     <li>Market regime change detection</li>
 *     </ul>
 * </li>
 * <li><strong>Risk Management:</strong>
 *     <ul>
 *     <li>Portfolio heat calculation</li>
 *     <li>Dynamic position sizing models</li>
 *     <li>Risk-adjusted performance metrics</li>
 *     </ul>
 * </li>
 * </ul>
 * 
 * <h2>Trading Strategy Integration</h2>
 * <ul>
 * <li><strong>Stop-Loss Calculation:</strong>
 *     <ul>
 *     <li>ATR-based stops (e.g., 2x ATR from entry)</li>
 *     <li>Adaptive stops that adjust to market volatility</li>
 *     <li>Trailing stops using ATR multiples</li>
 *     </ul>
 * </li>
 * <li><strong>Position Sizing:</strong>
 *     <ul>
 *     <li>Kelly Criterion with ATR-based risk estimates</li>
 *     <li>Fixed fractional position sizing</li>
 *     <li>Volatility-adjusted position allocation</li>
 *     </ul>
 * </li>
 * <li><strong>Market Filtering:</strong>
 *     <ul>
 *     <li>Avoid trading in extremely volatile conditions</li>
 *     <li>Scale down in high volatility environments</li>
 *     <li>Increase exposure in stable conditions</li>
 *     </ul>
 * </li>
 * </ul>
 * 
 * <h2>Market Regime Analysis</h2>
 * <ul>
 * <li><strong>Volatility Clustering:</strong> High TR periods tend to be followed by high TR periods</li>
 * <li><strong>Calm Before Storm:</strong> Extended low TR periods often precede volatility expansion</li>
 * <li><strong>Crisis Detection:</strong> Extreme TR values signal market stress and uncertainty</li>
 * <li><strong>Trend Strength:</strong> Increasing TR in trending markets confirms trend acceleration</li>
 * </ul>
 * 
 * <h2>Comparative Analysis</h2>
 * <ul>
 * <li><strong>vs. Simple Range:</strong> TR accounts for gaps; Range ignores overnight moves</li>
 * <li><strong>vs. Standard Deviation:</strong> TR measures absolute price movement; StdDev measures dispersion from mean</li>
 * <li><strong>vs. Beta:</strong> TR measures absolute volatility; Beta measures relative volatility to market</li>
 * </ul>
 * 
 * <h2>Implementation Notes</h2>
 * <ul>
 * <li><strong>First Bar Handling:</strong> Uses High - Low for initial calculation (no previous close available)</li>
 * <li><strong>Absolute Values:</strong> All differences use absolute values to ensure positive TR</li>
 * <li><strong>Maximum Function:</strong> Ensures True Range captures the largest price movement</li>
 * <li><strong>Immediate Calculation:</strong> Available from the second bar onward</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Basic True Range calculation
 * TRIndicator trueRange = new TRIndicator(series);
 * 
 * // Average True Range (ATR) - 14 period moving average of TR
 * ATRIndicator atr = new ATRIndicator(series, 14);
 * 
 * // Volatility-based position sizing
 * Num currentATR = atr.getValue(series.getEndIndex());
 * Num riskPerShare = currentATR.multipliedBy(2); // 2x ATR risk
 * Num riskCapital = portfolioValue.multipliedBy(0.01); // 1% portfolio risk
 * Num positionSize = riskCapital.dividedBy(riskPerShare);
 * 
 * // ATR-based stop losses
 * ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
 * NumericIndicator longStopPrice = NumericIndicator.of(closePrice).minus(atr.multipliedBy(2));
 * NumericIndicator shortStopPrice = NumericIndicator.of(closePrice).plus(atr.multipliedBy(2));
 * 
 * // Volatility breakout detection
 * SMAIndicator avgTR = new SMAIndicator(trueRange, 20);
 * Rule volatilityExpansion = new OverIndicatorRule(trueRange, avgTR.multipliedBy(2.0));
 * 
 * // Low volatility environment detection
 * Rule lowVolatility = new UnderIndicatorRule(trueRange, avgTR.multipliedBy(0.5));
 * 
 * // Gap analysis
 * HighPriceIndicator highPrice = new HighPriceIndicator(series);
 * LowPriceIndicator lowPrice = new LowPriceIndicator(series);
 * PreviousValueIndicator prevClose = new PreviousValueIndicator(closePrice, 1);
 * 
 * // Manual TR calculation for educational purposes
 * NumericIndicator normalRange = NumericIndicator.of(highPrice).minus(lowPrice);
 * NumericIndicator gapUpRange = NumericIndicator.of(highPrice).minus(prevClose);
 * NumericIndicator gapDownRange = NumericIndicator.of(prevClose).minus(lowPrice);
 * 
 * // Volatility-adjusted trading rules
 * Rule highVolatility = new OverIndicatorRule(trueRange, avgTR.multipliedBy(1.5));
 * Rule normalVolatility = highVolatility.negation().and(lowVolatility.negation());
 * 
 * // Only trade in normal volatility conditions
 * Rule filteredEntry = baseEntryRule.and(normalVolatility);
 * 
 * // Dynamic ATR trailing stop
 * ATRIndicator atr20 = new ATRIndicator(series, 20);
 * TrailingStopLossRule atrTrailingStop = new TrailingStopLossRule(
 *     closePrice, atr20.multipliedBy(3.0)); // 3x ATR trailing distance
 * }</pre>
 * 
 * @see org.ta4j.core.indicators.ATRIndicator
 * @see org.ta4j.core.indicators.helpers.HighPriceIndicator
 * @see org.ta4j.core.indicators.helpers.LowPriceIndicator
 * @see org.ta4j.core.indicators.helpers.ClosePriceIndicator
 * @see org.ta4j.core.indicators.helpers.PreviousValueIndicator
 * @since 0.1
 */
public class TRIndicator extends CachedIndicator<Num> {

    /**
     * Creates a True Range indicator.
     * 
     * <p>This constructor initializes the True Range calculation for the provided bar series.
     * The indicator will compute the maximum of three possible price ranges for each bar:
     * current range, gap up range, and gap down range, ensuring comprehensive volatility
     * measurement that includes overnight and weekend gaps.
     * 
     * <p>The True Range is immediately available from the second bar onward (index 1),
     * as it requires the previous bar's closing price for gap calculations.
     *
     * @param series the bar series containing OHLC price data (must not be null)
     * @throws IllegalArgumentException if series is null
     */
    public TRIndicator(BarSeries series) {
        super(series);
    }

    @Override
    protected Num calculate(int index) {
        Bar bar = getBarSeries().getBar(index);
        Num high = bar.getHighPrice();
        Num low = bar.getLowPrice();
        Num hl = high.minus(low);

        if (index == 0) {
            return hl.abs();
        }

        Num previousClose = getBarSeries().getBar(index - 1).getClosePrice();
        Num hc = high.minus(previousClose);
        Num cl = previousClose.minus(low);
        return hl.abs().max(hc.abs()).max(cl.abs());

    }

    /**
     * Returns the number of unstable bars for this indicator.
     * 
     * <p>The True Range indicator requires one bar to stabilize because it needs access
     * to the previous bar's closing price to calculate potential gap ranges. The first
     * bar (index 0) uses only the high-low range since no previous close is available.
     * 
     * <p>Starting from the second bar, the full True Range calculation is available,
     * providing complete gap-adjusted volatility measurements.
     *
     * @return {@code 1} (requires one bar for complete calculation)
     */
    @Override
    public int getCountOfUnstableBars() {
        return 1;
    }
}
