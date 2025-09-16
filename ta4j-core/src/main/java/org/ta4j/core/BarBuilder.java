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

import java.time.Duration;
import java.time.Instant;

import org.ta4j.core.num.Num;

/**
 * Builder for constructing immutable {@link Bar} instances and optionally
 * adding them to a target {@link BarSeries}.
 *
 * <p>This builder abstracts the creation of bars across different bar types
 * (time, tick, volume, amount, Heikin-Ashi, …) while ensuring number
 * precision compatibility with the target series. Instances are typically
 * obtained via {@link BarSeries#barBuilder()} which guarantees that the
 * produced bars use the same {@link org.ta4j.core.num.NumFactory} as the
 * series.
 *
 * <h2>Relationships</h2>
 * <ul>
 * <li>Created by a {@link BarBuilderFactory} that matches the series' data
 * type</li>
 * <li>Bound to a {@link BarSeries} through {@link #bindTo(BarSeries)} for
 * convenient {@link #add()} operations</li>
 * <li>Used by loaders/aggregators to compose consistent series for
 * indicators, {@link Strategy strategies}, and backtests</li>
 * </ul>
 *
 * <h2>Typical workflow</h2>
 * <pre>{@code
 * BarSeries series = new BaseBarSeriesBuilder()
 *     .withName("AAPL")
 *     .build();
 *
 * // Obtain a builder that is compatible with the series' Num type
 * BarBuilder builder = series.barBuilder().bindTo(series);
 *
 * builder.timePeriod(Duration.ofMinutes(1))
 *        .beginTime(begin)
 *        .endTime(end)
 *        .openPrice("189.12")
 *        .highPrice("190.02")
 *        .lowPrice("188.90")
 *        .closePrice("189.76")
 *        .volume("125432")
 *        .add(); // builds and appends to series
 * }</pre>
 */
public interface BarBuilder {

    /**
     * @param timePeriod the time period (optional if {@link #beginTime(Instant)}
     *                   and {@link #endTime(Instant)} are given)
     * @return {@code this}
     */
    BarBuilder timePeriod(Duration timePeriod);

    /**
     * @param beginTime the begin time of the bar period (optional if
     *                  {@link #endTime(Instant)} is given)
     * @return {@code this}
     */
    BarBuilder beginTime(Instant beginTime);

    /**
     * @param endTime the end time of the bar period (optional if
     *                {@link #beginTime(Instant)} is given)
     * @return {@code this}
     */
    BarBuilder endTime(Instant endTime);

    /**
     * @param openPrice the open price of the bar period
     * @return {@code this}
     */
    BarBuilder openPrice(Num openPrice);

    /**
     * @param openPrice the open price of the bar period
     * @return {@code this}
     */
    BarBuilder openPrice(Number openPrice);

    /**
     * @param openPrice the open price of the bar period
     * @return {@code this}
     */
    BarBuilder openPrice(String openPrice);

    /**
     * @param highPrice the highest price of the bar period
     * @return {@code this}
     */
    BarBuilder highPrice(Number highPrice);

    /**
     * @param highPrice the highest price of the bar period
     * @return {@code this}
     */
    BarBuilder highPrice(String highPrice);

    /**
     * @param highPrice the highest price of the bar period
     * @return {@code this}
     */
    BarBuilder highPrice(Num highPrice);

    /**
     * @param lowPrice the lowest price of the bar period
     * @return {@code this}
     */
    BarBuilder lowPrice(Num lowPrice);

    /**
     * @param lowPrice the lowest price of the bar period
     * @return {@code this}
     */
    BarBuilder lowPrice(Number lowPrice);

    /**
     * @param lowPrice the lowest price of the bar period
     * @return {@code this}
     */
    BarBuilder lowPrice(String lowPrice);

    /**
     * @param closePrice the close price of the bar period
     * @return {@code this}
     */
    BarBuilder closePrice(Num closePrice);

    /**
     * @param closePrice the close price of the bar period
     * @return {@code this}
     */
    BarBuilder closePrice(Number closePrice);

    /**
     * @param closePrice the close price of the bar period
     * @return {@code this}
     */
    BarBuilder closePrice(String closePrice);

    /**
     * @param volume the total traded volume of the bar period
     * @return {@code this}
     */
    BarBuilder volume(Num volume);

    /**
     * @param volume the total traded volume of the bar period
     * @return {@code this}
     */
    BarBuilder volume(Number volume);

    /**
     * @param volume the total traded volume of the bar period
     * @return {@code this}
     */
    BarBuilder volume(String volume);

    /**
     * @param amount the total traded amount of the bar period (if {@code null},
     *               then it is calculated by {@code closePrice * volume})
     * @return {@code this}
     */
    BarBuilder amount(Num amount);

    /**
     * @param amount the total traded amount of the bar period (if {@code null},
     *               then it is calculated by {@code closePrice * volume})
     * @return {@code this}
     */
    BarBuilder amount(Number amount);

    /**
     * @param amount the total traded amount of the bar period (if {@code null},
     *               then it is calculated by {@code closePrice * volume})
     * @return {@code this}
     */
    BarBuilder amount(String amount);

    /**
     * @param trades the number of trades of the bar period
     * @return {@code this}
     */
    BarBuilder trades(long trades);

    /**
     * @param trades the number of trades of the bar period
     * @return {@code this}
     */
    BarBuilder trades(String trades);

    /**
     * @param barSeries the series used for bar addition
     * @return {@code this}
     */
    BarBuilder bindTo(BarSeries barSeries);

    /**
     * @return bar created from obtained data
     */
    Bar build();

    /**
     * Builds bar with {@link #build()} and adds it to series
     */
    void add();
}
