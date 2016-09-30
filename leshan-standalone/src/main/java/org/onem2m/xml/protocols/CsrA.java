//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.28 at 04:33:37 PM CEST 
//


package org.onem2m.xml.protocols;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.onem2m.org/xml/protocols}announcedResource">
 *       &lt;sequence>
 *         &lt;element name="cst" type="{http://www.onem2m.org/xml/protocols}cseTypeID" minOccurs="0"/>
 *         &lt;element name="poa" type="{http://www.onem2m.org/xml/protocols}poaList" minOccurs="0"/>
 *         &lt;element name="cb" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="csi" type="{http://www.onem2m.org/xml/protocols}ID" minOccurs="0"/>
 *         &lt;element name="rr" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="nl" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="ch" type="{http://www.onem2m.org/xml/protocols}childResourceRef" maxOccurs="unbounded"/>
 *           &lt;choice maxOccurs="unbounded">
 *             &lt;element ref="{http://www.onem2m.org/xml/protocols}nodA"/>
 *             &lt;element ref="{http://www.onem2m.org/xml/protocols}cnt"/>
 *             &lt;element ref="{http://www.onem2m.org/xml/protocols}cntA"/>
 *             &lt;element ref="{http://www.onem2m.org/xml/protocols}grp"/>
 *             &lt;element ref="{http://www.onem2m.org/xml/protocols}grpA"/>
 *             &lt;element ref="{http://www.onem2m.org/xml/protocols}acp"/>
 *             &lt;element ref="{http://www.onem2m.org/xml/protocols}acpA"/>
 *             &lt;element ref="{http://www.onem2m.org/xml/protocols}sub"/>
 *             &lt;element ref="{http://www.onem2m.org/xml/protocols}pch"/>
 *             &lt;element ref="{http://www.onem2m.org/xml/protocols}schA"/>
 *             &lt;element ref="{http://www.onem2m.org/xml/protocols}lcpA"/>
 *           &lt;/choice>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "cst",
    "poa",
    "cb",
    "csi",
    "rr",
    "nl",
    "ch",
    "nodAOrCntOrCntA"
})
@XmlRootElement(name = "csrA")
public class CsrA
    extends AnnouncedResource
{

    protected BigInteger cst;
    @XmlList
    protected List<String> poa;
    @XmlSchemaType(name = "anyURI")
    protected String cb;
    protected String csi;
    protected Boolean rr;
    @XmlSchemaType(name = "anyURI")
    protected String nl;
    protected List<ChildResourceRef> ch;
    @XmlElements({
        @XmlElement(name = "nodA", namespace = "http://www.onem2m.org/xml/protocols", type = NodA.class),
        @XmlElement(name = "cnt", namespace = "http://www.onem2m.org/xml/protocols", type = Cnt.class),
        @XmlElement(name = "cntA", namespace = "http://www.onem2m.org/xml/protocols", type = CntA.class),
        @XmlElement(name = "grp", namespace = "http://www.onem2m.org/xml/protocols", type = Grp.class),
        @XmlElement(name = "grpA", namespace = "http://www.onem2m.org/xml/protocols", type = GrpA.class),
        @XmlElement(name = "acp", namespace = "http://www.onem2m.org/xml/protocols", type = Acp.class),
        @XmlElement(name = "acpA", namespace = "http://www.onem2m.org/xml/protocols", type = AcpA.class),
        @XmlElement(name = "sub", namespace = "http://www.onem2m.org/xml/protocols", type = Sub.class),
        @XmlElement(name = "pch", namespace = "http://www.onem2m.org/xml/protocols", type = Pch.class),
        @XmlElement(name = "schA", namespace = "http://www.onem2m.org/xml/protocols", type = SchA.class),
        @XmlElement(name = "lcpA", namespace = "http://www.onem2m.org/xml/protocols", type = LcpA.class)
    })
    protected List<Resource> nodAOrCntOrCntA;

    /**
     * Gets the value of the cst property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCst() {
        return cst;
    }

    /**
     * Sets the value of the cst property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCst(BigInteger value) {
        this.cst = value;
    }

    /**
     * Gets the value of the poa property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the poa property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPoa().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getPoa() {
        if (poa == null) {
            poa = new ArrayList<String>();
        }
        return this.poa;
    }

    /**
     * Gets the value of the cb property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCb() {
        return cb;
    }

    /**
     * Sets the value of the cb property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCb(String value) {
        this.cb = value;
    }

    /**
     * Gets the value of the csi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCsi() {
        return csi;
    }

    /**
     * Sets the value of the csi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCsi(String value) {
        this.csi = value;
    }

    /**
     * Gets the value of the rr property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRr() {
        return rr;
    }

    /**
     * Sets the value of the rr property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRr(Boolean value) {
        this.rr = value;
    }

    /**
     * Gets the value of the nl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNl() {
        return nl;
    }

    /**
     * Sets the value of the nl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNl(String value) {
        this.nl = value;
    }

    /**
     * Gets the value of the ch property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ch property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCh().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ChildResourceRef }
     * 
     * 
     */
    public List<ChildResourceRef> getCh() {
        if (ch == null) {
            ch = new ArrayList<ChildResourceRef>();
        }
        return this.ch;
    }

    /**
     * Gets the value of the nodAOrCntOrCntA property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nodAOrCntOrCntA property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNodAOrCntOrCntA().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NodA }
     * {@link Cnt }
     * {@link CntA }
     * {@link Grp }
     * {@link GrpA }
     * {@link Acp }
     * {@link AcpA }
     * {@link Sub }
     * {@link Pch }
     * {@link SchA }
     * {@link LcpA }
     * 
     * 
     */
    public List<Resource> getNodAOrCntOrCntA() {
        if (nodAOrCntOrCntA == null) {
            nodAOrCntOrCntA = new ArrayList<Resource>();
        }
        return this.nodAOrCntOrCntA;
    }

}