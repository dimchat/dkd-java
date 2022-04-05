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
package chat.dim.protocol;

import java.util.Map;

import chat.dim.dkd.Factories;
import chat.dim.type.MapWrapper;

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
public interface ReliableMessage extends SecureMessage {

    byte[] getSignature();

    /**
     *  Sender's Meta
     *  ~~~~~~~~~~~~~
     *  Extends for the first message package of 'Handshake' protocol.
     *
     * @param meta - Meta
     */
    void setMeta(Meta meta);
    Meta getMeta();

    /**
     *  Sender's Visa
     *  ~~~~~~~~~~~~~
     *  Extends for the first message package of 'Handshake' protocol.
     *
     * @param doc - Visa
     */
    void setVisa(Visa doc);
    Visa getVisa();

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
    SecureMessage verify();

    /**
     *  Reliable Message Delegate
     *  ~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    interface Delegate extends SecureMessage.Delegate {

        /**
         *  1. Decode 'message.signature' from String (Base64)
         *
         * @param signature - base64 string object
         * @param rMsg - reliable message
         * @return signature data
         */
        byte[] decodeSignature(Object signature, ReliableMessage rMsg);

        /**
         *  2. Verify the message data and signature with sender's public key
         *
         *  @param data - message content(encrypted) data
         *  @param signature - signature for message content(encrypted) data
         *  @param sender - sender ID/string
         *  @param rMsg - reliable message object
         *  @return YES on signature matched
         */
        boolean verifyDataSignature(byte[] data, byte[] signature, ID sender, ReliableMessage rMsg);
    }

    //
    //  Factory method
    //
    static ReliableMessage parse(Object msg) {
        if (msg == null) {
            return null;
        } else if (msg instanceof ReliableMessage) {
            return (ReliableMessage) msg;
        }
        Map<String, Object> info = MapWrapper.getMap(msg);
        assert info != null : "reliable message error: " + msg;
        Factory factory = getFactory();
        assert factory != null : "reliable message factory not ready";
        return factory.parseReliableMessage(info);
    }

    static Factory getFactory() {
        return Factories.reliableMessageFactory;
    }
    static void setFactory(Factory factory) {
        Factories.reliableMessageFactory = factory;
    }

    /**
     *  Message Factory
     *  ~~~~~~~~~~~~~~~
     */
    interface Factory {

        /**
         *  Parse map object to message
         *
         * @param msg - message info
         * @return ReliableMessage
         */
        ReliableMessage parseReliableMessage(Map<String, Object> msg);
    }
}
