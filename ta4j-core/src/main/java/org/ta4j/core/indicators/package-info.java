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
/**
 * Technical indicators for market analysis and trading strategy development.
 * 
 * <p>This package contains a comprehensive collection of technical indicators used in financial
 * market analysis. Technical indicators are mathematical calculations based on price, volume,
 * or other market data that help traders and analysts assess market conditions, identify trends,
 * and generate trading signals.
 * 
 * <h2>Indicator Categories</h2>
 * 
 * <h3>Trend Indicators</h3>
 * <p>Identify the direction and strength of market trends:
 * <ul>
 * <li>{@link org.ta4j.core.indicators.averages.SMAIndicator SMA} - Simple Moving Average</li>
 * <li>{@link org.ta4j.core.indicators.averages.EMAIndicator EMA} - Exponential Moving Average</li>
 * <li>{@link org.ta4j.core.indicators.adx.ADXIndicator ADX} - Average Directional Index</li>
 * <li>{@link org.ta4j.core.indicators.ParabolicSarIndicator Parabolic SAR} - Stop and Reverse</li>
 * </ul>
 * 
 * <h3>Momentum Oscillators</h3>
 * <p>Measure the speed and change of price movements:
 * <ul>
 * <li>{@link org.ta4j.core.indicators.RSIIndicator RSI} - Relative Strength Index</li>
 * <li>{@link org.ta4j.core.indicators.MACDIndicator MACD} - Moving Average Convergence Divergence</li>
 * <li>{@link org.ta4j.core.indicators.CCIIndicator CCI} - Commodity Channel Index</li>
 * <li>{@link org.ta4j.core.indicators.StochasticOscillatorKIndicator Stochastic} - Stochastic Oscillator</li>
 * </ul>
 * 
 * <h3>Volatility Indicators</h3>
 * <p>Measure market volatility and price dispersion:
 * <ul>
 * <li>{@link org.ta4j.core.indicators.ATRIndicator ATR} - Average True Range</li>
 * <li>{@link org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator Bollinger Bands} - Volatility bands</li>
 * <li>{@link org.ta4j.core.indicators.statistics.StandardDeviationIndicator Standard Deviation} - Price dispersion</li>
 * </ul>
 * 
 * <h3>Volume Indicators</h3>
 * <p>Analyze trading volume patterns:
 * <ul>
 * <li>{@link org.ta4j.core.indicators.volume.OnBalanceVolumeIndicator OBV} - On Balance Volume</li>
 * <li>{@link org.ta4j.core.indicators.volume.VWAPIndicator VWAP} - Volume Weighted Average Price</li>
 * <li>{@link org.ta4j.core.indicators.volume.VolumeSMAIndicator Volume SMA} - Volume Moving Average</li>
 * </ul>
 * 
 * <h3>Support and Resistance</h3>
 * <p>Identify key price levels:
 * <ul>
 * <li>{@link org.ta4j.core.indicators.pivotpoints.StandardPivotPointIndicator Pivot Points} - Support/resistance levels</li>
 * <li>{@link org.ta4j.core.indicators.donchian.DonchianChannelUpperIndicator Donchian Channels} - Breakout levels</li>
 * <li>{@link org.ta4j.core.indicators.helpers.HighestValueIndicator Highest Value} - Maximum in period</li>
 * <li>{@link org.ta4j.core.indicators.helpers.LowestValueIndicator Lowest Value} - Minimum in period</li>
 * </ul>
 * 
 * <h2>Helper Indicators</h2>
 * <p>The {@link org.ta4j.core.indicators.helpers helpers} package contains fundamental building blocks:
 * <ul>
 * <li><strong>Price Indicators:</strong> Extract OHLC prices from bars</li>
 * <li><strong>Mathematical Indicators:</strong> Basic arithmetic operations</li>
 * <li><strong>Utility Indicators:</strong> Constants, transformations, and calculations</li>
 * </ul>
 * 
 * <h2>Design Patterns</h2>
 * 
 * <h3>Inheritance Hierarchy</h3>
 * <ul>
 * <li>{@link org.ta4j.core.Indicator Indicator&lt;T&gt;} - Base interface for all indicators</li>
 * <li>{@link org.ta4j.core.indicators.AbstractIndicator AbstractIndicator&lt;T&gt;} - Basic implementation</li>
 * <li>{@link org.ta4j.core.indicators.CachedIndicator CachedIndicator&lt;T&gt;} - Performance-optimized with caching</li>
 * <li>{@link org.ta4j.core.indicators.RecursiveCachedIndicator RecursiveCachedIndicator&lt;T&gt;} - For recursive calculations</li>
 * </ul>
 * 
 * <h3>Performance Considerations</h3>
 * <ul>
 * <li><strong>Caching:</strong> Most indicators cache calculated values for performance</li>
 * <li><strong>Lazy Evaluation:</strong> Values calculated only when requested</li>
 * <li><strong>Memory Management:</strong> Consider bar series size for memory usage</li>
 * <li><strong>Dependency Chain:</strong> Complex indicators may have multiple dependencies</li>
 * </ul>
 * 
 * <h2>Usage Patterns</h2>
 * 
 * <h3>Basic Usage</h3>
 * <pre>{@code
 * // Create basic indicators
 * ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
 * SMAIndicator sma20 = new SMAIndicator(closePrice, 20);
 * RSIIndicator rsi = new RSIIndicator(closePrice, 14);
 * 
 * // Get current values
 * Num currentPrice = closePrice.getValue(series.getEndIndex());
 * Num currentSMA = sma20.getValue(series.getEndIndex());
 * Num currentRSI = rsi.getValue(series.getEndIndex());
 * }</pre>
 * 
 * <h3>Indicator Combinations</h3>
 * <pre>{@code
 * // Create multiple timeframe analysis
 * SMAIndicator sma10 = new SMAIndicator(closePrice, 10);
 * SMAIndicator sma50 = new SMAIndicator(closePrice, 50);
 * 
 * // Combine indicators for complex analysis
 * MACDIndicator macd = new MACDIndicator(closePrice, 12, 26);
 * EMAIndicator signalLine = macd.getSignalLine(9);
 * NumericIndicator histogram = macd.getHistogram(9);
 * }</pre>
 * 
 * <h3>Custom Indicators</h3>
 * <p>Extend {@link org.ta4j.core.indicators.CachedIndicator} to create custom indicators:
 * <pre>{@code
 * public class CustomIndicator extends CachedIndicator&lt;Num&gt; {
 *     public CustomIndicator(BarSeries series) {
 *         super(series);
 *     }
 *     
 *     protected Num calculate(int index) {
 *         // Your calculation logic here
 *         return getBarSeries().numFactory().zero();
 *     }
 *     
 *     public int getCountOfUnstableBars() {
 *         return 0; // Your stability period
 *     }
 * }
 * }</pre>
 * 
 * <h2>Best Practices</h2>
 * <ul>
 * <li><strong>Stability Checking:</strong> Always check {@link org.ta4j.core.Indicator#isStable()} before using values</li>
 * <li><strong>Parameter Selection:</strong> Use common periods (14, 20, 50, 200) for consistency</li>
 * <li><strong>Combination Analysis:</strong> Use multiple indicators for confirmation</li>
 * <li><strong>Backtesting:</strong> Validate indicator effectiveness with historical data</li>
 * <li><strong>Performance:</strong> Consider computation cost when using many indicators</li>
 * </ul>
 * 
 * @see org.ta4j.core.Indicator
 * @see org.ta4j.core.BarSeries
 * @see org.ta4j.core.rules
 * @see <a href="https://www.investopedia.com/terms/t/technicalindicator.asp">Investopedia - Technical Indicators</a>
 * @see <a href="http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators">StockCharts - Technical Indicators</a>
 * @since 0.1
 */
package org.ta4j.core.indicators;