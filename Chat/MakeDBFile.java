package java_level_two.lesson_seven;


import java.sql.*;

/**
 * Created by Tom on 20.09.2017.
 */
public class MakeDBFile implements IConstants {

    final String NAME_TABLE = "users";
    final String SQL_CREATE_TABLE = "CREATE TABLE" + NAME_TABLE +
            "(login CHAR(6) PRIMARY KEY NOT NULL," +
            " passwd CHAR(6) NOT NULL);";
    final String SQL_INSERT_MIKE  = "INSERT INTO " + NAME_TABLE +
            " (login, passwd) " +
            "VALUES ('mike', 'qwerty');";
    final String SQL_INSERT_JOHN  = "INSERT INTO " + NAME_TABLE +
            " (login, passwd) " +
            "VALUES ('john', '12345');";
    final String SQL_SELECT = "SELECT * FROM " + NAME_TABLE + ";";

    Connection connect;
    Statement stmt;
    ResultSet rs;
    String sql;

    public static void main(String[] args) {
        new MakeDBFile();
    }

    MakeDBFile(){

        //open db file
        try{
            Class.forName(DRIVER_NAME);
            connect = DriverManager.getConnection(SQLITE_DB);
        }catch (Exception ex){
        }

        //create table
        try{
            stmt = connect.createStatement();
            stmt.executeUpdate(SQL_CREATE_TABLE);
        }catch (Exception e){

        }

        //insert record
        try{
            stmt.executeUpdate(SQL_INSERT_MIKE);
            stmt.executeUpdate(SQL_INSERT_JOHN);
        }catch (Exception ex){

        }

        //print records

        try {
            rs = stmt.executeQuery(SQL_SELECT);
            System.out.println("LOGIN\tPASSWD");
            while(rs.next()){
                System.out.println(rs.getString("login") + "\t" +
                        rs.getString(PASSWD_COL));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
