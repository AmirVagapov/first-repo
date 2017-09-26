package java_level_two.lesson_seven;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Tom on 09.09.2017.
 */
public class Chat extends JFrame implements ActionListener, ChatInterface, IConstants{

    JTextArea chatWindow;
    JPanel jp;
    JScrollPane jscroll;
    JButton enterButton;
    JTextField enterText;
    JButton registerButton;
    JButton reloadServer;


    private static RegisterWindow registerWindow;

    Socket socket;
    PrintWriter writer;
    BufferedReader reader;
    boolean isAuthorized;
    SimpleDateFormat simpleDate = new SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.getDefault());
    String date;

    public static void main(String[] args) {
        new Chat();
    }

    Chat(){
        setTitle("My chat");
        setBounds(POS_X, POS_Y, WIDTH_WINDOW, HEIGHT_WINDOW);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        chatWindow = new JTextArea();
        jscroll = new JScrollPane(chatWindow);

        chatWindow.setEditable(false);
        chatWindow.setBackground(new Color(216, 189, 206));
        chatWindow.setFont(font);
        jp = new JPanel();
        jp.setLayout(new BorderLayout());

        registerWindow = new RegisterWindow(this);

        registerButton = new JButton("Authorized/ Create user");
        enterButton = new JButton("Enter");
        reloadServer = new JButton("New connect"); ////кнопка создания нового соединения при неудачной авторизации
        enterText = new JTextField();
        enterText.setFont(font);


        enterText.addActionListener(this);
        enterButton.addActionListener(this);
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerWindow.setVisible(true);
            }
        });
        reloadServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connect();
                DefaultCaret caret = (DefaultCaret)chatWindow.getCaret(); ////автоскролл
                caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
            }
        });

        add(jscroll);
        add(jp, BorderLayout.SOUTH);
        jp.add(enterText, BorderLayout.CENTER);
        jp.add(enterButton, BorderLayout.EAST);
        jp.add(registerButton, BorderLayout.SOUTH);
        jp.add(reloadServer, BorderLayout.WEST);

        setVisible(true);

        DefaultCaret caret = (DefaultCaret)chatWindow.getCaret(); ////автоскролл
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        connect();
    }

    public void connect(){
        try{
            socket = new Socket(SERVER_ADDR, SERVER_PORT);
            writer = new PrintWriter(socket.getOutputStream());
            reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            new Thread(new ServerListener()).start();

        }catch (Exception e){}
        openFile();
        chatWindow.append(AUTH_INVITATION + "\n");
        isAuthorized = false;
    }

    public class ServerListener implements  Runnable{
        String message;

        public void run(){

            try{
                while((message = reader.readLine()) != null){
                    if(message.startsWith("Hello, "))
                        isAuthorized = true;
                    if(!message.equals("\0") && isAuthorized)
                        chatWindow.append(date = simpleDate.format(new Date()) + "  " + message + "\n");
                        creatFile( date = simpleDate.format(new Date()) + "  " + message + "\n");
                    if(message.equals(AUTH_FAIL))
                        chatWindow.append(AUTH_FAIL + "\n" + NEW_CONNECT);
                    if(message.equals(WRONG_USERNAME))
                        chatWindow.append(WRONG_USERNAME + "\n" + NEW_CONNECT);
                }
            }catch (Exception e){

            }
        }
    }

    public void actionPerformed(ActionEvent e) {

        if(enterText.getText().length() != 0) {
            if(enterText.getText().equals(EXIT_COMMAND)) System.exit(-1);
            writer.println(enterText.getText() + "\n");
            writer.flush();
            enterText.setText("");
            enterText.requestFocusInWindow();

        }
    }

    ///авторизовать пользователя
     public void joinToChatAuthorized(String login, String password){
        writer.println(AUTH_SIGN + " " + login + " " + password);
        writer.flush();

    }

    ///создание нового пользователя
    public void joinToChatcreateUser(String login, String password){
        writer.println(CREAT_USER + " " + login + " " + password);
        writer.flush();
    }

    //открыть историю сообщений
    void openFile(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader("History.txt"));
            String line;
            ArrayList<String> lines = new ArrayList<>();
            while((line = reader.readLine()) != null){
                chatWindow.append(line + "\n");
            }
        }catch (Exception e){
            System.out.println("File 'History.txt' not found");
        }
    }

    ////создать файл истории сообщений
    void creatFile(String str){
        String string = str;
        try{
            FileOutputStream fos = new FileOutputStream("History.txt", true);
            fos.write(string.getBytes());
            fos.flush();
            fos.close();
        }catch (FileNotFoundException e) {
            System.out.println("File not found. File 'History' was created");
        }
        catch (IOException exc){
            exc.printStackTrace();
        }
        }
    }

