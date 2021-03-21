package Pieces;

public class Rook extends ChessPiece
{

	public Rook(int x, int y, int player)
	{
		super(x, y, player);
		this.whiteName = this.pathToAssets + "wr.png";
		this.blackName = this.pathToAssets + "br.png";
	}

}
