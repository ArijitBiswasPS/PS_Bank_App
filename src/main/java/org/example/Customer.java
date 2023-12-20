package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

import static org.example.Main.logger;

public class Customer {
    String customerName, customerEmail, customerAddress, customerGender, customerAadhar, customerPhone;
    int customerBalance = 0;
    public Customer(BufferedReader buff, Connection connection) {
        try {
            logger.log(Level.INFO,"Enter your name: ");
            this.customerName = buff.readLine();
            logger.log(Level.INFO,"Enter your email: ");
            this.customerEmail = buff.readLine();
            logger.log(Level.INFO,"Enter your address: ");
            this.customerAddress = buff.readLine();
            logger.log(Level.INFO,"Enter your gender: ");
            this.customerGender = buff.readLine();
            logger.log(Level.INFO,"Enter your aadhar number: ");
            customerAadhar = buff.readLine();
            logger.log(Level.INFO,"Enter your phone number: ");
            this.customerPhone = buff.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            PreparedStatement customer_entry = connection.prepareStatement("insert into icici (customerID, customerName, customerEmail, customerAddress, customerGender, customerAadhar, customerPhone, customerBalance) values (?,?,?,?,?,?,?,?)");
            customer_entry.setString(1,customerAadhar);
            customer_entry.setString(2,customerName);
            customer_entry.setString(3,customerEmail);
            customer_entry.setString(4,customerAddress);
            customer_entry.setString(5,customerGender);
            customer_entry.setString(6,customerAadhar);
            customer_entry.setString(7,customerPhone);
            customer_entry.setFloat(8,customerBalance);
            customer_entry.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCustomerAadhar() {
        return customerAadhar;
    }
}
