//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.06.18 at 11:28:07 AM MST 
//


package com.hcl.neo.dctm.microservices.excel.schema_objecttype;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.hcl.neo.bulk.rm.schema_objecttype package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.hcl.neo.bulk.rm.schema_objecttype
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ObjectTypeSet }
     * 
     */
    public ObjectTypeSet createObjectTypeSet() {
        return new ObjectTypeSet();
    }

    /**
     * Create an instance of {@link TypeInformation }
     * 
     */
    public TypeInformation createTypeInformation() {
        return new TypeInformation();
    }

    /**
     * Create an instance of {@link ValueInformation }
     * 
     */
    public ValueInformation createValueInformation() {
        return new ValueInformation();
    }

    /**
     * Create an instance of {@link ValueAssist }
     * 
     */
    public ValueAssist createValueAssist() {
        return new ValueAssist();
    }

    /**
     * Create an instance of {@link PropertyInformation }
     * 
     */
    public PropertyInformation createPropertyInformation() {
        return new PropertyInformation();
    }

}
