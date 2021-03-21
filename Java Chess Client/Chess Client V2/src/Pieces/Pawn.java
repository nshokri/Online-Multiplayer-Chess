package Pieces;

public class Pawn extends ChessPiece
{

	public Pawn(int x, int y, int player)
	{
		super(x, y, player);
		this.whiteName = this.pathToAssets + "wp.png";
		this.blackName = this.pathToAssets + "bp.png";
	}

}
