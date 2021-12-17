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

import java.util.List;
import java.util.Map;

import chat.dim.crypto.SymmetricKey;
import chat.dim.dkd.Factories;
import chat.dim.type.MapWrapper;

/**
 *  Secure Message
 *  ~~~~~~~~~~~~~~
 *  Instant Message encrypted by a symmetric key
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
 *      }
 *  }
 */
public interface SecureMessage extends Message {

    byte[] getData();

    byte[] getEncryptedKey();

    Map<String, Object> getEncryptedKeys();

    /*
     *  Decrypt the Secure Message to Instant Message
     *
     *    +----------+      +----------+
     *    | sender   |      | sender   |
     *    | receiver |      | receiver |
     *    | time     |  ->  | time     |
     *    |          |      |          |  1. PW      = decrypt(key, receiver.SK)
     *    | data     |      | content  |  2. content = decrypt(data, PW)
     *    | key/keys |      +----------+
     *    +----------+
     */

    /**
     *  Decrypt message, replace encrypted 'data' with 'content' field
     *
     * @return InstantMessage object
     */
    InstantMessage decrypt();

    /*
     *  Sign the Secure Message to Reliable Message
     *
     *    +----------+      +----------+
     *    | sender   |      | sender   |
     *    | receiver |      | receiver |
     *    | time     |  ->  | time     |
     *    |          |      |          |
     *    | data     |      | data     |
     *    | key/keys |      | key/keys |
     *    +----------+      | signature|  1. signature = sign(data, sender.SK)
     *                      +----------+
     */

    /**
     *  Sign message.data, add 'signature' field
     *
     * @return ReliableMessage object
     */
    ReliableMessage sign();

    /*
     *  Split/Trim group message
     *
     *  for each members, get key from 'keys' and replace 'receiver' to member ID
     */

    /**
     *  Split the group message to single person messages
     *
     *  @param members - group members
     *  @return secure/reliable message(s)
     */
    List<SecureMessage> split(List<ID> members);

    /**
     *  Trim the group message for a member
     *
     * @param member - group member ID/string
     * @return SecureMessage
     */
    SecureMessage trim(ID member);

    /**
     *  Secure Message Delegate
     *  ~~~~~~~~~~~~~~~~~~~~~~~
     */
    interface Delegate extends Message.Delegate {

        //
        //  Decrypt Key
        //

        /**
         *  1. Decode 'message.key' to encrypted symmetric key data
         *
         * @param key - base64 string object
         * @param sMsg - secure message object
         * @return encrypted symmetric key data
         */
        byte[] decodeKey(Object key, SecureMessage sMsg);

        /**
         *  2. Decrypt 'message.key' with receiver's private key
         *
         *  @param key - encrypted symmetric key data
         *  @param sender - sender/member ID string
         *  @param receiver - receiver/group ID string
         *  @param sMsg - secure message object
         *  @return serialized symmetric key
         */
        byte[] decryptKey(byte[] key, ID sender, ID receiver, SecureMessage sMsg);

        /**
         *  3. Deserialize message key from data (JsON / ProtoBuf / ...)
         *
         * @param key - serialized key data
         * @param sender - sender/member ID string
         * @param receiver - receiver/group ID string
         * @param sMsg - secure message object
         * @return symmetric key
         */
        SymmetricKey deserializeKey(byte[] key, ID sender, ID receiver, SecureMessage sMsg);

        //
        //  Decrypt Content
        //

        /**
         *  4. Decode 'message.data' to encrypted content data
         *
         * @param data - base64 string object
         * @param sMsg - secure message object
         * @return encrypted content data
         */
        byte[] decodeData(Object data, SecureMessage sMsg);

        /**
         *  5. Decrypt 'message.data' with symmetric key
         *
         *  @param data - encrypt content data
         *  @param password - symmetric key
         *  @param sMsg - secure message object
         *  @return serialized message content
         */
        byte[] decryptContent(byte[] data, SymmetricKey password, SecureMessage sMsg);

        /**
         *  6. Deserialize message content from data (JsON / ProtoBuf / ...)
         *
         * @param data - serialized content data
         * @param password - symmetric key
         * @param sMsg - secure message object
         * @return message content
         */
        Content deserializeContent(byte[] data, SymmetricKey password, SecureMessage sMsg);

        //
        //  Signature
        //

        /**
         *  1. Sign 'message.data' with sender's private key
         *
         *  @param data - encrypted message data
         *  @param sender - sender ID string
         *  @param sMsg - secure message object
         *  @return signature of encrypted message data
         */
        byte[] signData(byte[] data, ID sender, SecureMessage sMsg);

        /**
         *  2. Encode 'message.signature' to String (Base64)
         *
         * @param signature - signature of message.data
         * @param sMsg - secure message object
         * @return String object
         */
        Object encodeSignature(byte[] signature, SecureMessage sMsg);
    }

    //
    //  Factory method
    //
    static SecureMessage parse(Map<String, Object> msg) {
        if (msg == null) {
            return null;
        } else if (msg instanceof SecureMessage) {
            return (SecureMessage) msg;
        } else if (msg instanceof MapWrapper) {
            msg = ((MapWrapper) msg).getMap();
        }
        Factory factory = getFactory();
        assert factory != null : "secure message factory not ready";
        return factory.parseSecureMessage(msg);
    }

    static Factory getFactory() {
        return Factories.secureMessageFactory;
    }
    static void setFactory(Factory factory) {
        Factories.secureMessageFactory = factory;
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
         * @return SecureMessage
         */
        SecureMessage parseSecureMessage(Map<String, Object> msg);
    }
}
