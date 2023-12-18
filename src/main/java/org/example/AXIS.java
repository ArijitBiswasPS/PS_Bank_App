package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class AXIS implements RBI{
    Connection connection;
    int withdraw_count = 0;
    float axis_fd_ROI = 5, axis_credit_ROI = 6;
    String customer_id;
    public AXIS(BufferedReader buff, Connection connection, int check_customer) {
        this.connection = connection;
        boolean check_flag = true;
        if(check_customer == 1){
            // New customer
            Customer customer = new Customer(buff, connection);
            customer_id = customer.getCustomerAadhar();
        }else{
            // Existing customer
            System.out.print("Enter your customerID: ");
            try {
                customer_id = buff.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            int countCustomers;
            try {
                PreparedStatement book_find = connection.prepareStatement("select count(*) as customerID from axis where customerID = '" + customer_id + "'");
                ResultSet resultSet = book_find.executeQuery();
                resultSet.next();
                countCustomers = resultSet.getInt(1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if(countCustomers == 0){
                boolean valid_flag = true;
                while(valid_flag) {
                    System.out.println("You entered a invalid customerID");
                    System.out.print("Do you want to enter correct customerID? (yes/no) ");
                    String check_exit;
                    try {
                        check_exit = buff.readLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if(Objects.equals(check_exit, "no")){
                        valid_flag = false;
                        check_flag = false;
                    }
                }
            }
        }
        while(check_flag) {
            System.out.println("Select what you want to do?\n1. Deposit Money\n2. Withdraw Money\n3. Open FD\n4. Apply for Loan\n5. Apply for Credit Card\n6. Check Balance");
            int check_option;
            try {
                check_option = Integer.parseInt(buff.readLine());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            switch (check_option){
                case 1 -> {
                    System.out.print("Enter the amount you want to deposit: ");
                    float deposit_money;
                    try {
                        deposit_money = Float.parseFloat(buff.readLine());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    depositMoney(deposit_money, customer_id);
                }
                case 2 -> {
                    System.out.print("Enter the amount you want to withdraw: ");
                    float withdraw_money;
                    try {
                        withdraw_money = Float.parseFloat(buff.readLine());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    withdrawMoney(withdraw_money, customer_id);
                }
                case 3 -> {
                    System.out.print("Enter the amount you want to FD: ");
                    float fd_money;
                    try {
                        fd_money = Float.parseFloat(buff.readLine());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.print("Enter the years you want to invest: ");
                    int years;
                    try {
                        years = Integer.parseInt(buff.readLine());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    openFD(fd_money, years);
                }
                case 4 -> {
                    int reason, loan_years;
                    float loan_amount;
                    ArrayList<String> loan_reason = new ArrayList<>();
                    loan_reason.add("Home Loan");
                    loan_reason.add("Education Loan");
                    loan_reason.add("Personal Loan");
                    loan_reason.add("Car Loan");
                    System.out.println("Select your loan type: ");
                    for (int i = 0; i < loan_reason.size(); i++) {
                        System.out.println(i + 1 + ". " + loan_reason.get(i));
                    }
                    try {
                        reason = Integer.parseInt(buff.readLine());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    HashMap<String, Float> AXIS_loan_types = new HashMap<>();
                    AXIS_loan_types.put("Home Loan", 5f);
                    AXIS_loan_types.put("Education Loan", 3f);
                    AXIS_loan_types.put("Personal Loan", 6f);
                    AXIS_loan_types.put("Car Loan", 8f);
                    System.out.println("Thank you for choosing our " + loan_reason.get(reason - 1));
                    System.out.print("Enter your Loan amount: ");
                    try {
                        loan_amount = Float.parseFloat(buff.readLine());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.print("Enter your Loan tenure: ");
                    try {
                        loan_years = Integer.parseInt(buff.readLine());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    String loan_type = loan_reason.get(reason - 1);
                    applyLoan(loan_type, loan_amount, AXIS_loan_types.get(loan_type) + rbi_loan_ROI, loan_years, customer_id);
                }
                case 5 -> {
                    System.out.print("Enter you want your Credit card limit be: ");
                    float credit_amount;
                    try {
                        credit_amount = Float.parseFloat(buff.readLine());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    applyCreditCard(credit_amount, axis_credit_ROI + rbi_credit_ROI, customer_id);
                }
                case 6 -> System.out.println("Your current balance is: " + getBalance(customer_id));
                default -> System.out.println("Invalid operation selected");
            }
            System.out.print("Are you done? (yes/no) ");
            String check_exit;
            try {
                check_exit = buff.readLine();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            if(Objects.equals(check_exit, "yes")){
                check_flag = false;
            }
        }
    }

    @Override
    public void depositMoney(float deposit_money, String customerID) {
        try {
            PreparedStatement book_find = connection.prepareStatement("select * from axis where customerID = '" + customerID + "'");
            ResultSet resultSet = book_find.executeQuery();
            resultSet.next();
            float balance = resultSet.getFloat(8);
            balance += deposit_money;
            PreparedStatement book_update = connection.prepareStatement("update axis set customerBalance = " + balance + "where customerID = '" + customerID + "'");
            book_update.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void withdrawMoney(float withdraw_money, String customerID) {
        try {
            withdraw_count++;
            PreparedStatement book_find = connection.prepareStatement("select * from axis where customerID = '" + customerID + "'");
            ResultSet resultSet = book_find.executeQuery();
            resultSet.next();
            float balance = resultSet.getFloat(8);
            balance -= (withdraw_count > 3) ? withdraw_money * (1 + rbi_surcharge / 100) : withdraw_money;
            if(balance < 1000f){
                System.out.println("Insufficient Balance in your account");
                withdraw_count--;
                return;
            }
            PreparedStatement book_update = connection.prepareStatement("update axis set customerBalance = " + balance + "where customerID = '" + customerID + "'");
            book_update.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void openFD(float amount, int years) {
        float total_fd_money = amount;
        boolean flag = true;
        for(int year = 1; year <= years; year++){
            total_fd_money += total_fd_money * (axis_fd_ROI / 100);
            if(flag){
                System.out.println("After " + year + " year your capital is: " + total_fd_money);
                flag = false;
                continue;
            }
            System.out.println("After " + year + " years your capital is: " + total_fd_money);
        }
        float profit = total_fd_money - amount;
        System.out.println("After " + years + " years your profit is: " + profit);
    }

    @Override
    public void applyLoan(String loanType, float amount, float loan_ROI, int years, String customerID) {
        float balance;
        try {
            PreparedStatement check_loan = connection.prepareStatement("select * from axis where customerID = '" + customerID + "'");
            ResultSet resultSet = check_loan.executeQuery();
            resultSet.next();
            balance = resultSet.getFloat(8);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        if(amount > 2 * balance){
            System.out.println("You are ineligible for this Loan.");
            return;
        }
        float total_loan_money = (float) (amount * Math.pow(((100+loan_ROI)/100),years));
        float loan_amount = total_loan_money-amount;
        System.out.println("After " + years + " years your total loan amount " + "of your " + loanType + " is: " + loan_amount);
    }

    @Override
    public void applyCreditCard(float amount, float credit_ROI, String customerID) {
        float balance;
        try {
            PreparedStatement check_credit = connection.prepareStatement("select * from axis where customerID = '" + customerID + "'");
            ResultSet resultSet = check_credit.executeQuery();
            resultSet.next();
            balance = resultSet.getFloat(8);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        if(amount > 2 * balance){
            System.out.println("You are ineligible for this Loan.");
            return;
        }
        float monthly_credit_money = amount * (1 + credit_ROI / 100) / 12;
        System.out.println("In every month you have to pay: " + monthly_credit_money);
    }

    @Override
    public float getBalance(String customerID) {
        float balance;
        try {
            PreparedStatement find_balance = connection.prepareStatement("select * from axis where customerID = '" + customerID + "'");
            ResultSet resultSet = find_balance.executeQuery();
            resultSet.next();
            balance = resultSet.getFloat(8);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return balance;
    }
}
