package com.adyen.android.assignment

import com.adyen.android.assignment.money.Bill
import com.adyen.android.assignment.money.Change
import com.adyen.android.assignment.money.Coin
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class CashRegisterTest {
    @Test(expected = CashRegister.TransactionException::class)
    fun `transaction with an amount paid below the product(s) price should throw an exception`() {
        //given an amount paid of 0
        val amountPaid = Change.none()
        val cashRegister = CashRegister(Change.max())

        //when
        cashRegister.performTransaction(1500L, amountPaid)

        //then
        fail("A TransactionException should be thrown: the amount paid should be equal or higher to the product price")
    }

    @Test
    fun `transaction with an amount paid matching the product(s) price should return empty change`() {
        //given
        val amountPaid = Change().apply {
            add(Bill.TEN_EURO, 1)
            add(Bill.FIVE_EURO, 1)
        }
        val cashRegisterChange = Change.none()
        val cashRegister = CashRegister(cashRegisterChange)
        val expected = Change.none()

        //when the amount paid and the product's price are matching
        val actual = cashRegister.performTransaction(amountPaid.total, amountPaid)

        //then
        assertEquals(expected, actual)
    }

    @Test
    fun `transaction should return correct change`() {
        //given
        val amountPaid = Change().apply {
            add(Bill.TEN_EURO, 1)
            add(Bill.FIVE_EURO, 1)
            add(Coin.FIFTY_CENT, 1)
            add(Coin.FIFTY_CENT, 1)
        }
        val cashRegisterChange = Change().apply {
            Bill.values().forEach { add(it, 5) }
            Coin.values().forEach { add(it, 5) }
        }
        val cashRegister = CashRegister(cashRegisterChange)

        //when
        val actual = cashRegister.performTransaction(1500L, amountPaid)

        //then
        assertEquals(100L, actual.total)
    }

    @Test
    fun `transaction should return the minimal amount of change`() {
        //given
        val amountPaid = Change().apply {
            add(Bill.TEN_EURO, 1)
            add(Bill.FIVE_EURO, 1)
            add(Coin.FIFTY_CENT, 1)
            add(Coin.FIFTY_CENT, 1)
        }
        val cashRegisterChange = Change().apply {
            Bill.values().forEach { add(it, 5) }
            Coin.values().forEach { add(it, 5) }
        }
        val cashRegister = CashRegister(cashRegisterChange)
        val expected = Change().apply {
            add(Coin.ONE_EURO, 1)
        }

        //when
        val actual = cashRegister.performTransaction(1500L, amountPaid)

        //then
        assertEquals(expected, actual)
    }

    @Test(expected = CashRegister.TransactionException::class)
    fun `transaction should throw an exception when there is not enough change`() {
        //given a cash register with only bills
        // and an amount paid expecting a coin to be returned
        val amountPaid = Change().apply {
            add(Bill.TEN_EURO, 1)
            add(Bill.FIVE_EURO, 1)
            add(Coin.ONE_EURO, 1)
        }
        val cashRegisterChange = Change().apply {
            Bill.values().forEach { add(it, 1) }
        }
        val cashRegister = CashRegister(cashRegisterChange)


        //when
        cashRegister.performTransaction(1599L, amountPaid)

        //then
        fail("A TransactionException should be thrown: there is not enough change in the cash register")
    }

    @Test
    fun `the amount paid should be collected before computing the Change amount`() {
        //given
        val amountPaid = Change().apply {
            add(Bill.TEN_EURO, 1)
            add(Bill.FIVE_EURO, 1)
            add(Coin.ONE_EURO, 1)
        }
        val cashRegisterChange = Change().apply {
            Bill.values().forEach { add(it, 1) }
        }
        val cashRegister = CashRegister(cashRegisterChange)
        val expected = Change().apply {
            add(Coin.ONE_EURO, 1)
        }

        //when
        val actual = cashRegister.performTransaction(1500L, amountPaid)

        //then
        assertEquals(expected, actual)
    }


    @Test
    fun `the cash register should keep track of the Change that is in it`() {
        //given
        val amountPaid1 = Change().apply {
            add(Bill.TEN_EURO, 1)
            add(Bill.FIVE_EURO, 1)
            add(Coin.ONE_EURO, 1)
        }
        val amountPaid2 = Change().apply {
            add(Bill.TEN_EURO, 1)
        }
        val cashRegisterChange = Change().apply {
            Bill.values().forEach { add(it, 5) }
            Coin.values().forEach { add(it, 5) }
        }
        val cashRegister = CashRegister(cashRegisterChange)
        val expected = Change().apply {
            Bill.values().forEach { add(it, 5) }
            Coin.values().forEach { add(it, 5) }
            add(Bill.TEN_EURO, 2)
            add(Bill.FIVE_EURO, 1)
            add(Coin.ONE_EURO, 1)
            remove(Coin.ONE_CENT, 1)
            remove(Coin.TWO_EURO, 1)
            remove(Coin.FIFTY_CENT, 1)
        }

        //when
        cashRegister.performTransaction(1599L, amountPaid1)
        cashRegister.performTransaction(750L, amountPaid2)

        //then
        assertEquals(expected, cashRegisterChange)
    }

    @Test
    fun `the cash register should handle reimbursement`() {
        //given
        val amountPaid = Change.none()
        val cashRegisterChange = Change().apply {
            Bill.values().forEach { add(it, 5) }
            Coin.values().forEach { add(it, 5) }
        }
        val cashRegister = CashRegister(cashRegisterChange)
        val expected = Change().apply {
            add(Bill.TEN_EURO, 1)
            add(Bill.FIVE_EURO, 1)
        }

        //when
        val actual = cashRegister.performTransaction(-1500L, amountPaid)

        //then
        assertEquals(expected, actual)
    }

    @Test
    fun `if someone ask for change the cash register should return the minimal amount of change`() {
        //given
        val amountPaid = Change().apply {
            add(Bill.TEN_EURO, 1)
            add(Coin.ONE_EURO, 5)
        }
        val cashRegisterChange = Change().apply {
            Bill.values().forEach { add(it, 5) }
            Coin.values().forEach { add(it, 5) }
        }
        val cashRegister = CashRegister(cashRegisterChange)
        val expected = Change().apply {
            add(Bill.TEN_EURO, 1)
            add(Bill.FIVE_EURO, 1)
        }

        //when
        val actual = cashRegister.performTransaction(0, amountPaid)

        //then
        assertEquals(expected, actual)
    }


}
