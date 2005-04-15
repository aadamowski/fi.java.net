/*
 * Fast Infoset ver. 0.1 software ("Software")
 *
 * Copyright, 2004-2005 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Software is licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at:
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations.
 *
 *    Sun supports and benefits from the global community of open source
 * developers, and thanks the community for its important contributions and
 * open standards-based technology, which Sun has adopted into many of its
 * products.
 *
 *    Please note that portions of Software may be provided with notices and
 * open source licenses from such communities and third parties that govern the
 * use of those portions, and any licenses granted hereunder do not alter any
 * rights and obligations you may have under such open source licenses,
 * however, the disclaimer of warranty and limitation of liability provisions
 * in this License will apply to all Software in this distribution.
 *
 *    You acknowledge that the Software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any nuclear
 * facility.
 *
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 */

package com.sun.xml.fastinfoset.algorithm;

import com.sun.xml.fastinfoset.algorithm.BuiltInEncodingAlgorithm.WordListener;
import java.util.ArrayList;
import java.util.List;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.CharBuffer;
import java.util.ArrayList;
import org.jvnet.fastinfoset.EncodingAlgorithmException;

public class UUIDEncodingAlgorithm extends LongEncodingAlgorithm {
    
    public final Object convertFromCharacters(char[] ch, int start, int length) {
        final CharBuffer cb = CharBuffer.wrap(ch, start, length);
        final List longList = new ArrayList();
        
        matchWhiteSpaceDelimnatedWords(cb,
                new WordListener() {
            public void word(int start, int end) {
                String uuidValue = cb.subSequence(start, end).toString();
                fromUUIDString(uuidValue);
                longList.add(_msb);
                longList.add(_lsb);
            }
        }
        );
        
        return generateArrayFromList(longList);
    }
    
    public final void convertToCharacters(Object data, StringBuffer s) {
        if (!(data instanceof long[])) {
            throw new IllegalArgumentException("'data' not an instance of int[]");
        }
        
        final long[] ldata = (long[])data;

        for (int i = 0; i < ldata.length; i += 2) {
            s.append(toUUIDString(ldata[i], ldata[i + 1]));
            if (i != ldata.length) {
                s.append(' ');
            }
        }
        
    }    

    
    private long _msb;
    private long _lsb;
    
    final void fromUUIDString(String name) {
        String[] components = name.split("-");
        if (components.length != 5)
            throw new IllegalArgumentException("Invalid UUID string: "+name);
        for (int i=0; i<5; i++)
            components[i] = "0x"+components[i];

        _msb = Long.parseLong(components[0], 16);
        _msb <<= 16;
        _msb |= Long.parseLong(components[1], 16);
        _msb <<= 16;
        _msb |= Long.parseLong(components[2], 16);

        _lsb = Long.parseLong(components[3], 16);
        _lsb <<= 48;
        _lsb |= Long.parseLong(components[4], 16);
    }

    final String toUUIDString(long msb, long lsb) {
	return (digits(msb >> 32, 8) + "-" +
		digits(msb >> 16, 4) + "-" +
		digits(msb, 4) + "-" +
		digits(lsb >> 48, 4) + "-" +
		digits(lsb, 12));
    }
    
    final String digits(long val, int digits) {
	long hi = 1L << (digits * 4);
	return Long.toHexString(hi | (val & (hi - 1))).substring(1);
    }
    
}
