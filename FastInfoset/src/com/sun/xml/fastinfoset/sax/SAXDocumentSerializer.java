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


package com.sun.xml.fastinfoset.sax;

import com.sun.xml.fastinfoset.Encoder;
import com.sun.xml.fastinfoset.EncodingConstants;
import com.sun.xml.fastinfoset.util.AccessibleByteArrayOutputStream;
import com.sun.xml.fastinfoset.util.CharArray;
import com.sun.xml.fastinfoset.util.CharArrayString;
import com.sun.xml.fastinfoset.vocab.SerializerVocabulary;
import java.io.IOException;
import java.io.OutputStream;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public class SAXDocumentSerializer extends Encoder implements LexicalHandler {
    protected boolean _elementHasNamespaces = false;
        
    protected AccessibleByteArrayOutputStream _elementWithNamespacesOutputStream;
    protected OutputStream _outputStream;

    public SAXDocumentSerializer() {
        _elementWithNamespacesOutputStream = new AccessibleByteArrayOutputStream();
    }

    public void setOutputStream(OutputStream outputStream) {
        super.setOutputStream(outputStream);
        _outputStream = outputStream;
    }
    
    public final void startDocument() throws SAXException {
        try {
            encodeHeader(false);
            encodeInitialVocabulary();
        } catch (IOException e) {
            throw new SAXException("startDocument", e);
        }
    }

    public final void endDocument() throws SAXException {
        try {
            encodeDocumentTermination();
        } catch (IOException e) {
            throw new SAXException("endDocument", e);
        }
    }
    
    public final void startPrefixMapping(String prefix, String uri) throws SAXException {
        try {
            if (_elementHasNamespaces == false) {
                encodeTermination();
                _elementHasNamespaces = true;
                _elementWithNamespacesOutputStream.reset();
                _s = _elementWithNamespacesOutputStream;
                
                // Write out Element byte with namespaces
                _s.write(EncodingConstants.ELEMENT | EncodingConstants.ELEMENT_NAMESPACES_FLAG);
            }
            
            encodeNamespaceAttribute(prefix, uri);
        } catch (IOException e) {
            throw new SAXException("startElement", e);
        }
    }

    public final void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        // TODO consider using buffer for encoding of attributes, then pre-counting is not necessary
        final int attributeCount = (atts.getLength() > 0) ? countAttributes(atts) : 0;
        try {
            if (_elementHasNamespaces) {
                _elementHasNamespaces = false;

                byte[] buffer = _elementWithNamespacesOutputStream.getByteArray();
                if (attributeCount > 0) {
                    buffer[0] |= EncodingConstants.ELEMENT_ATTRIBUTE_FLAG;
                }
                
                _s = _outputStream;
                _s.write(buffer, 0, _elementWithNamespacesOutputStream.size());
                _s.write(EncodingConstants.TERMINATOR);
                
                _b = 0;
            } else {
                encodeTermination();
                
                _b = EncodingConstants.ELEMENT;
                if (attributeCount > 0) {
                    _b |= EncodingConstants.ELEMENT_ATTRIBUTE_FLAG;
                }
            }

            String prefix = getPrefixFromQualifiedName(qName);
            encodeElementQualifiedNameOnThirdBit(namespaceURI, prefix, localName);
            
            if (attributeCount > 0) {
                for (int i = 0; i < atts.getLength(); i++) {
                    prefix = getPrefixFromQualifiedName(atts.getQName(i));
                    encodeAttributeQualifiedNameAndValueOnSecondBit(atts.getURI(i), prefix, atts.getLocalName(i), atts.getValue(i));
                }
                _b = EncodingConstants.TERMINATOR;
                _terminate = true;
            }
        } catch (IOException e) {
            throw new SAXException("startElement", e);
        }
    }

    public final int countAttributes(Attributes atts) {
        // Count attributes ignoring any in the XMLNS namespace
        // Note, such attributes may be produced when transforming from a DOM node
        int count = 0;
        for (int i = 0; i < atts.getLength(); i++) {
            final String uri = atts.getURI(i);
            if (uri == "http://www.w3.org/2000/xmlns/" || uri.equals("http://www.w3.org/2000/xmlns/")) {
                continue;
            }
            count++;
        }
        return count;
    }
    
    public final void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        try {
            encodeElementTermination();
        } catch (IOException e) {
            throw new SAXException("startElement", e);
        }
    }
        
    public final void characters(char[] ch, int start, int length) throws SAXException {
        try {
            if (length == 0) {
                return;
            }
            encodeTermination();
            
            encodeCharacters(ch, start, length);
        } catch (IOException e) {
            throw new SAXException("startElement", e);
        }
    }    

    public final void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }
    
    public final void processingInstruction(String target, String data) throws SAXException {
        try {
            if (target == "") {
                throw new SAXException("processingInstruction: Target is empty");
            }
            encodeTermination();
            
            encodeProcessingInstruction(target, data);
        } catch (IOException e) {
            throw new SAXException("processingInstruction", e);
        }        
    }
    
    public final void setDocumentLocator(org.xml.sax.Locator locator) {
    }
    
    public final void skippedEntity(String name) throws SAXException {
    }
    
       

    // LexicalHandler
    
    public final void comment(char[] ch, int start, int length) throws SAXException {
        try {
            encodeTermination();
            
            encodeComment(ch, start, length);
        } catch (IOException e) {
            throw new SAXException("startElement", e);
        }
    }
  
    public final void startCDATA() throws SAXException {
    }
  
    public final void endCDATA() throws SAXException {
    }
    
    public final void startDTD(String name, String publicId, String systemId) throws SAXException {
    }

    public final void endDTD() throws SAXException {
    }
    
    public final void startEntity(String name) throws SAXException {
    }

    public final void endEntity(String name) throws SAXException {
    }    
}