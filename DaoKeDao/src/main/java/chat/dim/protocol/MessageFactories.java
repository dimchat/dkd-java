/* license: https://mit-license.org
 *
 *  Dao-Ke-Dao: Universal Message Module
 *
 *                                Written in 2020 by Moky <albert.moky@gmail.com>
 *
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Albert Moky
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

import java.util.HashMap;
import java.util.Map;

import chat.dim.core.EnvelopeFactory;
import chat.dim.core.MessageFactory;

final class MessageFactories {

    static final Map<Integer, Content.Factory> contentFactories = new HashMap<>();

    static Envelope.Factory envelopeFactory = null;

    static InstantMessage.Factory instantMessageFactory = null;
    static SecureMessage.Factory secureMessageFactory = null;
    static ReliableMessage.Factory reliableMessageFactory = null;

    static {
        registerFactories();
    }

    /**
     *  Register core factories
     */
    private static void registerFactories() {
        // Envelope factory
        EnvelopeFactory env = new EnvelopeFactory();
        Envelope.setFactory(env);

        // Message factories
        MessageFactory msg = new MessageFactory();
        InstantMessage.setFactory(msg);
        SecureMessage.setFactory(msg);
        ReliableMessage.setFactory(msg);
    }
}
