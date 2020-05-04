package com.adyen.android.assignment

import com.adyen.android.assignment.money.Change
import kotlin.math.min

/**
 * The CashRegister class holds the logic for performing transactions.
 *
 * @param change The change that the CashRegister is holding.
 */
class CashRegister(private val change: Change) {
    /**
     * Performs a transaction for a product/products with a certain price and a given amount.
     *
     * @param price The price of the product(s).
     * @param amountPaid The amount paid by the shopper.
     *
     * @return The change for the transaction.
     *
     * @throws TransactionException If the transaction cannot be performed.
     */
    fun performTransaction(price: Long, amountPaid: Change): Change {
        return when {
            amountPaid.total < price ->
                throw TransactionException("The amount paid by the shopper is not sufficient")
            else -> collectAndGiveChangeBack(amountPaid, amountPaid.total - price)
        }
    }

    /**
     * Add the given change to the cash register
     * and return the minimal amount of change if needed
     *
     * @param toCollect the change to be added to the cash register
     * @param amountToGiveBack the amount that need to be converted to change
     * @return the minimal amount of change if needed
     */
    private fun collectAndGiveChangeBack(toCollect: Change, amountToGiveBack: Long): Change {
        change.add(toCollect)
        if (amountToGiveBack == 0L) return Change.none()
        else {
            val result = Change()
            var amountToFind = amountToGiveBack
            //change is a TreeMap based on a comparator that will apply a descending order on the elements
            // so monetaryElementsAvailable will be sorted by descending minorValue
            val monetaryElementsAvailable = change.getElements()
            var i = 0
            while (amountToFind != 0L && i < monetaryElementsAvailable.size) {
                val currentMonetaryElement = monetaryElementsAvailable.elementAt(i)
                val monetaryElementCount =
                    amountToFind.toDouble() / currentMonetaryElement.minorValue.toDouble()
                if (monetaryElementCount >= 1) {
                    //If there is enough MonetaryElement in the cash register we take the monetaryElementCount
                    // number otherwise we take all the ones available in the cash register
                    val elementCountToWithdraw =
                        min(monetaryElementCount.toInt(), change.getCount(currentMonetaryElement))
                    result.add(currentMonetaryElement, elementCountToWithdraw)
                    change.remove(currentMonetaryElement, elementCountToWithdraw)
                    amountToFind -= elementCountToWithdraw * currentMonetaryElement.minorValue
                }
                i++
            }
            if (amountToFind == 0L) return result
            else throw TransactionException("There is not enough change available")
        }
    }

    class TransactionException(message: String, cause: Throwable? = null) :
        Exception(message, cause)
}
