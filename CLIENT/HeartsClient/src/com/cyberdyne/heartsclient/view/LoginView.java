package com.cyberdyne.heartsclient.view;


import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import com.cyberdyne.heartsclient.controller.ChatManager;
import com.cyberdyne.heartsclient.controller.LobbyManager;
import com.cyberdyne.heartsclient.controller.LoginManager;
import com.cyberdyne.heartsclient.controller.RegistrationManager;

public class LoginView extends JFrame {

	/**
	 * @uml.property  name="loginManager"
	 * @uml.associationEnd  multiplicity="(1 1)" inverse="loginView:com.cyberdyne.heartsclient.controller.LoginManager"
	 */
	LoginManager loginManager;
	/**
	 * @uml.property  name="logIn"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	JButton LogIn;
	/**
	 * @uml.property  name="registrazione"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	JButton Registrazione;
	/**
	 * @uml.property  name="labelWelcome"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	JLabel labelWelcome;
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
	 * @uml.property  name="labelRequired"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	JLabel labelRequired;
	/**
	 * @uml.property  name="labelRegistrazione"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	JLabel labelRegistrazione;
	/**
	 * @uml.property  name="labelLogo"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	JLabel labelLogo;
	/**
	 * @uml.property  name="textWelcome"
	 * @uml.associationEnd  readOnly="true"
	 */
	private JTextField  textWelcome;
	/**
	 * @uml.property  name="textUser"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JTextField textUser;
	/**
	 * @uml.property  name="textPass"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JTextField textPass;

	public LoginView(LoginManager loginManager) {

		/*try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}*/
		
		this.loginManager = loginManager;
		initializeFrame();
	}

	private void initializeFrame() {

		final JFrame f = new JFrame("Login Form");
		f.setLayout(null);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int w = dim.width;
		int h = dim.height;
		int x = (w/2)-(800/2);
		int y = (h/2)-(350/2);

		//labelLogo.setIcon(createImageIcon());
		labelLogo = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("sprites/hearts_logo.gif")));

		labelWelcome = new JLabel();
		labelWelcome.setText("Benvenuto! Per accedere al gioco, inserisci username e password");
		labelWelcome.setFont(new Font("Serif", Font.BOLD, 18));

		labelUser = new JLabel();
		labelUser.setText("Username: (*)");
		textUser = new JTextField();

		labelPass = new JLabel();
		labelPass.setText("Password: (*)");
		textPass = new JPasswordField();

		labelRegistrazione = new JLabel();
		labelRegistrazione.setText("Non sei ancora registrato?");

		labelRequired = new JLabel();
		labelRequired.setText("(*) Campi Obbligatori");

		LogIn = new JButton("Login");
		Registrazione = new JButton("Registrati ora!");

		labelWelcome.setBounds(35, 40, 600, 30);
		labelUser.setBounds(150,105,100,20);
		textUser.setBounds(270,100,200,30);
		labelPass.setBounds(150,135,100,20);
		textPass.setBounds(270,130,200,30);
		LogIn.setBounds(350,165,120,25);
		labelRequired.setBounds(150,165,200,20);
		labelRegistrazione.setBounds(150,240,200,20);
		Registrazione.setBounds(350,240,120,25);
		labelLogo.setBounds(570, 30, 202, 260);


		f.add(labelWelcome);
		f.add(labelUser);
		f.add(textUser);
		f.add(labelPass);
		f.add(textPass);
		f.add(LogIn);
		f.add(labelRequired);
		f.add(labelRegistrazione);
		f.add(Registrazione);
		f.add(labelLogo);

		f.setLocation(x, y);
		f.setVisible(true);
		f.setResizable(false);
		f.setSize(800,350);
		f.getRootPane().setDefaultButton(LogIn);

		LogIn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				String value1 = textUser.getText();
				String value2 = textPass.getText();

				if (value1.equals("") || value2.equals("")) {
					JOptionPane.showMessageDialog(null,"Inserisci i campi obbligatori","Error",JOptionPane.ERROR_MESSAGE);
				} else {
					int resultLogin = loginManager.LoginAccount(value1, value2);
					if (resultLogin == 1) {
						JOptionPane.showMessageDialog(null,"Errore durante il Login.","Error",JOptionPane.ERROR_MESSAGE);
					} else {
						// LOGIN A BUON FINE. SI APRIRA' UNA NUOVA VIEW!
						//JOptionPane.showMessageDialog(null,"Login avvenuto con successo!","Login",JOptionPane.INFORMATION_MESSAGE);
						f.setVisible(false);
						LobbyManager lobby = new LobbyManager();
						new ChatManager();

					}
				}
			}
		});
		Registrazione.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				RegistrationManager registrationManager = new RegistrationManager();
			}
		});
	}

//	ImageIcon createImageIcon () {
//		try {
//			System.out.println(getClass().getClassLoader().getResource("sprites/hearts_logo.gif").toString());
//			return new ImageIcon(getClass().getClassLoader().getResource("sprites/hearts_logo.gif"));
//		} catch (NullPointerException e) {
//			e.printStackTrace();
//			return new ImageIcon("Null");
//		}
//
//	}
}
