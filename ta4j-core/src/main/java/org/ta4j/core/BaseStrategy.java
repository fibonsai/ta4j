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
package org.ta4j.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base implementation of a trading {@link Strategy} with comprehensive functionality.
 * 
 * <p>BaseStrategy is the primary concrete implementation of the Strategy interface,
 * providing a complete trading strategy built from entry and exit rules. It handles
 * rule evaluation, signal generation, strategy combination, and logging functionality.
 * 
 * <h2>Core Features</h2>
 * <ul>
 * <li><strong>Rule-Based Logic:</strong> Combines entry and exit rules to generate trading signals</li>
 * <li><strong>Unstable Period Handling:</strong> Configurable warm-up period to avoid premature signals</li>
 * <li><strong>Strategy Combination:</strong> Support for AND, OR, and opposite strategy operations</li>
 * <li><strong>Logging & Debugging:</strong> Built-in trace logging for signal analysis</li>
 * <li><strong>Immutable Design:</strong> Thread-safe immutable strategy configuration</li>
 * </ul>
 * 
 * <h2>Strategy Lifecycle</h2>
 * <p>A BaseStrategy operates through a simple state-based evaluation:
 * <ol>
 * <li><strong>Initialization:</strong> Create strategy with entry/exit rules and optional parameters</li>
 * <li><strong>Warm-up Phase:</strong> Skip unstable bars to allow indicators to stabilize</li>
 * <li><strong>Signal Generation:</strong> Evaluate rules at each bar to generate entry/exit signals</li>
 * <li><strong>Decision Making:</strong> Return boolean signals based on current position state</li>
 * </ol>
 * 
 * <h2>Signal Logic</h2>
 * <ul>
 * <li><strong>Entry Signals:</strong> Generated when no position is open and entry rule is satisfied</li>
 * <li><strong>Exit Signals:</strong> Generated when position is open and exit rule is satisfied</li>
 * <li><strong>No Action:</strong> When in unstable period or when rules are not satisfied</li>
 * </ul>
 * 
 * <h2>Best Practices</h2>
 * <ul>
 * <li><strong>Unstable Period:</strong> Set to maximum warm-up period of any underlying indicators</li>
 * <li><strong>Rule Design:</strong> Ensure entry and exit rules are logically consistent</li>
 * <li><strong>Testing:</strong> Enable trace logging to debug strategy behavior</li>
 * <li><strong>Validation:</strong> Validate rules are not null during construction</li>
 * </ul>
 * 
 * <h2>Thread Safety</h2>
 * <p>BaseStrategy instances are <strong>immutable and thread-safe</strong> after construction.
 * The same strategy instance can be safely shared across multiple threads for concurrent
 * backtesting or analysis.
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Create indicators
 * ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
 * SMAIndicator sma20 = new SMAIndicator(closePrice, 20);
 * RSIIndicator rsi = new RSIIndicator(closePrice, 14);
 * 
 * // Create rules
 * Rule entryRule = new CrossedUpIndicatorRule(closePrice, sma20)
 *     .and(new UnderIndicatorRule(rsi, 30)); // Price above SMA and RSI oversold
 * 
 * Rule exitRule = new CrossedDownIndicatorRule(closePrice, sma20)
 *     .or(new OverIndicatorRule(rsi, 70)); // Price below SMA or RSI overbought
 * 
 * // Create strategy
 * Strategy strategy = new BaseStrategy("SMA-RSI Strategy", entryRule, exitRule, 20);
 * 
 * // Use strategy for backtesting
 * TradingRecord record = new BaseTradingRecord();
 * for (int i = strategy.getUnstableBars(); i <= series.getEndIndex(); i++) {
 *     if (strategy.shouldEnter(i, record)) {
 *         record.enter(i, series.getBar(i).getClosePrice(), series.numFactory().one());
 *     } else if (strategy.shouldExit(i, record)) {
 *         record.exit(i, series.getBar(i).getClosePrice(), series.numFactory().one());
 *     }
 * }
 * }</pre>
 * 
 * @see Strategy
 * @see Rule
 * @see TradingRecord
 * @see org.ta4j.core.backtest.BacktestExecutor
 * @since 0.1
 */
public class BaseStrategy implements Strategy {

    /** The logger. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    /** The class name. */
    private final String className = getClass().getSimpleName();

    /** The name of the strategy. */
    private final String name;

    /** The entry rule. */
    private final Rule entryRule;

    /** The exit rule. */
    private final Rule exitRule;

    /**
     * The number of first bars in a bar series that this strategy ignores. During
     * the unstable bars of the strategy, any trade placement will be canceled i.e.
     * no entry/exit signal will be triggered before {@code index == unstableBars}.
     */
    private int unstableBars;

    /**
     * Creates a strategy with the specified entry and exit rules.
     * 
     * <p>This is the simplest constructor that creates a strategy with:
     * <ul>
     * <li>Auto-generated name based on class name</li>
     * <li>No unstable period (starts trading immediately)</li>
     * </ul>
     * 
     * <p><strong>Warning:</strong> Consider setting an appropriate unstable period
     * if your rules use indicators that need time to stabilize.
     *
     * @param entryRule the rule that determines when to enter positions (must not be null)
     * @param exitRule  the rule that determines when to exit positions (must not be null)
     * @throws IllegalArgumentException if either rule is null
     */
    public BaseStrategy(Rule entryRule, Rule exitRule) {
        this(null, entryRule, exitRule, 0);
    }

    /**
     * Creates a strategy with entry/exit rules and a specified unstable period.
     * 
     * <p>This constructor is recommended when your strategy uses indicators that
     * require a warm-up period. The unstable period prevents the strategy from
     * generating signals before indicators have sufficient data.
     *
     * @param entryRule    the rule that determines when to enter positions (must not be null)
     * @param exitRule     the rule that determines when to exit positions (must not be null)
     * @param unstableBars the number of initial bars to ignore for signal generation (must be >= 0)
     * @throws IllegalArgumentException if rules are null or unstableBars < 0
     */
    public BaseStrategy(Rule entryRule, Rule exitRule, int unstableBars) {
        this(null, entryRule, exitRule, unstableBars);
    }

    /**
     * Creates a named strategy with the specified entry and exit rules.
     * 
     * <p>Providing a descriptive name helps with logging, debugging, and
     * identification in backtesting results. The name should describe the
     * strategy's approach (e.g., "SMA Crossover", "RSI Mean Reversion").
     *
     * @param name      the descriptive name for this strategy (may be null)
     * @param entryRule the rule that determines when to enter positions (must not be null)
     * @param exitRule  the rule that determines when to exit positions (must not be null)
     * @throws IllegalArgumentException if either rule is null
     */
    public BaseStrategy(String name, Rule entryRule, Rule exitRule) {
        this(name, entryRule, exitRule, 0);
    }

    /**
     * Creates a complete strategy with all configuration options.
     * 
     * <p>This is the most comprehensive constructor that allows full control over
     * strategy behavior. It's the recommended constructor for production strategies
     * that need proper naming and indicator stabilization.
     * 
     * <p><strong>Best Practice:</strong> Set unstableBars to the maximum warm-up
     * period required by any indicators used in the entry or exit rules.
     *
     * @param name         the descriptive name for this strategy (may be null)
     * @param entryRule    the rule that determines when to enter positions (must not be null)
     * @param exitRule     the rule that determines when to exit positions (must not be null)
     * @param unstableBars the number of initial bars to ignore for signal generation (must be >= 0)
     * @throws IllegalArgumentException if rules are null or unstableBars < 0
     */
    public BaseStrategy(String name, Rule entryRule, Rule exitRule, int unstableBars) {
        if (entryRule == null || exitRule == null) {
            throw new IllegalArgumentException("Rules cannot be null");
        }
        if (unstableBars < 0) {
            throw new IllegalArgumentException("Unstable bars must be >= 0");
        }
        this.name = name;
        this.entryRule = entryRule;
        this.exitRule = exitRule;
        this.unstableBars = unstableBars;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Rule getEntryRule() {
        return entryRule;
    }

    @Override
    public Rule getExitRule() {
        return exitRule;
    }

    @Override
    public int getUnstableBars() {
        return unstableBars;
    }

    @Override
    public void setUnstableBars(int unstableBars) {
        this.unstableBars = unstableBars;
    }

    @Override
    public boolean isUnstableAt(int index) {
        return index < unstableBars;
    }

    @Override
    public boolean shouldEnter(int index, TradingRecord tradingRecord) {
        boolean enter = Strategy.super.shouldEnter(index, tradingRecord);
        traceShouldEnter(index, enter);
        return enter;
    }

    @Override
    public boolean shouldExit(int index, TradingRecord tradingRecord) {
        boolean exit = Strategy.super.shouldExit(index, tradingRecord);
        traceShouldExit(index, exit);
        return exit;
    }

    @Override
    public Strategy and(Strategy strategy) {
        String andName = "and(" + name + "," + strategy.getName() + ")";
        int unstable = Math.max(unstableBars, strategy.getUnstableBars());
        return and(andName, strategy, unstable);
    }

    @Override
    public Strategy or(Strategy strategy) {
        String orName = "or(" + name + "," + strategy.getName() + ")";
        int unstable = Math.max(unstableBars, strategy.getUnstableBars());
        return or(orName, strategy, unstable);
    }

    @Override
    public Strategy opposite() {
        return new BaseStrategy("opposite(" + name + ")", exitRule, entryRule, unstableBars);
    }

    @Override
    public Strategy and(String name, Strategy strategy, int unstableBars) {
        return new BaseStrategy(name, entryRule.and(strategy.getEntryRule()), exitRule.and(strategy.getExitRule()),
                unstableBars);
    }

    @Override
    public Strategy or(String name, Strategy strategy, int unstableBars) {
        return new BaseStrategy(name, entryRule.or(strategy.getEntryRule()), exitRule.or(strategy.getExitRule()),
                unstableBars);
    }

    /**
     * Traces the {@code shouldEnter()} method calls.
     *
     * @param index the bar index
     * @param enter true if the strategy should enter, false otherwise
     */
    protected void traceShouldEnter(int index, boolean enter) {
        if (log.isTraceEnabled()) {
            log.trace(">>> {}#shouldEnter({}): {}", className, index, enter);
        }
    }

    /**
     * Traces the {@code shouldExit()} method calls.
     *
     * @param index the bar index
     * @param exit  true if the strategy should exit, false otherwise
     */
    protected void traceShouldExit(int index, boolean exit) {
        if (log.isTraceEnabled()) {
            log.trace(">>> {}#shouldExit({}): {}", className, index, exit);
        }
    }
}
