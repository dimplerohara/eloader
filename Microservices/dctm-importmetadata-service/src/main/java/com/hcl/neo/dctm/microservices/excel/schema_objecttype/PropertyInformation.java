//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.06.18 at 11:28:07 AM MST 
//


package com.hcl.neo.dctm.microservices.excel.schema_objecttype;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PropertyInformation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PropertyInformation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DefaultValues" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ValueAssist" type="{urn:rm.bulk.hcl.neo.com:schema.objecttype.20141806}ValueAssist" minOccurs="0"/>
 *         &lt;element name="ValueMap" type="{urn:rm.bulk.hcl.neo.com:schema.objecttype.20141806}ValueInformation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Dependencies" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="datatype" use="required" type="{urn:rm.bulk.hcl.neo.com:schema.objecttype.20141806}DataType" />
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="description" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="isArray" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="isSearchable" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="isDynamic" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="length" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="isNotNull" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="isRequired" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="isReadOnly" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="isHidden" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PropertyInformation", propOrder = {
    "defaultValues",
    "valueAssist",
    "valueMap",
    "dependencies"
})
public class PropertyInformation {

    @XmlElement(name = "DefaultValues")
    protected List<String> defaultValues;
    @XmlElement(name = "ValueAssist")
    protected ValueAssist valueAssist;
    @XmlElement(name = "ValueMap")
    protected List<ValueInformation> valueMap;
    @XmlElement(name = "Dependencies")
    protected List<String> dependencies;
    @XmlAttribute(name = "datatype", required = true)
    protected DataType datatype;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "label")
    protected String label;
    @XmlAttribute(name = "description")
    protected String description;
    @XmlAttribute(name = "isArray", required = true)
    protected boolean isArray;
    @XmlAttribute(name = "isSearchable", required = true)
    protected boolean isSearchable;
    @XmlAttribute(name = "isDynamic", required = true)
    protected boolean isDynamic;
    @XmlAttribute(name = "length", required = true)
    protected int length;
    @XmlAttribute(name = "isNotNull", required = true)
    protected boolean isNotNull;
    @XmlAttribute(name = "isRequired", required = true)
    protected boolean isRequired;
    @XmlAttribute(name = "isReadOnly", required = true)
    protected boolean isReadOnly;
    @XmlAttribute(name = "isHidden", required = true)
    protected boolean isHidden;

    /**
     * Gets the value of the defaultValues property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the defaultValues property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDefaultValues().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getDefaultValues() {
        if (defaultValues == null) {
            defaultValues = new ArrayList<String>();
        }
        return this.defaultValues;
    }

    /**
     * Gets the value of the valueAssist property.
     * 
     * @return
     *     possible object is
     *     {@link ValueAssist }
     *     
     */
    public ValueAssist getValueAssist() {
        return valueAssist;
    }

    /**
     * Sets the value of the valueAssist property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValueAssist }
     *     
     */
    public void setValueAssist(ValueAssist value) {
        this.valueAssist = value;
    }

    /**
     * Gets the value of the valueMap property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the valueMap property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValueMap().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ValueInformation }
     * 
     * 
     */
    public List<ValueInformation> getValueMap() {
        if (valueMap == null) {
            valueMap = new ArrayList<ValueInformation>();
        }
        return this.valueMap;
    }

    /**
     * Gets the value of the dependencies property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dependencies property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDependencies().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getDependencies() {
        if (dependencies == null) {
            dependencies = new ArrayList<String>();
        }
        return this.dependencies;
    }

    /**
     * Gets the value of the datatype property.
     * 
     * @return
     *     possible object is
     *     {@link DataType }
     *     
     */
    public DataType getDatatype() {
        return datatype;
    }

    /**
     * Sets the value of the datatype property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataType }
     *     
     */
    public void setDatatype(DataType value) {
        this.datatype = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabel(String value) {
        this.label = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the isArray property.
     * 
     */
    public boolean isIsArray() {
        return isArray;
    }

    /**
     * Sets the value of the isArray property.
     * 
     */
    public void setIsArray(boolean value) {
        this.isArray = value;
    }

    /**
     * Gets the value of the isSearchable property.
     * 
     */
    public boolean isIsSearchable() {
        return isSearchable;
    }

    /**
     * Sets the value of the isSearchable property.
     * 
     */
    public void setIsSearchable(boolean value) {
        this.isSearchable = value;
    }

    /**
     * Gets the value of the isDynamic property.
     * 
     */
    public boolean isIsDynamic() {
        return isDynamic;
    }

    /**
     * Sets the value of the isDynamic property.
     * 
     */
    public void setIsDynamic(boolean value) {
        this.isDynamic = value;
    }

    /**
     * Gets the value of the length property.
     * 
     */
    public int getLength() {
        return length;
    }

    /**
     * Sets the value of the length property.
     * 
     */
    public void setLength(int value) {
        this.length = value;
    }

    /**
     * Gets the value of the isNotNull property.
     * 
     */
    public boolean isIsNotNull() {
        return isNotNull;
    }

    /**
     * Sets the value of the isNotNull property.
     * 
     */
    public void setIsNotNull(boolean value) {
        this.isNotNull = value;
    }

    /**
     * Gets the value of the isRequired property.
     * 
     */
    public boolean isIsRequired() {
        return isRequired;
    }

    /**
     * Sets the value of the isRequired property.
     * 
     */
    public void setIsRequired(boolean value) {
        this.isRequired = value;
    }

    /**
     * Gets the value of the isReadOnly property.
     * 
     */
    public boolean isIsReadOnly() {
        return isReadOnly;
    }

    /**
     * Sets the value of the isReadOnly property.
     * 
     */
    public void setIsReadOnly(boolean value) {
        this.isReadOnly = value;
    }

    /**
     * Gets the value of the isHidden property.
     * 
     */
    public boolean isIsHidden() {
        return isHidden;
    }

    /**
     * Sets the value of the isHidden property.
     * 
     */
    public void setIsHidden(boolean value) {
        this.isHidden = value;
    }

}