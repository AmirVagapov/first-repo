package java_level_two.lesson_seven;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Tom on 24.09.2017.
 */
public class RegisterWindow extends JFrame implements ChatInterface, ActionListener{

    private final Chat chat;
    JRadioButton authorized = new JRadioButton("Authorized", true);
    JRadioButton create = new JRadioButton("Create new user");
    ButtonGroup btngrp = new ButtonGroup();
    JTextField login;
    JTextField psswrd;
    JLabel log;
    JLabel passw;
    JButton btnOk = new JButton("Ok, lets chat");


    public RegisterWindow(Chat chat){
        this.chat = chat;
        setSize(WIDTH_WINDOW, HEIGHT_WINDOW / 2);
        Rectangle chatBounds = chat.getBounds();
        int posX = (int)chatBounds.getCenterX() - WIDTH_WINDOW / 4;
        int posY = (int)chatBounds.getCenterY() - HEIGHT_WINDOW / 4;
        setLocation(posX, posY);
        setTitle("Authorized or create new user");
        setLayout(new GridLayout(10, 1));

        addButtons();
        addFields();
        psswrd.addActionListener(this);
        btnOk.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        letsChat();
    }

    void addButtons(){
        add(new JLabel("Create new user or authorized"));
        btngrp.add(authorized);
        btngrp.add(create);
        add(authorized);
        add(create);
    }

    void addFields(){
        log = new JLabel("Enter the login");
        passw = new JLabel("Enter the password");
        login = new JTextField();
        psswrd = new JTextField();
        add(log);
        add(login);
        add(passw);
        add(psswrd);
        add(btnOk);
    }


    void letsChat(){
        if(authorized.isSelected()) chat.joinToChatAuthorized(login.getText(), psswrd.getText());
        else chat.joinToChatcreateUser(login.getText(), psswrd.getText());
        login.setText("");
        psswrd.setText("");
        setVisible(false);
    }
}
