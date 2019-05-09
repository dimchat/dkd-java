package chat.dim.dkd;

import java.util.Map;

public interface InstantMessageDelegate {

    /**
     *  Encrypt the message.content to message.data with symmetric key
     *
     *  @param iMsg - instant message object
     *  @param content - message.content
     *  @param password - symmetric key
     *  @return encrypted message content data
     */
    byte[] encryptContent(InstantMessage iMsg, Content content, Map<String, Object> password);

    /**
     *  Encrypt the symmetric key with receiver's public key
     *
     *  @param iMsg - instant message object
     *  @param password - symmetric key to be encrypted
     *  @param receiver - receiver ID/string
     *  @return encrypted key data
     */
    byte[] encryptKey(InstantMessage iMsg, Map<String, Object> password, Object receiver);
}
