package com.cyberdyne.heartsclient.view;


import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.cyberdyne.heartsclient.controller.RegistrationManager;

public class RegistrationView extends JFrame {
	/**
	 * @uml.property  name="registrationManager"
	 * @uml.associationEnd  multiplicity="(1 1)" inverse="registrationView:com.cyberdyne.heartsclient.controller.RegistrationManager"
	 */
	RegistrationManager registrationManager;
	/**
	 * @uml.property  name="logIn"
	 * @uml.associationEnd  readOnly="true"
	 */
	JButton LogIn;
	/**
	 * @uml.property  name="registrazione"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	JButton Registrazione;
	/**
	 * @uml.property  name="labelReg"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	JLabel labelReg;
	/**
	 * @uml.property  name="labelUser"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	JLabel labelUser;
	/**
	 * @uml.property  name="labelPass"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	JLabel labelPass;
	/**
	 * @uml.property  name="labelNome"
	 * @uml.associationEnd  readOnly="true"
	 */
	JLabel labelNome;
	/**
	 * @uml.property  name="labelEmail"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	JLabel labelEmail;
	/**
	 * @uml.property  name="labelRequired"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	JLabel labelRequired;
	/**
	 * @uml.property  name="textUser"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JTextField  textUser;
	/**
	 * @uml.property  name="textPass"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JTextField textPass;
	/**
	 * @uml.property  name="textNome"
	 * @uml.associationEnd  readOnly="true"
	 */
	private JTextField textNome;
	/**
	 * @uml.property  name="textEmail"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JTextField textEmail;
	
	public RegistrationView(RegistrationManager registrationManager){
		this.registrationManager = registrationManager;
		initializeFrame();
	}

	private void initializeFrame() {
		final JFrame f = new JFrame("Registration Form");
	    f.setLayout(null);
	    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int w = dim.width;
		int h = dim.height;
		int x = (w/2)-(600/2);
		int y = (h/2)-(350/2);
		
		labelReg = new JLabel();
		labelReg.setText("Registrati per creare un nuovo account");
		labelReg.setFont(new Font("Serif", Font.BOLD, 18));
		
	    labelUser = new JLabel();
	    labelUser.setText("Username: (*)");
	    textUser = new JTextField(15);

	    labelPass = new JLabel();
	    labelPass.setText("Password: (*)");
	    textPass = new JPasswordField(15);
	    
	    /*labelNome = new JLabel();
	    labelNome.setText("Nome: (*)");
	    textNome = new JTextField(20);*/
	    
	    labelEmail = new JLabel();
	    labelEmail.setText("Email: (*)");
	    textEmail = new JTextField(30);
	    
	    labelRequired = new JLabel();
	    labelRequired.setText("(*) Campi Obbligatori");
	    
	    Registrazione = new JButton("Registrazione");
	   
	    labelReg.setBounds(170, 50, 600, 30);
	    labelUser.setBounds(150,100,100,20);
		textUser.setBounds(250,100,200,20);
		labelPass.setBounds(150,130,100,20);
		textPass.setBounds(250,130,200,20);
	    /*labelNome.setBounds(150,160,100,20);*/
	    /*textNome.setBounds(250,160,200,20);*/
	    labelEmail.setBounds(150,190,100,20);
	    textEmail.setBounds(250,190,200,20);
	    labelRequired.setBounds(270,250,200,20);
	    Registrazione.setBounds(350,220,100,20);
	    
	    f.add(labelReg);
	    f.add(labelUser);
	    f.add(textUser);
	    f.add(labelPass);
	    f.add(textPass);
	    /*f.add(labelNome);*/
	    /*f.add(textNome);*/
	    f.add(labelEmail);
	    f.add(textEmail);
	    f.add(Registrazione);
	    f.add(labelRequired);
	    
	    f.setLocation(x, y);
	    f.setVisible(true);
	    f.setResizable(false);
	    f.setSize(600, 350);
	    f.getRootPane().setDefaultButton(Registrazione);
	    
	    Registrazione.addActionListener(new ActionListener(){
	 	   public void actionPerformed(ActionEvent ae)
	 	  {
	 	    String value1=textUser.getText();
	 	    String value2=textPass.getText();
	 	    /*String value3=textNome.getText();*/
	 	    String value4=textEmail.getText();
	 	    if (value1.equals("") || value2.equals("") || value4.equals("")) {
		    	JOptionPane.showMessageDialog(null,"Inserisci i campi obbligatori","Errore",JOptionPane.ERROR_MESSAGE);
	 	    }else {
	 	    	int resultRegistration = registrationManager.createNewAccount(value1, value2, value4);
	 	    	if (resultRegistration==1) {
	 	    		JOptionPane.showMessageDialog(null,"Errore durante la Registrazione.","Error",JOptionPane.ERROR_MESSAGE);
	 	    	}else {
	 	    		JOptionPane.showMessageDialog(null,"Registrazione avvenuta con successo!","Login",JOptionPane.INFORMATION_MESSAGE);
	 	    		f.setVisible(false);
	 	    	}
	 	    }
	 	  }
	    });	
	}
}
