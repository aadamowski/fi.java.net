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


package com.sun.xml.fastinfoset;

import java.io.UnsupportedEncodingException;

public final class EncodingConstants {
    public static byte[] XML_DECL = null;

    static {
        try {
            XML_DECL = "<?xml encoding='finf'?>".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            XML_DECL = new byte[23];
            // TODO fill out bytes explicitly
            // then not necessary to call String.getBytes account
            // for failure
        }
    }
    
    public static final byte[] HEADER = {(byte)0xE0, 0, 0, 1};
    
    public static final int DOCUMENT_INITIAL_VOCABULARY_FLAG = 0x10; // 00010000
    public static final int DOCUMENT_NOTATIONS_FLAG = 0x08; // 00001000
    public static final int DOCUMENT_UNPARSED_ENTITIES_FLAG = 0x04; // 00000100
    public static final int DOCUMENT_STANDALONE_FLAG = 0x02; // 00000010
    public static final int DOCUMENT_VERSION_FLAG = 0x01; // 00000001

    public static final int INITIAL_VOCABULARY_EXTERNAL_VOCABULARY_FLAG = 0x10; // 00010000
    public static final int INITIAL_VOCABULARY_RESTRICTED_ALPHABETS_FLAG = 0x08; // 00001000
    public static final int INITIAL_VOCABULARY_ENCODING_ALGORITHMS_FLAG = 0x04; // 00000100
    public static final int INITIAL_VOCABULARY_PREFIXES_FLAG = 0x02; // 00000010
    public static final int INITIAL_VOCABULARY_NAMESPACE_NAMES_FLAG = 0x01; // 00000001
    public static final int INITIAL_VOCABULARY_LOCAL_NAMES_FLAG = 0x80; // 1000000
    public static final int INITIAL_VOCABULARY_OTHER_NCNAMES_FLAG = 0x40; // 01000000
    public static final int INITIAL_VOCABULARY_OTHER_URIS_FLAG = 0x20; // 00100000
    public static final int INITIAL_VOCABULARY_ATTRIBUTE_VALUES_FLAG = 0x10; // 00010000
    public static final int INITIAL_VOCABULARY_CONTENT_CHARACTER_CHUNKS_FLAG = 0x08; // 00001000
    public static final int INITIAL_VOCABULARY_OTHER_STRINGS_FLAG = 0x04; // 00000100
    public static final int INITIAL_VOCABULARY_ELEMENT_NAME_SURROGATES_FLAG = 0x02; // 0000010
    public static final int INITIAL_VOCABULARY_ATTRIBUTE_NAME_SURROGATES_FLAG = 0x01; // 00000001

    public static final int NAME_SURROGATE_PREFIX_FLAG = 0x02;
    public static final int NAME_SURROGATE_NAME_FLAG = 0x01;
    
    public static final int NOTATIONS = 0xC0; // 110000
    public static final int NOTATIONS_MASK = 0xFC; // 6 bits
    public static final int NOTATIONS_SYSTEM_IDENTIFIER_FLAG = 0x02;
    public static final int NOTATIONS_PUBLIC_IDENTIFIER_FLAG = 0x01;
    
    public static final int UNPARSED_ENTITIES = 0xD0; // 1101000
    public static final int UNPARSED_ENTITIES_MASK = 0xFE; // 7 bits
    public static final int UNPARSED_ENTITIES_PUBLIC_IDENTIFIER_FLAG = 0x01;
    
    public static final int PROCESSING_INSTRUCTION = 0xE1; // 11100001
    public static final int PROCESSING_INSTRUCTION_MASK = 0xFF; // 8 bits
    
    public static final int COMMENT = 0xE2; // 11100010
    public static final int COMMENT_MASK = 0xFF; // 8 bits
    
    public static final int DOCUMENT_TYPE_DECLARATION = 0xC4; // 110001
    public static final int DOCUMENT_TYPE_DECLARATION_MASK = 0xFC; // 6 bits
    public static final int DOCUMENT_TYPE_SYSTEM_IDENTIFIER_FLAG = 0x02;
    public static final int DOCUMENT_TYPE_PUBLIC_IDENTIFIER_FLAG = 0x01;
    
    public static final int ELEMENT = 0x00; // 0
    public static final int ELEMENT_ATTRIBUTE_FLAG = 0x40; // 01000000
    public static final int ELEMENT_NAMESPACES_FLAG = 0x38; // 00111000
    public static final int ELEMENT_LITERAL_QNAME_FLAG = 0x3C; // 00111100
    
    public static final int NAMESPACE_ATTRIBUTE = 0xCC; // 110011 00
    public static final int NAMESPACE_ATTRIBUTE_MASK = 0xFC; // 6 bits
    public static final int NAMESPACE_ATTRIBUTE_PREFIX_FLAG = 0x02;
    public static final int NAMESPACE_ATTRIBUTE_NAME_FLAG = 0x01;
    
    public static final int ATTRIBUTE_LITERAL_QNAME_FLAG = 0x78; // 01111000
    
    public static final int LITERAL_QNAME_PREFIX_FLAG = 0x02;
    public static final int LITERAL_QNAME_NAMESPACE_NAME_FLAG = 0x01;

    public static final int CHARACTER_CHUNK = 0x80; // 10
    public static final int CHARACTER_CHUNK_ADD_TO_TABLE_FLAG = 0x10; // 00010000
    
    public static final int UNEXPANDED_ENTITY_REFERENCE = 0xC8; // 110010
    public static final int UNEXPANDED_ENTITY_REFERENCE_MASK = 0xFC; // 6 bits
    public static final int UNEXPANDED_ENTITY_SYSTEM_IDENTIFIER_FLAG = 0x02;
    public static final int UNEXPANDED_ENTITY_PUBLIC_IDENTIFIER_FLAG = 0x01;
    
    public static final int NISTRING_ADD_TO_TABLE_FLAG = 0x40; // 01000000
    
    public static final int TERMINATOR = 0xF0;
    public static final int DOUBLE_TERMINATOR = 0xFF;

    
    public static final int ENCODING_ALGORITHM_BUILTIN_END = 9;
    public static final int ENCODING_ALGORITHM_APPLICATION_START = 31;
    
    
    // Octet string length contants
    
    public static final int OCTET_STRING_LENGTH_SMALL_LIMIT = 0;
    public static final int OCTET_STRING_LENGTH_MEDIUM_LIMIT = 1;
    public static final int OCTET_STRING_LENGTH_MEDIUM_FLAG = 2;
    public static final int OCTET_STRING_LENGTH_LARGE_FLAG = 3;
    
    public static final long OCTET_STRING_MAXIMUM_LENGTH = 4294967296L;
    
    /*
     * C.22
     */    
    public static final int OCTET_STRING_LENGTH_2ND_BIT_SMALL_LIMIT = 65;
    public static final int OCTET_STRING_LENGTH_2ND_BIT_MEDIUM_LIMIT = 321;
    public static final int OCTET_STRING_LENGTH_2ND_BIT_MEDIUM_FLAG = 0x40;
    public static final int OCTET_STRING_LENGTH_2ND_BIT_LARGE_FLAG = 0x60;
    public static final int OCTET_STRING_LENGTH_2ND_BIT_SMALL_MASK = 0x1F;

    public static final int[] OCTET_STRING_LENGTH_2ND_BIT_VALUES = {
        OCTET_STRING_LENGTH_2ND_BIT_SMALL_LIMIT,
        OCTET_STRING_LENGTH_2ND_BIT_MEDIUM_LIMIT,
        OCTET_STRING_LENGTH_2ND_BIT_MEDIUM_FLAG,
        OCTET_STRING_LENGTH_2ND_BIT_LARGE_FLAG
    };
    
    /*
     * C.23
     */    
    public static final int OCTET_STRING_LENGTH_5TH_BIT_SMALL_LIMIT = 9;
    public static final int OCTET_STRING_LENGTH_5TH_BIT_MEDIUM_LIMIT = 265;
    public static final int OCTET_STRING_LENGTH_5TH_BIT_MEDIUM_FLAG = 0x08;
    public static final int OCTET_STRING_LENGTH_5TH_BIT_LARGE_FLAG = 0x0C;
    public static final int OCTET_STRING_LENGTH_5TH_BIT_SMALL_MASK = 0x07;
    
    public static final int[] OCTET_STRING_LENGTH_5TH_BIT_VALUES = {
        OCTET_STRING_LENGTH_5TH_BIT_SMALL_LIMIT,
        OCTET_STRING_LENGTH_5TH_BIT_MEDIUM_LIMIT,
        OCTET_STRING_LENGTH_5TH_BIT_MEDIUM_FLAG,
        OCTET_STRING_LENGTH_5TH_BIT_LARGE_FLAG
    };

    /*
     * C.24
     */
    public static final int OCTET_STRING_LENGTH_7TH_BIT_SMALL_LIMIT = 3;
    public static final int OCTET_STRING_LENGTH_7TH_BIT_MEDIUM_LIMIT = 259;
    public static final int OCTET_STRING_LENGTH_7TH_BIT_MEDIUM_FLAG = 0x02;
    public static final int OCTET_STRING_LENGTH_7TH_BIT_LARGE_FLAG = 0x03;
    public static final int OCTET_STRING_LENGTH_7TH_BIT_SMALL_MASK = 0x01;

    public static final int[] OCTET_STRING_LENGTH_7TH_BIT_VALUES = {
        OCTET_STRING_LENGTH_7TH_BIT_SMALL_LIMIT,
        OCTET_STRING_LENGTH_7TH_BIT_MEDIUM_LIMIT,
        OCTET_STRING_LENGTH_7TH_BIT_MEDIUM_FLAG,
        OCTET_STRING_LENGTH_7TH_BIT_LARGE_FLAG
    };

    
    // Integer
    
    public static final int INTEGER_SMALL_LIMIT = 0;
    public static final int INTEGER_MEDIUM_LIMIT = 1;
    public static final int INTEGER_LARGE_LIMIT = 2;
    public static final int INTEGER_MEDIUM_FLAG = 3;
    public static final int INTEGER_LARGE_FLAG = 4;
    public static final int INTEGER_LARGE_LARGE_FLAG = 5;

    public static final int INTEGER_MAXIMUM_SIZE = 1048576;

    /*
     * C.25
     */
    public static final int INTEGER_2ND_BIT_SMALL_LIMIT = 64;
    public static final int INTEGER_2ND_BIT_MEDIUM_LIMIT = 8256;
    public static final int INTEGER_2ND_BIT_LARGE_LIMIT = INTEGER_MAXIMUM_SIZE;
    public static final int INTEGER_2ND_BIT_MEDIUM_FLAG = 0x40;
    public static final int INTEGER_2ND_BIT_LARGE_FLAG = 0x60;
    public static final int INTEGER_2ND_BIT_SMALL_MASK = 0x3F;
    public static final int INTEGER_2ND_BIT_MEDIUM_MASK = 0x1F;
    public static final int INTEGER_2ND_BIT_LARGE_MASK = 0x0F;
    
    public static final int[] INTEGER_2ND_BIT_VALUES = {
        INTEGER_2ND_BIT_SMALL_LIMIT,
        INTEGER_2ND_BIT_MEDIUM_LIMIT,
        INTEGER_2ND_BIT_LARGE_LIMIT,
        INTEGER_2ND_BIT_MEDIUM_FLAG,
        INTEGER_2ND_BIT_LARGE_FLAG,
        -1
    };

    /*
     * C.27
     */
    public static final int INTEGER_3RD_BIT_SMALL_LIMIT = 32;
    public static final int INTEGER_3RD_BIT_MEDIUM_LIMIT = 2080;
    public static final int INTEGER_3RD_BIT_LARGE_LIMIT = 526368;
    public static final int INTEGER_3RD_BIT_MEDIUM_FLAG = 0x20;
    public static final int INTEGER_3RD_BIT_LARGE_FLAG = 0x28;
    public static final int INTEGER_3RD_BIT_LARGE_LARGE_FLAG = 0x30;
    public static final int INTEGER_3RD_BIT_SMALL_MASK = 0x1F;
    public static final int INTEGER_3RD_BIT_MEDIUM_MASK = 0x07;
    public static final int INTEGER_3RD_BIT_LARGE_MASK = 0x07;
    public static final int INTEGER_3RD_BIT_LARGE_LARGE_MASK = 0x0F;

    public static final int[] INTEGER_3RD_BIT_VALUES = {
        INTEGER_3RD_BIT_SMALL_LIMIT,
        INTEGER_3RD_BIT_MEDIUM_LIMIT,
        INTEGER_3RD_BIT_LARGE_LIMIT,
        INTEGER_3RD_BIT_MEDIUM_FLAG,
        INTEGER_3RD_BIT_LARGE_FLAG,
        INTEGER_3RD_BIT_LARGE_LARGE_FLAG
    };
    
    /*
     * C.28
     */
    public static final int INTEGER_4TH_BIT_SMALL_LIMIT = 16;
    public static final int INTEGER_4TH_BIT_MEDIUM_LIMIT = 1040;
    public static final int INTEGER_4TH_BIT_LARGE_LIMIT = 263184;
    public static final int INTEGER_4TH_BIT_MEDIUM_FLAG = 0x10;
    public static final int INTEGER_4TH_BIT_LARGE_FLAG = 0x14;
    public static final int INTEGER_4TH_BIT_LARGE_LARGE_FLAG = 0x18;    
    public static final int INTEGER_4TH_BIT_SMALL_MASK = 0x0F;
    public static final int INTEGER_4TH_BIT_MEDIUM_MASK = 0x03;
    public static final int INTEGER_4TH_BIT_LARGE_MASK = 0x03;

    public static final int[] INTEGER_4TH_BIT_VALUES = {
        INTEGER_4TH_BIT_SMALL_LIMIT,
        INTEGER_4TH_BIT_MEDIUM_LIMIT,
        INTEGER_4TH_BIT_LARGE_LIMIT,
        INTEGER_4TH_BIT_MEDIUM_FLAG,
        INTEGER_4TH_BIT_LARGE_FLAG,
        INTEGER_4TH_BIT_LARGE_LARGE_FLAG
    };    
}
