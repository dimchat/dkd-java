
import chat.dim.dkd.*;

import java.util.Map;

public final class Transceiver implements InstantMessageDelegate, SecureMessageDelegate, ReliableMessageDelegate {
    @Override
    public byte[] encryptContent(Content content, Map<String, Object> password, InstantMessage iMsg) {
        return new byte[0];
    }

    @Override
    public byte[] encryptKey(Map<String, Object> password, Object receiver, InstantMessage iMsg) {
        return new byte[0];
    }

    @Override
    public boolean verifyData(byte[] data, byte[] signature, Object sender, ReliableMessage rMsg) {
        return false;
    }

    @Override
    public Map<String, Object> decryptKey(byte[] key, Object sender, Object receiver, SecureMessage sMsg) {
        return null;
    }

    @Override
    public Content decryptContent(byte[] data, Map<String, Object> password, SecureMessage sMsg) {
        return null;
    }

    @Override
    public byte[] signData(byte[] data, Object sender, SecureMessage sMsg) {
        return new byte[0];
    }
}
