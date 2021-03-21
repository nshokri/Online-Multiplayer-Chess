using Chess_Server.Pieces;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Chess_Server
{
    public class Game
    {
        public const int BOARD_WIDTH = 8;
        public const int BOARD_HEIGHT = 8;

        private ChessPiece[][] board;
	    private int turn;

        private int winner = 0;

        public Game()
        {
            //TODO: Setup board
            board = new ChessPiece[BOARD_WIDTH][];

            for (int i = 0; i < BOARD_WIDTH; i++)
            {
                board[i] = new ChessPiece[BOARD_HEIGHT];
            }

            turn = -1;

            /*Generate board*/

            /**Pawns**/
            //Black
            for (int i = 0; i < BOARD_WIDTH; i++)
            {
                board[i][1] = new Pawn(i, 1, 1);
            }
            //White
            for (int i = 0; i < BOARD_WIDTH; i++)
            {
                board[i][6] = new Pawn(i, 6, -1);
            }


            /**Rooks**/
            //Black
            board[0][0] = new Rook(0, 0, 1);
            board[BOARD_WIDTH - 1][0] = new Rook(BOARD_WIDTH - 1, 0, 1);

            //White
            board[0][BOARD_HEIGHT - 1] = new Rook(0, BOARD_HEIGHT - 1, -1);
            board[BOARD_WIDTH - 1][BOARD_HEIGHT - 1] = new Rook(BOARD_WIDTH - 1, BOARD_HEIGHT - 1, -1);


            /**Knights**/
            //Black
            board[1][0] = new Knight(1, 0, 1);
            board[BOARD_WIDTH - 2][0] = new Knight(BOARD_WIDTH - 2, 0, 1);

            //White
            board[1][BOARD_HEIGHT - 1] = new Knight(1, BOARD_HEIGHT - 1, -1);
            board[BOARD_WIDTH - 2][BOARD_HEIGHT - 1] = new Knight(BOARD_WIDTH - 2, BOARD_HEIGHT - 1, -1);


            /**Bishops**/
            //Black
            board[2][0] = new Bishop(2, 0, 1);
            board[BOARD_WIDTH - 3][0] = new Bishop(BOARD_WIDTH - 3, 0, 1);

            //White
            board[2][BOARD_HEIGHT - 1] = new Bishop(2, BOARD_HEIGHT - 1, -1);
            board[BOARD_WIDTH - 3][BOARD_HEIGHT - 1] = new Bishop(BOARD_WIDTH - 3, BOARD_HEIGHT - 1, -1);


            /**Kings**/
            //Black
            board[3][0] = new King(3, 0, 1);

            //White
            board[3][BOARD_HEIGHT - 1] = new King(3, BOARD_HEIGHT - 1, -1);

            /**Queens**/
            //Black
            board[4][0] = new Queen(4, 0, 1);

            //White
            board[4][BOARD_HEIGHT - 1] = new Queen(4, BOARD_HEIGHT - 1, -1);

        }


        public bool makeMove(int x, int y, int newX, int newY)
        {

            if (x >= BOARD_WIDTH || y >= BOARD_HEIGHT || x < 0 ||
                    y < 0 || board[x][y].getPlayer() != turn)
            {
                //TODO: Display some sort of error message
                return false;
            }

            ArrayList validMoves = getAllValidMoves(x, y);

            foreach (ChessPiece n in validMoves)
            {
                if (n.getX() == newX && n.getY() == newY)
                {
                    //Check if king dead
                    if (board[newX][newY] != null && board[newX][newY].GetType() == typeof(King))
                    {
                        winner = board[newX][newY].getPlayer() * -1;
                        Console.WriteLine("Game has been won by player " + winner);
                    }

                    //Make move
                    board[newX][newY] = board[x][y];
                    board[x][y] = null;

                    board[newX][newY].setX(newX);
                    board[newX][newY].setY(newY);

                    nextTurn();
                    return true;
                }
            }

            return false;
        }

        public ChessPiece[][] getBoard()
        {
            return board;
        }


        /**********************getAllValidMoves************************/
        public ArrayList getAllValidMoves(int x, int y)
        {
            ArrayList moves = new ArrayList();

            if (board[x][y] == null)
            {
                return moves;
            }

            /**Pawn**/
            if (board[x][y] is Pawn && board[x][y].getPlayer() == turn)
		{
                //One forward is clear
                if (((turn == 1 && y + 1 < BOARD_HEIGHT) || (turn == -1 && y - 1 >= 0))

                        && board[x][y + turn * 1] == null
                        && board[x][y].getPlayer() == turn)
                {
                    moves.Add(new Pawn(x, y + turn * 1, turn));

                    //Double step
                    if (((turn == 1 && y + 2 < BOARD_HEIGHT) || (turn == -1 && y - 2 >= 0))

                            && board[x][y + turn * 2] == null
                            && board[x][y].getPlayer() == turn
                            && ((turn == 1 && y == 1) || (turn == -1 && y == BOARD_HEIGHT - 2)))
                    {
                        moves.Add(new Pawn(x, y + turn * 2, turn));
                    }
                }


                //Can kill diagonal right
                if (((turn == 1 && y + 1 < BOARD_HEIGHT && x + 1 < BOARD_WIDTH)
                        || (turn == -1 && y - 1 >= 0 && x + 1 < BOARD_WIDTH))

                        && board[x + 1][y + turn] != null
                        && board[x + 1][y + turn].getPlayer() != turn)
                {

                    moves.Add(board[x + 1][y + turn]);
                }

                //Can kill diagonal left
                if (((turn == 1 && (y + 1 < BOARD_HEIGHT) && (x - 1 >= 0))
                    || (turn == -1 && y - 1 >= 0 && x - 1 >= 0))

                        && board[x - 1][y + turn] != null
                        && board[x - 1][y + turn].getPlayer() != turn)
                {
                    moves.Add(board[x - 1][y + turn]);
                }
            }

            /**Rook**/
            if (board[x][y] is Rook && board[x][y].getPlayer() == turn)
		{
                //Up movement
                for (int i = 1; i < BOARD_HEIGHT; i++)
                {
                    if (y + i < BOARD_HEIGHT && board[x][y + i] == null)
                    {
                        moves.Add(new Rook(x, y + i, turn));
                    }
                    else if (y + i >= BOARD_HEIGHT)
                    {
                        break;
                    }
                    else if (board[x][y + i].getPlayer() != turn)
                    {
                        moves.Add(new Rook(x, y + i, turn));
                        break;
                    }
                    else
                    {
                        break;
                    }
                }

                //Down movement
                for (int i = 1; i < BOARD_HEIGHT; i++)
                {
                    if (y - i >= 0 && board[x][y - i] == null)
                    {
                        moves.Add(new Rook(x, y - i, turn));
                    }
                    else if (y - i < 0)
                    {
                        break;
                    }
                    else if (board[x][y - i].getPlayer() != turn)
                    {
                        moves.Add(new Rook(x, y - i, turn));
                        break;
                    }
                    else
                    {
                        break;
                    }
                }

                //Right movement
                for (int i = 1; i < BOARD_WIDTH; i++)
                {
                    if (x + i < BOARD_WIDTH && board[x + i][y] == null)
                    {
                        moves.Add(new Rook(x + i, y, turn));
                    }
                    else if (x + i >= BOARD_WIDTH)
                    {
                        break;
                    }
                    else if (board[x + i][y].getPlayer() != turn)
                    {
                        moves.Add(new Rook(x + i, y, turn));
                        break;
                    }
                    else
                    {
                        break;
                    }
                }

                //Left movement
                for (int i = 1; i < BOARD_WIDTH; i++)
                {
                    if (x - i >= 0 && board[x - i][y] == null)
                    {
                        moves.Add(new Rook(x - i, y, turn));
                    }
                    else if (x - i < 0)
                    {
                        break;
                    }
                    else if (board[x - i][y].getPlayer() != turn)
                    {
                        moves.Add(new Rook(x - i, y, turn));
                        break;
                    }
                    else
                    {
                        break;
                    }
                }
            }

            /**Knight**/
            if (board[x][y] is Knight && board[x][y].getPlayer() == turn)
		{
                //Up-right direction
                if (x + 1 < BOARD_WIDTH && y + 2 < BOARD_HEIGHT)
                {
                    if (board[x + 1][y + 2] == null || board[x + 1][y + 2].getPlayer() != turn)
                    {
                        moves.Add(new Knight(x + 1, y + 2, turn));
                    }
                }

                //Up-left direction
                if (x - 1 >= 0 && y + 2 < BOARD_HEIGHT)
                {
                    if (board[x - 1][y + 2] == null || board[x - 1][y + 2].getPlayer() != turn)
                    {
                        moves.Add(new Knight(x - 1, y + 2, turn));
                    }
                }

                //Down-right direction
                if (x + 1 < BOARD_WIDTH && y - 2 >= 0)
                {
                    if (board[x + 1][y - 2] == null || board[x + 1][y - 2].getPlayer() != turn)
                    {
                        moves.Add(new Knight(x + 1, y - 2, turn));
                    }
                }

                //Down-left direction
                if (x - 1 >= 0 && y - 2 >= 0)
                {
                    if (board[x - 1][y - 2] == null || board[x - 1][y - 2].getPlayer() != turn)
                    {
                        moves.Add(new Knight(x - 1, y - 2, turn));
                    }
                }

                //Left-up direction
                if (x - 2 >= 0 && y + 1 < BOARD_HEIGHT)
                {
                    if (board[x - 2][y + 1] == null || board[x - 2][y + 1].getPlayer() != turn)
                    {
                        moves.Add(new Knight(x - 2, y + 1, turn));
                    }
                }

                //Left-Down direction
                if (x - 2 >= 0 && y - 1 >= 0)
                {
                    if (board[x - 2][y - 1] == null || board[x - 2][y - 1].getPlayer() != turn)
                    {
                        moves.Add(new Knight(x - 2, y - 1, turn));
                    }
                }

                //Right-up direction
                if (x + 2 < BOARD_WIDTH && y + 1 < BOARD_HEIGHT)
                {
                    if (board[x + 2][y + 1] == null || board[x + 2][y + 1].getPlayer() != turn)
                    {
                        moves.Add(new Knight(x + 2, y + 1, turn));
                    }
                }

                //Right-down direction
                if (x + 2 < BOARD_WIDTH && y - 1 >= 0)
                {
                    if (board[x + 2][y - 1] == null || board[x + 2][y - 1].getPlayer() != turn)
                    {
                        moves.Add(new Knight(x + 2, y - 1, turn));
                    }
                }
            }

            /**Bishop**/
            if (board[x][y] is Bishop && board[x][y].getPlayer() == turn)
		{
                //Up-right direction
                for (int i = 1; i < BOARD_WIDTH; i++)
                {
                    if (x + i < BOARD_WIDTH && y - i >= 0)
                    {
                        if (board[x + i][y - i] == null || board[x + i][y - i].getPlayer() != turn)
                        {
                            moves.Add(new Bishop(x + i, y - i, turn));

                            if (board[x + i][y - i] != null && board[x + i][y - i].getPlayer() != turn)
                            {
                                break;
                            }
                        }
                        else
                        {
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }

                //Up-left direction
                for (int i = 1; i < BOARD_WIDTH; i++)
                {
                    if (x - i >= 0 && y - i >= 0)
                    {
                        if (board[x - i][y - i] == null || board[x - i][y - i].getPlayer() != turn)
                        {
                            moves.Add(new Bishop(x - i, y - i, turn));

                            if (board[x - i][y - i] != null && board[x - i][y - i].getPlayer() != turn)
                            {
                                break;
                            }
                        }
                        else
                        {
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }

                //Down-right direction
                for (int i = 1; i < BOARD_WIDTH; i++)
                {
                    if (x + i < BOARD_WIDTH && y + i < BOARD_HEIGHT)
                    {
                        if (board[x + i][y + i] == null || board[x + i][y + i].getPlayer() != turn)
                        {
                            moves.Add(new Bishop(x + i, y + i, turn));

                            if (board[x + i][y + i] != null && board[x + i][y + i].getPlayer() != turn)
                            {
                                break;
                            }
                        }
                        else
                        {
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }

                //Down-left direction
                for (int i = 1; i < BOARD_WIDTH; i++)
                {
                    if (x - i >= 0 && y + i < BOARD_HEIGHT)
                    {
                        if (board[x - i][y + i] == null || board[x - i][y + i].getPlayer() != turn)
                        {
                            moves.Add(new Bishop(x - i, y + i, turn));

                            if (board[x - i][y + i] != null && board[x - i][y + i].getPlayer() != turn)
                            {
                                break;
                            }
                        }
                        else
                        {
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }
            }

            /**Queen**/
            if (board[x][y] is Queen && board[x][y].getPlayer() == turn)
		    {
                /**BISHOP MOVEMENT**/
                //Up-right direction
                for (int i = 1; i < BOARD_WIDTH; i++)
                {
                    if (x + i < BOARD_WIDTH && y - i >= 0)
                    {
                        if (board[x + i][y - i] == null || board[x + i][y - i].getPlayer() != turn)
                        {
                            moves.Add(new Queen(x + i, y - i, turn));

                            if (board[x + i][y - i] != null && board[x + i][y - i].getPlayer() != turn)
                            {
                                break;
                            }
                        }
                        else
                        {
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }

                //Up-left direction
                for (int i = 1; i < BOARD_WIDTH; i++)
                {
                    if (x - i >= 0 && y - i >= 0)
                    {
                        if (board[x - i][y - i] == null || board[x - i][y - i].getPlayer() != turn)
                        {
                            moves.Add(new Queen(x - i, y - i, turn));

                            if (board[x - i][y - i] != null && board[x - i][y - i].getPlayer() != turn)
                            {
                                break;
                            }
                        }
                        else
                        {
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }

                //Down-right direction
                for (int i = 1; i < BOARD_WIDTH; i++)
                {
                    if (x + i < BOARD_WIDTH && y + i < BOARD_HEIGHT)
                    {
                        if (board[x + i][y + i] == null || board[x + i][y + i].getPlayer() != turn)
                        {
                            moves.Add(new Queen(x + i, y + i, turn));

                            if (board[x + i][y + i] != null && board[x + i][y + i].getPlayer() != turn)
                            {
                                break;
                            }
                        }
                        else
                        {
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }

                //Down-left direction
                for (int i = 1; i < BOARD_WIDTH; i++)
                {
                    if (x - i >= 0 && y + i < BOARD_HEIGHT)
                    {
                        if (board[x - i][y + i] == null || board[x - i][y + i].getPlayer() != turn)
                        {
                            moves.Add(new Queen(x - i, y + i, turn));

                            if (board[x - i][y + i] != null && board[x - i][y + i].getPlayer() != turn)
                            {
                                break;
                            }
                        }
                        else
                        {
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }

                /**ROOK MOVEMENT**/
                //Up movement
                for (int i = 1; i < BOARD_HEIGHT; i++)
                {
                    if (y + i < BOARD_HEIGHT && board[x][y + i] == null)
                    {
                        moves.Add(new Queen(x, y + i, turn));
                    }
                    else if (y + i >= BOARD_HEIGHT)
                    {
                        break;
                    }
                    else if (board[x][y + i].getPlayer() != turn)
                    {
                        moves.Add(new Queen(x, y + i, turn));
                        break;
                    }
                    else
                    {
                        break;
                    }
                }

                //Down movement
                for (int i = 1; i < BOARD_HEIGHT; i++)
                {
                    if (y - i >= 0 && board[x][y - i] == null)
                    {
                        moves.Add(new Queen(x, y - i, turn));
                    }
                    else if (y - i < 0)
                    {
                        break;
                    }
                    else if (board[x][y - i].getPlayer() != turn)
                    {
                        moves.Add(new Queen(x, y - i, turn));
                        break;
                    }
                    else
                    {
                        break;
                    }
                }

                //Right movement
                for (int i = 1; i < BOARD_WIDTH; i++)
                {
                    if (x + i < BOARD_WIDTH && board[x + i][y] == null)
                    {
                        moves.Add(new Queen(x + i, y, turn));
                    }
                    else if (x + i >= BOARD_WIDTH)
                    {
                        break;
                    }
                    else if (board[x + i][y].getPlayer() != turn)
                    {
                        moves.Add(new Queen(x + i, y, turn));
                        break;
                    }
                    else
                    {
                        break;
                    }
                }

                //Left movement
                for (int i = 1; i < BOARD_WIDTH; i++)
                {
                    if (x - i >= 0 && board[x - i][y] == null)
                    {
                        moves.Add(new Queen(x - i, y, turn));
                    }
                    else if (x - i < 0)
                    {
                        break;
                    }
                    else if (board[x - i][y].getPlayer() != turn)
                    {
                        moves.Add(new Queen(x - i, y, turn));
                        break;
                    }
                    else
                    {
                        break;
                    }
                }
            }

            /**King**/
            if (board[x][y] is King && board[x][y].getPlayer() == turn)
		{
                /**BISHOP MOVEMENT**/
                //Up-right direction

                if (x + 1 < BOARD_WIDTH && y - 1 >= 0)
                {
                    if (board[x + 1][y - 1] == null || board[x + 1][y - 1].getPlayer() != turn)
                    {
                        moves.Add(new King(x + 1, y - 1, turn));
                    }
                }


                //Up-left direction
                if (x - 1 >= 0 && y - 1 >= 0)
                {
                    if (board[x - 1][y - 1] == null || board[x - 1][y - 1].getPlayer() != turn)
                    {
                        moves.Add(new King(x - 1, y - 1, turn));
                    }
                }

                //Down-right direction
                if (x + 1 < BOARD_WIDTH && y + 1 < BOARD_HEIGHT)
                {
                    if (board[x + 1][y + 1] == null || board[x + 1][y + 1].getPlayer() != turn)
                    {
                        moves.Add(new King(x + 1, y + 1, turn));
                    }
                }

                //Down-left direction
                if (x - 1 >= 0 && y + 1 < BOARD_HEIGHT)
                {
                    if (board[x - 1][y + 1] == null || board[x - 1][y + 1].getPlayer() != turn)
                    {
                        moves.Add(new King(x - 1, y + 1, turn));
                    }
                }

                /**ROOK MOVEMENT**/
                //Up movement
                if (y + 1 < BOARD_HEIGHT && (board[x][y + 1] == null || board[x][y + 1].getPlayer() != turn))
                {
                    moves.Add(new King(x, y + 1, turn));
                }

                //Down movement
                if (y - 1 >= 0 && (board[x][y - 1] == null || board[x][y - 1].getPlayer() != turn))
                {
                    moves.Add(new King(x, y - 1, turn));
                }


                //Right movement
                if (x + 1 < BOARD_WIDTH && (board[x + 1][y] == null || board[x + 1][y].getPlayer() != turn))
                {
                    moves.Add(new King(x + 1, y, turn));
                }

                //Left movement
                if (x - 1 >= 0 && (board[x - 1][y] == null || board[x - 1][y].getPlayer() != turn))
                {
                    moves.Add(new King(x - 1, y, turn));
                }

            }

            return moves;
        }

        private void nextTurn()
        {
            if (turn == 1)
            {
                turn = -1;
            }
            else if (turn == -1)
            {
                turn = 1;
            }
        }

        public int getTurn()
        {
            return turn;
        }

        public int isOver()
        {
 
            return winner;
        }
    }
}
