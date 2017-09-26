package java_level_two.lesson_seven;




import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Tom on 19.09.2017.
 */
public class SimpleClient implements IConstants {
    Socket socket;
    PrintWriter writer;
    BufferedReader reader;
    Scanner scanner;
    String message;

    public static void main(String[] args) {
        new SimpleClient();
    }

    SimpleClient() {
        scanner = new Scanner(System.in);
        System.out.println(CONNECT_TO_SERVER);
        try {
            socket = new Socket(SERVER_ADDR, SERVER_PORT);
            writer = new PrintWriter(socket.getOutputStream());
            reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            System.out.println(Enter_OR_CREATE_USER);
            int choice = scanner.nextInt();
            do {
                switch (choice) {
                    case 1:
                        System.out.println("Creating new user");
                        writer.println(createNewUser());
                        break;
                    case 2:
                        writer.println(getLoginAndPassword()); // send: auth <login> <passwd>
                        break;
                    default:
                        System.out.println(Enter_OR_CREATE_USER);
                        choice = scanner.nextInt();
                        break;
                }
            }while(!(choice == 1 || choice == 2));
            new Thread(new ServerListener()).start();
            do {
                message = scanner.nextLine();
                writer.println(message);
                writer.flush();
            } while (!message.equals(EXIT_COMMAND));
            socket.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println(CONNECT_CLOSED);
    }

    /**
     * getLoginAndPassword: read login and password from keyboard
     */
    String getLoginAndPassword() {
        System.out.print(LOGIN_PROMPT);
        Scanner scanner1 = new Scanner(System.in);
        String login = scanner1.nextLine();
        System.out.print(PASSWD_PROMPT);
        return AUTH_SIGN + " " + login + " " + scanner1.nextLine();
    }

    String createNewUser(){
        Scanner scanner1 = new Scanner(System.in);
        System.out.print(ENTER_NEW_LOGIN);
        String newLogin = scanner1.nextLine();
        System.out.print(ENTER_NEW_PASS);
        return CREAT_USER + " " + newLogin + " " + scanner1.nextLine();
    }

    /**
     * ServerListener: get messages from Server
     */
    class ServerListener implements Runnable {
        @Override
        public void run() {
            try {
                while ((message = reader.readLine()) != null) {
                    System.out.print(message.equals("\0")?
                            CLIENT_PROMPT : message + "\n");
                    if (message.equals(AUTH_FAIL))
                        System.exit(-1); // terminate client
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
