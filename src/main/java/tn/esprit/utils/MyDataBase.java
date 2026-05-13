package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {
    private final String URL="jdbc:mysql://localhost:3306/skillquestproject";
    private final String USER="root";
    private final String PASSWORD="";
    private Connection connection;
    //etape 2
    private static MyDataBase instance;
    //1 etape
    private MyDataBase(){
        try {
            this.connection= DriverManager.getConnection(URL,USER,PASSWORD);
            System.out.println("connection ok!!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
//etape3
    public static MyDataBase getInstance(){
        if (instance==null){
            instance=new MyDataBase();
        }
        return instance;
    }
    public Connection getConnection(){
        return connection;
    }
}
