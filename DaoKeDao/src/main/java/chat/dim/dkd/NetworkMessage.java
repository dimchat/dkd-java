/* license: https://mit-license.org
 *
 *  Dao-Ke-Dao: Universal Message Module
 *
 *                                Written in 2019 by Moky <albert.moky@gmail.com>
 *
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Albert Moky
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * ==============================================================================
 */
package chat.dim.dkd;

import java.util.Map;

import chat.dim.protocol.Meta;
import chat.dim.protocol.ReliableMessage;
import chat.dim.protocol.SecureMessage;
import chat.dim.protocol.Visa;

/**
 *  Reliable Message signed by an asymmetric key
 *  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *  This class is used to sign the SecureMessage
 *  It contains a 'signature' field which signed with sender's private key
 *
 *  data format: {
 *      //-- envelope
 *      sender   : "moki@xxx",
 *      receiver : "hulk@yyy",
 *      time     : 123,
 *      //-- content data and key/keys
 *      data     : "...",  // base64_encode(symmetric)
 *      key      : "...",  // base64_encode(asymmetric)
 *      keys     : {
 *          "ID1": "key1", // base64_encode(asymmetric)
 *      },
 *      //-- signature
 *      signature: "..."   // base64_encode()
 *  }
 */
final class NetworkMessage extends EncryptedMessage implements ReliableMessage {

    private byte[] signature;

    private Meta meta = null;
    private Visa visa = null;

    NetworkMessage(Map<String, Object> dictionary) {
        super(dictionary);
        // lazy load
        signature = null;
    }

    @Override
    public ReliableMessage.Delegate getDelegate() {
        return (ReliableMessage.Delegate) super.getDelegate();
    }

    @Override
    public byte[] getSignature() {
        if (signature == null) {
            Object base64 = get("signature");
            assert base64 != null : "signature cannot be empty";
            signature = getDelegate().decodeSignature(base64, this);
        }
        return signature;
    }

    /**
     *  Sender's Meta
     *  ~~~~~~~~~~~~~
     *  Extends for the first message package of 'Handshake' protocol.
     *
     * @param info - Meta object or dictionary
     */
    @Override
    public void setMeta(Meta info) {
        ReliableMessage.setMeta(info, getMap());
        meta = info;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Meta getMeta() {
        if (meta == null) {
            meta = ReliableMessage.getMeta(getMap());
        }
        return meta;
    }

    /**
     *  Sender's Visa
     *  ~~~~~~~~~~~~~
     *  Extends for the first message package of 'Handshake' protocol.
     *
     * @param doc - visa document
     */
    @Override
    public void setVisa(Visa doc) {
        ReliableMessage.setVisa(doc, getMap());
        visa = doc;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Visa getVisa() {
        if (visa == null) {
            visa = ReliableMessage.getVisa(getMap());
        }
        return visa;
    }

    /*
     *  Verify the Reliable Message to Secure Message
     *
     *    +----------+      +----------+
     *    | sender   |      | sender   |
     *    | receiver |      | receiver |
     *    | time     |  ->  | time     |
     *    |          |      |          |
     *    | data     |      | data     |  1. verify(data, signature, sender.PK)
     *    | key/keys |      | key/keys |
     *    | signature|      +----------+
     *    +----------+
     */

    /**
     *  Verify 'data' and 'signature' field with sender's public key
     *
     * @return SecureMessage object
     */
    @Override
    public SecureMessage verify() {
        byte[] data = getData();
        if (data == null) {
            throw new NullPointerException("failed to decode content data: " + this);
        }
        byte[] signature = getSignature();
        if (signature == null) {
            throw new NullPointerException("failed to decode message signature: " + this);
        }
        // 1. verify data signature with sender's public key
        if (getDelegate().verifyDataSignature(data, signature, getSender(), this)) {
            // 2. pack message
            Map<String, Object> map = copyMap(false);
            map.remove("signature");
            return SecureMessage.parse(map);
        } else {
            //throw new RuntimeException("message signature not match: " + this);
            return null;
        }
    }
}
