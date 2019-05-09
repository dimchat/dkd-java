package chat.dim.dkd;

import java.util.Map;

public interface SecureMessageDelegate {

    /**
     *  Decrypt key data to a symmetric key with receiver's private key
     *
     *  @param sMsg - secure message object
     *  @param key - encrypted key data
     *  @param sender - sender ID/string
     *  @param receiver - receiver(group) ID/string
     *  @return symmetric key
     */
    Map<String, Object> decryptKey(SecureMessage sMsg, byte[] key, Object sender, Object receiver);

    /**
     *  Decrypt encrypted data to message.content with symmetric key
     *
     *  @param sMsg - secure message object
     *  @param data - encrypt content data
     *  @param password - symmetric key
     *  @return message content
     */
    Content decryptContent(SecureMessage sMsg, byte[] data, Map<String, Object> password);

    /**
     *  Sign the message data(encrypted) with sender's private key
     *
     *  @param sMsg - secure message object
     *  @param data - encrypted message data
     *  @param sender - sender ID/string
     *  @return signature
     */
    byte[] signData(SecureMessage sMsg, byte[] data, Object sender);
}
