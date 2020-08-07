
import java.util.Map;

import chat.dim.Content;
import chat.dim.InstantMessage;
import chat.dim.InstantMessageDelegate;
import chat.dim.ReliableMessage;
import chat.dim.ReliableMessageDelegate;
import chat.dim.SecureMessage;
import chat.dim.SecureMessageDelegate;
import chat.dim.format.Base64;
import chat.dim.protocol.ContentType;
import chat.dim.protocol.TextContent;

public final class Transceiver<ID> implements InstantMessageDelegate<ID>, SecureMessageDelegate<ID>, ReliableMessageDelegate<ID> {

    // 1.1.
    @Override
    public byte[] serializeContent(Content<ID> content, Map<String, Object> password, InstantMessage<ID> iMsg) {
        return new byte[0];
    }

    // 1.2.
    @Override
    public byte[] encryptContent(byte[] data, Map<String, Object> password, InstantMessage<ID> iMsg) {
        return new byte[0];
    }

    // 1.3.
    @Override
    public Object encodeData(byte[] data, InstantMessage<ID> iMsg) {
        return Base64.encode(data);
    }

    // 1.4.
    @Override
    public byte[] serializeKey(Map<String, Object> password, InstantMessage<ID> iMsg) {
        return new byte[0];
    }

    // 1.5.
    @Override
    public byte[] encryptKey(byte[] data, ID receiver, InstantMessage<ID> iMsg) {
        return new byte[0];
    }

    // 1.6.
    @Override
    public Object encodeKey(byte[] key, InstantMessage<ID> iMsg) {
        return Base64.encode(key);
    }

    // 1.7.
    @Override
    public byte[] signData(byte[] data, ID sender, SecureMessage<ID> sMsg) {
        return new byte[0];
    }

    // 1.8.
    @Override
    public Object encodeSignature(byte[] signature, SecureMessage<ID> sMsg) {
        return Base64.encode(signature);
    }

    // 2.1.
    @Override
    public byte[] decodeSignature(Object signature, ReliableMessage<ID> rMsg) {
        return Base64.decode((String) signature);
    }

    // 2.2.
    @Override
    public boolean verifyDataSignature(byte[] data, byte[] signature, ID sender, ReliableMessage<ID> rMsg) {
        return false;
    }

    // 2.3.
    @Override
    public byte[] decodeKey(Object key, SecureMessage<ID> sMsg) {
        return Base64.decode((String) key);
    }

    // 2.4.
    @Override
    public byte[] decryptKey(byte[] key, ID sender, ID receiver, SecureMessage<ID> sMsg) {
        return null;
    }

    // 2.5.
    @Override
    public Map<String, Object> deserializeKey(byte[] key, ID sender, ID receiver, SecureMessage<ID> sMsg) {
        return null;
    }

    // 2.6.
    @Override
    public byte[] decodeData(Object data, SecureMessage<ID> sMsg) {
        return Base64.decode((String) data);
    }

    // 2.7.
    @Override
    public byte[] decryptContent(byte[] data, Map<String, Object> password, SecureMessage<ID> sMsg) {
        return null;
    }

    // 2.8.
    @Override
    public Content deserializeContent(byte[] data, Map<String, Object> password, SecureMessage<ID> sMsg) {
        return null;
    }

    static {
        Content.register(ContentType.TEXT, TextContent.class);
    }
}
