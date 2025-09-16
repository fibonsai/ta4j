/**
 * Cost model abstractions and implementations for backtesting.
 *
 * <p>Defines {@link org.ta4j.core.analysis.cost.CostModel} and concrete models
 * for transaction and borrowing costs used by cash flow and return
 * calculations.
 *
 * <h2>Included models</h2>
 * <ul>
 * <li>{@link org.ta4j.core.analysis.cost.FixedTransactionCostModel} - Fixed fee per trade</li>
 * <li>{@link org.ta4j.core.analysis.cost.LinearTransactionCostModel} - Fee ∝ price × amount</li>
 * <li>{@link org.ta4j.core.analysis.cost.LinearBorrowingCostModel} - Short borrow fee ∝ periods</li>
 * <li>{@link org.ta4j.core.analysis.cost.ZeroCostModel} - No fees (baseline)</li>
 * </ul>
 */
package org.ta4j.core.analysis.cost;
