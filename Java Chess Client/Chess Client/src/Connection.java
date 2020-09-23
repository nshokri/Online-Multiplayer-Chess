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
	
	private Stages stage = Stages.Unconnected;
	
	private enum Stages
	{
		Unconnected, Connected, GameInprogress, GameEnd
	}
	
	public Connection(Game game)
	{
		this.game = game;

        try {
            socket = new Socket("127.0.0.1", 4000);
            writer = new PrintWriter(socket.getOutputStream(), true);
            
            writer.println("127.0.0.1 has connected!");

    		//Receive
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            /*String message = "";
            
            
            //Wait for connection to be accepted
            do
            {
            	message = reader.readLine();
            	if (message == null)
            	{
            		message = "";
            	}
            	else
            	{
            		System.out.println(message);
            	}
            }
            while (!message.equals("Connection Accepted!"));
           
        	
        	System.out.println("3");
        	
        	//Wait for game to start
        	do
            {
            	message = reader.readLine();
            	if (message == null)
            	{
            		message = "";
            	}
            	else
            	{
            		System.out.println(message);
            	}
            }
            while (!message.equals("Starting..."));

        	
        	//Get board and changes
        	do
            {
            	message = reader.readLine();
            	
            	if (message == null)
            	{
            		message = "";
            	}
            	else if (message.substring(0, 2).equals("/S"))
            	{
            		//System.out.println(message);
            		updateBoard(message, game);
            	}
            }
            while (!message.equals("Updated"));
        	*/
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
	}
	
	public void process()
	{
		String message = null;
		try
		{
			message = reader.readLine();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		//Stages
		if (stage == Stages.Unconnected)
		{
			if (message != null && message.equals("Connection Accepted!"))
			{
				System.out.println(message);
				stage = Stages.Connected;
			}
		}
		else if (stage == Stages.Connected)
		{
			if (message != null && message.substring(0, message.indexOf(" ")).equals("Starting..."))
			{
				System.out.println(Integer.parseInt(message.substring(message.indexOf(" ") + 1)));
				game.setUser(Integer.parseInt(message.substring(message.indexOf(" ") + 1)));
				stage = Stages.GameInprogress;
			}
		}
		else if (stage == Stages.GameInprogress)
		{
			//System.out.println(message.substring(0, 2));
			if (message != null && message.substring(0, 2).equals("/S"))
        	{
        		updateBoard(message, game);
        	}
		}
		else if (stage == Stages.GameEnd)
		{
			this.endConnection();
		}
	}
	
	public void sendMove(int oldX, int oldY, int newX, int newY)
	{
		writer.println("Move: " + oldX + " " + oldY + " " + newX + " " + newY);
		writer.flush();
	}
	
	public void endConnection()
	{
        try 
        {
        	System.out.println("Connection Closed!");
    		writer.close();
			socket.close();
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
		
		/*
		if (game.getUser() != game.getTurn())
		{
			game.nextTurn();
		}*/
		
		//System.out.println("Updated");
		//System.out.println("TURN: " + game.getTurn());
	}
}
