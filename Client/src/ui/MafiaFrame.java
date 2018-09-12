package ui;

import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import mapia.ClientSocket;
import mapia.Game;
import mapia.Login;

public class MafiaFrame extends JFrame implements ActionListener, KeyListener {

	LogInPanel logInPanel;
	JoinPanel joinPanel;
	//WatingPanel watingPanel;
	//GamePanel gamePanel;
	
	JPanel watingPanel;
	JPanel gamePanel;
	

	ClientSocket socket;

	ObjectOutputStream out;
	ObjectInputStream in;

	String elected;
	
	String presentId;
	
	Login login;
	
	boolean host;
	
	Game game;
	
	Set<String> userList;
	
	//wating
	
	JScrollPane watingScrollPane;
	JTextField watingTextfield;
	MafiaFrame win;
	JTextArea watingTextArea;
	List watingUserList;
	JButton watingBtnEnter;
	JButton btnStart;
	JLabel watingLblTitle;
	JLabel watingLblList;
	
	
	//game
	JTextField gameTextField;
	JTextField gameTextField_role;
	JTextArea gameTextArea;
	
	JScrollPane gameScrollPane;
	JLabel lblGame;
	List aliveList;
	JLabel lblAliveList;
	List deadList;
	JLabel lblDead;
	JLabel lblRole;
	
	JButton gamebtnEnter;

	Dialog voteDialog;
	JButton btnVote;
	JRadioButton[] candidate;
	ButtonGroup group;
	Enumeration<AbstractButton> enums;
	
	boolean vote;
	
	Set<String> alive;
	Set<String> dead;
	
	
	{
		
		//대기 화면
		watingPanel = new JPanel();
		
		watingPanel.setLayout(null);
		
		watingTextArea = new JTextArea();
		watingTextArea.setEditable(false);
		
		
		
		watingScrollPane = new JScrollPane(watingTextArea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		watingScrollPane.setBounds(12, 35, 489, 442);
		
		
		watingPanel.add(watingScrollPane);
		
		watingLblTitle = new JLabel("Wating");
		watingLblTitle.setBounds(247, 10, 57, 15);
		watingPanel.add(watingLblTitle);
		
		watingTextfield = new JTextField();
		watingTextfield.setBounds(12, 487, 374, 30);
		watingPanel.add(watingTextfield);
		watingTextfield.setColumns(10);
		
		watingBtnEnter = new JButton("ENTER");
		watingBtnEnter.setBounds(393, 486, 111, 30);
		watingPanel.add(watingBtnEnter);
		
		watingUserList = new List();
		watingUserList.setBounds(531, 61, 132, 300);
		watingPanel.add(watingUserList);
		
		watingLblList = new JLabel("WATING LIST");
		watingLblList.setBounds(559, 35, 77, 15);
		watingPanel.add(watingLblList);
		
		
		
		btnStart = new JButton("START");
		btnStart.setBounds(547, 382, 97, 23);
		watingPanel.add(btnStart);

		
		watingTextfield.addKeyListener(this);
		watingBtnEnter.addActionListener(this);
		btnStart.addActionListener(this);
		
		
		btnStart.setEnabled(false);
		userList = new HashSet<>();
		
		
		//게임 화면
		
		gamePanel = new JPanel();
		
		gamePanel.setLayout(null);

		gameTextArea = new JTextArea();

		gameTextArea.setEditable(false);

		gameScrollPane = new JScrollPane(gameTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		gameScrollPane.setBounds(27, 53, 489, 402);
		gamePanel.add(gameScrollPane);

		lblGame = new JLabel("GAME");
		lblGame.setBounds(237, 28, 57, 15);
		gamePanel.add(lblGame);

		aliveList = new List();
		aliveList.setBounds(540, 53, 132, 201);
		gamePanel.add(aliveList);

		lblAliveList = new JLabel("USERLIST");
		lblAliveList.setBounds(577, 28, 74, 15);
		gamePanel.add(lblAliveList);

		deadList = new List();
		deadList.setBounds(540, 296, 132, 102);
		gamePanel.add(deadList);

		lblDead = new JLabel("DEAD");
		lblDead.setBounds(592, 275, 57, 15);
		gamePanel.add(lblDead);

		gameTextField = new JTextField();
		
		
		gameTextField.setColumns(10);
		gameTextField.setBounds(27, 498, 374, 30);
		gamePanel.add(gameTextField);

		gamebtnEnter = new JButton("ENTER");
		gamebtnEnter.setBounds(408, 497, 111, 30);
		gamePanel.add(gamebtnEnter);

		lblRole = new JLabel("ROLE :");
		lblRole.setBounds(37, 465, 47, 15);
		gamePanel.add(lblRole);

		gameTextField_role = new JTextField();
		gameTextField_role.setEditable(false);
		gameTextField_role.setText("시민");
		
		gameTextField_role.setBounds(81, 465, 116, 21);
		gamePanel.add(gameTextField_role);
		gameTextField_role.setColumns(10);
		
		
		gameTextField.addKeyListener(this);
		gamebtnEnter.addActionListener(this);
		
		vote = false;
		
		alive = new HashSet<>();
		dead = new HashSet<>();
		
		
	}
	
	
	public static void main(String[] args) {
		
		
		MafiaFrame mf = new MafiaFrame();
	}



	public MafiaFrame() {
		
		String url="70.12.110.162";

		socket = new ClientSocket(url, 7777);
		out = socket.getOos();
		in = socket.getOis();
		
		login = new Login(in, out);
		

		setTitle("MAFIA v1.0");

		logInPanel = new LogInPanel(this,out);
		joinPanel = new JoinPanel(this,out);

		
		host = false;
		
		add(this.logInPanel);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(750, 600);
		setLocationRelativeTo(null);

		
		
		setVisible(true);

	}

	public void change(String paneName) { //로그인이랑 게임에서 exit할때 wating 패널으로 넘어가는거 두개 다른거 확인하고 나중에 서버 연결할때 고쳐야됨
		if (paneName.equals("login")) {
			getContentPane().removeAll();
			getContentPane().add(new LogInPanel(this, out));
			revalidate();
			repaint();
		} else if (paneName.equals("join")) {
			this.setFocusTraversalPolicy(null);
			getContentPane().removeAll();
			getContentPane().add(new JoinPanel(this, out));
			revalidate();
			repaint();
		} else if (paneName.equals("wating")) {
			this.setFocusTraversalPolicy(null);
			getContentPane().removeAll();
			getContentPane().add(watingPanel);
			revalidate();
			repaint();
			
		} else if (paneName.equals("game")) {
			getContentPane().removeAll();
			getContentPane().add(gamePanel);
			revalidate();
			repaint();
		}
	}

	public String login(String id, String pw) {
		
		
		String result = login.signin(id, pw);
		if(result.equals("1")) {
			game = new Game(out, id);
			Thread receiver = new Thread(new Receiver(socket, id, game));
			receiver.start();
							
		}
		return result;
	}
	
	public boolean idCheck(String id) {
		if(login.idCheck(id).equals("0")) return true;
		else return false;
	}
	
	public void join(String id, String pw) {
		login.signup(id, pw);
	}
	
	public void addUser(Set<String> userList) {
		
		watingUserList.removeAll();
		
		
		for(String username : userList) {
			
			watingUserList.add(username);
		}
		
		
		System.out.println("addUser실행됨");
	}
	

	
	public void watingMsg(String msg) {
		watingTextArea.append(msg + "\n");
		watingTextArea.setCaretPosition(watingTextArea.getDocument().getLength());
	}
	
	public void gameMsg(String msg) {
		gameTextArea.append(msg + "\n");
		gameTextArea.setCaretPosition(gameTextArea.getDocument().getLength());
	}
	
	
	public void setAlive(Set<String> alives) {
		aliveList.removeAll();
		for(String username : alives) {
			
			aliveList.add(username);
		}
	}
	
	public void setDead(Set<String> deads) {
		deadList.removeAll();
		for(String username : deads) {
			
			deadList.add(username);
		}
	}
	

	
	
	class Receiver extends Thread { // 들어오는 데이터(채팅내용)
		ClientSocket socket;
		ObjectInputStream in;
		String user;
		Game game;
		

		public Receiver(ClientSocket socket, String user, Game game) {
			System.out.println("[client [Receiver]] 생성자 : "+in+", "+user+", "+game);  //Debug
			this.socket=socket;
			this.user=user;
			this.game=game;
			in=socket.getOis();
			
			
		}

		@Override
		public void run() {
			//게임방에 접속한 후 게임방의 현재 상태를 입력받음.(현재의 유저 정보 등)
			System.out.println("[client [Receiver]] run()");  //Debug
			game.start();

			
			
			
			//게임 대시 시작
			while (true) {
				// 게임 대기중
				System.out.println("[client [Receiver]] gameWait()로 진입");  //Debug
				gameWait(in);
				System.out.println("[client [Receiver]] 게임대기 종료");  //Debug
				// 게임 시작
				System.out.println("[client [Receiver]] gameStart()로 진입");  //Debug
				gameStart(in);
				System.out.println("[client [Receiver]] 게임 종료");  //Debug
			}
		}

		public void gameWait(ObjectInputStream in) {
			System.out.println("[client [Receiver]] gameWait()");  //Debug
			while (true) {
				HashMap<String, String> receiveMsg = null;
				Object tmp;
				try {
					tmp = in.readObject();
					if (tmp instanceof HashMap<?, ?>)
						receiveMsg = (HashMap<String, String>) tmp;

					String request = receiveMsg.get("request");
					if (request.equals("msg")) {
						System.out.println("이거 밑은 메세지");
						
						String msg = game.receiveChat(receiveMsg);
						
						watingMsg(msg);
						
					}else if (request.equals("init")){ // 처음 접속
						System.out.println("이거는 init인데 잘 몰겟");
						
						userList = game.init(receiveMsg);
						System.out.println(userList);
						addUser(userList);
						
						host = game.getHost();
						
						if(userList.size()==4) {
							if(host) btnStart.setEnabled(true);
						}
											
					} else if (request.equals("removeUser")) { // 유저가 나감
						System.out.println("유저 나감");
						
						String id = receiveMsg.get("id");
						
						userList.remove(id);
						
						if(userList.size()==4) {
							if(host) btnStart.setEnabled(true);
						}
						
						String msg = game.removeUser(id);
						
						watingMsg(msg);
						
						addUser(userList);
						
					} else if (request.equals("host")){ // 방장 바뀜
						System.out.println("이거는 방장 관련");
						
						watingMsg(game.sethost(receiveMsg));
						
						host = game.getHost();
						
						if(userList.size()==4) {
							if(host) btnStart.setEnabled(true);
						}
						
					} else if (request.equals("addUser")) { // 유저가 들어옴
						System.out.println("이거 밑은 유저 들옴");
						
						String id = receiveMsg.get("id");
						
						userList.add(id);
						
						if(userList.size()==4) {
							if(host) btnStart.setEnabled(true);
						}
						
						System.out.println("addd유저의 user 리스트");
						System.out.println(userList);
						
						String result = game.addUser(id);
						
						watingMsg(result);
						
						addUser(userList);

					}else {  //게임 시작
						change("game");
						return;
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void gameStart(ObjectInputStream in) {
			System.out.println("[client [Receiver]] gameStart()"); // Debug
			// 역할 받기
			HashMap<String, String> receiveMsg = null;
			Object tmp;
			try {

				tmp = in.readObject();
				if (tmp instanceof HashMap<?, ?>)
					receiveMsg = (HashMap<String, String>) tmp;

				System.out.println("receiveMsg.get(\"status\") : " + receiveMsg.get("status"));

				String status = receiveMsg.get("status");
				gameTextField_role.setText(status);
				gameMsg(game.setStatus(status));
				alive.addAll(userList);
				for(String user : alive) {
					aliveList.add(user);
				}

			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 역할 받고 게임 시작
			while (true) {
				try {
					tmp = in.readObject();
					if (tmp instanceof HashMap<?, ?>)
						receiveMsg = (HashMap<String, String>) tmp;

					String request = receiveMsg.get("request");
					if (request.equals("msg")) { // 채팅
						
						String msg = game.receiveChat(receiveMsg);
						
						gameMsg(msg);
						
						if(msg.contains("님이 사망하셨습니다.")) {
							for(String name : alive) {
								if(msg.contains(name)) {
									alive.remove(name);
									dead.add(name);
									setAlive(alive);
									setDead(dead);
								}
							}
						}
						
					} else if (request.equals("vote")) { // 투표시작 요청 받음
						if(gameTextField_role.getText().equals("사망"));
						else {
							vote(alive);
							game.vote(elected);
						}
					} else if (request.equals("night")) { // 찬반 투표 요청
						
						if(gameTextField_role.getText().equals("시민")||gameTextField_role.getText().equals("사망")) 
							gameTextField.setEnabled(false);
						else
							gameTextField.setEnabled(true);
						game.night();
					} else if (request.equals("day")) { // 찬반 투표 요청
						
						if(gameTextField_role.getText().equals("사망"))
							gameTextField.setEnabled(false);
						else
							gameTextField.setEnabled(true);
						
						game.day();
					} else if (request.equals("removeUser")) { // 유저가 나감
						game.removeUser(receiveMsg.get("id"));
					} else if (request.equals("host")) { // 방장 바뀜
						game.sethost(receiveMsg);
					} else if (request.equals("setStatus")) { // 사람 죽을 시 죽음으로 상태 변경
						
						game.setStatus(receiveMsg.get("status"));
						
						
					} else if (request.equals("gameEnd")) { // 게임 종료
						vote=false;
						game.gameEnd();
						return;
					}

				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		}
	}


	
	
	
	
	
	
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getSource().equals(watingTextfield)) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				game.sendChat(watingTextfield.getText());
				watingTextfield.setText("");
			}
		}
		
		if (e.getSource().equals(gameTextField)) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) { 
				game.sendChat(gameTextField.getText());
				gameTextField.setText("");
			}
		}
	}



	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(watingBtnEnter)) { 
			game.sendChat(watingTextfield.getText());
			watingTextfield.setText("");
		}
		
		if(e.getSource().equals(btnStart)) {
			game.sendStart();
		}
		
		if(e.getSource().equals(gamebtnEnter)) {
			game.sendChat(gameTextField.getText());
			gameTextField.setText("");
		}
		
		
		if(e.getSource().equals(btnVote)) {
			boolean selected = false;
			String selectedPerson = "";
			while(enums.hasMoreElements()) { 
				JRadioButton result = (JRadioButton)enums.nextElement();
				if(result.isSelected()) {
					selectedPerson = result.getText();
					selected = true;
				}
				
			}
			if(selected) {
				elected = selectedPerson;
				voteDialog.dispose();
			}else {
				JDialog warning = new JDialog(win, "WARNING!", true);
				JLabel msg = new JLabel("You didn't select!",JLabel.CENTER);
				JButton ok = new JButton("OK");
				ok.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						warning.dispose();
					}
				});
				
				warning.getContentPane().setLayout(new FlowLayout());;
				warning.getContentPane().add(msg);
				warning.getContentPane().add(ok);
				
			}

		}
		
	}
	
	
	public void vote(Set<String> candidates) {
		
		
		voteDialog = new Dialog(win,"VOTE",true);
		voteDialog.setLayout(new FlowLayout());
		
		voteDialog.setSize(200, 150);
		voteDialog.setLocationRelativeTo(null);
		
		elected="";
		
		JLabel msg = new JLabel("Select who will die",JLabel.CENTER);
		System.out.println("여기 밑이 캔디데이트 사이즈");
		System.out.println(candidates.size());
		
		candidate = new JRadioButton[candidates.size()];
		
		candidate[0] = new JRadioButton();
		candidate[1] = new JRadioButton();
		candidate[2] = new JRadioButton();
		candidate[3] = new JRadioButton();
		
		
		group = new ButtonGroup();
		
		
		Iterator<String> li = candidates.iterator();
		int index=0	;
		while(li.hasNext()) {
			String candi = li.next();
			System.out.println(candi);
			candidate[index].setText(candi);
			group.add(candidate[index]);
		}
		btnVote = new JButton("VOTE");
		
		voteDialog.add(msg);
		for(int i=0;i<candidates.size();i++) {
			voteDialog.add(candidate[i]);
		}
		voteDialog.add(btnVote);
		
		enums = group.getElements();
		
		
		
		
		btnVote.addActionListener(this);
		
		voteDialog.setVisible(true);
		
	}



	
	
	

	
}
