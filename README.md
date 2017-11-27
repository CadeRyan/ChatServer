# Chat Server CS4400
Chat Server written in Java for CS4400 Internet Applications, 4th year, Trinity College Dublin, by Cade Ryan

## Project Spec
Made to handle multiple clients concurrently, handling each request and sending the appropriate responses  
It is possible to: 
- Become a recognised client of the server
- Join a chatroom using a given chatroom name 
- Leave any chatroom you are a member of  
- Send chat messages to every member of a chatroom you are in
- Disconnect entirely as a client of the server  
- Kill the service completely  
___________________________________________________________________________

### Joining a chatroom  
In order for a client to join a chatroom, the following can be sent:
>JOIN_CHATROOM: [chatroom name]  
>CLIENT_IP: [IP Address of client if UDP | 0 if TCP]  
>PORT: [port number of client if UDP | 0 if TCP]  
>CLIENT_NAME: [string Handle to identifier client user]  

Ther Server will creat this client if they did not previously exist, then create this chatroom (with the client as a member) if did not previously exist. If it
did exists then the client will be added to the list of people in this chatroom and connect them through the chatroom. The following response will be return to 
the cient as a recognition of their successful attempt to join the server: 
>JOINED_CHATROOM: [chatroom name]  
>SERVER_IP: [IP address of chat room]  
>PORT: [port number of chat room]  
>ROOM_REF: [integer that uniquely identifies chat room on server]  
>JOIN_ID: [integer that uniquely identifies client joining]  

The server will then also post a chat message in the chatroom notifying every member of the chatroom that this client has joined the chatroom.
___________________________________________________________________________
  
### Leaving a chatroom  
If a client would like to leave a chatroom they are in, they may send the following:
>LEAVE_CHATROOM: [ROOM_REF]  
>JOIN_ID: [integer previously provided by server on join]  
>CLIENT_NAME: [string Handle to identifier client user] 

The server will then send the client a receipt of having left the chatroom, and will also post into the chatroom that the client has now left.
___________________________________________________________________________

### Chat Messages
Clients may post messages into a chatroom at any time using the following structure:
>CHAT: [ROOM_REF]  
>JOIN_ID: [integer identifying client to server]  
>CLIENT_NAME: [string identifying client user]  
>MESSAGE: [string terminated with '\n\n']  

A chat message of the following format then gets sent into the chatroom by the Server:
>CHAT: [ROOM_REF]  
>CLIENT_NAME: [string identifying client user]  
>MESSAGE: [string terminated with '\n\n']  
___________________________________________________________________________

### Disconnecting  
Clients may disconnect entirely using the following message to the Server:  
>DISCONNECT: [IP address of client if UDP | 0 if TCP]  
>PORT: [port number of client it UDP | 0 id TCP]  
>CLIENT_NAME: [string handle to identify client user]  
___________________________________________________________________________

### Kill Service
The Server can be shut down completely by any Client at any time using the following message:  
>KILL_SERVICE  
___________________________________________________________________________

to run this code using command prompt, type compile.sh to compile the program, 
followed by start.sh #portNumber to run it

