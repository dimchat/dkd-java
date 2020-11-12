
package chat.dim;

import chat.dim.crypto.SymmetricKey;
import chat.dim.format.Base64;
import chat.dim.protocol.Content;
import chat.dim.protocol.ID;
import chat.dim.protocol.InstantMessage;
import chat.dim.protocol.ReliableMessage;
import chat.dim.protocol.SecureMessage;

import java.util.Map;

public final class Transceiver implements MessageDelegate {

    @Override
    public Content getContent(Map<String, Object> content) {
//        return chat.dim.protocol.Content.getInstance(content);
        return null;
    }

    // 1.1.
    @Override
    public byte[] serializeContent(Content content, SymmetricKey password, InstantMessage iMsg) {
        return new byte[0];
    }

    // 1.2.
    @Override
    public byte[] encryptContent(byte[] data, SymmetricKey password, InstantMessage iMsg) {
        return new byte[0];
    }

    // 1.3.
    @Override
    public Object encodeData(byte[] data, InstantMessage iMsg) {
        return Base64.encode(data);
    }

    // 1.4.
    @Override
    public byte[] serializeKey(SymmetricKey password, InstantMessage iMsg) {
        return new byte[0];
    }

    // 1.5.
    @Override
    public byte[] encryptKey(byte[] data, ID receiver, InstantMessage iMsg) {
        return new byte[0];
    }

    // 1.6.
    @Override
    public Object encodeKey(byte[] key, InstantMessage iMsg) {
        return Base64.encode(key);
    }

    // 1.7.
    @Override
    public byte[] signData(byte[] data, ID sender, SecureMessage sMsg) {
        return new byte[0];
    }

    // 1.8.
    @Override
    public Object encodeSignature(byte[] signature, SecureMessage sMsg) {
        return Base64.encode(signature);
    }

    // 2.1.
    @Override
    public byte[] decodeSignature(Object signature, ReliableMessage rMsg) {
        return Base64.decode((String) signature);
    }

    // 2.2.
    @Override
    public boolean verifyDataSignature(byte[] data, byte[] signature, ID sender, ReliableMessage rMsg) {
        return false;
    }

    // 2.3.
    @Override
    public byte[] decodeKey(Object key, SecureMessage sMsg) {
        return Base64.decode((String) key);
    }

    // 2.4.
    @Override
    public byte[] decryptKey(byte[] key, ID sender, ID receiver, SecureMessage sMsg) {
        return null;
    }

    // 2.5.
    @Override
    public SymmetricKey deserializeKey(byte[] key, ID sender, ID receiver, SecureMessage sMsg) {
        return null;
    }

    // 2.6.
    @Override
    public byte[] decodeData(Object data, SecureMessage sMsg) {
        return Base64.decode((String) data);
    }

    // 2.7.
    @Override
    public byte[] decryptContent(byte[] data, SymmetricKey password, SecureMessage sMsg) {
        return null;
    }

    // 2.8.
    @Override
    public Content deserializeContent(byte[] data, SymmetricKey password, SecureMessage sMsg) {
        return null;
    }
}
