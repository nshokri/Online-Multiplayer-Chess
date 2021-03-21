import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;

import Pieces.ChessPiece;

public class Window extends JComponent
{
	private JFrame window;
	private ChessPiece[][] board;
	private Game game;
	
	private int squareWidth;
	private int squareHeight;
	
	private int x = -1;
	private int y = -1;
	
	private boolean moving = false;
	
	private Connection connection;
	private Thread connectionThread;
	
	public Window(int width, int height, Game game)
	{
		window = new JFrame();
		
		window.setSize(width, height);
		window.setTitle("Chess");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		window.setBackground(Color.gray);
		window.setFocusable(true);
		window.requestFocusInWindow();
		window.setResizable(false);
		
		squareWidth = (window.getWidth() / Game.BOARD_WIDTH) - 1;
		squareHeight = (window.getHeight() / Game.BOARD_HEIGHT) - 4;
		
		this.game = game;
		this.connection = new Connection(game);
		
		connectionThread = new Thread(new Runnable()
		{
			public void run()
			{
				while (game.isOver() == 0)
				{
					connection.process();
					window.repaint();
					window.revalidate();
				}
			}
		});
		
		connectionThread.start();
		
		this.board = game.getBoard();
		
		this.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				
				if (e.getButton() == MouseEvent.BUTTON1 && game.getUser() == game.getTurn())
				{
					int oldX = x;
					int oldY = y;
					
					//Find click location on board
					x = (int) Math.floor((int) (e.getX() + 0.0 / window.getWidth()) / 100);
					y = (int) Math.floor((int) (e.getY() + 0.0 / window.getHeight()) / 100);
				
					ArrayList<ChessPiece> validMoves = null;
					
					if (moving == true)
					{
						validMoves = game.getAllValidMoves(oldX, oldY);
						
						//Check for valid move
						for (ChessPiece n : validMoves)
						{
							if (x == n.getX() && y == n.getY())
							{
								game.makeMove(oldX, oldY, x, y);
								connection.sendMove(oldX, oldY, x, y);
							}
						}
						
						x = -1;
						y = -1;
						moving = false;
						
					}
					else
					{
						if (board[x][y] == null)
						{
							x = -1;
							y = -1;
							moving = false;
						}
						else
						{
							moving = true;
						}
					}
					window.repaint();
				}
			} 
		});
		
		window.add(this);
		window.revalidate();
		
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 =  (Graphics2D) g;
		
		if (game.isOver() != 0)
		{
			try
			{
				connection.process();
				//connection.endConnection();
				//connectionThread.join();
				
				if (game.getUser() == game.isOver())
				{
					System.out.println("You Win!");
				}
				else
				{
					System.out.println("You Lost!");
				}
				
				window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		g2.setColor(Color.WHITE);
		g2.setStroke(new BasicStroke(1));
		
		ArrayList<ChessPiece> validMoves = null;
		if (x != -1 && y != -1)
		{
			validMoves = game.getAllValidMoves(x, y);
		}
		
		//Draw chess board
		for (int i = 0; i < Game.BOARD_WIDTH; i++)
		{
			for (int j = 0; j < Game.BOARD_HEIGHT; j++)
			{
				/*Color pattern*/
				if (j % 2 == 0)
				{
					if (i % 2 == 0)
					{
						g2.setColor(new Color(245, 206, 115));
					}
					else
					{
						g2.setColor(new Color(230, 167, 21));
					}
				}
				else
				{
					if (i % 2 == 1)
					{
						g2.setColor(new Color(245, 206, 115));
					}
					else
					{
						g2.setColor(new Color(230, 167, 21));
					}
				}
				/********************************************************/
				
				g2.fillRect(i * squareWidth, j * squareHeight, squareWidth, squareHeight);
				
				//Draw click
				if (x != -1 && y != -1 && x == i && y == j)
				{
					g2.setColor(new Color(250, 217, 30));
					g2.fillRect(x * squareWidth, y * squareHeight, squareWidth, squareHeight);
				}
				
				
				//Draw possible moves
				if (moving == true && validMoves != null)
				{
					for (ChessPiece n : validMoves)
					{
						if (n.getX() == i && n.getY() == j)
						{
							g2.setColor(new Color(255, 238, 143));
							g2.fillRect(n.getX() * squareWidth, n.getY() * squareHeight, squareWidth, squareHeight);
						}
					}
				}
	
				
				//Draw chess piece
				if (board[i][j] != null)
				{
					Image img = board[i][j].draw();
					
					img = img.getScaledInstance(squareWidth, squareHeight, 0);
					
					g2.drawImage(img, i * squareWidth + 1, j * squareHeight - 4, null);
				}
				
			}
		}
	}
}
