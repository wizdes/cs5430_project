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
CipherFactory.java
EncryptedObject.java
EncryptionKeys.java
GenerateAndWriteKeys.java
HMACMessage.java
KeyFactory.java
KeysObject.java	
SecureTransport.java
SecureTransportInterface.java


ii) machine authentication (package)
This package deals with authenticating machines by exchanging session keys using established public/private keys, as well as sending secure messages after the session key exchange.

Files:
MachineAuth.java
MachineAuthInterface.java
Msg01_AuthenticationRequest.java
Msg02_KeyResponse.java
Msg03_AuthenticationAgreement.java

b) Transport Layer

i) files (package)
This package handles writing to and reading from a file. The interface allows for the user to read and write either via text string or a byte array.

Files:
File_Handler.java - implements writing to and reading from a file. Uses java.io.* to access the file and input/output. 
File_Handler_Interface.java - provides an interface to read and write from a file.

ii) network (package)
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

c) Application Layer

i) messages (package)
There are several Message classes in this package. All derive from the base Message class and implement a different type of Message that is to be sent. 

Files:
Message.java - the base class of Message; it stores the message and allows for an easy interface to encrypt, decrypt and serialize into a network message
!!!^_^_^_^_^!!! Add the other files here (not sure if what others I haven't included)


ii) demo (package)

This package deals with the actual scaffolding of the program; it provides the GUI that allows a tester to observe the functionality of the cryptographic substrate.

Files:
Communication.java - 
CommunicationInterface.java - 
EncryptionDemoFunctionality.java - 
EncryptionDemoGUI.form - 
EncryptionDemoGUI.java - 

d) Testing 
i) Machine Authentication
ii) RSA Crypto
iii) Encryption
iv) Messages
v) Network 