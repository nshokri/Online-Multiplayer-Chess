using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Runtime.CompilerServices;
using System.Text;
using System.Threading.Tasks;
using Chess_Server.Pieces;

namespace Chess_Server
{
    class Server
    {
        private TcpListener tcpListener = new TcpListener(IPAddress.Any, 4000);

        private static ArrayList gameInstances;

        public static void Main(string[] args)
        {
            Server server = new Server();
            server.StartServer();

        }

        private class GameInstance
        {
            public NetworkStream whitePlayer;
            public NetworkStream blackPlayer;

            public StreamWriter whitePlayerWriter;
            public StreamWriter blackPlayerWriter;

            public StreamReader whitePlayerReader;
            public StreamReader blackPlayerReader;

            public Game game;

            public int gameID;

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

                gameInstances.Remove(gameID);
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
                            Console.WriteLine("Connection has ended for white client");
                            sendMessage("/D");
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
                            Console.WriteLine("Connection has ended for black client");
                            sendMessage("/D");
                            break; //todo: let the client know connection has been disconnected
                        }
                    }

                    if (move.Substring(0, 5).Equals("Move:"))
                    {
                        int[] data = move.Substring(6).Split(' ').Select(p => int.Parse(p)).ToArray();

                        if (game.makeMove(data[0], data[1], data[2], data[3]))
                        {
                            //Console.WriteLine("Move from " + data[0] + data[1] + " to " + data[2] + data[3] + " has been made.");
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
                // Write the white player stream
                try
                {
                    whitePlayerWriter.WriteLine(message);
                    whitePlayerWriter.Flush();
                }
                catch (Exception e)
                {
                    Console.WriteLine("Error: White player disconnected.");
                }

                // Write the black player stream
                try
                {

                    blackPlayerWriter.WriteLine(message);
                    blackPlayerWriter.Flush();
                }
                catch (Exception e)
                {
                    Console.WriteLine("Error: Black player disconnected.");
                }
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

            /*
            public void setWhitePlayerStream(ref NetworkStream whitePlayer)
            {
                this.whitePlayer = whitePlayer;
                //this.whitePlayerWriter = new StreamWriter(whitePlayer);
            }

            public void setBlackPlayerStream(ref NetworkStream blackPlayer)
            {
                this.blackPlayer = blackPlayer;
                //this.blackPlayerWriter = new StreamWriter(blackPlayer);
            }

            public ref NetworkStream getWhitePlayerStream()
            {
                return ref whitePlayer;
            }
            public ref NetworkStream getBlackPlayerStream()
            {
                return ref blackPlayer;
            }*/
        }

        public Server()
        {
            gameInstances = new ArrayList();
        }

        private bool StartServer()
        {
            try
            {
                // Wait for incoming connections on its own thread and create a new thread to handle each accepted connection
                tcpListener.Start();
                Console.WriteLine("Listing at Port 4000");

                while (true)
                {
                    if (tcpListener.Pending())
                    {
                        tcpListener.BeginAcceptTcpClient(new AsyncCallback(this.ProcessEvents), tcpListener);
                    }
                }

            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
                return false;
            }

            return true;
        }

        // ProcessEvents() - Thread function that will be run each time a new connection is accepted
        // Preconditions: N/A
        // Postconditions: Adds client to the game queue
        private void ProcessEvents(IAsyncResult asyn)
        {
            try
            {

                TcpListener processListen = (TcpListener)asyn.AsyncState;
                TcpClient tcpClient = processListen.EndAcceptTcpClient(asyn);
                //TcpClient tcpClient = processListen.AcceptTcpClient();
                // tcpClient.NoDelay = true;

                // The reader and writer streams are what we will work with to communicate to and from the socket
                NetworkStream myStream = tcpClient.GetStream();
                StreamWriter writer = new StreamWriter(myStream);
                StreamReader readerStream = null;

                
                if (myStream.CanRead)
                {
                    readerStream = new StreamReader(myStream);
                    string request = readerStream.ReadLine();

                    // Print the clients message (will be their host name)
                    Console.WriteLine(request); // mine
                                                
                    // Send an acknowledgement to the client that their connection was accepted
                    writer.WriteLine("Connection Accepted!");
                    writer.Flush();

                }


                /**************Game Logic w/ server**************/

                //Find an available game if possible
                int gameID = availableGame(ref myStream);

                if (!streamExists(ref myStream) && gameID != -1)
                {
                    GameInstance game = ((GameInstance)gameInstances[gameID]);
                    game.gameID = gameID;
                    game.blackPlayer = myStream;
                    //((GameInstance)gameInstances[game]).blackPlayer = myStream;
                    game.blackPlayerReader = readerStream;

                    // Sanity check to make sure a client did not disconnect
                    if (game.blackPlayer != null && game.whitePlayer != null)
                    {

                        game.startGame();
                    }
                }
                //All games are full (or there are no games) so create a new lobby and wait for new players
                else
                {
                    int newGame = gameInstances.Add(new GameInstance());

                    ((GameInstance)gameInstances[newGame]).whitePlayer = myStream;
                    //((GameInstance)gameInstances[newGame]).whitePlayer = myStream;
                    ((GameInstance)gameInstances[newGame]).whitePlayerReader = readerStream;
                }

                /**************************************/

                //myStream.Close();
                //tcpClient.Close();
                //tcpListener.BeginAcceptTcpClient(new AsyncCallback(this.ProcessEvents), tcpListener); // Um what??? again??
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }
        }

        // streamExists() - Checks to see if this client is already in a game (mostly a sanity check)
        // Preconditions: N/A
        // Postconditions: Returns whether the given stream is in a currently ongoing game
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

        // availableGame()
        // Preconditions: N/A
        // Postconditions: Returns whether there is a spot open in any of the games
        [MethodImpl(MethodImplOptions.Synchronized)]
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

        // removeFinishedGames() - DEPRECATED
        // Preconditions: N/A
        // Postconditions: Removes any game instances that are done
        [MethodImpl(MethodImplOptions.Synchronized)]
        private void removeFinishedGames()
        {
            Console.WriteLine(gameInstances.Count);
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
