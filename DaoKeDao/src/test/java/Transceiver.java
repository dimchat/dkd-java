
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

public final class Transceiver implements InstantMessageDelegate, SecureMessageDelegate, ReliableMessageDelegate {

    // 1.1.
    @Override
    public byte[] serializeContent(Content content, Map<String, Object> password, InstantMessage iMsg) {
        return new byte[0];
    }

    // 1.2.
    @Override
    public byte[] encryptContent(byte[] data, Map<String, Object> password, InstantMessage iMsg) {
        return new byte[0];
    }

    // 1.3.
    @Override
    public Object encodeData(byte[] data, InstantMessage iMsg) {
        return Base64.encode(data);
    }

    // 1.4.
    @Override
    public byte[] serializeKey(Map<String, Object> password, InstantMessage iMsg) {
        return new byte[0];
    }

    // 1.5.
    @Override
    public byte[] encryptKey(byte[] data, Object receiver, InstantMessage iMsg) {
        return new byte[0];
    }

    // 1.6.
    @Override
    public Object encodeKey(byte[] key, InstantMessage iMsg) {
        return Base64.encode(key);
    }

    // 1.7.
    @Override
    public byte[] signData(byte[] data, Object sender, SecureMessage sMsg) {
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
    public boolean verifyDataSignature(byte[] data, byte[] signature, Object sender, ReliableMessage rMsg) {
        return false;
    }

    // 2.3.
    @Override
    public byte[] decodeKey(Object key, SecureMessage sMsg) {
        return Base64.decode((String) key);
    }

    // 2.4.
    @Override
    public byte[] decryptKey(byte[] key, Object sender, Object receiver, SecureMessage sMsg) {
        return null;
    }

    // 2.5.
    @Override
    public Map<String, Object> deserializeKey(byte[] key, Object sender, Object receiver, SecureMessage sMsg) {
        return null;
    }

    // 2.6.
    @Override
    public byte[] decodeData(Object data, SecureMessage sMsg) {
        return Base64.decode((String) data);
    }

    // 2.7.
    @Override
    public byte[] decryptContent(byte[] data, Map<String, Object> password, SecureMessage sMsg) {
        return null;
    }

    // 2.8.
    @Override
    public Content deserializeContent(byte[] data, Map<String, Object> password, SecureMessage sMsg) {
        return null;
    }

    static {
        Content.register(ContentType.TEXT, TextContent.class);
    }
}
