# Online-Multiplayer-Chess
A standard chess game that can be played online with others. The client utilizes sockets for the connection, JFrame and JComponent for graphics and Threads for handling listening to the server while updating the game window. The server handles all the main logic and final decisions on legal moves to prevent the client from exploiting the game. The client contains a copy of the same logic to assist the user when highlighting what moves are legal, but even if the client is tampered with to allow illegal moves it will not affect the real game on the server.

# Todo
Add a SQL Database to allow for user accounts, rank, and a match history.
Add a menu to the client that gives the user options to change settings, start  game, login (^), view match history (^).
Fix bugs

# Notes
The cleint is set to connect to localhost(or 127.0.0.1) at port 4000 and the server is set to be listening on port 4000. If you would like to use multiple devices to run this, please manually change these values on both the client ("Connection.java") and the server("Server.cs")
.
The auto generated files that come with Visual Studio C# projects have been provided so I recommend using Visual Studio for opening the project. If you choose not to use Visual Studio, only copy the (.cs) files and the "Pieces" folder into the C# project.

If you would like to contribute to this project, please feel free to contact me and I would be more than happy to accept any help.

# Bugs
Client very slow, sometimes unresponsive, due to event listeners using the same thread that is used to draw graphics.
Client very rarely allows an illegal move for the user to make (although the server catches this and does not allow it).
Server crashes if overflown with connection requests.
