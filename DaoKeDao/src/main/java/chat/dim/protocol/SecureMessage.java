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

import chat.dim.plugins.SharedMessageExtensions;

/**
 *  Secure Message
 *  <p>
 *      Instant Message encrypted by a symmetric key
 *  </p>
 *
 *  <blockquote><pre>
 *  data format: {
 *      //-- envelope
 *      sender   : "moki@xxx",
 *      receiver : "hulk@yyy",
 *      time     : 123,
 *      //-- content data and key/keys
 *      data     : "...",  // base64_encode( symmetric_encrypt(content))
 *      key      : "...",  // base64_encode(asymmetric_encrypt(password))
 *      keys     : {
 *          "ID1": "key1", // base64_encode(asymmetric_encrypt(password))
 *      }
 *  }
 *  </pre></blockquote>
 */
public interface SecureMessage extends Message {

    byte[] getData();

    byte[] getEncryptedKey();

    // String => String
    Map<String, Object> getEncryptedKeys();

    //
    //  Factory methods
    //
    static SecureMessage parse(Object msg) {
        return SharedMessageExtensions.secureHelper.parseSecureMessage(msg);
    }

    static Factory getFactory() {
        return SharedMessageExtensions.secureHelper.getSecureMessageFactory();
    }
    static void setFactory(Factory factory) {
        SharedMessageExtensions.secureHelper.setSecureMessageFactory(factory);
    }

    /**
     *  General Helper
     */
    interface Helper {

        void setSecureMessageFactory(Factory factory);
        Factory getSecureMessageFactory();

        SecureMessage parseSecureMessage(Object msg);

    }

    /**
     *  Message Factory
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
