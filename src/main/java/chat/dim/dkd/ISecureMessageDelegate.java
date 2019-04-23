package chat.dim.dkd;

import chat.dim.dkd.content.Content;

import java.util.HashMap;

public interface ISecureMessageDelegate {

    /**
     *  Decrypt key data to a symmetric key with receiver's private key
     *
     *  @param sMsg - secure message object
     *  @param key - encrypted key data
     *  @param sender - sender/member ID string
     *  @param receiver - receiver/group ID string
     *  @return symmetric key
     */
    public HashMap<String, Object> decryptKey(SecureMessage sMsg, byte[] key, String sender, String receiver);

    /**
     *  Decrypt encrypted data to message.content with symmetric key
     *
     *  @param sMsg - secure message object
     *  @param data - encrypt content data
     *  @param password - symmetric key
     *  @return message content
     */
    public Content decryptContent(SecureMessage sMsg, byte[] data, HashMap<String, Object> password);

    /**
     *  Sign the message data(encrypted) with sender's private key
     *
     *  @param sMsg - secure message object
     *  @param data - encrypted message data
     *  @param sender - sender ID string
     *  @return signature
     */
    public byte[] signData(SecureMessage sMsg, byte[] data, String sender);
}
