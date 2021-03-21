package Pieces;

public class King extends ChessPiece
{

	public King(int x, int y, int player)
	{
		super(x, y, player);
		this.whiteName = this.pathToAssets + "wk.png";
		this.blackName = this.pathToAssets + "bk.png";
	}

}