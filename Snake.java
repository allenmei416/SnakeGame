//Allen Mei & Jason Zhu
//June 12th, 2019
//ICS 3U7 - 01
//Final Assignment

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Snake extends JFrame{
	
	public static void main (String[] args) {
		Snake s = new Snake();
		Board board_object = new Board();
			
		s.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		s.setSize(835, 868);
		s.add(board_object);
		s.setVisible(true);
		Timer t = new Timer(100, board_object);
		t.start();
	}      
}

class Board extends JPanel implements KeyListener, ActionListener {	
	
	int[] xSnake = new int[1600];
	int[] ySnake = new int[1600];
	int lengthSnake;	
	
	//variables for buttons
	private JButton button_exit, button_details;
	
	//variables of powerups or traps
	int randomAppleX;
	int randomAppleY;
	
	int randomPowerUpX;
	int randomPowerUpY;
	
	int randomInvincibilityX;
	int randomInvincibilityY;
	
	int randomDecreaseTrapX;
	int randomDecreaseTrapY;

	int randomTrapX[] = new int[80];
	int randomTrapY[] = new int[80];

	int randomIncreaseX[] = new int[80];
	int randomIncreaseY[] = new int[80];
	
	//variables for direction
	int pressedKey = KeyEvent.VK_RIGHT;
    int oldPressedKey;
	
	//variables to control timings of messages or count score
	int scoreMessage;
	int scoreMessageTwo;
	int scoreMessageThree;
	int scoreMessageFour;

    int counter;
	int counter1;
	int counter2;
	int trapCounter;

	int direction;
	int decrease;
	
	//boolean variables for gameplay
	boolean ate;
	boolean snakeAlive;
	boolean powerUpActivated;
	boolean trapActivated;
	boolean increaseActivated;
	boolean gamePlayed;
	boolean snakeInvincible;
	boolean shortenDisplayed;
	boolean invincibleDisplayed;
	boolean decreaseTrapDisplayed;
	boolean decreaseTrapActivated;

	// timers
	private Timer invincibleTimer = new Timer(10 * 1000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			snakeInvincible = false;
		}
	});
	
	//constructor
	Board() {
		scoreMessage = 0;
		scoreMessageTwo = 0;
		scoreMessageThree = 0;
		scoreMessageFour = 0;

		counter = 0;
		counter1 = 0;
		counter2 = 0;
		lengthSnake = 8;	
		
		randomInvincibilityX = -1;
		randomInvincibilityY = -1;
		
		ate = false;
		snakeAlive = false;
		powerUpActivated = false;
		trapActivated = false;
		increaseActivated = false;
		gamePlayed = false;
		
		//displaying buttons
		button_details = new JButton("Details");
		button_details.setForeground(Color.white);
		button_details.setBounds(520, 0, 200, 60);
		button_details.setBackground(new Color(59, 89, 182));
		
		button_exit = new JButton("Exit");
		button_exit.setForeground(Color.white);
		button_exit.setBounds(720, 0, 100, 60);
		button_exit.setBackground(new Color(59, 89, 182));
		
		//layout of buttons
		this.setLayout(null);
		this.add(button_details);
		this.add(button_exit);
		
		button_details.addActionListener(this);
		button_exit.addActionListener(this);
		
		setFocusable(true);
		addKeyListener(this);
		
		appleRandom();
		shortenPowerUpRandom();
		trapRandom();
		lengthIncreaseRandom();		
		invincibilityRandom();
		decreaseTrapRandom();
	}
	
	//original snake spawn
	private void spawnSnake() {
		for (int i = 0; i < lengthSnake; i++) {
			ySnake[i] = 200;
			xSnake[i] = 200 - (i * 20);
		}
	}
	
	//method to paint components of the game
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.white);
		g.fillRect(0, 0, 820, 60);
		g.fillRect(0, 720, 820, 110);
		
		
		//paint powerups
		if ((lengthSnake % 6 == 0 || lengthSnake % 5 == 0) && lengthSnake > 10) {
			g.setColor(Color.green);
			g.fillRect(randomPowerUpX, randomPowerUpY, 20, 20);
		}
		
		if (randomInvincibilityX > 0 && !snakeInvincible) {
			g.setColor(Color.yellow);
			g.fillRect(randomInvincibilityX, randomInvincibilityY, 20, 20);
			invincibleDisplayed = true;
		}
		
		if (trapCounter > 5 && counter % 4 == 0) {
			g.setColor(Color.cyan);
			g.fillRect(randomDecreaseTrapX, randomDecreaseTrapY, 20, 20);
			decreaseTrapDisplayed = true;
		}
		
		for (int i = 0; i < trapCounter / 2; i++) {
			g.setColor(Color.black);
			g.fillRect(randomTrapX[i], randomTrapY[i], 20, 20);
		}
		
		for (int i = 0; i < trapCounter / 2; i++) {
			g.setColor(Color.magenta);
			g.fillRect(randomIncreaseX[i], randomIncreaseY[i], 20, 20);
		}		
		
		if (randomInvincibilityX >= 0 && lengthSnake > 10 && !snakeInvincible) {
			g.setColor(Color.yellow);
			g.fillRect(randomInvincibilityX, randomInvincibilityY, 20, 20);		}
		
		//paint apple
		g.setColor(Color.red);
		g.fillRect(randomAppleX, randomAppleY, 20, 20);
		
		//paint score
		g.setColor(Color.black);
		g.setFont(new Font("TimesRoman", Font.BOLD, 40));  
		g.drawString("" + counter,400,40);		
		
		//paint messages for traps or powerups
		if (decreaseTrapActivated) {
			if (lengthSnake < scoreMessageFour) {
				g.setFont(new Font("TimesRoman", Font.BOLD, 40));
				g.drawString("-5 Traps!",200,40);
			} else {
				decreaseTrapActivated = false;
			}
		}
		if (powerUpActivated){
			if (lengthSnake < scoreMessage){
				g.setFont(new Font("TimesRoman", Font.BOLD, 20));
				g.drawString("Snake length decreased",20,40);
			} else {
				powerUpActivated = false;
			}
		}
		
		if (trapActivated){
			if (lengthSnake < scoreMessageTwo){
				g.setFont(new Font("TimesRoman", Font.BOLD, 20));
				g.drawString("Trap!",300,40);
			} else {
				trapActivated = false;
			}
		}
		
		if (increaseActivated){
			if (lengthSnake < scoreMessageThree){
				g.setFont(new Font("TimesRoman", Font.BOLD, 40));
				g.drawString("+10!",600,780);
			} else {
				increaseActivated = false;
			}
		}		
		if (snakeInvincible) {
			g.setColor(Color.black);
			g.setFont(new Font("TimesRoman", Font.BOLD, 20));
			g.drawString("INVINCIBLE", 700, 780);
		}
		
		//paint message for starting game
		if (!snakeAlive){			
			g.setColor(Color.black);
			if (gamePlayed) {
				g.setFont(new Font("TimesRoman", Font.BOLD, 40));  
				g.drawString("GAME OVER",290,400);
			}
			g.setFont(new Font("TimesRoman", Font.BOLD, 30)); 
			g.drawString("Press space to start",286,440);
		}
		
		//paint snake 
		if (snakeAlive && snakeInvincible) {	
			for (int i = 0; i < lengthSnake; i++) {
				if (i == 0) { 
					g.setColor(Color.blue); 
				} else{
					g.setColor(Color.green);
				}
				g.fillRect(xSnake[i], ySnake[i], 20, 20);
			}	
		} else if (snakeAlive) {
			for (int i = 0; i < lengthSnake; i++) {
				if (i == 0) { 
					g.setColor(Color.green); 
				} else{
					g.setColor(Color.blue);
				}
				g.fillRect(xSnake[i], ySnake[i], 20, 20);
			}	
		} else {
			gameOver();			
		}		
		
		//paint rules
		g.setColor(Color.black);
		g.setFont(new Font("TimesRoman", Font.BOLD, 18));  
		g.drawString("Rules", 30 , 740);
		g.setFont(new Font("TimesRoman", Font.BOLD, 14)); 
		g.drawString("1. Use the arrow keys to eat the apples without hitting any walls or itself.", 30 , 755);
		g.drawString("2. Traps will increase the length or decrease the score (magenta and black).", 30 , 770);
		g.drawString("3. The traps will increase over time, but cyan blocks which will reduce the number of traps.", 30 , 785);
		g.drawString("4. Green blocks will decrease the length, yellow blocks give you invincibilty for 10secs.", 30 , 800);
		g.drawString("5. Press any button to exit the game, space button will restart the game.", 30 , 815);
				
	}
	
	//taking in the key pressed
	public void keyPressed(KeyEvent e) {
		oldPressedKey = pressedKey;
		pressedKey = e.getKeyCode();
	}
	
	//execute actions required
	public void actionPerformed(ActionEvent e) {
		moveSnakeCoor();
		checkApple();
		checkWalls();
		checkInvincibility();
		checkPowerUp();
		checkDecreaseTrap();
		if (!snakeInvincible) {
			checkHitTail();
			checkTrap();
			checkLengthIncrease();
		}
		repaint();
		if(e.getSource().equals(button_exit)) {
			JOptionPane.showMessageDialog(null, "Thank you for playing!", "Exit",JOptionPane.WARNING_MESSAGE ); 
			System.exit(0);
		} else if (e.getSource().equals(button_details)) {
			JOptionPane.showMessageDialog(null, "Snake - Allen & Jason", "SNAKE",JOptionPane.WARNING_MESSAGE );
		}
	}
	
	public void keyReleased(KeyEvent e){}
	public void keyTyped(KeyEvent e){}
	
	//directions
	public void up() {
		ySnake[0] -= 20;
	}
	
	public void down() {
		ySnake[0] += 20;
	}
	
	public void left() {
		xSnake[0] -= 20;
	}
	
	public void right() {
		xSnake[0] += 20;;
	}
	
	//moving snake
	public void moveSnakeCoor(){		
		for (int i = lengthSnake; i > 0; i--) {
			xSnake[i] = xSnake[(i - 1)];
			ySnake[i] = ySnake[(i - 1)];
		}
		
		if (pressedKey == KeyEvent.VK_DOWN) 
			if(!(direction == 2)) {
				down();
				direction = 1;
			} else up();
		if (pressedKey == KeyEvent.VK_UP) 
			if (!(direction == 1)) {
				up();
				direction = 2;
			} else down();
		if (pressedKey == KeyEvent.VK_LEFT) 
			if (!(direction == 4)) {
				left();
				direction = 3;
			} else right();
		if (pressedKey == KeyEvent.VK_RIGHT) 
			if (!(direction == 3)) {
				right();
				direction = 4;
			} else left();
	}	
	
	//randomly deciding positions of powerups or traps 
	//giving instructions for each
	public void appleRandom() {
		
		int randomNum = (int) (Math.random() * 33 + 3);
		randomAppleX = randomNum * 20;
	
		randomNum = (int) (Math.random() * 33 + 3);
		randomAppleY = randomNum * 20;
	}
	
	public void checkApple(){
		if ((randomAppleX == xSnake[0]) && (randomAppleY == ySnake[0])){
			ate = true;
			lengthSnake++;
			counter++;
			trapCounter++;
			appleRandom();
			trapRandom();
			lengthIncreaseRandom();
			decreaseTrapRandom();
			invincibilityRandom();
		}		
	}
	
	public void shortenPowerUpRandom() {
		int randomNum = (int) (Math.random() * 33 + 3);
		randomPowerUpX = randomNum * 20;

		randomNum = (int) (Math.random() * 33 + 3);
		randomPowerUpY = randomNum * 20;
	}

	public void checkPowerUp() {
		if (randomPowerUpX == xSnake[0] && randomPowerUpY == ySnake[0]) {
			powerUpActivated = true;
			lengthSnake -= 3;
			scoreMessage = lengthSnake + 1;			
			shortenPowerUpRandom();
		}
		shortenDisplayed = false;
	}
	
	public void invincibilityRandom() {
		if (Math.random() < 0.4) {
			int randomNum1 = (int) (Math.random() * 33 + 3);
			randomInvincibilityX = randomNum1 * 20;

			randomNum1 = (int) (Math.random() * 33 + 3);
			randomInvincibilityY = randomNum1 * 20;
		}
	}

	public void checkInvincibility() {
		if (randomInvincibilityX == xSnake[0] && randomInvincibilityY == ySnake[0] && invincibleDisplayed) {
			snakeInvincible = true;
			invincibleTimer.restart();
			invincibilityRandom();
		}
		invincibleDisplayed = false;
	}
	
	public void decreaseTrapRandom() {
		if (Math.random() < 0.8) {
			int randomNum = (int) (Math.random() * 33 + 3);
			randomDecreaseTrapX = randomNum * 20;
		
			randomNum = (int) (Math.random() * 33 + 3);
			randomDecreaseTrapY = randomNum * 20;
		}
	}

	public void checkDecreaseTrap() {
		if (randomDecreaseTrapX == xSnake[0] && randomDecreaseTrapY == ySnake[0] && decreaseTrapDisplayed) {
			trapCounter -= 3;
			scoreMessageFour = lengthSnake + 1;
			decreaseTrapRandom();
		}
		decreaseTrapDisplayed = false;
	}
	
	public void trapRandom(){
		for (int i = 0; i < trapCounter / 2; i++) {
			int randomNum = (int) (Math.random() * 33 + 3);
			randomTrapX[i] = randomNum * 20;
			
			randomNum = (int) (Math.random() * 33 + 3);
			randomTrapY[i] = randomNum * 20;
		}
	}
	
	public void checkTrap(){
		for (int i = 0; i < trapCounter / 2; i++) {
			if ((randomTrapX[i] == xSnake[0]) && (randomTrapY[i] == ySnake[0])){
				counter -= 2;
				scoreMessageTwo = lengthSnake+1;
				trapActivated = true;
				trapRandom();
			}
		}
	}
	
	public void lengthIncreaseRandom(){
		for (int i = 0; i < trapCounter / 2; i++) {
			int randomNum = (int) (Math.random() * 33 + 3);
			randomIncreaseX[i] = randomNum * 20;
			
			randomNum = (int) (Math.random() * 33 + 3);
			randomIncreaseY[i] = randomNum * 20;
		}
	}
	
	public void checkLengthIncrease(){
		for (int i = 0; i < trapCounter / 2; i++) {
			if ((randomIncreaseX[i] == xSnake[0]) && (randomIncreaseY[i] == ySnake[0])){
				lengthSnake += 10;
				scoreMessageThree = lengthSnake+1;
				increaseActivated = true;
				lengthIncreaseRandom();
			}
		}
	}
	
	public boolean checkHitTail() {
		for (int i = lengthSnake; i > 0; i--) {	
			if ((xSnake[i] == xSnake[0]) && (ySnake[i] == ySnake[0])){
				snakeAlive = false;
			}		
		}
		return snakeAlive;
	}
	
	public void checkWalls(){
		if (xSnake[0] > 800 || xSnake[0] < 0 || ySnake[0] == 720 || ySnake[0] < 60) {
			snakeAlive = false;
		}
	}

	//actions after the game is over
	private void gameOver() {		
		if (pressedKey == KeyEvent.VK_SPACE){
			pressedKey = KeyEvent.VK_RIGHT;
			setVisible(true);
			repaint();		
			
			for(int i = 0; i < lengthSnake; i++){
				ySnake[i] = 200;
				xSnake[i] = 200 - (i * 20);
			}		
			
			scoreMessage = 0;
			scoreMessageTwo = 0;
			scoreMessageThree = 0;
			scoreMessageFour = 0;

			counter = 0;
			counter1 = 0;
			counter2 = 0;
			trapCounter = 0;
			lengthSnake = 8;	
			
			randomInvincibilityX = -1;
			randomInvincibilityY = -1;
			
			ate = false;
			snakeAlive = false;
			powerUpActivated = false;
			trapActivated = false;
			increaseActivated = false;	
			snakeInvincible = false;
			gamePlayed = true;
				
			snakeAlive = true;
			Board board_object = new Board();	
		}
	}	
}
