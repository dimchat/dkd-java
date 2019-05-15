/* license: https://mit-license.org
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

public interface SecureMessageDelegate {

    /**
     *  Decrypt key data to a symmetric key with receiver's private key
     *
     *  @param sMsg - secure message object
     *  @param key - encrypted key data
     *  @param sender - sender ID/string
     *  @param receiver - receiver(group) ID/string
     *  @return symmetric key
     */
    Map<String, Object> decryptKey(SecureMessage sMsg, byte[] key, Object sender, Object receiver);

    /**
     *  Decrypt encrypted data to message.content with symmetric key
     *
     *  @param sMsg - secure message object
     *  @param data - encrypt content data
     *  @param password - symmetric key
     *  @return message content
     */
    Content decryptContent(SecureMessage sMsg, byte[] data, Map<String, Object> password);

    /**
     *  Sign the message data(encrypted) with sender's private key
     *
     *  @param sMsg - secure message object
     *  @param data - encrypted message data
     *  @param sender - sender ID/string
     *  @return signature
     */
    byte[] signData(SecureMessage sMsg, byte[] data, Object sender);
}
