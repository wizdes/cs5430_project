package security_layer;


import application.encryption_demo.CommunicationInterface;
import application.encryption_demo.Messages.DiscoveryMessage;
import application.encryption_demo.Messages.Message;
import configuration.Constants;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignedObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import transport_layer.discovery.DiscoveryResponseMessage;
import transport_layer.discovery.DiscoveryTransport;
import transport_layer.files.FileHandler;
import transport_layer.files.FileTransportInterface;
import transport_layer.network.NetworkTransport;
import transport_layer.network.NetworkTransportInterface;

/**
 *
 * @author Patrick C. Berens
 */
public class SecureTransport implements SecureTransportInterface{
    private EncryptionKeys keys;
    private FileTransportInterface fileTransport = new FileHandler();
    private NetworkTransportInterface networkTransport;
    private DiscoveryTransport discoveryTransport;
    private CommunicationInterface communication;
    private Authentications authInstance;
    private ConcurrentMap<String, Integer> lastReceived = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger();
    private ConcurrentMap<String, EncryptedAESMessage> pendingHumanAuth = new ConcurrentHashMap<>();
    private Profile profile;
    
    public SecureTransport(String password){
        Key personalKey = KeyFactory.generateSymmetricKey(password);
        keys = new EncryptionKeys(personalKey, password);
        authInstance = new Authentications(keys, this);
    }
    
    public SecureTransport(Profile profile, String password, CommunicationInterface communication) {        
        Key personalKey = KeyFactory.generateSymmetricKey(password);
        keys = new EncryptionKeys(personalKey, profile.ident, password);
        
        this.profile = profile;
        
        keys.privateKey = profile.keys.privateKey;
        keys.signingKey = profile.keys.signingKey;
        keys.publicKeys = profile.keys.publicKeys;
        keys.verifyingKeys = profile.keys.verifyingKeys;
        keys.asymmetricKeyVersions = profile.keys.asymmetricKeyVersions;
        
        //Add a symmetricKey for self so can send messages to self
        keys.addSymmetricKey(profile.ident, (SecretKey)KeyFactory.generateSymmetricKey());
        keys.addHMACKey(profile.ident, (SecretKey)KeyFactory.generateSymmetricKey());
        
       
        this.networkTransport = new NetworkTransport(profile.ident, profile.host, profile.port, this);
        this.discoveryTransport = new DiscoveryTransport(profile, networkTransport);
        this.communication = communication;
        
        authInstance = new Authentications(keys, this);
    }
    
    @Override
    public void addPeer(String peerIdent, String host, int port){
        networkTransport.addPeer(peerIdent, host, port);
    }
    
    @Override
    public boolean authenticate(String machineIdent) {
        //If machine has been authenticated or trying to authenticate with itself, return
        if (authInstance.hasAuthenticated(machineIdent) || machineIdent.equals(keys.ident)) {
            return true;
        }
        final Lock authenticateLock = new ReentrantLock(true);

        MachineAuthenticationMessage msg = authInstance.constructInitialAuthMessage();
        Condition authenticationComplete = authenticateLock.newCondition();
        
        try {
            authenticateLock.lock();
            authInstance.addAuthentication(machineIdent, msg, authenticationComplete, authenticateLock);
            sendRSAEncryptedMessage(machineIdent, msg);
            authenticationComplete.await();
            authInstance.removeAuthentication(machineIdent);
        } catch (InterruptedException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + keys.ident + "]", ex);
            }
            return false;
        }
        finally{
            authenticateLock.unlock();
            return true;
        }
    }
        
    @Override
    public boolean sendAESEncryptedMessage(String destination, Message m) {
        return sendAESEncryptedMessage(destination, m, keys.getSymmetricKey(destination), keys.getHMACKey(destination));
    }
    
    @Override
    public boolean sendAESEncryptedMessage(String destination, Message m, SecretKey secretKey, SecretKey HMACKey) {
        if(Constants.DEBUG_ON){
            Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + keys.ident + "] Sending " + EncryptedAESMessage.class.getName() + " to " + destination + ".");
        }
        if (secretKey == null) secretKey = keys.getSymmetricKey(destination);
        
        if (secretKey == null) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + keys.ident + "] No symemetric key found for " + destination + ".");
            }
            return false;
        }
        byte[] iv = CipherFactory.generateRandomIV();
        
        Cipher cipher = CipherFactory.constructAESEncryptionCipher(secretKey, iv);
        
        Serializable sendingMessage = null;
        if(m instanceof HumanAuthenticationMessage){
            sendingMessage = m;
        } else { 
            sendingMessage = new ApplicationMessage(m, counter.incrementAndGet()); 
        }
        try {
            SealedObject encryptedObject = new SealedObject(sendingMessage, cipher);
            byte[] hmac = CipherFactory.HMAC(HMACKey, encryptedObject);
            EncryptedAESMessage encryptedMessage = new EncryptedAESMessage(encryptedObject, iv, hmac);
            return networkTransport.send(destination, encryptedMessage);
        } catch (IOException | IllegalBlockSizeException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + keys.ident + "]", ex);
            }
            return false;
        }
    }

    @Override
    public boolean sendRSAEncryptedMessage(String destination, Message m) {
        if(Constants.DEBUG_ON){
            Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + keys.ident + "] Sending " + EncryptedRSAMessage.class.getName() + " to " + destination + ".");
        }
        byte[] iv = new byte[16];
        PublicKey publicKey = keys.getPublicKey(destination);
        if (publicKey == null) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + keys.ident + "] No public key found for " + destination + ".");
            }
            return false;
        }
        Cipher cipher = CipherFactory.constructRSAEncryptionCipher(publicKey);
        try {
            SealedObject encryptedObject = new SealedObject(m, cipher);
            Signature signature = Signature.getInstance(CipherFactory.SIGNING_ALGORITHM);
            SignedObject signedObject = new SignedObject(encryptedObject, (PrivateKey)keys.signingKey, signature);

            EncryptedMessage encryptedMessage = new EncryptedRSAMessage(signedObject);

            boolean wasSuccessful = networkTransport.send(destination, encryptedMessage);
            return wasSuccessful;
        } catch (IOException | IllegalBlockSizeException | NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + keys.ident + "]", ex);
            }
            return false;
        }
    }

    @Override
    public boolean processEncryptedMessage(String sourceOfMessage, EncryptedMessage encryptedMessage) throws InvalidHMACException {
        boolean success = false;
        SecretKey secretKey = null;
        SecretKey HMACKey = null;
        if(Constants.DEBUG_ON){
            Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + keys.ident + "] Processing " + EncryptedMessage.class.getName() + " from " + sourceOfMessage + ".");
        }
        SealedObject encryptedObject;
        try {
            Cipher cipher = null;
            switch(encryptedMessage.getAlgorithm()){
                case CipherFactory.AES_ALGORITHM:
                    
                    EncryptedAESMessage aesMessage = (EncryptedAESMessage)encryptedMessage;
                    encryptedObject = aesMessage.encryptedObject;

                    HMACKey = keys.getHMACKey(sourceOfMessage);
                    secretKey = keys.getSymmetricKey(sourceOfMessage);
                    
                    if (HMACKey == null || secretKey == null) {
                        this.pendingHumanAuth.put(sourceOfMessage, aesMessage);
                        return true;
                    }                    
                    
                    byte[] hmac = CipherFactory.HMAC(HMACKey, encryptedObject);
                    
                    if (!Arrays.equals(hmac, aesMessage.HMAC)) {
                        throw new InvalidHMACException("[User: " + keys.ident + "] Invalid HMAC from " + sourceOfMessage + ".");
                    }
                    
                    if (secretKey == null) {    //Never enters because of null check above, but leave since someone might remove that
                        if(Constants.DEBUG_ON){
                            Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + keys.ident + "] No symemetric key found for " + sourceOfMessage + ".");
                        }
                    }
                    cipher = CipherFactory.constructAESDecryptionCipher(secretKey, aesMessage.iv);
                    break;
                    
                case CipherFactory.SIGNING_ALGORITHM:
                    EncryptedRSAMessage rsaMessage = (EncryptedRSAMessage)encryptedMessage;
                    SignedObject signedObject = rsaMessage.signedObject;
                    PublicKey verifyingKey = keys.getVerifyingKey(sourceOfMessage);
                    Signature sig = Signature.getInstance(CipherFactory.SIGNING_ALGORITHM);
                    boolean verified = signedObject.verify(verifyingKey, sig);
                    if (!verified) {
                        throw new SignatureException("[User: " + keys.ident + "] Unverified message from " + sourceOfMessage);
                    } else {
                        encryptedObject = (SealedObject)signedObject.getObject();
                        cipher = CipherFactory.constructRSADecryptionCipher(keys.privateKey);
                    }
                    break;
                   
                default:
                    throw new NoSuchAlgorithmException("[User: " + keys.ident + "] Attempted to process a message encrypted with an unsupported algorithm.");
            }
            
            Object decryptedObj = encryptedObject.getObject(cipher);
            if (decryptedObj instanceof HumanAuthenticationMessage) {
                if(Constants.DEBUG_ON){
                    Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + keys.ident + "] Processing decrypted " + HumanAuthenticationMessage.class.getName() + " from " + sourceOfMessage + ".");
                }
                authInstance.processHumanAuthenticationRequest(sourceOfMessage, (HumanAuthenticationMessage)decryptedObj, this);
                success = true;
            } else if (decryptedObj instanceof MachineAuthenticationMessage) {
                if(Constants.DEBUG_ON){
                    Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + keys.ident + "] Processing decrypted " + MachineAuthenticationMessage.class.getName() + " from " + sourceOfMessage + ".");
                }
                authInstance.processMachineAuthenticationRequest(sourceOfMessage, 
                                                          (MachineAuthenticationMessage)decryptedObj,
                                                          this);
                success = true;
            } else if (decryptedObj instanceof ApplicationMessage) {
                if(Constants.DEBUG_ON){
                    Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + keys.ident + "] Processing decrypted " + ApplicationMessage.class.getName() + " from " + sourceOfMessage + ".");
                }
                ApplicationMessage appMessage = (ApplicationMessage)decryptedObj;
                Integer currentCounter = lastReceived.get(sourceOfMessage);
                if (currentCounter == null || appMessage.counter > currentCounter) {
                    communication.depositMessage(appMessage.message);
                    success = true;
                    lastReceived.put(sourceOfMessage, appMessage.counter);
                } else {
                    if(Constants.DEBUG_ON){
                        Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + keys.ident + "] Invalid counter on " + ApplicationMessage.class.getName() + " from " + sourceOfMessage + ".");
                    }
                }
            }
        } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | SignatureException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + keys.ident + "]", ex);
            }
        }
        
        return success;
    }
    
    @Override
    public void processDiscoveryResponse(DiscoveryResponseMessage msg){
        if(Constants.DEBUG_ON){
            Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + keys.ident + "] Processing " + DiscoveryResponseMessage.class.getName() + " from " + msg.owner + ".");
        }
        PublicKey pk = keys.getPublicKey(msg.owner);
        long myKeyVersion = keys.getAsymmetricKeyVersion(keys.ident);
        long myOwnersKeyVersion = keys.getAsymmetricKeyVersion(msg.owner);
        
        System.out.println(pk);
        System.out.println(myOwnersKeyVersion);
        System.out.println(msg.ownerAsymmetricKeyVersion);
        System.out.println(myKeyVersion);
        System.out.println(msg.clientsKeyVersionNumberHeldByOwner);
        
        
        if(pk != null && myOwnersKeyVersion == msg.ownerAsymmetricKeyVersion && myKeyVersion == msg.clientsKeyVersionNumberHeldByOwner){
            communication.updatePeers(msg.owner, msg.ip, msg.port, msg.documents, true);
        } else{
            communication.updatePeers(msg.owner, msg.ip, msg.port, msg.documents, false);
        }
    }
    
    private void processDiscoveryMessage(DiscoveryMessage dm) {
        if(Constants.DEBUG_ON){
            Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + keys.ident + "] Processing " + DiscoveryMessage.class.getName() + " from " + dm.sourceID + ".");
        }
        if(!dm.sourceID.equals(profile.ident) && this.profile.documentsOpenForDiscovery.size() > 0){
            List<String> documentNames = new ArrayList<>(this.profile.documentsOpenForDiscovery);   //must copy here, possibly due to transient flag
            long myKeyVersion = profile.getAsymmetricKeyVersionNumber(this.profile.ident);
            long clientsKeyVersion = profile.getAsymmetricKeyVersionNumber(dm.sourceID);
            DiscoveryResponseMessage responseMessage = new DiscoveryResponseMessage(profile.ident, profile.host, profile.port, documentNames, myKeyVersion, clientsKeyVersion);
            networkTransport.addPeer(dm.sourceID, dm.sourceIP, dm.sourcePort);
            networkTransport.send(dm.sourceID, responseMessage);
        }
    }
    
    @Override
    public boolean processPlaintextMessage(String sourceOfMessage, PlaintextMessage msg) {
        if(Constants.DEBUG_ON){
            Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + keys.ident + "] Processing " + PlaintextMessage.class.getName() + " from " + sourceOfMessage + ".");
        }
        if (msg.m instanceof DiscoveryMessage) {
            processDiscoveryMessage((DiscoveryMessage)msg.m);
        } else if (msg.m instanceof HumanAuthenticationMessage) {
            authInstance.processHumanAuthenticationRequest(sourceOfMessage, (HumanAuthenticationMessage)msg.m, this);
        }
        
        return true;
    }
    
    @Override
    public boolean writeEncryptedFile(String filename, Message contents) {
        try {
            //Encrypt file with personal key
            byte[] iv = CipherFactory.generateRandomIV();
            Cipher cipher = CipherFactory.constructAESEncryptionCipher(keys.personalKey, iv);
            SealedObject encryptedObject = new SealedObject(contents, cipher);
            
            String salt = KeyFactory.generateSalt();
            Key hmacKey = KeyFactory.generateSymmetricKey(keys.password, salt);
            byte[] hmac = CipherFactory.HMAC(hmacKey, encryptedObject);
            
            EncryptedAESFile file = new EncryptedAESFile(encryptedObject, iv, hmac, salt);
            return fileTransport.writeFile(filename, file);
            
        } catch (IOException | IllegalBlockSizeException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + keys.ident + "]", ex);
            }
            return false;
        }   
    }

    @Override
    public Message readEncryptedFile(String filename) {
        if (!new File(filename).exists()) {
            return null;
        }
        try {
            EncryptedAESFile file = (EncryptedAESFile)fileTransport.readFile(filename);
            
            //Verify hmac
            Key hmacKey = KeyFactory.generateSymmetricKey(keys.password, file.hmacSalt);
            byte[] hmac = CipherFactory.HMAC(hmacKey, file.encryptedObject);        
            if (!Arrays.equals(hmac, file.HMAC)) {
                throw new SignatureException("[User: " + keys.ident + "] Invalid HMAC");
            }
            
            Cipher cipher = CipherFactory.constructAESDecryptionCipher(keys.personalKey, file.iv);
            return (Message)file.encryptedObject.getObject(cipher);
        } catch (SignatureException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + keys.ident + "] Bad signature reading " + filename + ".");
            }
            return null;
        } catch (IOException | ClassNotFoundException | IllegalBlockSizeException | BadPaddingException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + keys.ident + "]", ex);
            }
            return null;
        }
    }

    @Override
    public String readUnencryptedFile(String filename) {
        return fileTransport.openUnserializedFile(filename);
    }
    
    @Override
    public void shutdown() {
        this.networkTransport.shutdown();
    }


    @Override
    public ArrayList<Integer> findPeers(int myID) {
        ArrayList<Integer> peers = new ArrayList<Integer>();
        peers.add(0);
        peers.add(1);
        peers.add(2);
        return peers;
    }

    @Override
    public boolean initializeHumanAuthenticate(String destination) {
        if(Constants.DEBUG_ON){
            Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + keys.ident + "] Starting initial human authentication with " + destination + ".");
        }

        final Lock authenticateLock = new ReentrantLock(true);
        
        HumanAuthenticationMessage msg = authInstance.constructInitialHAuthMessage();
        Condition authenticationComplete = authenticateLock.newCondition();
        
        try {
            authenticateLock.lock();
            // send the message and wait
            authInstance.addAuthentication(destination, msg, authenticationComplete, authenticateLock);
            sendPlainTextMessage(destination, msg);
        }
        finally{
            authenticateLock.unlock();
            return true;
        }
    }

    @Override
    public void broadcastDiscovery() {
        discoveryTransport.broadcastDiscovery();
    }
    
    @Override
    public String getPIN(String ID){
        return authInstance.getPIN(ID);
    }

    @Override
    public boolean addPIN(String ownerID, String PIN) {
        if (!this.pendingHumanAuth.containsKey(ownerID)) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + keys.ident + "] Haven't yet received PIN message from owner: " + ownerID + ".");
            }
            return false;
        }
        
        SecretKey pinKey = (SecretKey) KeyFactory.generateSymmetricKey(PIN);
        SecretKey HMACKey = (SecretKey) KeyFactory.generateSymmetricKey(PIN + "HMAC");
        keys.addSymmetricKey(ownerID, pinKey);
        keys.addHMACKey(ownerID, HMACKey);
        
        EncryptedAESMessage m = this.pendingHumanAuth.get(ownerID);
        try {
            boolean success = processEncryptedMessage(ownerID, m);
            if(success){
                keys.removeSymmetricKey(ownerID);
                keys.removeHMACKey(ownerID);
                pendingHumanAuth.remove(ownerID);
            }
            return success;
        } catch (InvalidHMACException ex) {
            keys.removeSymmetricKey(ownerID);
            keys.removeHMACKey(ownerID);
            if(Constants.DEBUG_ON){
                Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + keys.ident + "] Failed to decrypt PIN message from owner: " + ownerID + ".", ex);
            }
            return false;
        }
    }

    @Override
    public boolean sendPlainTextMessage(String destination, Message m) {
        if(Constants.DEBUG_ON){
            Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + keys.ident + "] Sending " + PlaintextMessage.class.getName() + " to " + destination + ".");
        }
        PlaintextMessage sendMsg = new PlaintextMessage();
        sendMsg.m = m;
        return networkTransport.send(destination, sendMsg);
    }

    @Override
    public void displayPIN(String ID, String PIN) {
        communication.displayPIN(ID, PIN);
    }
}
