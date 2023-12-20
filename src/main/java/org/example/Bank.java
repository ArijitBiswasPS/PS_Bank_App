package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Level;

import static org.example.Main.logger;

public class Bank {
    public Bank(BufferedReader buff, Connection connection, int selectedBank){
        logger.log(Level.INFO,"1. Are you a new customer?\n2. Are you an existing customer?");
        int check_customer;
        try {
            check_customer = Integer.parseInt(buff.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        switch (selectedBank){
            case 1 -> new ICICI(buff, connection, check_customer);
            case 2 -> new HDFC(buff, connection, check_customer);
            case 3 -> new SBI(buff, connection, check_customer);
            case 4 -> new AXIS(buff, connection, check_customer);
            case 5 -> new IDFC(buff, connection, check_customer);
        }
    }
}
