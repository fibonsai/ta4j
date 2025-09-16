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

/**
 * A trading strategy that combines entry and exit {@link Rule rules} to generate buy/sell signals.
 * 
 * <p>A Strategy is the core component for automated trading decisions in Ta4j. It consists of:
 * <ul>
 * <li><strong>Entry Rule</strong> - Determines when to open a new position (buy signal)</li>
 * <li><strong>Exit Rule</strong> - Determines when to close an existing position (sell signal)</li>
 * <li><strong>Unstable Period</strong> - Initial bars to ignore during strategy warm-up</li>
 * </ul>
 * 
 * <h2>Strategy Lifecycle</h2>
 * <p>A strategy operates in a simple state machine:</p>
 * <ol>
 * <li><strong>No Position</strong> - Evaluates entry rule to determine if a new position should be opened</li>
 * <li><strong>Open Position</strong> - Evaluates exit rule to determine if the position should be closed</li>
 * </ol>
 * 
 * <h2>Strategy Combination</h2>
 * <p>Strategies can be combined using logical operators:</p>
 * <ul>
 * <li>{@link #and(Strategy)} - Both strategies must agree on signals</li>
 * <li>{@link #or(Strategy)} - Either strategy can trigger signals</li>
 * <li>{@link #opposite()} - Reverses entry and exit rules</li>
 * </ul>
 * 
 * <h2>Unstable Period</h2>
 * <p>Strategies should account for indicator warm-up periods by setting {@link #setUnstableBars(int)}.
 * During this initial period, the strategy will not generate any trading signals, allowing
 * underlying indicators to stabilize.</p>
 * 
 * @see Rule
 * @see BaseStrategy
 * @see org.ta4j.core.TradingRecord
 * @since 0.1
 */
public interface Strategy {

    /**
     * Returns the human-readable name of this strategy.
     * 
     * <p>The strategy name is typically used for logging, debugging, and displaying
     * results in backtesting reports. If no name was provided during construction,
     * implementations may return a default name based on the strategy type.
     *
     * @return the strategy name (may be null if not set)
     */
    String getName();

    /**
     * Returns the entry rule that determines when to open new positions.
     * 
     * <p>The entry rule is evaluated when no position is currently open.
     * When the rule is satisfied, the strategy will recommend opening a new position.
     *
     * @return the entry rule (never null)
     * @see #shouldEnter(int)
     */
    Rule getEntryRule();

    /**
     * Returns the exit rule that determines when to close existing positions.
     * 
     * <p>The exit rule is evaluated when a position is currently open.
     * When the rule is satisfied, the strategy will recommend closing the position.
     *
     * @return the exit rule (never null)
     * @see #shouldExit(int)
     */
    Rule getExitRule();

    /**
     * Creates a new strategy that is the logical AND combination of this strategy and another.
     * 
     * <p>The resulting strategy will only generate signals when <strong>both</strong> strategies
     * agree. For entry signals, both strategies must recommend entering. For exit signals,
     * both strategies must recommend exiting.
     * 
     * <p>The unstable period of the combined strategy will be the maximum of both strategies.
     *
     * @param strategy the other strategy to combine with (must not be null)
     * @return a new strategy representing the AND combination
     * @throws IllegalArgumentException if strategy is null
     * @see #and(String, Strategy, int)
     */
    Strategy and(Strategy strategy);

    /**
     * Creates a new strategy that is the logical OR combination of this strategy and another.
     * 
     * <p>The resulting strategy will generate signals when <strong>either</strong> strategy
     * recommends action. For entry signals, either strategy can trigger entry. For exit signals,
     * either strategy can trigger exit.
     * 
     * <p>The unstable period of the combined strategy will be the maximum of both strategies.
     *
     * @param strategy the other strategy to combine with (must not be null)
     * @return a new strategy representing the OR combination
     * @throws IllegalArgumentException if strategy is null
     * @see #or(String, Strategy, int)
     */
    Strategy or(Strategy strategy);

    /**
     * Creates a named AND combination of this strategy with another, using a custom unstable period.
     * 
     * <p>This method provides more control over the combined strategy compared to {@link #and(Strategy)}.
     * You can specify a custom name and explicit unstable period for the resulting strategy.
     *
     * @param name         the name for the combined strategy (may be null)
     * @param strategy     the other strategy to combine with (must not be null)
     * @param unstableBars the number of initial bars to ignore (must be >= 0)
     * @return a new strategy representing the AND combination
     * @throws IllegalArgumentException if strategy is null or unstableBars < 0
     * @see #and(Strategy)
     */
    Strategy and(String name, Strategy strategy, int unstableBars);

    /**
     * Creates a named OR combination of this strategy with another, using a custom unstable period.
     * 
     * <p>This method provides more control over the combined strategy compared to {@link #or(Strategy)}.
     * You can specify a custom name and explicit unstable period for the resulting strategy.
     *
     * @param name         the name for the combined strategy (may be null)
     * @param strategy     the other strategy to combine with (must not be null)
     * @param unstableBars the number of initial bars to ignore (must be >= 0)
     * @return a new strategy representing the OR combination
     * @throws IllegalArgumentException if strategy is null or unstableBars < 0
     * @see #or(Strategy)
     */
    Strategy or(String name, Strategy strategy, int unstableBars);

    /**
     * Creates the opposite (inverted) version of this strategy.
     * 
     * <p>The opposite strategy swaps the entry and exit rules:
     * <ul>
     * <li>The original <strong>entry</strong> rule becomes the new <strong>exit</strong> rule</li>
     * <li>The original <strong>exit</strong> rule becomes the new <strong>entry</strong> rule</li>
     * </ul>
     * 
     * <p>This can be useful for testing contrarian strategies or for implementing
     * short-selling strategies from long-only strategies.
     *
     * @return a new strategy with inverted entry/exit rules
     */
    Strategy opposite();

    /**
     * Sets the number of initial bars that this strategy should ignore.
     * 
     * <p>During the unstable period, the strategy will not generate any trading signals,
     * allowing underlying indicators to accumulate enough data for reliable calculations.
     * This should typically be set to the maximum unstable period of any indicators
     * used in the strategy's rules.
     *
     * @param unstableBars the number of initial bars to ignore (must be >= 0)
     * @throws IllegalArgumentException if unstableBars < 0
     * @see #getUnstableBars()
     * @see #isUnstableAt(int)
     */
    void setUnstableBars(int unstableBars);

    /**
     * Returns the number of initial bars that this strategy ignores.
     * 
     * <p>This represents the "warm-up" period during which the strategy will not
     * generate any trading signals. The value should be set high enough to allow
     * all underlying indicators to stabilize.
     *
     * @return the number of unstable bars (>= 0)
     * @see #setUnstableBars(int)
     * @see #isUnstableAt(int)
     */
    int getUnstableBars();

    /**
     * Checks if this strategy is in its unstable period at the given index.
     * 
     * <p>A strategy is considered unstable during its initial warm-up period when
     * underlying indicators may not have sufficient data for reliable calculations.
     * No trading signals should be generated during this period.
     *
     * @param index the bar index to check (must be >= 0)
     * @return true if the strategy is unstable at this index, false if stable
     * @see #getUnstableBars()
     * @see #setUnstableBars(int)
     */
    boolean isUnstableAt(int index);

    /**
     * Determines if the strategy recommends any trading action at the given index.
     * 
     * <p>This method evaluates whether the strategy should enter a new position or
     * exit an existing one based on the current position state in the trading record.
     * It automatically delegates to {@link #shouldEnter(int, TradingRecord)} or 
     * {@link #shouldExit(int, TradingRecord)} as appropriate.
     * 
     * <p>If no position is open, it checks for entry signals. If a position is open,
     * it checks for exit signals. The strategy will not operate during unstable periods.
     *
     * @param index         the bar index to evaluate (must be >= 0)
     * @param tradingRecord the trading history containing current position state (must not be null)
     * @return true if any trading action is recommended, false otherwise
     * @see #shouldEnter(int, TradingRecord)
     * @see #shouldExit(int, TradingRecord)
     * @see #isUnstableAt(int)
     */
    default boolean shouldOperate(int index, TradingRecord tradingRecord) {
        Position position = tradingRecord.getCurrentPosition();
        if (position.isNew()) {
            return shouldEnter(index, tradingRecord);
        } else if (position.isOpened()) {
            return shouldExit(index, tradingRecord);
        }
        return false;
    }

    /**
     * Determines if the strategy recommends entering a new position at the given index.
     * 
     * <p>This is a convenience method that calls {@link #shouldEnter(int, TradingRecord)}
     * with a null trading record. Use this when you don't need to consider trading history
     * in your entry decisions.
     *
     * @param index the bar index to evaluate (must be >= 0)
     * @return true if entry is recommended, false otherwise
     * @see #shouldEnter(int, TradingRecord)
     */
    default boolean shouldEnter(int index) {
        return shouldEnter(index, null);
    }

    /**
     * Determines if the strategy recommends entering a new position at the given index.
     * 
     * <p>This method evaluates the entry rule to determine if a new position should be opened.
     * The strategy will not recommend entry during unstable periods, even if the entry rule
     * is satisfied.
     * 
     * <p>Some strategies may need access to trading history (e.g., to avoid re-entering
     * too quickly after an exit). The trading record provides this context.
     *
     * @param index         the bar index to evaluate (must be >= 0)
     * @param tradingRecord the trading history for context (may be null)
     * @return true if entry is recommended, false otherwise
     * @see #getEntryRule()
     * @see #isUnstableAt(int)
     */
    default boolean shouldEnter(int index, TradingRecord tradingRecord) {
        return !isUnstableAt(index) && getEntryRule().isSatisfied(index, tradingRecord);
    }

    /**
     * Determines if the strategy recommends exiting the current position at the given index.
     * 
     * <p>This is a convenience method that calls {@link #shouldExit(int, TradingRecord)}
     * with a null trading record. Use this when you don't need to consider trading history
     * in your exit decisions.
     *
     * @param index the bar index to evaluate (must be >= 0)
     * @return true if exit is recommended, false otherwise
     * @see #shouldExit(int, TradingRecord)
     */
    default boolean shouldExit(int index) {
        return shouldExit(index, null);
    }

    /**
     * Determines if the strategy recommends exiting the current position at the given index.
     * 
     * <p>This method evaluates the exit rule to determine if an open position should be closed.
     * The strategy will not recommend exit during unstable periods, even if the exit rule
     * is satisfied.
     * 
     * <p>Some strategies may need access to trading history (e.g., to implement trailing stops
     * or time-based exits). The trading record provides this context.
     *
     * @param index         the bar index to evaluate (must be >= 0)
     * @param tradingRecord the trading history for context (may be null)
     * @return true if exit is recommended, false otherwise
     * @see #getExitRule()
     * @see #isUnstableAt(int)
     */
    default boolean shouldExit(int index, TradingRecord tradingRecord) {
        return !isUnstableAt(index) && getExitRule().isSatisfied(index, tradingRecord);
    }
}
