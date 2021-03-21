package Pieces;

public class Queen extends ChessPiece
{

	public Queen(int x, int y, int player)
	{
		super(x, y, player);
		this.whiteName = this.pathToAssets + "wq.png";
		this.blackName = this.pathToAssets + "bq.png";
	}

}
