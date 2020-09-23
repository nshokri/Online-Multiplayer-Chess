/*
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

public class ConnectionTest
{
	public static void main(String[] args) throws IOException
	{
		Socket socket;
		Game game = new Game();
		Chess chess = new Chess(800, 800, Chess.WHITE, game);

        try {
            socket = new Socket("127.0.0.1", 4000);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            
            writer.println("127.0.0.1 has connected!");

            System.out.println("1");

    		//Receive
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message = "";
            
            System.out.println("2");
            
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
        	
        	
        	
           writer.close();
           socket.close();
            
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
	}
	
	private static void updateBoard(String message, Game game)
	{
		Scanner scan = new Scanner(message);
		scan.next(); //Skip the starting character
		
		ChessPiece[][] newBoard = new ChessPiece[game.BOARD_WIDTH][game.BOARD_HEIGHT];
		
		
		//Parse board and update client
		for (int i = 0; i < Game.BOARD_WIDTH; i++)
		{
			for (int j = 0; j < Game.BOARD_HEIGHT; j++)
			{
				String full = scan.next();
				//System.out.println(Integer.parseInt(full.substring(0, full.indexOf("1") + 1)));
				
				System.out.println(full);
				
				if (full.equals("N"))
				{
					newBoard[i][j] = null;
					
					continue;
				}
				
				int player = Integer.parseInt(full.substring(0, full.indexOf("1") + 1));
				String piece = full.substring(full.indexOf("1") + 1);
				
				switch (piece)
				{
				
				case ("Chess_Server.Pieces.Pawn"):
					newBoard[i][j] = new Pawn(i, j, player);
					break;
				
				case ("Chess_Server.Pieces.Rook"):
					newBoard[i][j] = new Rook(i, j, player);
					break;
				
				case ("Chess_Server.Pieces.Bishop"):
					newBoard[i][j] = new Bishop(i, j, player);
					break;
				
				case ("Chess_Server.Pieces.Knight"):
					newBoard[i][j] = new Knight(i, j, player);
					break;
				
				case ("Chess_Server.Pieces.King"):
					newBoard[i][j] = new King(i, j, player);
					break;
				
				case ("Chess_Server.Pieces.Queen"):
					newBoard[i][j] = new Queen(i, j, player);
        		break;
				}
			}
		}
		
		ChessPiece[][] oldBoard = game.getBoard();
		oldBoard = newBoard;
	}
}*/
