package Pieces;

public class Knight extends ChessPiece
{
	public Knight(int x, int y, int player)
	{
		super(x, y, player);
		this.whiteName = this.pathToAssets + "wn.png";
		this.blackName = this.pathToAssets + "bn.png";
	}

}
