Patrick Berens(pcb73)
Matt Goggin(mg343)
Yi Li(yl2326)

README.txt which contains the names and a description of the contents for the other files in the directory.

Source files that contain the source needed to compile and run your software. This should include any scaffolding used in the Demo you will give. We will look over excerpts of your code to see whether your project is based on clean design and was implemented using good software engineering practices (including secure coding practices). Projects containing code vulnerabilities will be severely penalized.
Table of Contents

1) Running our code
2) Packages
  application
    encryption_demo
      forms
      messages
  configuration
    document
  security_layer
    authentications
  transport_layer
    discovery
    files
    network

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
  
  What follows is a high level description of our source code structure, including
  a brief description of each package.  For further detail, consult the comments
  found alongside the source code

  application/encryption_demo
    All the files for running the application front end

  application/encryption_demo/forms
    Forms used for the application front end

  application/encryption_demo/messages
    Application specific messages exchanged over the network

  configuration
    A collection of constants and settings used through the application
    
  document
    The logic of the actual document objects
    Document.java
      The lowest level Document API; offers simple inserts, removes, and assigns levels
      to sections of the document
    AuthorizationTransport
      Maintains an instance of a Document, and also manages a list of users who are authorized
      for which levels of access.  This is what is serialized (encrypted) to the disk when 
      a file is saved.
    NetworkDocumentHandler
      Manages an instance of an AuthorizationTransport, as well as a collection of
      peers who are participating in a session.  It is responsible for sending
      messages across the network to continue to update everyone's documents

  security_layer
    Files responsible for securely transporting data, both over the network and
    to and from disk
    
  security_layer/authentications
    Code to support the Secure Remote Password protocol implementation we use to establish
    secure connections between document editors.

  transport_layer
    discovery
      Files responsible for discovering other potential collaborators listening 
      on the network.
    files
      Files for reading/writing encrypted files from the OS
    network
      Files for sending and receiving TCP messages between app instances


Manual Testing Script

1. Start Owner - Enter user configuration information
	Precondition: “Configuration” window opens
-Input: Username: 0, IP: localhost, port: 6000
Postcondition: “Secure Document Viewer” window opens. Should have a tab that show peers, and a series of buttons on the bottom.

2. Start Client - Enter client configuration information
	Precondition: “Configuration” window opens.
	Input: Username: 1, IP: localhost, port: 6001
Postcondition:  “Secure Document Viewer” window opens. Should have a tab that show peers, and a series of buttons on the bottom.

[Notice: Window title contain username]

3. Owner “0” - Starting a document
	Click “Start Document” button(lower right)
	Dialog opens prompting for a document name
		-Input: Doc1, Click “Ok” button.
	    Dialog opens asking if you would like to use default levels.
    -Input: Click “Yes” button.
  Postcondition: Document editing window appears.[Notice it is in its own tab].

4. Imagine the client stops by the owner’s office as says: Hi, I am “1” and I would like to collaborate with you.
	Owner: Click “Generate PIN” button(bottom middle)
	Dialog opens asking for username of person wanting to collaborate.
		-Input: 1
	PIN dialog opens, write down PIN and hand it to “1”
		-Or just type it into the second window :-).
		-Then click “Ok” button.

5. Client “1” - Identifying peers
	Click “Discover Peers” button(bottom left). Notice how the Peers table now contains the document being shared by “0”
		-If this doesn’t work, try clicking discover peers again.
		-If this still doesn’t work, click “Add Peer Manually”.
			-Input into the following prompts: 0, localhost, 6000, Doc1

6. Client “1” - Creating account
	Click on peer in table. This should highlight the row.
	Click “Create Account” button(lower right)
		-Input into prompt: PASS1111pass, PASS1111pass, [PIN from step 4.]
		-Click “Ok”
	Prompt should have popped up saying “Account created”. Click “Ok”.
	
7. Client “1” - Logging into document
	Precondition: Row is still highlighted in peers table.
	Click on “Login” button(lower right).
		-Input into prompt: PASS1111pass. Click “Login” button.
	Postcondition: Document editing window appears.

8. Once you have an authenticated editing session, with one Document Owner, and one Client, you can perform the following steps to see our authorization functionality.

  The Client types "hello" into his editing pane
    ~> it should also display in the owner's pane
  
  The owner types ", world", at the end of this document
    ~> both document panes should read "hello, world"
    
  The owner uses the interface to the right of the panel to update
  the security level of a piece of text.  You can use indices into
  the document to label the section you wish to change the level of.
  In this case, put the range "7" - "11" in the text fields, choose 
  "Privileged" from the dropdown and then press "Set Level".
    ~> The text should turn blue on the owner's panel, and the client's panel
       should now read "hello, XXXXX"
  
  The owner can change the level they are writing at by using the dropdown at
  the very bottom right of the Document Collaboration Pane. Select
  "Secret" from the drop down, and type some more text into the editing
  pane -> "hello, world he said"
    ~> the text should appear green in the owners document pane 
       and the clients pane should read "hello, XXXXXXXXXXXXX"
       
  On the client's editing pane, select "Request Change Level", and
  choose "Privileged" from the menu. Click "Change/Request Access".
  On the owner's document, click "Ok" at the popup
    ~> the client's GUI should now read "hello, worldXXXXXXXX"
    
  Using the owners GUI, downgrade the client's user level to "Normal".
    ~> the client GUI's text should return to "hello, XXXXXXXXXXXXX"
  
  Now, use the owner's GUI to declassify all the text back to normal.
  Enter 0, 19 in the range inputs and set the text to "Normal".
    ~> the client GUI's text should return to "hello, world he said"
  
  


  
