import java.util.ArrayList;

import Pieces.Bishop;
import Pieces.ChessPiece;
import Pieces.King;
import Pieces.Knight;
import Pieces.Pawn;
import Pieces.Queen;
import Pieces.Rook;


public class Game
{
	// todo: implement checkmate
	//Can change these for experimenting
	public static final int BOARD_WIDTH = 8;
	public static final int BOARD_HEIGHT = 8;
	
	private ChessPiece[][] board;
	private int turn;

	private int user;
	
    private int winner = 0;
	
	public Game()
	{
		board = new ChessPiece[BOARD_WIDTH][BOARD_HEIGHT];
		turn = -1;
		
		/*Generate board*/

		/**Pawns**/
		//Black
		for (int i = 0; i < BOARD_WIDTH; i++)
		{
			board[i][1] = new Pawn(i, 1, 1);
		}
		//White
		for (int i = 0; i < BOARD_WIDTH; i++)
		{
			board[i][6] = new Pawn(i, 6, -1);
		}
		
		
		/**Rooks**/
		//Black
		board[0][0] = new Rook(0, 0, 1);
		board[BOARD_WIDTH - 1][0] = new Rook(BOARD_WIDTH - 1, 0, 1);
		
		//White
		board[0][BOARD_HEIGHT - 1] = new Rook(0, BOARD_HEIGHT - 1, -1);
		board[BOARD_WIDTH - 1][BOARD_HEIGHT - 1] = new Rook(BOARD_WIDTH - 1, BOARD_HEIGHT - 1, -1);
		
		
		/**Knights**/
		//Black
		board[1][0] = new Knight(1, 0, 1);
		board[BOARD_WIDTH - 2][0] = new Knight(BOARD_WIDTH - 2, 0, 1);
		
		//White
		board[1][BOARD_HEIGHT - 1] = new Knight(1, BOARD_HEIGHT - 1, -1);
		board[BOARD_WIDTH - 2][BOARD_HEIGHT - 1] = new Knight(BOARD_WIDTH - 2, BOARD_HEIGHT - 1, -1);
	
		
		/**Bishops**/
		//Black
		board[2][0] = new Bishop(2, 0, 1);
		board[BOARD_WIDTH - 3][0] = new Bishop(BOARD_WIDTH - 3, 0, 1);
		
		//White
		board[2][BOARD_HEIGHT - 1] = new Bishop(2, BOARD_HEIGHT - 1, -1);
		board[BOARD_WIDTH - 3][BOARD_HEIGHT - 1] = new Bishop(BOARD_WIDTH - 3, BOARD_HEIGHT - 1, -1);
		
		
		/**Kings**/
		//Black
		board[3][0] = new King(3, 0, 1);
		
		//White
		board[3][BOARD_HEIGHT - 1] = new King(3, BOARD_HEIGHT - 1, -1);
		
		/**Queens**/
		//Black
		board[4][0] = new Queen(4, 0, 1);
		
		//White
		board[4][BOARD_HEIGHT - 1] = new Queen(4, BOARD_HEIGHT - 1, -1);
		
	}
	
	
	public void makeMove(int x, int y, int newX, int newY)
	{
		
		if (x >= BOARD_WIDTH || y >= BOARD_HEIGHT || x < 0 ||
				y < 0 || board[x][y].getPlayer() != turn)
		{
			//TODO: Display some sort of error message
			return;
		}
		
		ArrayList<ChessPiece> validMoves = getAllValidMoves(x, y);
		
		for (ChessPiece n : validMoves)
		{
			if (n.getX() == newX && n.getY() == newY)
			{
				
				//Check if king dead
                if (board[newX][newY] instanceof King)
                {
                    winner = board[newX][newY].getPlayer() * -1;
                }
				
				//Make move
				board[newX][newY] = board[x][y];
				board[x][y] = null;
				
				board[newX][newY].setX(newX);
				board[newX][newY].setY(newY);
				
				nextTurn();
				return;
			}
			else
			{
				//TODO: Display some sort of error message
			}
		}
	}
	
	public ChessPiece[][] getBoard()
	{
		return board;
	}
	
	
	/**********************getAllValidMoves************************/
	public ArrayList<ChessPiece> getAllValidMoves(int x, int y)
	{
		ArrayList<ChessPiece> moves = new ArrayList<ChessPiece>();
		
		if (board[x][y] == null)
		{
			return moves;
		}
		
		/**Pawn**/
		if (board[x][y] instanceof Pawn && board[x][y].getPlayer() == turn)
		{
			//One forward is clear
			if (((turn == 1 && y + 1 < BOARD_HEIGHT) || (turn == -1 && y - 1 >= 0 ))
					
					&& board[x][y + turn * 1] == null
					&& board[x][y].getPlayer() == turn)
			{
				moves.add(new Pawn(x, y + turn * 1, turn));
				
				//Double step
				if (((turn == 1 && y + 2 < BOARD_HEIGHT) || (turn == -1 && y - 2 >= 0 ))
						
						&& board[x][y + turn * 2] == null
						&& board[x][y].getPlayer() == turn
						&& ((turn == 1 && y == 1) || (turn == -1 && y == BOARD_HEIGHT - 2)))
				{
					moves.add(new Pawn(x, y + turn * 2, turn));
				}
			}
			
			
			//Can kill diagonal right
			if (((turn == 1 && y + 1 < BOARD_HEIGHT && x + 1 < BOARD_WIDTH)
					|| (turn == -1 && y - 1 >= 0 && x + 1 < BOARD_WIDTH))
					
					&& board[x + 1][y + turn] != null
					&& board[x + 1][y + turn].getPlayer() != turn)
			{
				
				moves.add(board[x + 1][y + turn]);
			}
			
			//Can kill diagonal left
			if (((turn == 1 && (y + 1 < BOARD_HEIGHT) && (x - 1 >= 0))
				|| (turn == -1 && y - 1 >= 0 && x - 1 >= 0))
				
					&& board[x - 1][y + turn] != null
					&& board[x - 1][y + turn].getPlayer() != turn)
			{
				moves.add(board[x - 1][y + turn]);
			}
		}
		
		/**Rook**/
		if (board[x][y] instanceof Rook && board[x][y].getPlayer() == turn)
		{
			//Up movement
			for (int i = 1; i < BOARD_HEIGHT; i++)
			{
				if (y + i < BOARD_HEIGHT && board[x][y + i] == null)
				{
					moves.add(new Rook(x, y + i, turn));
				}
				else if (y + i >= BOARD_HEIGHT)
				{
					break;
				}
				else if (board[x][y + i].getPlayer() != turn)
				{
					moves.add(new Rook(x, y + i, turn));
					break;
				}
				else
				{
					break;
				}
			}
			
			//Down movement
			for (int i = 1; i < BOARD_HEIGHT; i++)
			{
				if (y - i >= 0 && board[x][y - i] == null)
				{
					moves.add(new Rook(x, y - i, turn));
				}
				else if (y - i < 0)
				{
					break;
				}
				else if (board[x][y - i].getPlayer() != turn)
				{
					moves.add(new Rook(x, y - i, turn));
					break;
				}
				else
				{
					break;
				}
			}
			
			//Right movement
			for (int i = 1; i < BOARD_WIDTH; i++)
			{
				if (x + i < BOARD_WIDTH && board[x + i][y] == null)
				{
					moves.add(new Rook(x + i, y, turn));
				}
				else if (x + i >= BOARD_WIDTH)
				{
					break;
				}
				else if (board[x + i][y].getPlayer() != turn)
				{
					moves.add(new Rook(x + i, y, turn));
					break;
				}
				else
				{
					break;
				}
			}
			
			//Left movement
			for (int i = 1; i < BOARD_WIDTH; i++)
			{
				if (x - i >= 0 && board[x - i][y] == null)
				{
					moves.add(new Rook(x - i, y, turn));
				}
				else if (x - i < 0)
				{
					break;
				}
				else if (board[x - i][y].getPlayer() != turn)
				{
					moves.add(new Rook(x - i, y, turn));
					break;
				}
				else
				{
					break;
				}
			}
		}
		
		/**Knight**/
		if (board[x][y] instanceof Knight && board[x][y].getPlayer() == turn)
		{
			//Up-right direction
			if (x + 1 < BOARD_WIDTH && y + 2 < BOARD_HEIGHT)
			{
				if (board[x + 1][y + 2] == null || board[x + 1][y + 2].getPlayer() != turn)
				{
					moves.add(new Knight(x + 1, y + 2, turn));
				}
			}
			
			//Up-left direction
			if (x - 1 >= 0 && y + 2 < BOARD_HEIGHT)
			{
				if (board[x - 1][y + 2] == null || board[x - 1][y + 2].getPlayer() != turn)
				{
					moves.add(new Knight(x - 1, y + 2, turn));
				}
			}
			
			//Down-right direction
			if (x + 1 < BOARD_WIDTH && y - 2 >= 0)
			{
				if (board[x + 1][y - 2] == null || board[x + 1][y - 2].getPlayer() != turn)
				{
					moves.add(new Knight(x + 1, y - 2, turn));
				}
			}
			
			//Down-left direction
			if (x - 1 >= 0 && y - 2 >= 0)
			{
				if (board[x - 1][y - 2] == null || board[x - 1][y - 2].getPlayer() != turn)
				{
					moves.add(new Knight(x - 1, y - 2, turn));
				}
			}
			
			//Left-up direction
			if (x - 2 >= 0 && y + 1 < BOARD_HEIGHT)
			{
				if (board[x - 2][y + 1] == null || board[x - 2][y + 1].getPlayer() != turn)
				{
					moves.add(new Knight(x - 2, y + 1, turn));
				}
			}
			
			//Left-Down direction
			if (x - 2 >= 0 && y - 1 >= 0)
			{
				if (board[x - 2][y - 1] == null || board[x - 2][y - 1].getPlayer() != turn)
				{
					moves.add(new Knight(x - 2, y - 1, turn));
				}
			}
			
			//Right-up direction
			if (x + 2 < BOARD_WIDTH && y + 1 < BOARD_HEIGHT)
			{
				if (board[x + 2][y + 1] == null || board[x + 2][y + 1].getPlayer() != turn)
				{
					moves.add(new Knight(x + 2, y + 1, turn));
				}
			}
			
			//Right-down direction
			if (x + 2 < BOARD_WIDTH && y - 1 >= 0)
			{
				if (board[x + 2][y - 1] == null || board[x + 2][y - 1].getPlayer() != turn)
				{
					moves.add(new Knight(x + 2, y - 1, turn));
				}
			}
		}
		
		/**Bishop**/
		if (board[x][y] instanceof Bishop && board[x][y].getPlayer() == turn)
		{
			//Up-right direction
			for (int i = 1; i < BOARD_WIDTH; i++)
			{
				if (x + i < BOARD_WIDTH && y - i >= 0)
				{
					if (board[x + i][y - i] == null || board[x + i][y - i].getPlayer() != turn)
					{
						moves.add(new Bishop(x + i, y - i, turn));
						
						if (board[x + i][y - i] != null && board[x + i][y - i].getPlayer() != turn)
						{
							break;
						}
					}
					else
					{
						break;
					}
				}
				else
				{
					break;
				}
			}
			
			//Up-left direction
			for (int i = 1; i < BOARD_WIDTH; i++)
			{
				if (x - i >= 0 && y - i >= 0)
				{
					if (board[x - i][y - i] == null || board[x - i][y - i].getPlayer() != turn)
					{
						moves.add(new Bishop(x - i, y - i, turn));
						
						if (board[x - i][y - i] != null && board[x - i][y - i].getPlayer() != turn)
						{
							break;
						}
					}
					else
					{
						break;
					}
				}
				else
				{
					break;
				}
			}
		
			//Down-right direction
			for (int i = 1; i < BOARD_WIDTH; i++)
			{
				if (x + i < BOARD_WIDTH && y + i < BOARD_HEIGHT)
				{
					if (board[x + i][y + i] == null || board[x + i][y + i].getPlayer() != turn)
					{
						moves.add(new Bishop(x + i, y + i, turn));
						
						if (board[x + i][y + i] != null && board[x + i][y + i].getPlayer() != turn)
						{
							break;
						}
					}
					else
					{
						break;
					}
				}
				else
				{
					break;
				}
			}
			
			//Down-left direction
			for (int i = 1; i < BOARD_WIDTH; i++)
			{
				if (x - i >= 0 && y + i < BOARD_HEIGHT)
				{
					if (board[x - i][y + i] == null || board[x - i][y + i].getPlayer() != turn)
					{
						moves.add(new Bishop(x - i, y + i, turn));
						
						if (board[x - i][y + i] != null && board[x - i][y + i].getPlayer() != turn)
						{
							break;
						}
					}
					else
					{
						break;
					}
				}
				else
				{
					break;
				}
			}
		}
		
		/**Queen**/
		if (board[x][y] instanceof Queen && board[x][y].getPlayer() == turn)
		{
			/**BISHOP MOVEMENT**/
			//Up-right direction
			for (int i = 1; i < BOARD_WIDTH; i++)
			{
				if (x + i < BOARD_WIDTH && y - i >= 0)
				{
					if (board[x + i][y - i] == null || board[x + i][y - i].getPlayer() != turn)
					{
						moves.add(new Queen(x + i, y - i, turn));
						
						if (board[x + i][y - i] != null && board[x + i][y - i].getPlayer() != turn)
						{
							break;
						}
					}
					else
					{
						break;
					}
				}
				else
				{
					break;
				}
			}
			
			//Up-left direction
			for (int i = 1; i < BOARD_WIDTH; i++)
			{
				if (x - i >= 0 && y - i >= 0)
				{
					if (board[x - i][y - i] == null || board[x - i][y - i].getPlayer() != turn)
					{
						moves.add(new Queen(x - i, y - i, turn));
						
						if (board[x - i][y - i] != null && board[x - i][y - i].getPlayer() != turn)
						{
							break;
						}
					}
					else
					{
						break;
					}
				}
				else
				{
					break;
				}
			}
		
			//Down-right direction
			for (int i = 1; i < BOARD_WIDTH; i++)
			{
				if (x + i < BOARD_WIDTH && y + i < BOARD_HEIGHT)
				{
					if (board[x + i][y + i] == null || board[x + i][y + i].getPlayer() != turn)
					{
						moves.add(new Queen(x + i, y + i, turn));
						
						if (board[x + i][y + i] != null && board[x + i][y + i].getPlayer() != turn)
						{
							break;
						}
					}
					else
					{
						break;
					}
				}
				else
				{
					break;
				}
			}
			
			//Down-left direction
			for (int i = 1; i < BOARD_WIDTH; i++)
			{
				if (x - i >= 0 && y + i < BOARD_HEIGHT)
				{
					if (board[x - i][y + i] == null || board[x - i][y + i].getPlayer() != turn)
					{
						moves.add(new Queen(x - i, y + i, turn));
						
						if (board[x - i][y + i] != null && board[x - i][y + i].getPlayer() != turn)
						{
							break;
						}
					}
					else
					{
						break;
					}
				}
				else
				{
					break;
				}
			}
			
			/**ROOK MOVEMENT**/
			//Up movement
			for (int i = 1; i < BOARD_HEIGHT; i++)
			{
				if (y + i < BOARD_HEIGHT && board[x][y + i] == null)
				{
					moves.add(new Queen(x, y + i, turn));
				}
				else if (y + i >= BOARD_HEIGHT)
				{
					break;
				}
				else if (board[x][y + i].getPlayer() != turn)
				{
					moves.add(new Queen(x, y + i, turn));
				}
				else
				{
					break;
				}
			}
			
			//Down movement
			for (int i = 1; i < BOARD_HEIGHT; i++)
			{
				if (y - i >= 0 && board[x][y - i] == null)
				{
					moves.add(new Queen(x, y - i, turn));
				}
				else if (y - i < 0)
				{
					break;
				}
				else if (board[x][y - i].getPlayer() != turn)
				{
					moves.add(new Queen(x, y - i, turn));
				}
				else
				{
					break;
				}
			}
			
			//Right movement
			for (int i = 1; i < BOARD_WIDTH; i++)
			{
				if (x + i < BOARD_WIDTH && board[x + i][y] == null)
				{
					moves.add(new Queen(x + i, y, turn));
				}
				else if (x + i >= BOARD_WIDTH)
				{
					break;
				}
				else if (board[x + i][y].getPlayer() != turn)
				{
					moves.add(new Queen(x + i, y, turn));
				}
				else
				{
					break;
				}
			}
			
			//Left movement
			for (int i = 1; i < BOARD_WIDTH; i++)
			{
				if (x - i >= 0 && board[x - i][y] == null)
				{
					moves.add(new Queen(x - i, y, turn));
				}
				else if (x - i < 0)
				{
					break;
				}
				else if (board[x - i][y].getPlayer() != turn)
				{
					moves.add(new Queen(x - i, y, turn));
				}
				else
				{
					break;
				}
			}
		}
		
		/**King**/
		if (board[x][y] instanceof King && board[x][y].getPlayer() == turn)
		{
			/**BISHOP MOVEMENT**/
			//Up-right direction

			if (x + 1 < BOARD_WIDTH && y - 1 >= 0)
			{
				if (board[x + 1][y - 1] == null || board[x + 1][y - 1].getPlayer() != turn)
				{
					moves.add(new King(x + 1, y - 1, turn));
				}
			}
				
			
			//Up-left direction
			if (x - 1 >= 0 && y - 1 >= 0)
			{
				if (board[x - 1][y - 1] == null || board[x - 1][y - 1].getPlayer() != turn)
				{
					moves.add(new King(x - 1, y - 1, turn));
				}
			}
		
			//Down-right direction
			if (x + 1 < BOARD_WIDTH && y + 1 < BOARD_HEIGHT)
			{
				if (board[x + 1][y + 1] == null || board[x + 1][y + 1].getPlayer() != turn)
				{
					moves.add(new King(x + 1, y + 1, turn));
				}
			}
			
			//Down-left direction
			if (x - 1 >= 0 && y + 1 < BOARD_HEIGHT)
			{
				if (board[x - 1][y + 1] == null || board[x - 1][y + 1].getPlayer() != turn)
				{
					moves.add(new King(x - 1, y + 1, turn));
				}
			}
			
			/**ROOK MOVEMENT**/
			//Up movement
			if (y + 1 < BOARD_HEIGHT && (board[x][y + 1] == null || board[x][y + 1].getPlayer() != turn))
			{
				moves.add(new King(x, y + 1, turn));
			}
			
			//Down movement
			if (y - 1 >= 0 && (board[x][y - 1] == null || board[x][y - 1].getPlayer() != turn))
			{
				moves.add(new King(x, y - 1, turn));
			}
			
			
			//Right movement
			if (x + 1 < BOARD_WIDTH && (board[x + 1][y] == null || board[x + 1][y].getPlayer() != turn))
			{
				moves.add(new King(x + 1, y, turn));
			}
			
			//Left movement
			if (x - 1 >= 0 && (board[x - 1][y] == null || board[x - 1][y].getPlayer() != turn))
			{
				moves.add(new King(x - 1, y, turn));
			}

		}
		
		return moves;
	}
	
	public void nextTurn()
	{
		if (turn == 1)
		{
			turn = -1;
		}
		else if (turn == -1)
		{
			turn = 1;
		}
	}
	
	public int getUser()
	{
		return user;
	}
	
	public void setUser(int user)
	{
		this.user = user;
	}
	
	public int getTurn()
	{
		return turn;
	}
	
	public int getWinner()
	{
		return winner;
	}
	
	public void setWinner(int win)
	{
		this.winner = win;
	}
}
