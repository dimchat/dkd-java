
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
import chat.dim.protocol.ForwardContent;
import chat.dim.protocol.TextContent;

public final class Transceiver<ID, K extends Map, M extends Map, P extends Map>
        implements InstantMessageDelegate<ID, K, M, P>,
        SecureMessageDelegate<ID, K, M, P>,
        ReliableMessageDelegate<ID, K, M, P> {

    // 1.1.
    @Override
    public byte[] serializeContent(Content<ID> content, K password, InstantMessage<ID, K, M, P> iMsg) {
        return new byte[0];
    }

    // 1.2.
    @Override
    public byte[] encryptContent(byte[] data, K password, InstantMessage<ID, K, M, P> iMsg) {
        return new byte[0];
    }

    // 1.3.
    @Override
    public Object encodeData(byte[] data, InstantMessage<ID, K, M, P> iMsg) {
        return Base64.encode(data);
    }

    // 1.4.
    @Override
    public byte[] serializeKey(K password, InstantMessage<ID, K, M, P> iMsg) {
        return new byte[0];
    }

    // 1.5.
    @Override
    public byte[] encryptKey(byte[] data, ID receiver, InstantMessage<ID, K, M, P> iMsg) {
        return new byte[0];
    }

    // 1.6.
    @Override
    public Object encodeKey(byte[] key, InstantMessage<ID, K, M, P> iMsg) {
        return Base64.encode(key);
    }

    // 1.7.
    @Override
    public byte[] signData(byte[] data, ID sender, SecureMessage<ID, K, M, P> sMsg) {
        return new byte[0];
    }

    // 1.8.
    @Override
    public Object encodeSignature(byte[] signature, SecureMessage<ID, K, M, P> sMsg) {
        return Base64.encode(signature);
    }

    // 2.1.
    @Override
    public byte[] decodeSignature(Object signature, ReliableMessage<ID, K, M, P> rMsg) {
        return Base64.decode((String) signature);
    }

    // 2.2.
    @Override
    public boolean verifyDataSignature(byte[] data, byte[] signature, ID sender, ReliableMessage<ID, K, M, P> rMsg) {
        return false;
    }

    // 2.3.
    @Override
    public byte[] decodeKey(Object key, SecureMessage<ID, K, M, P> sMsg) {
        return Base64.decode((String) key);
    }

    // 2.4.
    @Override
    public byte[] decryptKey(byte[] key, ID sender, ID receiver, SecureMessage<ID, K, M, P> sMsg) {
        return null;
    }

    // 2.5.
    @Override
    public K deserializeKey(byte[] key, ID sender, ID receiver, SecureMessage<ID, K, M, P> sMsg) {
        return null;
    }

    // 2.6.
    @Override
    public byte[] decodeData(Object data, SecureMessage<ID, K, M, P> sMsg) {
        return Base64.decode((String) data);
    }

    // 2.7.
    @Override
    public byte[] decryptContent(byte[] data, K password, SecureMessage<ID, K, M, P> sMsg) {
        return null;
    }

    // 2.8.
    @Override
    public Content deserializeContent(byte[] data, K password, SecureMessage<ID, K, M, P> sMsg) {
        return null;
    }

    static {
        // Forward content for Top-Secret message
        Content.register(ContentType.FORWARD, ForwardContent.class);
        // Text content
        Content.register(ContentType.TEXT, TextContent.class);
    }
}
