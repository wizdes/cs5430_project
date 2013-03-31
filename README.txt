Patrick Berens(pcb73)
Matt Goggin(mg343)
Yi Li(yl2326)

Table of Contents
1) Running our code
2) Packages
  application
  configuration
  security_layer
  transport_layer

1) Running our code

In order to run our submitted code on a Linux OS, change directory into the root of this folder, and run the "run.sh" command.  This will start a single instance of the application.  Opening a new terminal to the same directory you may start a second instance of the application in a separate JVM.

Once you have two instances of the application running, you may proceed as follows to start a chat session:
- In each GUI, create a new account with a different numeric ID, e.g. "0" and "1", and with a different port.
- On one GUI, click "Start Chat", in the next window that appears, and give your chat session a name.
- In the other GUI, click "Discover", and you should see the table populated with an entry for the first GUI's chat session.
  - Click that row, and then "Join".
  - The first GUI will pop up a dialog box with a PIN
  - Type that PIN into the dialog box presented on the second GUI
  - After entering a correct PIN, a window appears where you can send and receive chats.


2) Packages
  
  What follows is a high level description of our source code files.  We have
  omitted files that did not change from the previous phase, and focused on the
  files that are pertinent to our Phase IV updates.

  application/encryption_demo
    All the files for running the actual application front end
  application/encryption_demo/forms
    Forms used for the application front end
  application/encryption_demo/Messages
    DiscoveryMessage.java
      Message to indicate to an owner another user has manually input their IP:port
    RequestDocUpdateMessage.java
      Sent from a user to a chat owner asking them to update the document
    UpdateDocumentMessage.java
      Broadcasted update from the owner of a chat to everyone else participating
    RequestJoinDocMessage.java
      Sent to an owner to request to join a chat.

  configuration
    Constants.java
      Collection of static constant fields
    
  security_layer
    All the files pertaining to human and machine authentication, sending AES
    and RSA encrypted messages.  Below are the new files which are pertinent
    to human authentication.
      Authentications.java
        Class manages and responds to in progress authentications, both human
        and machine
      EncryptedAESHumanAuthMessage.java
        Parent class for the AES encrypted messages used in human authentication
      HumanAuthenticationMessage.java
        Parent class for the plain text messages used in human authentication
      HA_Msg1.java
      HA_Msg2.java
      HA_Msg3.java   
        The three human authentication messages as exchanged in the human auth 
        protocol (see FUNCTIONALITY.txt)
      EncryptionKeys.java
        Class which manages a collection of keys, both for the user of the 
        application as well as other programs they are interacting with. This
        object is serialized and written to file with the user's profile

      PINFunctionality.java
        Generators and functionality related to PINs
      Profile.java
        The profile for a user, with their username, keys, etc. This is
        serialized and written to disk, encrypted with the user's password
    
  transport_layer
    discovery
      DiscoveryPacket.java
        A UDP broadcast sent out to discover owners hosting chats
      DiscoveryResponseMessage.java
        The response message owners return when discovered
      DiscoveryTransport.java
        Wraps MulticastClient and MulticastServer to abstract sending
        and receiving UDP broadcasts
      MulticastClient.java
        Listens for incoming UDP broadcasts
      MulticastServer.java    
        Broadcasts UDP packets
    files
      Files for reading/writing encrypted files from the OS
    network
      Files for sending and receiving TCP messages between app instances

  
