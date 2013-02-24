README.txt which contains the names and a description of the contents for the other files in the directory.
Source files that contain the source needed to compile and run your software. This should include files comprising the cryptographic substrate that will later be used in your project scaffolding and GUI code used for the Demo you will give the graders to suggest that your system actually works as intended.

Table of Contents:
1) Introduction
2) Sections
	a) File_Handler
	b) Keys
	c) Machine_Authentication
	d) Encryption
	e) Messages
	f) Network
	g) GUI Scaffolding
3) Testing 
	- Machine Authentication
	- RSA Crypto
	- Encryption
	- Messages
	- Network


1) Introduction:

In this phase, we implemented the cryptographic substrate to our colloborative text editor. We split the different parts of the substrate into seperate packages to encapsulate different parts of the implementation and abstracting away different parts of the code behind interfaces. 

2) Sections
There are 7 main packages to implemented different parts of the cryptographic substrate.

a) File_Handler

This package handles writing to and reading from a file. The interface allows for the user to read and write either via text string or a byte array.

Files:
File_Handler.java - implements writing to and reading from a file. Uses java.io.* to access the file and input/output. 
File_Handler_Interface.java - provides an interface to read and write from a file.


b) Keys

This package holds the class that contains all the information a particular will need to connect to others, namely the public key of the user's peers and the user's own private key.

Files:
Keys_Func.java - class that contains the data structure necessary for a user to connect to others


c) Machine_Authentication

This package provides an interface that allows two machines to authenticate with each other using hybrid authentication. This package also allows the machines to communicate with each other after the authentication using the shared public key.

Machine_Auth.java - implements all the authentication of one machine to another. This class exchanges a session key for a session use and creates an interface that allows a user to send messages to another.
Machine_Auth_Interface.java - This interface provides the functions that are called to authenticate a machine to another as well as sending secure messages.


d) Encryption

This package includes all the encryption/decryption algorithms for encryption/decryption of all communication from the program to other programs or to file.

Files:
AES.java - implements the functions that allow for the creation of AES keys given a password, as well as the encryption and decryption of messages under this encryption scheme
RSA_Crypto.java - implements the functions used to create a public/private RSA key pair, as well as encrypt via public key and decrypt via private key


e) Messages

There are several Message classes in this package. All derive from the base Message class and implement a different type of Message that is to be sent. 

Files:
Message.java - the base class of Message; it stores the message and allows for an easy interface to encrypt, decrypt and serialize into a network message
!!!^_^_^_^_^!!! Add the other files here (not sure if what others I haven't included)

f) Network

This package provides the network layer and implements send, receive and other network elements. !!!^_^_^_^_^_^!!!! MORE STUFF HERE

Files:
Client.java - 
ClientListenerThread.java - 
Network.java - 
NetworkInterface.java - 
Node.java -
Pair.java - 
Server.java - 
ServerThread.java - 


g) GUI Scaffolding

This package is used to provide the GUI scaffolding to demonstrate how our cryptographic layer works.

Files:
EncryptionDemoFunctionality.java - The functionality class that implements the actions specified by the GUI. 
EncryptionDemoGUI.form - Part of the GUI class.
EncryptionDemoGUI.java - The GUI class that draws the elements of the GUI.

3) Testing

a) Machine Authentication

This unit test package tests the machine authentication functionality.

Files: 
Machine_AuthMsgSend.java - Tests that messages after the initial hybrid encryption handshake is completed can be sent securely with the session key.
Machine_AuthTest.java - Tests the initial exchange of the session key works.


b) RSA Crypto

The unit test package tests the RSA public/private key encryption/decryption functionality. 

Files:
RSA_CryptoTest.java - This class encrypts a string with a public key, decrypts it and confirms that the string is correct.


c) Encryption

The unit test package tests the AES symmetric encryption/decryption functionality.

Files:
AESTest.java - THis class encrypts a string with an AES key and decrypts it, confirming the correctness of the string.


d) Messages

This unit test package tests the message classes and confirms their functionality.

Files:
MessageTest.java - Tests the Message class functionality.
DemoMessageTest.java - Tests the DemoMessage class functionality.
EncryptedMsgwNonceTest.java - Tests the EncryptedMsgwNonceTest class functionality.
AESEncryptedMessageTest.java - Tests the AESEncryptedMessageTest class functionality. 


e) Network

This unit test package tests the functionality of the Network class.

Files:
NetworkTest.java - A general test of the functionality of the network layer.
ServerTest.java - Tests the server class of the network layer.

