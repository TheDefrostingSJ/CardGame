package UI;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import GameLogic.MainControl;

public class GameLauncher implements GUIStartInterface{
	
	//Holds everything
	public static JFrame launcherWindow = new JFrame("Mrs. JJ");
	static JFrame gameWindow = new JFrame( "Mrs. JJ" );

	private JButton host;
    private JButton connect;
    private static JTextField ipInField;
    private JLabel playerInfoLabel;
    private JLabel ipAdLabel;
    private JLabel ipInfoLabel;
    private JLabel ipInLabel;
    private JButton startButton;
    
    
    public static GuiPanel gamePanel;
    
    static MainControl gameLogic;
	
	public GameLauncher() throws IOException {
		
		gamePanel = new GuiPanel();
		
		gameLogic = new MainControl( gamePanel, this );
		
		GuiPanel.gameLogic = gameLogic;
		
		launcherWindow.setSize( 400, 300 );
		launcherWindow.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		
		gameWindow.setResizable( false );
		
		//construct components
        host = new JButton ("Host");
        host.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent ae) {
        		//Obtains IP info and displays it
        		try {
					ipPull();
					gameLogic.hostGame();
					
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
        		
        		//Names the start window to player 1 if we are the host
        		//TODO remove this
        		launcherWindow.setTitle( "Hosting..." );
        		playerInfoLabel.setText("You are hosting.  Waiting for players...");
        		
        		host.setEnabled(false);
        		connect.setEnabled(false);
        		ipInField.setEnabled( false );
        		
        		ipInfoLabel.setVisible( true );
                ipAdLabel.setVisible( true );
        	}
        });
        
        connect = new JButton ("Connect");
        connect.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent ae) {
        		//Sends IP info to networking
        		System.out.println(getIPFromText());
        		gameLogic.joinGame(getIPFromText());
        		playerConnected(gameLogic.playerId);
        	}
        });
        
        startButton = new JButton ("Start Game");
        startButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent ae) {
        		//Starts the game if all players are connected
        		
					try {
						
						play();
					} catch (IOException e) {
						e.printStackTrace();
					}
        	}
        });        

        //adjust size and set layout
        launcherWindow.setPreferredSize (new Dimension (350, 400));
        launcherWindow.setLayout (null);
        launcherWindow.setResizable( false );
        
        //Center the window
        launcherWindow.setLocationRelativeTo(null);  
        
        //Host button
        host.setBounds (25, 220, 100, 25);
        
        //Connect button
        connect.setBounds ( 260, 220, 100, 25);
        
        //Start game button
        startButton.setBounds (260, 20, 100, 25);
        startButton.setVisible(false);
        
        //Top info label
        playerInfoLabel = new JLabel ("Please choose \"Host\" or \"Connect\"");
        playerInfoLabel.setBounds (25, 20, 300, 25);
        
        //Connection info
        ipInfoLabel = new JLabel ("Your Connection Info:");
        ipAdLabel = new JLabel ("############");
        
        ipInfoLabel.setVisible( false );
        ipAdLabel.setVisible( false );
        
        int connInfoX = 25;
        int connInfoY = 150;
		ipInfoLabel.setBounds (connInfoX, connInfoY, 130, 25);
		ipAdLabel.setBounds (connInfoX, connInfoY + 30, 200, 25);
        		
        //IP input
        ipInLabel = new JLabel( "Server IP:" );
        ipInField = new JTextField();
        
        int ipX = 210;
        int ipY = 150;
        ipInLabel.setBounds (ipX, ipY, 100, 25);
        ipInField.setBounds( ipX, ipY + 30, 150, 25 );
        ipInField.setText( "127.0.0.1" );
		
        //add components
        launcherWindow.add (host);
        launcherWindow.add (connect);
        launcherWindow.add (ipInField);
        launcherWindow.add (playerInfoLabel);
        launcherWindow.add (ipAdLabel);
        launcherWindow.add (ipInfoLabel);
        launcherWindow.add (startButton);
        launcherWindow.add (ipInLabel);
        
		launcherWindow.setVisible(true);
	}
	
	private String getIPFromText() {
		return ipInField.getText();
	}
	
	public void playerConnected(int player) {
		System.out.println(player);
		if(player==3) {
			playerInfoLabel.setText("All Players Connected");
			startButton.setVisible(true);
		}
	}
	
	
	private void ipPull() throws UnknownHostException{
		InetAddress inetAddress = InetAddress.getLocalHost();
		ipAdLabel.setText("IP: " + inetAddress.getHostAddress());
	}
	
	public static void play() throws IOException {
		
		gameWindow.setSize( 1000, 800 );
		gameWindow.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		
		//gamePanel = new GuiPanel();
		gameWindow.getContentPane().add( gamePanel );
		gameLogic.startGame();
		gameWindow.pack();
		gameWindow.setVisible( true );
		launcherWindow.dispose();
		
	}
	
	public static void clientPlay() throws IOException{
		gameWindow.setSize( 1000, 800 );
		gameWindow.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		
		//Center the window
		gameWindow.setLocationRelativeTo(null);  
		
		//gamePanel = new GuiPanel();
		gameWindow.getContentPane().add( gamePanel );
		gameWindow.pack();
		gameWindow.setVisible( true );
		launcherWindow.dispose();

		
	}
	

	

	@Override
	public void connectedToServer( int player ) {
		
		connect.setEnabled(false);
		host.setEnabled(false);
		ipInField.setEnabled( false );
		
		playerInfoLabel.setText("Connected to server. Waiting for game start");
		
		
	}

	

	
	
	
}
