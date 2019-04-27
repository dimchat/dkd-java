package chat.dim.dkd;

public interface ReliableMessageDelegate {

    /**
     *  Verify the message data and signature with sender's public key
     *
     *  @param rMsg - reliable message object
     *  @param data - message data
     *  @param signature - signature for message data
     *  @param sender - sender ID string
     *  @return YES on signature match
     */
    public boolean verifyData(ReliableMessage rMsg, byte[] data, byte[] signature, String sender);
}
