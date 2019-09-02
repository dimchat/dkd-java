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
     *  Decode 'message.key' to encrypted symmetric key data
     *
     * @param key - base64 string object
     * @param sMsg - secure message object
     * @return encrypted symmetric key data
     */
    byte[] decodeKey(Object key, SecureMessage sMsg);

    /**
     *  Decrypt 'message.key' with receiver's private key
     *
     *  @param key - encrypted symmetric key data
     *  @param sender - sender/member ID string
     *  @param receiver - receiver/group ID string
     *  @param sMsg - secure message object
     *  @return symmetric key
     */
    Map<String, Object> decryptKey(byte[] key, Object sender, Object receiver, SecureMessage sMsg);

    /**
     *  Decode 'message.data' to encrypted content data
     *
     * @param data - base64 string object
     * @param sMsg - secure message object
     * @return encrypted content data
     */
    byte[] decodeData(Object data, SecureMessage sMsg);

    /**
     *  Decrypt 'message.data' with symmetric key
     *
     *  @param data - encrypt content data
     *  @param password - symmetric key
     *  @param sMsg - secure message object
     *  @return content object
     */
    Content decryptContent(byte[] data, Map<String, Object> password, SecureMessage sMsg);

    /**
     *  Sign 'message.data' with sender's private key
     *
     *  @param data - encrypted message data
     *  @param sender - sender ID string
     *  @param sMsg - secure message object
     *  @return signature of encrypted message data
     */
    byte[] signData(byte[] data, Object sender, SecureMessage sMsg);

    /**
     *  Encode 'message.signature' to String(Base64)
     *
     * @param signature - signature of message.data
     * @param sMsg - secure message object
     * @return String object
     */
    Object encodeSignature(byte[] signature, SecureMessage sMsg);
}
