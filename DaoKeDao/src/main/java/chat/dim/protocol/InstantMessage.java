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

import java.util.Date;
import java.util.List;
import java.util.Map;

import chat.dim.crypto.SymmetricKey;
import chat.dim.dkd.Factories;
import chat.dim.type.MapWrapper;

/**
 *  Instant Message
 *  ~~~~~~~~~~~~~~~
 *
 *  data format: {
 *      //-- envelope
 *      sender   : "moki@xxx",
 *      receiver : "hulk@yyy",
 *      time     : 123,
 *      //-- content
 *      content  : {...}
 *  }
 */
public interface InstantMessage extends Message {

    // message content
    Content getContent();

    /*
     *  Encrypt the Instant Message to Secure Message
     *
     *    +----------+      +----------+
     *    | sender   |      | sender   |
     *    | receiver |      | receiver |
     *    | time     |  ->  | time     |
     *    |          |      |          |
     *    | content  |      | data     |  1. data = encrypt(content, PW)
     *    +----------+      | key/keys |  2. key  = encrypt(PW, receiver.PK)
     *                      +----------+
     */

    /**
     *  Encrypt message, replace 'content' field with encrypted 'data'
     *
     * @param password - symmetric key
     * @return SecureMessage object
     */
    SecureMessage encrypt(SymmetricKey password);

    /**
     *  Encrypt group message, replace 'content' field with encrypted 'data'
     *
     * @param password - symmetric key
     * @param members - group members
     * @return SecureMessage object
     */
    SecureMessage encrypt(SymmetricKey password, List<ID> members);

    /**
     *  Instant Message Delegate
     *  ~~~~~~~~~~~~~~~~~~~~~~~~
     */
    interface Delegate extends Message.Delegate {

        //
        //  Encrypt Content
        //

        /**
         *  1. Serialize 'message.content' to data (JsON / ProtoBuf / ...)
         *
         * @param iMsg - instant message object
         * @param content - message.content
         * @param password - symmetric key
         * @return serialized content data
         */
        byte[] serializeContent(Content content, SymmetricKey password, InstantMessage iMsg);

        /**
         *  2. Encrypt content data to 'message.data' with symmetric key
         *
         * @param iMsg - instant message object
         * @param data - serialized data of message.content
         * @param password - symmetric key
         * @return encrypted message content data
         */
        byte[] encryptContent(byte[] data, SymmetricKey password, InstantMessage iMsg);

        /**
         *  3. Encode 'message.data' to String (Base64)
         *
         * @param iMsg - instant message object
         * @param data - encrypted content data
         * @return String object
         */
        Object encodeData(byte[] data, InstantMessage iMsg);

        //
        //  Encrypt Key
        //

        /**
         *  4. Serialize message key to data (JsON / ProtoBuf / ...)
         *
         * @param iMsg - instant message object
         * @param password - symmetric key
         * @return serialized key data
         */
        byte[] serializeKey(SymmetricKey password, InstantMessage iMsg);

        /**
         *  5. Encrypt key data to 'message.key' with receiver's public key
         *
         * @param iMsg - instant message object
         * @param data - serialized data of symmetric key
         * @param receiver - receiver ID string
         * @return encrypted symmetric key data
         */
        byte[] encryptKey(byte[] data, ID receiver, InstantMessage iMsg);

        /**
         *  6. Encode 'message.key' to String (Base64)
         *
         * @param iMsg - instant message object
         * @param data - encrypted symmetric key data
         * @return String object
         */
        Object encodeKey(byte[] data, InstantMessage iMsg);
    }

    //
    //  Factory methods
    //
    static InstantMessage create(Envelope head, Content body) {
        Factory factory = getFactory();
        assert factory != null : "instant message factory not ready";
        return factory.createInstantMessage(head, body);
    }
    static InstantMessage parse(Map<String, Object> msg) {
        if (msg == null) {
            return null;
        } else if (msg instanceof InstantMessage) {
            return (InstantMessage) msg;
        } else if (msg instanceof MapWrapper) {
            msg = ((MapWrapper) msg).getMap();
        }
        Factory factory = getFactory();
        assert factory != null : "instant message factory not ready";
        return factory.parseInstantMessage(msg);
    }

    static Factory getFactory() {
        return Factories.instantMessageFactory;
    }
    static void setFactory(Factory factory) {
        Factories.instantMessageFactory = factory;
    }

    static long generateSerialNumber(int msgType, Date now) {
        Factory factory = getFactory();
        assert factory != null : "instant message factory not ready";
        return factory.generateSerialNumber(msgType, now);
    }

    /**
     *  Message Factory
     *  ~~~~~~~~~~~~~~~
     */
    interface Factory {

        /**
         *  Generate SN for message content
         *
         * @param msgType - content type
         * @param now     - message time
         * @return SN (serial number as msg id)
         */
        long generateSerialNumber(int msgType, Date now);

        /**
         *  Create instant message with envelope & content
         *
         * @param head - message envelope
         * @param body - message content
         * @return InstantMessage
         */
        InstantMessage createInstantMessage(Envelope head, Content body);

        /**
         *  Parse map object to message
         *
         * @param msg - message info
         * @return InstantMessage
         */
        InstantMessage parseInstantMessage(Map<String, Object> msg);
    }
}
