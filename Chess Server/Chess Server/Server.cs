using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace Chess_Server
{
    class Server
    {
        private TcpListener tcpListener = new TcpListener(IPAddress.Any, 4000);

        private ArrayList gameInstances;

        public static void Main(string[] args)
        {
            Server server = new Server();
            server.StartServer();

            while (true) ;

        }

        public class GameInstance
        {
            public NetworkStream whitePlayer;
            public NetworkStream blackPlayer;

            public StreamWriter whitePlayerWriter;
            public StreamWriter blackPlayerWriter;

            public StreamReader whitePlayerReader;
            public StreamReader blackPlayerReader;

            public Game game;

            public bool running = true;

            public GameInstance()
            {
                whitePlayer = null;
                blackPlayer = null;
                game = null;
            }

            ~GameInstance()
            {
                whitePlayer.Close();
                blackPlayer.Close();
                whitePlayerWriter.Close();
                blackPlayerWriter.Close();
                whitePlayerReader.Close();
                blackPlayerReader.Close();
            }

            public async void startGame()
            {
                game = new Game();
                //running = true;

                whitePlayerWriter = new StreamWriter(whitePlayer);
                blackPlayerWriter = new StreamWriter(blackPlayer);

                Console.WriteLine("Game has been started!");

                //Notify clients that the game is starting and send them their color
                whitePlayerWriter.WriteLine("Starting... -1");
                whitePlayerWriter.Flush();

                blackPlayerWriter.WriteLine("Starting... 1");
                blackPlayerWriter.Flush();

                //Send the clients the starting board
                // sendMessage(encodeBoard());
                // sendMessage("Updated");

                //Get moves
                while (game.isOver() == 0)
                {
                    Console.WriteLine("Waiting for move...");
                    string move;

                    if (game.getTurn() == -1)
                    {
                        try
                        {
                            move = await whitePlayerReader.ReadLineAsync();
                            //move = whitePlayerReader.ReadLine();
                        }
                        catch (Exception e)
                        {
                            Console.WriteLine("Connection has ended for client");
                            break;
                        }
                    }
                    else
                    {
                        
                        try
                        {
                            move = await blackPlayerReader.ReadLineAsync();
                            //move = blackPlayerReader.ReadLine();
                        }
                        catch (Exception e)
                        {
                            Console.WriteLine("Connection has ended for client");
                            break;
                        }
                    }

                    if (move.Substring(0, 5).Equals("Move:"))
                    {
                        int[] data = move.Substring(6).Split(' ').Select(p => int.Parse(p)).ToArray();

                        if (game.makeMove(data[0], data[1], data[2], data[3]))
                        {
                            Console.WriteLine("Move from " + data[0] + data[1] + " to " + data[2] + data[3] + " has been made.");
                            sendMessage(encodeBoard());
                            sendMessage("Updated");
                        }
                    }
                }

                //Game is over
                this.running = false;
            }

            public void sendMessage(string message)
            {
                whitePlayerWriter.WriteLine(message);
                whitePlayerWriter.Flush();

                blackPlayerWriter.WriteLine(message);
                blackPlayerWriter.Flush();
            }

            public string encodeBoard()
            {
                string encodedBoard = "/S ";
                ChessPiece[][] board = this.game.getBoard();

                for (int i = 0; i < board.Length; i++)
                {
                    for (int j = 0; j < board[i].Length; j++)
                    {
                        if (board[i][j] != null)
                        {
                            encodedBoard += board[i][j].getPlayer() + board[i][j].GetType().FullName + " ";
                        }
                        else
                        {
                            encodedBoard += "N ";
                        }
                    }
                }

                return encodedBoard + "/T " + game.getTurn(); //Add terminating string
            }
        }

        public Server()
        {
            this.gameInstances = new ArrayList();
        }

        private bool StartServer()
        {
            try
            {
                tcpListener.Start();
                tcpListener.BeginAcceptTcpClient(new AsyncCallback(this.ProcessEvents), tcpListener);

                Console.WriteLine("Listing at Port 4000");
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
                return false;
            }

            return true;
        }

        private void ProcessEvents(IAsyncResult asyn)
        {
            try
            {

                TcpListener processListen = (TcpListener)asyn.AsyncState;
                TcpClient tcpClient = processListen.EndAcceptTcpClient(asyn);
                //TcpClient tcpClient = processListen.AcceptTcpClient();
                // tcpClient.NoDelay = true;
                NetworkStream myStream = tcpClient.GetStream();
                StreamWriter writer = new StreamWriter(myStream);
                StreamReader readerStream = null;
                if (myStream.CanRead)
                {
                    readerStream = new StreamReader(myStream);
                    string request = readerStream.ReadLine();

                    Console.WriteLine(request); // mine
                                                

                    writer.WriteLine("Connection Accepted!");
                    writer.Flush();

                }

                //Clean game list
                removeFinishedGames();
                /**************Game Logic w/ server**************/

                //Find an available game if possible
                if (!streamExists(ref myStream) && availableGame(ref myStream) != -1)
                {
                    int game = availableGame(ref myStream);

                    ((GameInstance)gameInstances[game]).blackPlayer = myStream;
                    ((GameInstance)gameInstances[game]).blackPlayerReader = readerStream;

                    if (((GameInstance)gameInstances[game]).blackPlayer != null && ((GameInstance)gameInstances[game]).whitePlayer != null)
                    {
                        ((GameInstance)gameInstances[game]).startGame();
                    }
                }
                //All games are full (or there are no games) so create a new lobby and wait for new players
                else if (!streamExists(ref myStream))
                {
                    int game = gameInstances.Add(new GameInstance());

                    ((GameInstance)gameInstances[game]).whitePlayer = myStream;
                    ((GameInstance)gameInstances[game]).whitePlayerReader = readerStream;
                }

                /**************************************/

                //myStream.Close();
                //tcpClient.Close();
                tcpListener.BeginAcceptTcpClient(new AsyncCallback(this.ProcessEvents), tcpListener); // Um what??? again??
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }
        }

        private Boolean streamExists(ref NetworkStream stream)
        {
            foreach (GameInstance n in gameInstances)
            {
                //Stream already exists
                if (n.whitePlayer != null && n.whitePlayer.Equals(stream))
                {
                    return true;
                }

                if (n.blackPlayer != null && n.blackPlayer.Equals(stream))
                {
                    return true;
                }
            }

            return false;
        }

        private int availableGame(ref NetworkStream stream)
        {
            //Already in a game
            if (streamExists(ref stream))
            {
                return -1;
            }

            for (int i = 0; i < gameInstances.Count; i++)
            {
                if (((GameInstance)gameInstances[i]).blackPlayer == null)
                {
                    return i;
                }
            }

            return -1;
        }

        private void removeFinishedGames()
        {
            foreach (GameInstance n in gameInstances)
            {
                if (n.running == false)
                {
                    gameInstances.Remove(n);
                }
            }
        }
    }
}
