
import chat.dim.Content;
import chat.dim.ID;
import chat.dim.InstantMessage;
import chat.dim.InstantMessageDelegate;
import chat.dim.ReliableMessage;
import chat.dim.ReliableMessageDelegate;
import chat.dim.SecureMessage;
import chat.dim.crypto.SymmetricKey;
import chat.dim.format.Base64;

import java.util.Map;

public final class Transceiver implements InstantMessageDelegate<ID, SymmetricKey>, ReliableMessageDelegate<ID, SymmetricKey> {

    @Override
    public ID getID(Object identifier) {
        return ID.getInstance(identifier);
    }

    @Override
    public Content<ID> getContent(Map<String, Object> content) {
        return chat.dim.protocol.Content.getInstance(content);
    }

    // 1.1.
    @Override
    public byte[] serializeContent(Content<ID> content, SymmetricKey password, InstantMessage<ID, SymmetricKey> iMsg) {
        return new byte[0];
    }

    // 1.2.
    @Override
    public byte[] encryptContent(byte[] data, SymmetricKey password, InstantMessage<ID, SymmetricKey> iMsg) {
        return new byte[0];
    }

    // 1.3.
    @Override
    public Object encodeData(byte[] data, InstantMessage<ID, SymmetricKey> iMsg) {
        return Base64.encode(data);
    }

    // 1.4.
    @Override
    public byte[] serializeKey(SymmetricKey password, InstantMessage<ID, SymmetricKey> iMsg) {
        return new byte[0];
    }

    // 1.5.
    @Override
    public byte[] encryptKey(byte[] data, ID receiver, InstantMessage<ID, SymmetricKey> iMsg) {
        return new byte[0];
    }

    // 1.6.
    @Override
    public Object encodeKey(byte[] key, InstantMessage<ID, SymmetricKey> iMsg) {
        return Base64.encode(key);
    }

    // 1.7.
    @Override
    public byte[] signData(byte[] data, ID sender, SecureMessage<ID, SymmetricKey> sMsg) {
        return new byte[0];
    }

    // 1.8.
    @Override
    public Object encodeSignature(byte[] signature, SecureMessage<ID, SymmetricKey> sMsg) {
        return Base64.encode(signature);
    }

    // 2.1.
    @Override
    public byte[] decodeSignature(Object signature, ReliableMessage<ID, SymmetricKey> rMsg) {
        return Base64.decode((String) signature);
    }

    // 2.2.
    @Override
    public boolean verifyDataSignature(byte[] data, byte[] signature, ID sender, ReliableMessage<ID, SymmetricKey> rMsg) {
        return false;
    }

    // 2.3.
    @Override
    public byte[] decodeKey(Object key, SecureMessage<ID, SymmetricKey> sMsg) {
        return Base64.decode((String) key);
    }

    // 2.4.
    @Override
    public byte[] decryptKey(byte[] key, ID sender, ID receiver, SecureMessage<ID, SymmetricKey> sMsg) {
        return null;
    }

    // 2.5.
    @Override
    public SymmetricKey deserializeKey(byte[] key, ID sender, ID receiver, SecureMessage<ID, SymmetricKey> sMsg) {
        return null;
    }

    // 2.6.
    @Override
    public byte[] decodeData(Object data, SecureMessage<ID, SymmetricKey> sMsg) {
        return Base64.decode((String) data);
    }

    // 2.7.
    @Override
    public byte[] decryptContent(byte[] data, SymmetricKey password, SecureMessage<ID, SymmetricKey> sMsg) {
        return null;
    }

    // 2.8.
    @Override
    public Content deserializeContent(byte[] data, SymmetricKey password, SecureMessage<ID, SymmetricKey> sMsg) {
        return null;
    }
}
