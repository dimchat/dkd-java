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

import java.util.Date;
import java.util.Map;
import java.util.Random;

import chat.dim.protocol.Content;
import chat.dim.protocol.ContentType;
import chat.dim.protocol.ID;
import chat.dim.type.Dictionary;

/**
 *  Message Content
 *  ~~~~~~~~~~~~~~~
 *  This class is for creating message content
 *
 *  data format: {
 *      'type'    : 0x00,            // message type
 *      'sn'      : 0,               // serial number
 *
 *      'group'   : 'Group ID',      // for group message
 *
 *      //-- message info
 *      'text'    : 'text',          // for text message
 *      'command' : 'Command Name',  // for system command
 *      //...
 *  }
 */
public class BaseContent extends Dictionary implements Content {

    // message type: text, image, ...
    private int type;

    // serial number: random number to identify message content
    private long sn;

    // message time
    private Date time;

    public BaseContent(Map<String, Object> dictionary) {
        super(dictionary);
        // lazy load
        type = 0;
        sn = 0;
        time = null;
    }

    public BaseContent(ContentType msgType) {
        this(msgType.value);
    }

    public BaseContent(int msgType) {
        super();
        type = msgType;
        sn = randomPositiveInteger();
        time = new Date();
        put("type", type);
        put("sn", sn);
        put("time", time.getTime() / 1000);
    }

    private static long randomPositiveInteger() {
        Random random = new Random();
        long sn = random.nextLong();
        if (sn > 0) {
            return sn;
        } else if (sn < 0) {
            return -sn;
        }
        // ZERO? do it again!
        return 9527 + 9394; // randomPositiveInteger();
    }

    @Override
    public int getType() {
        if (type == 0) {
            type = Content.getType(getMap());
        }
        return type;
    }

    @Override
    public long getSerialNumber() {
        if (sn == 0) {
            sn = Content.getSerialNumber(getMap());
        }
        return sn;
    }

    @Override
    public Date getTime() {
        if (time == null) {
            time = Content.getTime(getMap());
        }
        return time;
    }

    // Group ID/string for group message
    //    if field 'group' exists, it means this is a group message
    @Override
    public ID getGroup() {
        return Content.getGroup(getMap());
    }

    @Override
    public void setGroup(ID group) {
        Content.setGroup(group, getMap());
    }
}
