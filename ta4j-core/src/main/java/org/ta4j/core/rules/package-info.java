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
 * Trading rules for building sophisticated trading strategies and signal generation.
 * 
 * <p>This package contains a comprehensive collection of trading rules that serve as the building
 * blocks for creating automated trading strategies. Rules evaluate market conditions and return
 * boolean signals that can be combined using logical operators to form complex trading logic.
 * 
 * <h2>Rule Architecture</h2>
 * <p>All rules implement the {@link org.ta4j.core.Rule} interface and follow a consistent pattern:
 * <ul>
 * <li><strong>Evaluation Method:</strong> {@code isSatisfied(int index, TradingRecord tradingRecord)}</li>
 * <li><strong>Logical Operators:</strong> {@code and()}, {@code or()}, {@code xor()}, {@code negation()}</li>
 * <li><strong>Immutability:</strong> Rules are immutable and can be safely reused</li>
 * <li><strong>Composability:</strong> Rules can be combined to create complex conditions</li>
 * </ul>
 * 
 * <h2>Rule Categories</h2>
 * 
 * <h3>Comparison Rules</h3>
 * <p>Compare indicators with thresholds or other indicators:
 * <ul>
 * <li>{@link OverIndicatorRule} - Value above threshold or another indicator</li>
 * <li>{@link UnderIndicatorRule} - Value below threshold or another indicator</li>
 * <li>{@link IsEqualRule} - Values are equal within tolerance</li>
 * </ul>
 * 
 * <h3>Crossover Rules</h3>
 * <p>Detect crossing events between indicators:
 * <ul>
 * <li>{@link CrossedUpIndicatorRule} - Upward crossover detection</li>
 * <li>{@link CrossedDownIndicatorRule} - Downward crossover detection</li>
 * </ul>
 * 
 * <h3>Trend and Pattern Rules</h3>
 * <p>Identify trend characteristics and patterns:
 * <ul>
 * <li>{@link IsRisingRule} - Indicator is trending upward</li>
 * <li>{@link IsFallingRule} - Indicator is trending downward</li>
 * <li>{@link IsHighestRule} - Value at highest point in period</li>
 * <li>{@link IsLowestRule} - Value at lowest point in period</li>
 * </ul>
 * 
 * <h3>Boolean Logic Rules</h3>
 * <p>Direct boolean indicator evaluation:
 * <ul>
 * <li>{@link BooleanIndicatorRule} - Evaluates boolean indicators</li>
 * <li>{@link BooleanRule} - Fixed true/false values</li>
 * <li>{@link FixedRule} - Always returns same boolean value</li>
 * </ul>
 * 
 * <h3>Risk Management Rules</h3>
 * <p>Position and risk control rules:
 * <ul>
 * <li>{@link StopLossRule} - Stop loss protection</li>
 * <li>{@link StopGainRule} - Take profit protection</li>
 * <li>{@link TrailingStopLossRule} - Dynamic stop loss</li>
 * <li>{@link AverageTrueRangeStopLossRule} - ATR-based stop loss</li>
 * </ul>
 * 
 * <h3>Time-Based Rules</h3>
 * <p>Rules based on time and timing:
 * <ul>
 * <li>{@link DayOfWeekRule} - Specific days of the week</li>
 * <li>{@link TimeRangeRule} - Time-of-day restrictions</li>
 * <li>{@link WaitForRule} - Wait specified number of bars</li>
 * </ul>
 * 
 * <h3>Composite Rules</h3>
 * <p>Complex combinations and logic:
 * <ul>
 * <li>{@link AndRule} - Logical AND combination</li>
 * <li>{@link OrRule} - Logical OR combination</li>
 * <li>{@link XorRule} - Logical XOR combination</li>
 * <li>{@link NotRule} - Logical negation</li>
 * <li>{@link ChainRule} - Sequential rule evaluation</li>
 * </ul>
 * 
 * <h2>Usage Patterns</h2>
 * 
 * <h3>Basic Rule Creation</h3>
 * <pre>{@code
 * // Simple threshold rules
 * Rule rsiOverbought = new OverIndicatorRule(rsi, 70);
 * Rule rsiOversold = new UnderIndicatorRule(rsi, 30);
 * 
 * // Crossover rules
 * Rule goldenCross = new CrossedUpIndicatorRule(ema12, ema26);
 * Rule deathCross = new CrossedDownIndicatorRule(ema12, ema26);
 * }</pre>
 * 
 * <h3>Rule Combination</h3>
 * <pre>{@code
 * // Logical combinations
 * Rule entryRule = priceAboveSMA.and(volumeHigh).and(rsiOversold.negation());
 * Rule exitRule = priceBelowSMA.or(rsiOverbought).or(stopLoss);
 * 
 * // Complex conditions
 * Rule trendConfirmation = goldenCross.and(new OverIndicatorRule(rsi, 50));
 * Rule reversalSignal = rsiOverbought.and(new IsFallingRule(closePrice, 3));
 * }</pre>
 * 
 * <h3>Strategy Integration</h3>
 * <pre>{@code
 * // Create comprehensive strategy
 * Rule entryRule = new CrossedUpIndicatorRule(price, sma20)
 *     .and(new OverIndicatorRule(volume, avgVolume))
 *     .and(new UnderIndicatorRule(rsi, 80));
 * 
 * Rule exitRule = new CrossedDownIndicatorRule(price, sma20)
 *     .or(new StopLossRule(closePrice, Percentage.valueOf(5)))
 *     .or(new StopGainRule(closePrice, Percentage.valueOf(10)));
 * 
 * Strategy strategy = new BaseStrategy("Trend Following", entryRule, exitRule);
 * }</pre>
 * 
 * <h2>Performance Considerations</h2>
 * <ul>
 * <li><strong>Evaluation Cost:</strong> Rules are evaluated once per bar, keep logic simple</li>
 * <li><strong>Indicator Dependencies:</strong> Rules inherit performance characteristics of underlying indicators</li>
 * <li><strong>Combination Efficiency:</strong> Use short-circuit evaluation when possible</li>
 * <li><strong>Memory Usage:</strong> Rules maintain references to indicators, consider memory implications</li>
 * </ul>
 * 
 * <h2>Best Practices</h2>
 * <ul>
 * <li><strong>Rule Composition:</strong> Build complex logic from simple, testable rules</li>
 * <li><strong>Indicator Stability:</strong> Ensure underlying indicators are stable before evaluation</li>
 * <li><strong>Logical Clarity:</strong> Use clear, descriptive rule combinations</li>
 * <li><strong>Performance Testing:</strong> Validate rule effectiveness through backtesting</li>
 * <li><strong>Risk Management:</strong> Always include appropriate risk control rules</li>
 * </ul>
 * 
 * @see org.ta4j.core.Rule
 * @see org.ta4j.core.Strategy
 * @see org.ta4j.core.Indicator
 * @since 0.1
 */
package org.ta4j.core.rules;