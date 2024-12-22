/* license: https://mit-license.org
 *
 *  Dao-Ke-Dao: Universal Message Module
 *
 *                                Written in 2022 by Moky <albert.moky@gmail.com>
 *
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Albert Moky
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
package chat.dim.plugins;

import java.util.Date;
import java.util.Map;

import chat.dim.protocol.Content;
import chat.dim.protocol.Envelope;
import chat.dim.protocol.ID;
import chat.dim.protocol.InstantMessage;
import chat.dim.protocol.ReliableMessage;
import chat.dim.protocol.SecureMessage;

/**
 *  Message GeneralFactory
 *  ~~~~~~~~~~~~~~~~~~~~~~
 */
public interface MessageHelper {

    //
    //  Content
    //

    void setContentFactory(int type, Content.Factory factory);

    Content.Factory getContentFactory(int type);

    int getContentType(Map<?, ?> content, int defaultValue);

    Content parseContent(Object content);

    //
    //  Envelope
    //

    void setEnvelopeFactory(Envelope.Factory factory);

    Envelope.Factory getEnvelopeFactory();

    Envelope createEnvelope(ID from, ID to, Date when);

    Envelope parseEnvelope(Object env);

    //
    //  InstantMessage
    //

    void setInstantMessageFactory(InstantMessage.Factory factory);

    InstantMessage.Factory getInstantMessageFactory();

    InstantMessage createInstantMessage(Envelope head, Content body);

    InstantMessage parseInstantMessage(Object msg);

    long generateSerialNumber(int msgType, Date now);

    //
    //  SecureMessage
    //

    void setSecureMessageFactory(SecureMessage.Factory factory);

    SecureMessage.Factory getSecureMessageFactory();

    SecureMessage parseSecureMessage(Object msg);

    //
    //  ReliableMessage
    //

    void setReliableMessageFactory(ReliableMessage.Factory factory);

    ReliableMessage.Factory getReliableMessageFactory();

    ReliableMessage parseReliableMessage(Object msg);

}
