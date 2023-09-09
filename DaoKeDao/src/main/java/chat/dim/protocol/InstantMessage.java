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
import java.util.Map;

import chat.dim.msg.FactoryManager;

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
    // only for rebuild content
    void setContent(Content body);

    //
    //  Factory methods
    //
    static InstantMessage create(Envelope head, Content body) {
        FactoryManager man = FactoryManager.getInstance();
        return man.generalFactory.createInstantMessage(head, body);
    }
    static InstantMessage parse(Object msg) {
        FactoryManager man = FactoryManager.getInstance();
        return man.generalFactory.parseInstantMessage(msg);
    }

    static long generateSerialNumber(int msgType, Date now) {
        FactoryManager man = FactoryManager.getInstance();
        return man.generalFactory.generateSerialNumber(msgType, now);
    }

    static Factory getFactory() {
        FactoryManager man = FactoryManager.getInstance();
        return man.generalFactory.getInstantMessageFactory();
    }
    static void setFactory(Factory factory) {
        FactoryManager man = FactoryManager.getInstance();
        man.generalFactory.setInstantMessageFactory(factory);
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
