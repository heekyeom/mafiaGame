package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class JoinPanel extends JPanel implements ActionListener, KeyListener{
	
	MafiaFrame win;
	JTextField textField;
	JPasswordField passwordField;
	JPasswordField passwordField_1;
	JButton btnJoin;
	JButton btnIdCheck;
	JLabel lblIssame;
	JLabel lblIsvalid;
	
	ObjectOutputStream out;
	
	boolean idCheck;
	boolean pwCheck;
	
	public JoinPanel(MafiaFrame win, ObjectOutputStream out) {
		this.win = win;
		this.out = out;
		
		idCheck = false;
		pwCheck = false;
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("ID :");
		lblNewLabel.setBounds(180, 200, 76, 40);
		add(lblNewLabel);
		
		textField = new JTextField();
		textField.setBounds(268, 210, 157, 21);
		add(textField);
		textField.setColumns(10);
		
		JLabel lblPw = new JLabel("PW :");
		lblPw.setBounds(180, 241, 76, 40);
		add(lblPw);
		
		JLabel lblConfirmPw = new JLabel("Confirm PW :");
		lblConfirmPw.setBounds(180, 280, 76, 40);
		add(lblConfirmPw);
		
		btnIdCheck = new JButton("ID Check");
		btnIdCheck.setBounds(461, 209, 97, 23);
		add(btnIdCheck);
		
		btnJoin = new JButton("JOIN");
		
		
		btnJoin.setBounds(301, 345, 97, 23);
		btnJoin.setEnabled(false);
		add(btnJoin);
		
		JLabel lblJoin = new JLabel("JOIN");
		lblJoin.setBounds(341, 163, 57, 15);
		add(lblJoin);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(268, 251, 157, 21);
		add(passwordField);
		
		passwordField_1 = new JPasswordField();
		passwordField_1.setBounds(268, 290, 157, 21);
		add(passwordField_1);
		
		lblIssame = new JLabel("Not Typed");
		lblIssame.setBounds(441, 293, 57, 15);
		add(lblIssame);
		
		lblIsvalid = new JLabel("Check ID");
		lblIsvalid.setBounds(570, 213, 57, 15);
		add(lblIsvalid);
		
		
		btnIdCheck.addActionListener(this);
		btnJoin.addActionListener(this);
		textField.addKeyListener(this);
		passwordField.addKeyListener(this);
		passwordField_1.addKeyListener(this);
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getSource().equals(textField)) {
			idCheck = false;
			lblIsvalid.setText("Check ID");
			btnJoin.setEnabled(false);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		
		
		if (e.getSource().equals(passwordField)) {
			String password1 = new String(passwordField.getPassword());
			String password2 = new String(passwordField_1.getPassword());

			if (password1.equals(password2)) {
				lblIssame.setText("Same");
				pwCheck = true;

				if (idCheck) {
					btnJoin.setEnabled(true);
				}
	
			} else if (password1.equals("") || password2.equals("")) {
				lblIssame.setText("Not Typed");
				btnJoin.setEnabled(false);
				pwCheck = false;
			}

			else {
				lblIssame.setText("Not Same");
				btnJoin.setEnabled(false);
				pwCheck = false;
			}

		}

		if (e.getSource().equals(passwordField_1)) {
			String password1 = new String(passwordField.getPassword());
			String password2 = new String(passwordField_1.getPassword());

			if (password2.equals(password1)) {
				lblIssame.setText("Same");
				pwCheck = true;
				if (idCheck)
					btnJoin.setEnabled(true);
			} else if (password1.equals("") || password2.equals("")) {
				lblIssame.setText("Not Typed");
				btnJoin.setEnabled(false);
				pwCheck = false;
			}

			else {
				lblIssame.setText("Not Same");
				btnJoin.setEnabled(false);
				pwCheck = false;
			}
		}		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(btnIdCheck)) { 
			String id = textField.getText();
			idCheck = win.idCheck(id);
			if(idCheck) {
				lblIsvalid.setText("Valid");
				if(pwCheck) {
					btnJoin.setEnabled(true);
				}
			}else {
				lblIsvalid.setText("Not Valid");				
			}
			
		}

		if (e.getSource().equals(btnJoin)) {
			String id = textField.getText();
			String pw = new String(passwordField.getPassword());
			win.join(id, pw);
			win.change("login");
		}
	}
}
