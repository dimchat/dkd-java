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

import chat.dim.plugins.MessageSharedHolder;
import chat.dim.type.Mapper;

/**
 *  Message Content
 *  ~~~~~~~~~~~~~~~
 *  This class is for creating message content
 *
 *  data format: {
 *      'type'    : 0x00,           // message type
 *      'sn'      : 0,              // serial number
 *
 *      'time'    : 123,            // message time
 *      'group'   : 'Group ID',     // for group message
 *
 *      //-- message info
 *      'text'    : 'text',         // for text message
 *      'command' : 'Command Name'  // for system command
 *      //...
 *  }
 */
public interface Content extends Mapper {

    // content type
    int getType();

    // serial number as message id
    long getSerialNumber();

    // message time
    Date getTime();

    // Group ID/string for group message
    //    if field 'group' exists, it means this is a group message
    ID getGroup();
    void setGroup(ID group);

    //
    //  Factory method
    //
    static Content parse(Object content) {
        return MessageSharedHolder.helper.parseContent(content);
    }

    static Factory getFactory(int type) {
        return MessageSharedHolder.helper.getContentFactory(type);
    }
    static void setFactory(int type, Factory factory) {
        MessageSharedHolder.helper.setContentFactory(type, factory);
    }

    /**
     *  Content Factory
     *  ~~~~~~~~~~~~~~~
     */
    interface Factory {

        /**
         *  Parse map object to content
         *
         * @param content - content info
         * @return Content
         */
        Content parseContent(Map<String, Object> content);
    }

}
