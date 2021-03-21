import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import Pieces.Bishop;
import Pieces.ChessPiece;
import Pieces.King;
import Pieces.Knight;
import Pieces.Pawn;
import Pieces.Queen;
import Pieces.Rook;

public class Connection
{
	private Socket socket;
	private Game game;
	
	private PrintWriter writer;
	private BufferedReader reader;

	private final boolean DEBUG = true;
	private boolean sent = false;

	private double startTime;
	private double endTime;

	public enum Stages
	{
		Unconnected, Connected, GameInprogress, GameEnd
	}

	private Stages stage = Stages.Unconnected;
	
	public Connection(Game game)
	{
		this.game = game;

        try {
            socket = new Socket("127.0.0.1", 4000);
            writer = new PrintWriter(socket.getOutputStream(), true);
            
            writer.println("127.0.0.1 has connected!");

    		//Receive
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
	}
	
	public void process()
	{
		if (DEBUG && sent)
		{
			endTime = System.currentTimeMillis();
			System.out.println("Round-Trip Time = " + (endTime - startTime) + "ms");
			sent = false;
		}

		String message = null;

		// Opponent disconnected or we are done communicating with server
		if (stage == null)
		{
			return;
		}

		try
		{
			message = reader.readLine();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		if (message == null)
		{
			return;
		}
		
		//Stages
		if (stage == Stages.Unconnected)
		{
			if (message.equals("Connection Accepted!"))
			{
				System.out.println(message);
				stage = Stages.Connected;
			}
		}
		else if (stage == Stages.Connected)
		{
			if (message.substring(0, message.indexOf(" ")).equals("Starting..."))
			{
				int playerNum = Integer.parseInt(message.substring(message.indexOf(" ") + 1)); // -1 is white, 1 is black
				System.out.println(playerNum);
				game.setUser(playerNum);
				stage = Stages.GameInprogress;
			}
		}
		else if (stage == Stages.GameInprogress)
		{

			if (message.substring(0, 2).equals("/S"))
        	{
        		updateBoard(message, game);
        	}
			else if (message.substring(0, 2).equals("/D"))
			{
				System.out.println("Opponent has disconnected! You win by default.");
				game.setWinner(game.getUser());
				stage = null;
				this.endConnection();
			}
		}
		else if (stage == Stages.GameEnd)
		{
			this.endConnection();
			stage = null;
		}
	}
	
	public void sendMove(int oldX, int oldY, int newX, int newY)
	{
		if (DEBUG && !sent)
		{
			startTime = System.currentTimeMillis();
			sent = true;
		}

		writer.println("Move: " + oldX + " " + oldY + " " + newX + " " + newY);
		writer.flush();
	}
	
	public void endConnection()
	{
        try 
        {
    		writer.close();
			socket.close();

			System.out.println("Connection Closed!");
		} 
        catch (IOException e)
        {
			e.printStackTrace();
        }
		
	}
	
	private void updateBoard(String message, Game game)
	{
		Scanner scan = new Scanner(message);
		scan.next(); //Skip the starting character
		
		
		boolean whiteKingAlive = false;
		boolean blackKingAlive = false;
		
		//Parse board and update client
		for (int i = 0; i < Game.BOARD_WIDTH; i++)
		{
			for (int j = 0; j < Game.BOARD_HEIGHT; j++)
			{
				String full = scan.next();
				//System.out.println(Integer.parseInt(full.substring(0, full.indexOf("1") + 1)));
				
				//System.out.println(full);
				
				if (full.equals("N"))
				{
					game.getBoard()[i][j] = null;
					
					continue;
				}
				
				int player = Integer.parseInt(full.substring(0, full.indexOf("1") + 1));
				String piece = full.substring(full.indexOf("1") + 1);
				
				switch (piece)
				{
				
				case ("Chess_Server.Pieces.Pawn"):
					game.getBoard()[i][j] = new Pawn(i, j, player);
					break;
				
				case ("Chess_Server.Pieces.Rook"):
					game.getBoard()[i][j] = new Rook(i, j, player);
					break;
				
				case ("Chess_Server.Pieces.Bishop"):
					game.getBoard()[i][j] = new Bishop(i, j, player);
					break;
				
				case ("Chess_Server.Pieces.Knight"):
					game.getBoard()[i][j] = new Knight(i, j, player);
					break;
				
				case ("Chess_Server.Pieces.King"):
					game.getBoard()[i][j] = new King(i, j, player);
					break;
				
				case ("Chess_Server.Pieces.Queen"):
					game.getBoard()[i][j] = new Queen(i, j, player);
        		break;
				}
				
				//Check if king alive
		        if (game.getBoard()[i][j] instanceof King)
		        {
		        	if (game.getBoard()[i][j].getPlayer() == -1)
		        	{
		        		whiteKingAlive = true;
		        	}
		        	else
		        	{
		        		blackKingAlive = true;
		        	}
		        }
			}
		}
		
		scan.next(); // remove terminating char
		
		if (whiteKingAlive == false)
		{
			game.setWinner(1);
			stage = Stages.GameEnd;
			System.out.println("Ended");
		}
		else if (blackKingAlive == false)
		{
			game.setWinner(-1);
			stage = Stages.GameEnd;
			System.out.println("Ended");
		}
		
		
		if (game.getTurn() != Integer.parseInt(scan.next()))
		{
			game.nextTurn();
		}

		// A king has died
		if (stage == Stages.GameEnd)
		{
			process();
		}
		/*
		if (game.getUser() != game.getTurn())
		{
			game.nextTurn();
		}*/
		
		//System.out.println("Updated");
		//System.out.println("TURN: " + game.getTurn());
	}

	public Stages getStage()
	{
		return stage;
	}
}
