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

public interface InstantMessageDelegate extends MessageDelegate {

    /**
     *  Encrypt 'message.content' to 'message.data' with symmetric key
     *
     *  @param content - content object
     *  @param password - symmetric key
     *  @param iMsg - instant message object
     *  @return encrypted message content data
     */
    byte[] encryptContent(Content content, Map<String, Object> password, InstantMessage iMsg);

    /**
     *  Encode 'message.data' to String(Base64)
     *
     * @param data - encrypted content data
     * @param iMsg - instant message object
     * @return String object
     */
    Object encodeData(byte[] data, InstantMessage iMsg);

    /**
     *  Encrypt 'message.key' with receiver's public key
     *
     *  @param iMsg - instant message object
     *  @param password - symmetric key to be encrypted
     *  @param receiver - receiver ID/string
     *  @return encrypted key data
     */
    byte[] encryptKey(Map<String, Object> password, Object receiver, InstantMessage iMsg);

    /**
     *  Encode 'message.key' to String(Base64)
     *
     * @param key - encrypted key data
     * @param iMsg - instant message object
     * @return String object
     */
    Object encodeKey(byte[] key, InstantMessage iMsg);
}
