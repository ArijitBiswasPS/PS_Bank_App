package org.example;

import org.junit.Test;

import static org.junit.Assert.*;

public class ICICITest {

    @Test
    public void depositMoney() {
        ICICI mICICI = new ICICI();
        mICICI.depositMoney(5000, "123");
        int actBal = 5000;
        assertEquals(5000, actBal);
    }

    @Test
    public void withdrawMoney() {
    }

    @Test
    public void openFD() {
    }

    @Test
    public void applyLoan() {
    }

    @Test
    public void applyCreditCard() {
    }

    @Test
    public void getBalance() {
    }
}