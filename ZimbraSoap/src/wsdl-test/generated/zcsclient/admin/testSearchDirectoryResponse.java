
package generated.zcsclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for searchDirectoryResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="searchDirectoryResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="calresource" type="{urn:zimbraAdmin}calendarResourceInfo"/>
 *           &lt;element ref="{urn:zimbraAdmin}dl"/>
 *           &lt;element ref="{urn:zimbraAdmin}alias"/>
 *           &lt;element name="account" type="{urn:zimbraAdmin}accountInfo"/>
 *           &lt;element ref="{urn:zimbraAdmin}domain"/>
 *           &lt;element name="cos" type="{urn:zimbraAdmin}cosInfo"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="more" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="searchTotal" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "searchDirectoryResponse", propOrder = {
    "calresourceOrDlOrAlias"
})
public class testSearchDirectoryResponse {

    @XmlElements({
        @XmlElement(name = "account", type = testAccountInfo.class),
        @XmlElement(name = "cos", type = testCosInfo.class),
        @XmlElement(name = "dl", type = testDistributionListInfo.class),
        @XmlElement(name = "alias", type = testAliasInfo.class),
        @XmlElement(name = "calresource", type = testCalendarResourceInfo.class),
        @XmlElement(name = "domain", type = testDomainInfo.class)
    })
    protected List<testAdminObjectInfo> calresourceOrDlOrAlias;
    @XmlAttribute(name = "more", required = true)
    protected boolean more;
    @XmlAttribute(name = "searchTotal", required = true)
    protected long searchTotal;

    /**
     * Gets the value of the calresourceOrDlOrAlias property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the calresourceOrDlOrAlias property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCalresourceOrDlOrAlias().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testAccountInfo }
     * {@link testCosInfo }
     * {@link testDistributionListInfo }
     * {@link testAliasInfo }
     * {@link testCalendarResourceInfo }
     * {@link testDomainInfo }
     * 
     * 
     */
    public List<testAdminObjectInfo> getCalresourceOrDlOrAlias() {
        if (calresourceOrDlOrAlias == null) {
            calresourceOrDlOrAlias = new ArrayList<testAdminObjectInfo>();
        }
        return this.calresourceOrDlOrAlias;
    }

    /**
     * Gets the value of the more property.
     * 
     */
    public boolean isMore() {
        return more;
    }

    /**
     * Sets the value of the more property.
     * 
     */
    public void setMore(boolean value) {
        this.more = value;
    }

    /**
     * Gets the value of the searchTotal property.
     * 
     */
    public long getSearchTotal() {
        return searchTotal;
    }

    /**
     * Sets the value of the searchTotal property.
     * 
     */
    public void setSearchTotal(long value) {
        this.searchTotal = value;
    }

}
