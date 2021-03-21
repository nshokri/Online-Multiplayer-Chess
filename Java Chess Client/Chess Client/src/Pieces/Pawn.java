package Pieces;

public class Pawn extends ChessPiece
{

	public Pawn(int x, int y, int player)
	{
		super(x, y, player);
		this.whiteName = "wp.png";
		this.blackName = "bp.png";
	}

}
