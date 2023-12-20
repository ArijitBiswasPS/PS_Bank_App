package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;

import static org.example.Main.logger;

public class ICICI implements RBI{
    Connection connection;
    int withdraw_count = 0;
    float icici_fd_ROI = 5, icici_credit_ROI = 6;
    String customer_id;
    public ICICI() {
        String url = "jdbc:mysql://localhost:3306/bank";
        String user = "root";
        String password = "Aftershock@1";
        try {
            this.connection = DriverManager.getConnection(url,user,password);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    public ICICI(BufferedReader buff, Connection connection, int check_customer) {
        this.connection = connection;
        boolean check_flag = true;
        if(check_customer == 1){
            // New customer
            Customer customer = new Customer(buff, connection);
            customer_id = customer.getCustomerAadhar();
        }else{
            // Existing customer
            logger.log(Level.INFO,"Enter your customerID: ");
            try {
                customer_id = buff.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            int countCustomers;
            try {
                PreparedStatement book_find = connection.prepareStatement("select count(*) as customerID from icici where customerID = '" + customer_id + "'");
                ResultSet resultSet = book_find.executeQuery();
                resultSet.next();
                countCustomers = resultSet.getInt(1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if(countCustomers == 0){
                boolean valid_flag = true;
                while(valid_flag) {
                    logger.log(Level.INFO,"You entered a invalid customerID");
                    logger.log(Level.INFO,"Do you want to enter correct customerID? (yes/no) ");
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
            logger.log(Level.INFO,"Select what you want to do?\n1. Deposit Money\n2. Withdraw Money\n3. Open FD\n4. Apply for Loan\n5. Apply for Credit Card\n6. Check Balance");
            int check_option;
            try {
                check_option = Integer.parseInt(buff.readLine());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            switch (check_option){
                case 1 -> {
                    logger.log(Level.INFO,"Enter the amount you want to deposit: ");
                    float deposit_money;
                    try {
                        deposit_money = Float.parseFloat(buff.readLine());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    depositMoney(deposit_money, customer_id);
                }
                case 2 -> {
                    logger.log(Level.INFO,"Enter the amount you want to withdraw: ");
                    float withdraw_money;
                    try {
                        withdraw_money = Float.parseFloat(buff.readLine());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    withdrawMoney(withdraw_money, customer_id);
                }
                case 3 -> {
                    logger.log(Level.INFO,"Enter the amount you want to FD: ");
                    float fd_money;
                    try {
                        fd_money = Float.parseFloat(buff.readLine());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    logger.log(Level.INFO,"Enter the years you want to invest: ");
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
                    logger.log(Level.INFO,"Select your loan type: ");
                    for (int i = 0; i < loan_reason.size(); i++) {
                        logger.log(Level.INFO,i + 1 + ". " + loan_reason.get(i));
                    }
                    try {
                        reason = Integer.parseInt(buff.readLine());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    HashMap<String, Float> ICICI_loan_types = new HashMap<>();
                    ICICI_loan_types.put("Home Loan", 5f);
                    ICICI_loan_types.put("Education Loan", 3f);
                    ICICI_loan_types.put("Personal Loan", 6f);
                    ICICI_loan_types.put("Car Loan", 8f);
                    logger.log(Level.INFO,"Thank you for choosing our " + loan_reason.get(reason - 1));
                    logger.log(Level.INFO,"Enter your Loan amount: ");
                    try {
                        loan_amount = Float.parseFloat(buff.readLine());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    logger.log(Level.INFO,"Enter your Loan tenure: ");
                    try {
                        loan_years = Integer.parseInt(buff.readLine());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    String loan_type = loan_reason.get(reason - 1);
                    applyLoan(loan_type, loan_amount, ICICI_loan_types.get(loan_type) + rbi_loan_ROI, loan_years, customer_id);
                }
                case 5 -> {
                    logger.log(Level.INFO,"Enter you want your Credit card limit be: ");
                    float credit_amount;
                    try {
                        credit_amount = Float.parseFloat(buff.readLine());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    applyCreditCard(credit_amount, icici_credit_ROI + rbi_credit_ROI, customer_id);
                }
                case 6 -> logger.log(Level.INFO,"Your current balance is: " + getBalance(customer_id));
                default -> logger.log(Level.INFO,"Invalid operation selected");
            }
            logger.log(Level.INFO,"Are you done? (yes/no) ");
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
            PreparedStatement book_find = connection.prepareStatement("select * from icici where customerID = '" + customerID + "'");
            ResultSet resultSet = book_find.executeQuery();
            resultSet.next();
            float balance = resultSet.getFloat(8);
            balance += deposit_money;
            PreparedStatement book_update = connection.prepareStatement("update icici set customerBalance = " + balance + "where customerID = '" + customerID + "'");
            book_update.executeUpdate();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void withdrawMoney(float withdraw_money, String customerID) {
        try {
            withdraw_count++;
            PreparedStatement book_find = connection.prepareStatement("select * from icici where customerID = '" + customerID + "'");
            ResultSet resultSet = book_find.executeQuery();
            resultSet.next();
            float balance = resultSet.getFloat(8);
            balance -= (withdraw_count > 3) ? withdraw_money * (1 + rbi_surcharge / 100) : withdraw_money;
            if(balance < 1000f){
                logger.log(Level.INFO,"Insufficient Balance in your account");
                withdraw_count--;
                return;
            }
            PreparedStatement book_update = connection.prepareStatement("update icici set customerBalance = " + balance + "where customerID = '" + customerID + "'");
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
            total_fd_money += total_fd_money * (icici_fd_ROI / 100);
            if(flag){
                logger.log(Level.INFO,"After " + year + " year your capital is: " + total_fd_money);
                flag = false;
                continue;
            }
            logger.log(Level.INFO,"After " + year + " years your capital is: " + total_fd_money);
        }
        float profit = total_fd_money - amount;
        logger.log(Level.INFO,"After " + years + " years your profit is: " + profit);
    }

    @Override
    public void applyLoan(String loanType, float amount, float loan_ROI, int years, String customerID) {
        float balance;
        try {
            PreparedStatement check_loan = connection.prepareStatement("select * from icici where customerID = '" + customerID + "'");
            ResultSet resultSet = check_loan.executeQuery();
            resultSet.next();
            balance = resultSet.getFloat(8);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        if(amount > 2 * balance){
            logger.log(Level.INFO,"You are ineligible for this Loan.");
            return;
        }
        float total_loan_money = (float) (amount * Math.pow(((100+loan_ROI)/100),years));
        float loan_amount = total_loan_money-amount;
        logger.log(Level.INFO,"After " + years + " years your total loan amount " + "of your " + loanType + " is: " + loan_amount);
    }

    @Override
    public void applyCreditCard(float amount, float credit_ROI, String customerID) {
        float balance;
        try {
            PreparedStatement check_credit = connection.prepareStatement("select * from icici where customerID = '" + customerID + "'");
            ResultSet resultSet = check_credit.executeQuery();
            resultSet.next();
            balance = resultSet.getFloat(8);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        if(amount > 2 * balance){
            logger.log(Level.INFO,"You are ineligible for this Loan.");
            return;
        }
        float monthly_credit_money = amount * (1 + credit_ROI / 100) / 12;
        logger.log(Level.INFO,"In every month you have to pay: " + monthly_credit_money);
    }

    @Override
    public float getBalance(String customerID) {
        float balance;
        try {
            PreparedStatement find_balance = connection.prepareStatement("select * from icici where customerID = '" + customerID + "'");
            ResultSet resultSet = find_balance.executeQuery();
            resultSet.next();
            balance = resultSet.getFloat(8);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return balance;
    }
}
