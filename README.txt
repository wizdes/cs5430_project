Patrick Berens(pcb73)
Matt Goggin(mg343)
Yi Li(yl2326)

1) Running our code

In order to run our submitted code on a Linux OS, change directory into the root of this folder, and start the program with the following command:
java -jar securedit.jar

You may start additional instances by running the same command in a new terminal

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
    AuthorizationDocument
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

8. Authorization
  Once you have an authenticated editing session, with one Document Owner, and one Client, you can perform the following steps to see our authorization functionality.

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
  
9. Encrypted Files
  
  You can pick up this section from the previous.  
  
  To make for a more interesting demo, have the Owner update the client's
  level to "Privileged", (click the "Change User Level" button), and also
  insert some Top Secret text (use the dropdown at the very bottom right
  to select "Top Secret", and type a few words).
  
  Have the owner save an encrypted version of the document, by clicking
  "Save Encrypted File".  Save it anywhere on your machine.
  
  Shut down both the owner and client applications, and start them
  back up again.
  
  Follow Steps 1-2 to start the client and owner instances of the 
  application again. 
  
  In the owner's GUI, click "Start Document", and then choose "Open
  Encrypted File".  Choose the file you saved previously.  
  
  Enter an incorrect password;
    ~> You should see a failure dialog
  
  Enter the correct password
    ~> The text we constructed previously should now be present in the
       editing pane
  
  On the Client's GUI, discover the document the owner is hosting by
  clicking the "Discover Peers" button. Click that row and then the
  "Login" button.
    ~> the client should be able to login with out a pin exchange this time
    ~> the client should retain the level we assigned them previously
    ~> the text should appear at the same levels as were assigned 
        previously
  

  
