using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Chess_Server.Pieces
{
    public abstract class ChessPiece
    {
        protected int player; //Which player owns this piece (should be -1 or 1)

        protected int x;
        protected int y;

        public ChessPiece(int x, int y, int player)
        {
            this.x = x;
            this.y = y;
            this.player = player;
        }

        public Boolean isNotMyPiece(ChessPiece other)
        {
            //Can't kill own pieces
            if (other.player == this.player)
            {
                return false;
            }

            return true;
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
}
