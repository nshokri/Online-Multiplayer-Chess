package Pieces;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public abstract class ChessPiece
{
	private int player; // Which player owns this piece (should be -1 or 1)

	private int x;
	private int y;
	
	protected String whiteName;
	protected String blackName;

	protected String pathToAssets = "src/Assets/";
	
	public ChessPiece(int x, int y, int player)
	{
		this.x = x;
		this.y = y;
		this.player = player;
	}
	
	public boolean isNotMyPiece(ChessPiece other)
	{
		//Can't kill own pieces
		if (other.player == this.player)
		{
			return false;
		}
		
		return true;
	}
	
	public Image draw()
	{
		String link = null;
		if (player == -1)
		{
			link = whiteName;
		}
		else
		{
			link = blackName;
		}
		
		Image img = null;
		
		try
		{
			img = ImageIO.read(new File(link));
		}
		catch (IOException e)
		{
			System.out.println("Can't find image");
			e.printStackTrace();
		}
		
		return img;
	}
	
	
	public int getPlayer()
	{
		return player;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}
	
	public void setX(int x)
	{
		this.x = x;
	}

	public void setY(int y)
	{
		this.y = y;
	}
}
