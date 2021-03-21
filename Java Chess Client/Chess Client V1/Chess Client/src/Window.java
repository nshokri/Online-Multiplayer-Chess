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

		// Set basic window properties
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

		// Attach the instance a game and connection that this window will be operating with
		this.game = game;
		this.connection = new Connection(game);

		// Run the connection on a separate thread so that one does not block the other (they both require while loops)
		connectionThread = new Thread(new Runnable()
		{
			public void run()
			{
				// game.getWinner() will return either 1 or -1 once the game is done and a winner is decided
				while (game.getWinner() == 0)
				{
					try
					{
						connection.process();
						window.repaint();
						window.revalidate();
					}
					catch (Exception e)
					{
						System.err.println("Lost connection with server...");
					}

				}
			}
		});
		
		connectionThread.start();
		
		this.board = game.getBoard();

		// Create a listener to detect when a mouse click occurs on the window
		this.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				// If it is a left click and it is the user's turn to move
				if (e.getButton() == MouseEvent.BUTTON1 && game.getUser() == game.getTurn())
				{
					int oldX = x; // todo: it would make more sense if we moved this to the else right???
					int oldY = y;
					
					//Find click location on board
					x = (int) Math.floor((int) (e.getX() + 0.0 / window.getWidth()) / 100);
					y = (int) Math.floor((int) (e.getY() + 0.0 / window.getHeight()) / 100);

					ArrayList<ChessPiece> validMoves = null;

					// Second click -- user selects where the piece should move
					if (moving == true)
					{
						// Get all legal moves that the current selected piece can move
						validMoves = game.getAllValidMoves(oldX, oldY);
						
						// Check to see if the player's move is valid
						for (ChessPiece n : validMoves)
						{
							if (x == n.getX() && y == n.getY())
							{
								game.makeMove(oldX, oldY, x, y);
								connection.sendMove(oldX, oldY, x, y);
							}
						}

						// Reset all vars (back to the first click stage)
						x = -1;
						y = -1;
						moving = false;
						
					}
					// First click -- user selects which piece should be moved
					else
					{
						// If the player clicked on a spot with no piece, remove the highlighted spot on the board
						if (board[x][y] == null)
						{
							x = -1;
							y = -1;
							moving = false;
						}
						// The user has selected a piece to move
						else
						{
							moving = true;
						}
					}
					window.repaint();
				}
			} 
		});

		// Add the mouse listener to the current window
		window.add(this);
		window.revalidate();
		
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 =  (Graphics2D) g;

		// NOTE: this is essentially a while loop because paintComponent() gets called each frame
		if (game.getWinner() != 0)
		{
			try
			{
				connection.process();
				//connection.endConnection();
				//connectionThread.join();

				// Tell the user the out come
				if (game.getUser() == game.getWinner())
				{
					System.out.println("You Win!");
				}
				else
				{
					System.out.println("You Lost!");
				}

				// Close the window
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
