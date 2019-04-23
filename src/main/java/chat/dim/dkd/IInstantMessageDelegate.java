package chat.dim.dkd;

import chat.dim.dkd.content.Content;

import java.util.HashMap;

public interface IInstantMessageDelegate {

    /**
     *  Upload the message.content.data to CDN and return the URL
     *
     *  @param iMsg - instant message object
     *  @param data - content.data
     *  @param filename - content.filename
     *  @param password - SymmetricKey to decrypt the content.data
     *  @return URL to download the file data
     */
    public String uploadFileData(InstantMessage iMsg, byte[] data, String filename, HashMap<String, Object> password);

    /**
     *  Download file data from the URL
     *
     *  @param iMsg - instant message object
     *  @param url - URL to download the file data
     *  @param password - SymmetricKey to decrypt the file data
     *  @return decrypted file data
     */
    public byte[] downloadFileData(InstantMessage iMsg, String url, HashMap<String, Object> password);

    /**
     *  Encrypt the message.content to message.data with symmetric key
     *
     *  @param iMsg - instant message object
     *  @param content - message.content
     *  @param password - symmetric key
     *  @return encrypted message content data
     */
    public byte[] encryptContent(InstantMessage iMsg, Content content, HashMap<String, Object> password);

    /**
     *  Encrypt the symmetric key with receiver's public key
     *
     *  @param iMsg - instant message object
     *  @param password - symmetric key to be encrypted
     *  @param receiver - receiver ID string
     *  @return encrypted key data
     */
    public byte[] encryptKey(InstantMessage iMsg, HashMap<String, Object> password, String receiver);
}
