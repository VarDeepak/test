package Backend;

import java.sql.*;

// Database Schema
// 

public class JDBC {
    private Connection connection;
    public JDBC()
    {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankproject", "root", "deepak123");
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setDetails(String firstName,String lastName,String phoneNo,String password,double balance,String accountNumber)
    {
        try {
            // Prepare the query statement
            String query = "insert into accounts(accountNumber,firstName,lastName,phoneNumber,password,balance) values (?,?,?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, accountNumber);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, phoneNo);
            statement.setString(5, password);
            statement.setDouble(6, balance);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkAccountAndPassword(String accountNumber, String password) {
        boolean isMatch = false;
        try {
// Prepare the query statement
            String query = "SELECT password FROM accounts WHERE accountNumber = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, accountNumber);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Check if the account number exists
            if (resultSet.next()) {
                // Retrieve the stored password from the database
                String storedPassword = resultSet.getString("password");

                // Compare the stored password with the input password
                passwordEncrypt obj = new passwordEncrypt();
                if (obj.check(password,storedPassword)) {
                    isMatch = true;
                }else
                {
                    System.out.println("Password Incoorecty");
                    return false;
                }
            }else
            {
                System.out.println("Account do not exist ");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isMatch;
    }

        public String[] getAccountDetails(String accountNumber) {
        String firstName = "";
        String lastName = "";
        String phoneNumber = "";
        double balance = 0.0;

        try {
            // Prepare the query statement
            String query = "SELECT firstName, lastName, phoneNumber, balance FROM accounts WHERE accountNumber = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, accountNumber);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                // Retrieve the account details
                firstName = resultSet.getString("firstName");
                lastName = resultSet.getString("lastName");
                phoneNumber = resultSet.getString("phoneNumber");
                balance = resultSet.getDouble("balance");
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        String[] details = {firstName,lastName,phoneNumber,String.valueOf(balance)};

// Return the account details
            return details;
    }

    public void deposit_money(double deposit_money,String accountNumber) throws SQLException {

        try {
        String query = "update accounts set balance = balance + ? where accountNumber = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setDouble(1, deposit_money);
        statement.setString(2, accountNumber);

        statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public boolean money_withdrawl(double withdraw_money,String accountNumber) throws SQLException {
        String query = "select balance from accounts where accountNumber = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, accountNumber);

        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        double balance = resultSet.getDouble("balance");

        if(balance < withdraw_money)
        {
            return false;
        }else
        {
            String query1 = "update accounts set balance = balance - ? where accountNumber = ?";
            PreparedStatement statement1 = connection.prepareStatement(query1);
            statement1.setDouble(1, withdraw_money);
            statement1.setString(2, accountNumber);
            statement1.executeUpdate();
        }

        return true;
    }

    public int transfer_money(double transfer_money,String accountNumber,String transferAccountNumber) throws SQLException {
        // check whether the transfer account number exists in the database or not
        String query = "select * from accounts where accountNumber = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, transferAccountNumber);
        ResultSet resultSet = statement.executeQuery();

        if(resultSet.next())  //transfer account exits
        {
            //check whether the transfer money is more than balance or not
            String query1 = "select balance from accounts where accountNumber = ?";
            PreparedStatement statement1 = connection.prepareStatement(query1);
            statement.setString(1, accountNumber);

            ResultSet resultSet1 = statement.executeQuery();
            resultSet1.next();
            double balance = resultSet1.getDouble("balance");

            if(balance < transfer_money)
            {
                return 1;  //balance is less than the transfer money
            }else  //transfer balance sucessfully
            {
                String query2 = "update accounts set balance = balance - ? where accountNumber = ?";
                PreparedStatement statement2 = connection.prepareStatement(query2);
                statement2.setDouble(1, transfer_money);
                statement2.setString(2, accountNumber);
                statement2.executeUpdate();

                String query3 = "update accounts set balance = balance + ? where accountNumber = ?";
                PreparedStatement statement3 = connection.prepareStatement(query3);
                statement3.setDouble(1, transfer_money);
                statement3.setString(2, transferAccountNumber);
                statement3.executeUpdate();

            }

        }else
        {
            return 0;  //transfer account do no exist
        }


        return 10;


    }

    }
