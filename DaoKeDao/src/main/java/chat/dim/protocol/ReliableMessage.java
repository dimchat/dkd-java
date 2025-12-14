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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import chat.dim.ext.SharedMessageExtensions;

/**
 *  Reliable Message signed by an asymmetric key
 *  <p>
 *      This class is used to sign the SecureMessage.
 *      It contains a 'signature' field which signed with sender's private key
 *  </p>
 *
 *  <blockquote><pre>
 *  data format: {
 *      //-- envelope
 *      "sender"   : "moki@xxx",
 *      "receiver" : "hulk@yyy",
 *      "time"     : 123,
 *
 *      //-- content data and key/keys
 *      "data"     : "...",  // base64_encode( symmetric_encrypt(content))
 *      "keys"     : {
 *          "{ID}"   : "...",  // base64_encode(asymmetric_encrypt(pwd))
 *          "digest" : "..."   // hash(pwd.data)
 *      },
 *      //-- signature
 *      "signature": "..."   // base64_encode(asymmetric_sign(data))
 *  }
 *  </pre></blockquote>
 */
public interface ReliableMessage extends SecureMessage {

    byte[] getSignature();

    //
    //  Conveniences
    //

    static List<ReliableMessage> convert(Iterable<?> array) {
        List<ReliableMessage> messages = new ArrayList<>();
        ReliableMessage msg;
        for (Object item : array) {
            msg = parse(item);
            if (msg == null) {
                continue;
            }
            messages.add(msg);
        }
        return messages;
    }
    static List<Map<String, Object>> revert(Iterable<ReliableMessage> messages) {
        List<Map<String, Object>> array = new ArrayList<>();
        for (ReliableMessage msg : messages) {
            array.add(msg.toMap());
        }
        return array;
    }

    //
    //  Factory methods
    //

    static ReliableMessage parse(Object msg) {
        return SharedMessageExtensions.reliableHelper.parseReliableMessage(msg);
    }

    static Factory getFactory() {
        return SharedMessageExtensions.reliableHelper.getReliableMessageFactory();
    }
    static void setFactory(Factory factory) {
        SharedMessageExtensions.reliableHelper.setReliableMessageFactory(factory);
    }

    /**
     *  Message Factory
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
