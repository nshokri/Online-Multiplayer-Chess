package Pieces;

public class Bishop extends ChessPiece
{

	public Bishop(int x, int y, int player) 
	{
		super(x, y, player);
		this.whiteName = this.pathToAssets + "wb.png";
		this.blackName = this.pathToAssets + "bb.png";
	}

}
