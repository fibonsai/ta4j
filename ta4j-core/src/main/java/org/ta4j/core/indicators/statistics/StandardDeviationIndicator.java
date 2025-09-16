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
package org.ta4j.core.indicators.statistics;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

/**
 * Standard Deviation indicator for measuring price volatility and dispersion.
 * 
 * <p>Standard Deviation is a statistical measure that quantifies the amount of variation
 * or dispersion of a dataset from its mean value. In technical analysis, it's primarily
 * used to measure price volatility, serving as the foundation for many volatility-based
 * indicators and trading strategies.
 * 
 * <h2>Calculation</h2>
 * <p>Standard Deviation = √(Variance)
 * <br>Where Variance = Σ(Xi - μ)² / N
 * <ul>
 * <li><strong>Xi:</strong> Individual price values</li>
 * <li><strong>μ:</strong> Mean (average) of the price values</li>
 * <li><strong>N:</strong> Number of periods</li>
 * <li><strong>√:</strong> Square root operation</li>
 * </ul>
 * 
 * <h2>Volatility Measurement</h2>
 * <p>Standard deviation quantifies price volatility:
 * <ul>
 * <li><strong>High Values:</strong> Indicate high volatility and price dispersion</li>
 * <li><strong>Low Values:</strong> Indicate low volatility and price stability</li>
 * <li><strong>Relative Comparison:</strong> Compare current volatility to historical levels</li>
 * <li><strong>Risk Assessment:</strong> Higher standard deviation indicates higher risk</li>
 * </ul>
 * 
 * <h2>Trading Applications</h2>
 * <ul>
 * <li><strong>Bollinger Bands:</strong>
 *     <ul>
 *     <li>Upper Band = SMA + (2 × Standard Deviation)</li>
 *     <li>Lower Band = SMA - (2 × Standard Deviation)</li>
 *     <li>Measures market volatility and overbought/oversold conditions</li>
 *     </ul>
 * </li>
 * <li><strong>Volatility Breakouts:</strong>
 *     <ul>
 *     <li>Low standard deviation periods often precede price breakouts</li>
 *     <li>High standard deviation indicates ongoing volatile periods</li>
 *     </ul>
 * </li>
 * <li><strong>Risk Management:</strong>
 *     <ul>
 *     <li>Position sizing based on current volatility levels</li>
 *     <li>Stop-loss placement using volatility-based distances</li>
 *     <li>Portfolio risk assessment and diversification</li>
 *     </ul>
 * </li>
 * <li><strong>Market Regime Detection:</strong>
 *     <ul>
 *     <li>Identify calm vs. volatile market periods</li>
 *     <li>Adjust trading strategies based on volatility environment</li>
 *     </ul>
 * </li>
 * </ul>
 * 
 * <h2>Volatility Analysis</h2>
 * <ul>
 * <li><strong>Relative Volatility:</strong> Compare current std dev to historical averages</li>
 * <li><strong>Volatility Clustering:</strong> High volatility periods tend to cluster together</li>
 * <li><strong>Mean Reversion:</strong> Volatility tends to revert to long-term averages</li>
 * <li><strong>Volatility Expansion:</strong> Periods of low volatility often precede expansion</li>
 * </ul>
 * 
 * <h2>Market Context</h2>
 * <ul>
 * <li><strong>Trending Markets:</strong> Often show sustained directional movement with moderate volatility</li>
 * <li><strong>Ranging Markets:</strong> Typically exhibit higher relative volatility within the range</li>
 * <li><strong>Breakout Markets:</strong> Show initial volatility expansion followed by trending</li>
 * <li><strong>Crisis Periods:</strong> Characterized by extremely high volatility readings</li>
 * </ul>
 * 
 * <h2>Advantages</h2>
 * <ul>
 * <li>Objective, mathematical measure of price dispersion</li>
 * <li>Foundation for many sophisticated technical indicators</li>
 * <li>Useful for risk assessment and position sizing</li>
 * <li>Helps identify market regime changes</li>
 * <li>Works across all timeframes and asset classes</li>
 * </ul>
 * 
 * <h2>Limitations</h2>
 * <ul>
 * <li>Backward-looking measure based on historical data</li>
 * <li>Assumes normal distribution of price changes</li>
 * <li>Can be slow to adapt to sudden volatility changes</li>
 * <li>May not capture all forms of market risk</li>
 * </ul>
 * 
 * <h2>Common Period Settings</h2>
 * <ul>
 * <li><strong>Short-term:</strong> 10-14 periods (more responsive to recent changes)</li>
 * <li><strong>Standard:</strong> 20 periods (most common for Bollinger Bands)</li>
 * <li><strong>Long-term:</strong> 50-100 periods (smoother, longer-term volatility)</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Create 20-period standard deviation
 * ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
 * StandardDeviationIndicator stdDev20 = new StandardDeviationIndicator(closePrice, 20);
 * 
 * // Bollinger Bands using standard deviation
 * SMAIndicator sma20 = new SMAIndicator(closePrice, 20);
 * BollingerBandsMiddleIndicator bbMiddle = new BollingerBandsMiddleIndicator(sma20);
 * BollingerBandsUpperIndicator bbUpper = new BollingerBandsUpperIndicator(bbMiddle, stdDev20, 2.0);
 * BollingerBandsLowerIndicator bbLower = new BollingerBandsLowerIndicator(bbMiddle, stdDev20, 2.0);
 * 
 * // Volatility-based rules
 * SMAIndicator avgVolatility = new SMAIndicator(stdDev20, 50);
 * Rule lowVolatility = new UnderIndicatorRule(stdDev20, avgVolatility.multipliedBy(0.5));
 * Rule highVolatility = new OverIndicatorRule(stdDev20, avgVolatility.multipliedBy(2.0));
 * 
 * // Volatility breakout strategy
 * Rule volatilityBreakout = lowVolatility; // Low vol often precedes breakouts
 * Rule priceBreakout = new CrossedUpIndicatorRule(closePrice, bbUpper);
 * Rule combinedBreakout = volatilityBreakout.and(priceBreakout);
 * 
 * // Risk-adjusted position sizing
 * Num currentStdDev = stdDev20.getValue(series.getEndIndex());
 * Num basePosition = series.numFactory().numOf(1000);
 * Num riskAdjustedSize = basePosition.dividedBy(currentStdDev); // Smaller positions in high volatility
 * 
 * // Volatility filter for strategy
 * Rule normalVolatility = new UnderIndicatorRule(stdDev20, avgVolatility.multipliedBy(1.5));
 * Rule filteredStrategy = entrySignal.and(normalVolatility); // Only trade in normal volatility
 * 
 * // Multi-timeframe volatility analysis
 * StandardDeviationIndicator stdDevShort = new StandardDeviationIndicator(closePrice, 10);
 * StandardDeviationIndicator stdDevLong = new StandardDeviationIndicator(closePrice, 50);
 * Rule volatilityExpansion = new OverIndicatorRule(stdDevShort, stdDevLong.multipliedBy(1.2));
 * }</pre>
 * 
 * @see VarianceIndicator
 * @see org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator
 * @see org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator
 * @see org.ta4j.core.indicators.averages.SMAIndicator
 * @see <a href="https://www.investopedia.com/terms/s/standarddeviation.asp">Investopedia - Standard Deviation</a>
 * @see <a href="https://school.stockcharts.com/doku.php?id=technical_indicators:standard_deviation_volatility">StockCharts - Standard Deviation</a>
 * @since 0.1
 */
public class StandardDeviationIndicator extends CachedIndicator<Num> {

    private final VarianceIndicator variance;

    /**
     * Creates a Standard Deviation indicator for measuring price volatility.
     * 
     * <p>This constructor calculates the standard deviation of the specified indicator
     * over the given time period. The calculation uses the population standard deviation
     * formula, taking the square root of the variance to provide a volatility measure
     * in the same units as the original indicator.
     * 
     * <p>The standard deviation quantifies how much individual values deviate from
     * the mean, making it essential for volatility-based analysis and risk management.
     *
     * @param indicator the indicator to calculate standard deviation for (must not be null)
     * @param barCount  the period for standard deviation calculation, typically 20 (must be > 0)
     * @throws IllegalArgumentException if indicator is null or barCount <= 0
     */
    public StandardDeviationIndicator(Indicator<Num> indicator, int barCount) {
        super(indicator);
        this.variance = new VarianceIndicator(indicator, barCount);
    }

    @Override
    protected Num calculate(int index) {
        return variance.getValue(index).sqrt();
    }

    @Override
    public int getCountOfUnstableBars() {
        return 0;
    }
}
