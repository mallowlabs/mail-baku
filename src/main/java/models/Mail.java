package models;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.mail.Address;
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Mail {
    private String id;
    private String subject;
    private String content;
    private Address[] toAddresses;
    private Address[] ccAddresses;
    private Address[] bccAddresses;
    private Date sentDate;
    private Address[] fromAddresses;
    private Attachment[] attachments;

    public static final String EXT = ".dat";

    public Mail() {

    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the toAddresses
     */
    public Address[] getToAddresses() {
        return toAddresses;
    }

    /**
     * @param toAddresses the toAddresses to set
     */
    public void setToAddresses(Address[] toAddresses) {
        this.toAddresses = toAddresses;
    }

    /**
     * @return the ccAddresses
     */
    public Address[] getCcAddresses() {
        return ccAddresses;
    }

    /**
     * @param ccAddresses the ccAddresses to set
     */
    public void setCcAddresses(Address[] ccAddresses) {
        this.ccAddresses = ccAddresses;
    }

    /**
     * @return the bccAddresses
     */
    public Address[] getBccAddresses() {
        return bccAddresses;
    }

    /**
     * @param bccAddresses the bccAddresses to set
     */
    public void setBccAddresses(Address[] bccAddresses) {
        this.bccAddresses = bccAddresses;
    }

    /**
     * @return the sentDate
     */
    public Date getSentDate() {
        return sentDate;
    }

    /**
     * @param sentDate the sentDate to set
     */
    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    /**
     * @return the fromAddresses
     */
    public Address[] getFromAddresses() {
        return fromAddresses;
    }

    /**
     * @param fromAddresses the fromAddresses to set
     */
    public void setFromAddresses(Address[] fromAddresses) {
        this.fromAddresses = fromAddresses;
    }

    /**
     * @return the attachments
     */
    public Attachment[] getAttachments() {
        return attachments;
    }

    /**
     * @param attachments the attachments to set
     */
    public void setAttachments(Attachment[] attachments) {
        this.attachments = attachments;
    }

    /**
     * @return the attached
     */
    @JsonIgnore
    public boolean isAttached() {
        return ArrayUtils.isNotEmpty(getAttachments());
    }

    @JsonIgnore
    public String getToAddressesReadable() {
        return getAddressesReadable(getToAddresses());
    }

    @JsonIgnore
    public String getCcAddressesReadable() {
        return getAddressesReadable(getCcAddresses());
    }

    @JsonIgnore
    public String getBccAddressesReadable() {
        return getAddressesReadable(getBccAddresses());
    }

    @JsonIgnore
    public String getFromAddressesReadable() {
        return getAddressesReadable(getFromAddresses());
    }

    protected String getAddressesReadable(Address[] addresses) {
        List<String> list = new ArrayList<>();
        if (addresses == null) {
            return StringUtils.EMPTY;
        }

        for (Address address : addresses) {
            try {
                list.add(MimeUtility.decodeText(address.toString()));
            } catch (UnsupportedEncodingException e) {
                list.add(address.toString());
            }
        }
        return StringUtils.join(list, ' ');
    }

    public Optional<Attachment> getAttachment(int index) {
        return Arrays.stream(getAttachments()).filter(a -> a.getIndex() == index).findFirst();
    }

}
