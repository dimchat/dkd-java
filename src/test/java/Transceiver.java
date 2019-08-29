
import java.util.Map;

import chat.dim.dkd.*;

public final class Transceiver implements InstantMessageDelegate, SecureMessageDelegate, ReliableMessageDelegate {

    @Override
    public byte[] encryptContent(Content content, Map<String, Object> password, InstantMessage iMsg) {
        return new byte[0];
    }

    @Override
    public Object encodeData(byte[] data, InstantMessage iMsg) {
        return Base64.encode(data);
    }

    @Override
    public byte[] encryptKey(Map<String, Object> password, Object receiver, InstantMessage iMsg) {
        return new byte[0];
    }

    @Override
    public Object encodeKey(byte[] key, InstantMessage iMsg) {
        return Base64.encode(key);
    }

    @Override
    public boolean verifyDataSignature(byte[] data, byte[] signature, Object sender, ReliableMessage rMsg) {
        return false;
    }

    @Override
    public byte[] decodeSignature(Object signature, ReliableMessage rMsg) {
        return Base64.decode((String) signature);
    }

    @Override
    public Map<String, Object> decryptKey(byte[] key, Object sender, Object receiver, SecureMessage sMsg) {
        return null;
    }

    @Override
    public byte[] decodeKey(Object key, SecureMessage sMsg) {
        return Base64.decode((String) key);
    }

    @Override
    public Content decryptContent(byte[] data, Map<String, Object> password, SecureMessage sMsg) {
        return null;
    }

    @Override
    public byte[] decodeData(Object data, SecureMessage sMsg) {
        return Base64.decode((String) data);
    }

    @Override
    public byte[] signData(byte[] data, Object sender, SecureMessage sMsg) {
        return new byte[0];
    }

    @Override
    public Object encodeSignature(byte[] signature, SecureMessage sMsg) {
        return Base64.encode(signature);
    }
}
