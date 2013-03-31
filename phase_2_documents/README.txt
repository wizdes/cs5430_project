Patrick Berens(pcb73)
Matt Goggin(mg343)
Yi Li(yl2326)

README.txt which contains the names and a description of the contents for the other files in the directory.
Source files that contain the source needed to compile and run your software. This should include files comprising the cryptographic substrate that will later be used in your project scaffolding and GUI code used for the Demo you will give the graders to suggest that your system actually works as intended.

Table of Contents:
1) Introduction
2) Sections
	a) Security Layer
		i) encryption
		ii) machine authentication
	b) Transport Layer
		i) files
		ii) network
	c) Application Layer
		i) messages
		ii) demo
	d) Testing 
		i) Machine Authentication (network, messages, file encryption, machine authentication protocols)
		ii) Encryption (encryption)
3) Running our Scaffolding Code

1) Introduction:

In this phase, we implemented the cryptographic substrate to our colloborative text editor. We split the different parts of the substrate into seperate layers to encapsulate different parts of the implementation and abstracting away different parts of the code behind interfaces. 

2) Sections
There are 4 main layers and 7 main packages to implemented different parts of the cryptographic substrate. 

The Security Layer implements the security elements, including the encryption/decryption mechanisms, HMACs, machine authentication, secur transport, etc. 

The Transport Layer implements the underlying network elements and saving to files. 

The Application Layer has the message classes that the main application uses, as well the GUI code for our scaffolding.

The Testing Layer has all our unit test elements.  

a) Security Layer

i) security_layer (package)
This package deals mostly with the encryption, decryption and the secure transport of objects. It also generates and writes keys.

Files:
CipherFactory.java - a factory class that generates ciphers given a key
EncryptedObject.java - encapsulates objects and serializes them for network transfer
EncryptionKeys.java - a machine's set of keys used for communication
GenerateAndWriteKeys.java - a class that generates and write keys
HMACMessage.java - a class that contains the HMAC and a message
KeyFactory.java - a factory class that generates keys (optionally given a password)
KeysObject.java	- a class used to serializing and de-serializing keys to a file
SecureTransport.java - the main layer that implements the interface used by the application and the network layer
SecureTransportInterface.java - the interface used to securely transport messages between machines


ii) machine authentication (package)
This package deals with authenticating machines by exchanging session keys using established public/private keys, as well as sending secure messages after the session key exchange.

Files:
MachineAuth.java - the implementation of the machine authentication interface
MachineAuthInterface.java - the interface used to authenticate between machines used by the secure transport layer
Msg01_AuthenticationRequest.java - a class extending from class 'Message' that requests authentication of a machine
Msg02_KeyResponse.java - a class extending from class 'Message' that responses to the authentication request with a key
Msg03_AuthenticationAgreement.java - a class extending from class 'Message' that signals an agreement of authentication 

b) Transport Layer

i) files (package)
This package handles writing to and reading from a file. The interface allows for the user to read and write either via text string or a byte array.

Files:
File_Handler.java - implements writing to and reading from a file. Uses java.io.* to access the file and input/output. 
File_Handler_Interface.java - provides an interface to read and write from a file.

ii) network (package)
This package provides the network layer and implements send, receive and other network elements. 

Files:
Client.java - the class responsible for interacting in the system as a client
ClientListenerThread.java - a class that runs concurrently with the program to handle client requests from the network
NetworkTransport.java - the class that implements the network transport interface
NetworkTransportInterface.java - the main network transport layer that is used by 'SecureTransport' to communicate with other machines
Node.java - a class that represents a machine in a abstract topology
Pair.java - a class that represents a pair of values
Server.java - the class responsible for interacting in the system as a server
ServerThread.java - a class that runs concurrently with the program to handle server requests from the network

c) Application Layer

i) messages (package)
There are several Message classes in this package. All derive from the base Message class and implement a different type of Message that is to be sent. 

Files:
Message.java - the base class of Message; it stores the message and allows for an easy interface to encrypt, decrypt and serialize into a network message. These are also inherited by the messages in Machine Authentication


ii) demo (package)

This package deals with the actual scaffolding of the program; it provides the GUI that allows a tester to observe the functionality of the cryptographic substrate.

Files:
Communication.java - the class that implements the communication interface
CommunicationInterface.java - the interface that connects the GUI with the 'SecureTransport' interface
EncryptionDemoFunctionality.java - the class that implements the user interface in the GUI
EncryptionDemoGUI.form - part of the EncryptionDemoGUI
EncryptionDemoGUI.java - the GUI scaffolding code that demonstrates on encryption demo

d) Testing 
i) Machine Authentication (network, messages, file encryption, machine authentication protocols)
This package is used to test the entire stack of the program, including the network, messages, file encryption/decryption, and the machine authentication protocols

Files:
CommunicationInterfaceTest.java - the class that applies an integration test of all our systems in a JUnit format
CommunicationTest.java - the class that tests specific elements of communication, such as sending AES messages, sending RSA messages

ii) Encryption (encryption)
This package is used to test our various encryption and MAC schemes.

Files:
CipherFactoryAESTest.java - a class that test our implementation of AES Encryption
CipherFactoryHMACTest.java - a class that tests our implementation of HMACing messages
CipherFactoryRSATest.java - a class that tests our implementation of RSA public/private key encryption 

3) Running our Scaffolding Code
In order to run our code follow these steps (tested on Ubuntu):
- Change directory into the root of our code submission
- run "./keygen"
- run "./demo1.sh"
- (in a new terminal) run "./demo2.sh"

You should now have 2 GUI's open that you can interact with.
- Press "Update Properties" on each GUI
- On one of the GUI's press "Authorize Machine"
- On either GUI, select the "sent messages" tab, and type and send a message
  - it will appear in the other GUI's received messages tab.
- On either GUI, select the files tab, and open the text file in the root of our submission
  - (it only works on files in the root of our directory)
  - If you encrypt the file, you can go back to the OS and see the encrypted file.
  - Pressing decrypt will load it back into the editor
  - If you wish, you can encrypt the file, close down the application, and then 
    reopen it to decrypt it again (but remember you have to "Update Properties")
    every time you open the app.

    

