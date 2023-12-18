package org.example;

public interface RBI {
    float rbi_surcharge = 1;
    float rbi_loan_ROI = 2;
    float rbi_credit_ROI = 3;
    void depositMoney(float deposit_money, String customerID);
    void withdrawMoney(float withdraw_money, String customerID);
    void openFD(float amount, int years);
    void applyLoan(String loanType, float amount, float loan_ROI, int years, String customerID);
    void applyCreditCard(float amount, float credit_ROI, String customerID);
    float getBalance(String customerID);
}
