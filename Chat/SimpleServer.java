package java_level_two.lesson_seven;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 * Created by Tom on 19.09.2017.
 */
public class SimpleServer implements IConstants{

    int count;
    ServerSocket server;
    Socket socket;
    static HashMap<String, Socket> hm = new HashMap<>();
    List<ClientHandler> clients;

    public static void main(String[] args) {
        new SimpleServer();
    }

    SimpleServer() {

        System.out.println(SERVER_START);
        new Thread(new CommandHandler()).start();
        clients = new ArrayList<>();
        hm = new HashMap<>();
        try {
            server = new ServerSocket(SERVER_PORT);
            while (true) {
                socket = server.accept();
                count++;
                System.out.println("#" + count + CLIENT_JOINED);
                ClientHandler client = new ClientHandler(socket);
                clients.add(client);
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println(SERVER_STOP);
    }

    /**
     * checkAuthentication: check login and password
     */
    private boolean checkAuthentication(String login, String passwd) {
        Connection connect;
        boolean result = false;
        try {
            // connect db
            Class.forName(DRIVER_NAME);
            connect = DriverManager.getConnection(SQLITE_DB);
            // looking for login && passwd in db
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(SQL_SELECT.replace("?", login));
            while (rs.next())
                result = rs.getString(PASSWD_COL).equals(passwd);
            // close all
            rs.close();
            stmt.close();
            connect.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return result;
    }


    ///метод проверяет существует ли пользователь, которому отправляется сообщение
    private boolean checkUser(String login){
        Connection connect;
        boolean result = false;
        String log = login;
        try {
            // connect db
            Class.forName(DRIVER_NAME);
            connect = DriverManager.getConnection(SQLITE_DB);
            // looking for login && passwd in db
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(SQL_SELECT.replace("?", log));
            while(rs.next()) {
                result = rs.getString("login").equals(log);
            }
            // close all
            rs.close();
            stmt.close();
            connect.close();
        } catch (Exception ex) {
            return false;
        }
        return result;
    }

    ///добавить нового пользователя в БД
    private void addNewUser(String login, String password){
        Connection connect;
        String insertUser = "INSERT INTO " + "users" +
                " (login, passwd) " +
                "VALUES ('"+ login +"', '" + password + "');";
        try {
            Class.forName(DRIVER_NAME);
            connect = DriverManager.getConnection(SQLITE_DB);
            Statement stmt = connect.createStatement();
            stmt.executeUpdate(insertUser);
        }catch (Exception e){

        }
    }

    /**
     * CommandHandler: processing of commands from server console
     */
    class CommandHandler implements Runnable {
        Scanner scanner = new Scanner(System.in);

        @Override
        public void run() {
            String command;
            do
                command = scanner.nextLine();
            while (!command.equals(EXIT_COMMAND));
            try {
                for(ClientHandler client : clients) {
                    client.socket.close();
                }
               server.close();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    //отправка сообщений всем пользователям
    void broadcastMessage(String msg){

        for(ClientHandler client : clients)
            client.sendMsg(msg);
    }
    /**
     * ClientHandler: service requests of clients
     */
    public class ClientHandler implements Runnable {
        BufferedReader reader;
        PrintWriter writer;
        Socket socket;
        String name;

        ClientHandler(Socket clientSocket) {
            try {
                socket = clientSocket;
                reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream());
                name = "Client #" + (clients.size() + 1);
            } catch(Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        void sendMsg(String msg){
            try{
                writer.println(msg);
                writer.flush();
            }catch (Exception ex){
            }
        }

        ////метод отправляет сообщение другому пользователю
        void sendMessageToAnotherUser(Socket clientSocket, String mess, String name){
            try {
                writer = new PrintWriter(clientSocket.getOutputStream());
                writer.println("'PRIVATE MSG FROM ' " + name + ":" + mess);
                writer.flush();
            }catch (IOException r){
                r.printStackTrace();
            }
        }

        @Override
        public void run() {
            String message;
            try {
                do {
                    message = reader.readLine();
                    if (!message.equals(null) && !message.equals("")) {
                        writer = new PrintWriter(this.socket.getOutputStream());
                        System.out.println(name + ": " + message);
                        if (message.startsWith(AUTH_SIGN)) {
                            String[] wds = message.split(" ");
                            if (checkAuthentication(wds[1], wds[2])) {
                                name = wds[1];
                                hm.put(name, this.socket);
                                sendMsg("Hello, " + name);
                                //sendMsg("\0");
                                broadcastMessage(name + CLIENT_JOINED_CHAT);
                            } else {
                                System.out.println(name + ": " + AUTH_FAIL);
                                sendMsg(AUTH_FAIL);
                                break;
                                //message = EXIT_COMMAND;
                            }
                        }else if(message.startsWith(CREATE)){
                            String[] logAndPass = message.split(" ");
                            name = logAndPass[1];
                            if(!checkUser(name)) {
                                hm.put(name, this.socket);
                                sendMsg("Hello, " + name);
                                broadcastMessage(name + CLIENT_JOINED_CHAT);
                                addNewUser(logAndPass[1], logAndPass[2]);
                            }else{
                                System.out.println("'" + name + "' " + WRONG_USERNAME);
                                sendMsg(WRONG_USERNAME);
                                break;
                            }
                        } else if(message.contains("/w")){
                            String array[] = message.split(" ");
                            sendMsg("'PRIVATE MSG FOR' " + array[1] + ": " +
                                    message.replaceAll("/w " + array[1], "" ));
                            if(checkUser(array[1])){
                                sendMessageToAnotherUser(hm.get(array[1]),
                                        message.replaceAll("/w " + array[1], ""), name);
                            }
                        }
                        else if (!message.equalsIgnoreCase(EXIT_COMMAND)) {
                            broadcastMessage(name + ": " + message);
                        }
                    }
                } while (!message.equalsIgnoreCase(EXIT_COMMAND));
                broadcastMessage(name + " has left the chart");
                hm.remove(this);
                clients.remove(this);
                socket.close();
                System.out.println(name + CLIENT_DISCONNECTED);
            } catch(Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
