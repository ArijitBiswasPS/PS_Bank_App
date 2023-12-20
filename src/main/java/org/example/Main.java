package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    BufferedReader buff;
    InputStreamReader isr;
//    public final static Logger logger = LogManager.getLogManager();
    public final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    public Main() {
        isr = new InputStreamReader(System.in);
        if(buff==null) {
            buff = new BufferedReader(isr);
        }
    }
    public static void main(String[] args) {
        // DB Connection
        String url = System.getenv("dburl");
        String user = System.getenv("dbuser");
        String password = System.getenv("dbpassword");
        Connection connection;
        try {
            connection = DriverManager.getConnection(url,user,password);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        Main obj = new Main();
        int selectedBank;
        boolean check_flag = true;
        while(check_flag) {
            logger.log(Level.INFO,"Welcome to IBS:)\nPlease select your bank\n1. ICICI\n2. HDFC\n3. SBI\n4. AXIS\n5. IDFC");
            try {
                selectedBank = Integer.parseInt(obj.buff.readLine());
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            new Bank(obj.buff,connection,selectedBank);
            if(selectedBank > 5){
                logger.log(Level.INFO,"Enter valid bank number");
                continue;
            }
            logger.log(Level.INFO,"Do you want to exit from IBS? (yes/no) ");
            String check_exit;
            try {
                check_exit = obj.buff.readLine();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            if(Objects.equals(check_exit, "yes")){
                check_flag = false;
            }
        }
    }
}