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
package chat.dim;

public interface InstantMessageDelegate<ID, KEY> extends MessageDelegate<ID> {

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
    byte[] serializeContent(Content<ID> content, KEY password, InstantMessage<ID, KEY> iMsg);

    /**
     *  2. Encrypt content data to 'message.data' with symmetric key
     *
     * @param iMsg - instant message object
     * @param data - serialized data of message.content
     * @param password - symmetric key
     * @return encrypted message content data
     */
    byte[] encryptContent(byte[] data, KEY password, InstantMessage<ID, KEY> iMsg);

    /**
     *  3. Encode 'message.data' to String (Base64)
     *
     * @param iMsg - instant message object
     * @param data - encrypted content data
     * @return String object
     */
    Object encodeData(byte[] data, InstantMessage<ID, KEY> iMsg);

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
    byte[] serializeKey(KEY password, InstantMessage<ID, KEY> iMsg);

    /**
     *  5. Encrypt key data to 'message.key' with receiver's public key
     *
     * @param iMsg - instant message object
     * @param data - serialized data of symmetric key
     * @param receiver - receiver ID string
     * @return encrypted symmetric key data
     */
    byte[] encryptKey(byte[] data, ID receiver, InstantMessage<ID, KEY> iMsg);

    /**
     *  6. Encode 'message.key' to String (Base64)
     *
     * @param iMsg - instant message object
     * @param data - encrypted symmetric key data
     * @return String object
     */
    Object encodeKey(byte[] data, InstantMessage<ID, KEY> iMsg);
}
