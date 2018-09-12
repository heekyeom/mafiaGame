package ui;



import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;



public class LogInPanel extends JPanel implements ActionListener, KeyListener {
	
	
	
	
	MafiaFrame win;
	JTextField textField;
	JPasswordField passwordField;
	JButton btnJoin;
	JButton btnLogin;
	
	TabOrder tabOrder;
	
	ObjectOutputStream out;
	
	public LogInPanel(MafiaFrame win, ObjectOutputStream out) {
		this.win = win;
		this.out = out;
		
		setLayout(null);
				
		
		JLabel lblId = new JLabel("ID : ");
		lblId.setBounds(227, 234, 23, 15);
		add(lblId);
		
		JLabel lblPw = new JLabel("PW : ");
		lblPw.setBounds(227, 258, 30, 15);
		add(lblPw);
		
		textField = new JTextField();
		
		textField.setBounds(262, 231, 116, 21);
		add(textField);
		textField.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(262, 255, 116, 21);
		add(passwordField);
		
		btnLogin = new JButton("LOG IN");
		btnLogin.setBounds(390, 230, 77, 43);
		add(btnLogin);
		
		JLabel lblMafiaV = new JLabel("MAFIA v1.0");
		lblMafiaV.setBounds(321, 200, 77, 21);
		add(lblMafiaV);
		
		btnJoin = new JButton("JOIN");
		btnJoin.setBounds(301, 299, 97, 23);
		add(btnJoin);

		
		
		passwordField.addKeyListener(this);
		btnLogin.addActionListener(this);
		btnJoin.addActionListener(this);
		
		Vector<Component> order = new Vector<>(4);
		order.add(textField);
		order.add(passwordField);
		order.add(btnLogin);
		order.add(btnJoin);
		
		tabOrder = new TabOrder(order);
		win.setFocusTraversalPolicy(tabOrder);
		
		
	}


	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getSource().equals(textField)||arg0.getSource().equals(passwordField)) {
			if (arg0.getKeyCode() == KeyEvent.VK_ENTER) { 
				String id = textField.getText();
				String pw = new String(passwordField.getPassword());
				String result = win.login(id, pw);
				if(result.equals("1")) {
					win.presentId = "id"; //현재 접속 id정보 가지고 있슴
					win.change("wating");
				}else {
					JDialog warning = new JDialog(win, "WARNING!", true);
					JLabel msg = new JLabel("Check your id and password",JLabel.CENTER);
					JButton ok = new JButton("OK");
					ok.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							warning.dispose();
						}
					});
					
					warning.setLayout(new FlowLayout());;
					warning.add(msg);
					warning.add(ok);
				}
			}
		}
	}


	@Override
	public void keyReleased(KeyEvent arg0) {
		
	}


	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(btnLogin)) { 
			String id = textField.getText();
			String pw = new String(passwordField.getPassword());
			String result = win.login(id, pw);
			if(result.equals("1")) {
				win.presentId = "id"; //현재 접속 id정보 가지고 있슴
				win.change("wating");
			}else {
				Dialog warning = new JDialog(win, "WARNING!", true);
				JLabel msg = new JLabel("Check your id and password",JLabel.CENTER);
				JButton ok = new JButton("OK");
				ok.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						warning.dispose();
					}
				});
				
				warning.setLayout(new FlowLayout());
				warning.setSize(200,100);
				warning.setLocationRelativeTo(null);
				warning.add(msg);
				warning.add(ok);
				warning.setVisible(true);
			}
		}
		
		
		if (e.getSource().equals(btnJoin)) { 
			win.change("join");
		}		
	}
	
	
	class TabOrder extends FocusTraversalPolicy {
		
		Vector<Component> order;
		
		public TabOrder(Vector<Component> order) {
			this.order = new Vector<>(order.size());
			this.order.addAll(order);
		}
		

		@Override
		public Component getComponentAfter(Container aContainer, Component aComponent) {
			int idx = (order.indexOf(aComponent)+1)%order.size();
			return order.get(idx);
		}

		@Override
		public Component getComponentBefore(Container aContainer, Component aComponent) {
			int idx = order.indexOf(aComponent)-1;
			if(idx<0) {
				idx = order.size()-1;
			}
			return order.get(idx);
		}

		@Override
		public Component getDefaultComponent(Container aContainer) {
			return order.get(0);
		}

		@Override
		public Component getFirstComponent(Container aContainer) {
			return order.get(0);
		}

		@Override
		public Component getLastComponent(Container aContainer) {
			return order.lastElement();
		}

	}
	
}
