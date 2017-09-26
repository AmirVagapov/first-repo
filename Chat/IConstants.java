package java_level_two.lesson_seven;

/**
 * Created by Tom on 19.09.2017.
 */
public interface IConstants {
    String DRIVER_NAME = "org.sqlite.JDBC";
    String SQLITE_DB = "jdbc:sqlite:chat.db";
    String SERVER_ADDR = "localhost"; // server net name or "127.0.0.1"
    int SERVER_PORT = 2048; // servet port
    String SERVER_START = "Server is started...";
    String SERVER_STOP = "Server stopped.";
    String CLIENT_JOINED = " client joined.";
    String CLIENT_DISCONNECTED = " disconnected.";
    String CLIENT_PROMPT = "$ "; // client prompt
    String LOGIN_PROMPT = "Login: ";
    String PASSWD_PROMPT = "Passwd: ";
    String AUTH_SIGN = "auth";
    String AUTH_FAIL = "Authentication failure. Try again.";
    String SQL_SELECT = "SELECT * FROM users WHERE login = '?'";
    String PASSWD_COL = "passwd";
    String CONNECT_TO_SERVER = "Connection to server established.";
    String CONNECT_CLOSED = "Connection closed.";
    String EXIT_COMMAND = "exit"; // command for exit
    String CREAT_USER = "create";
    String ENTER_NEW_LOGIN = "Enter new login: ";
    String ENTER_NEW_PASS = "Enter new password: ";
    String Enter_OR_CREATE_USER = "If you want to create new user send '1', if you want to authorize send '2'";
    String CLIENT_JOINED_CHAT = " has been joined the chat";
    String WRONG_USERNAME = "This username is already registered.";
    String CREATE = "create";
    String NEW_CONNECT = "Press 'New connect'";
}
