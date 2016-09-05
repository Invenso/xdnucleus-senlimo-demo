package com.invenso.xperido.senlimo.resources.objects;

import java.util.List;

/**
 * Holds all the fields for an email
 */
public class EmailAddressing {

    private String mailFrom;
    private List<String> mailTo;
    private List<String> mailCc;
    private List<String> mailBCc;

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public List<String> getMailTo() {
        return mailTo;
    }

    public void setMailTo(List<String> mailTo) {
        this.mailTo = mailTo;
    }

    public List<String> getMailCc() {
        return mailCc;
    }

    public void setMailCc(List<String> mailCc) {
        this.mailCc = mailCc;
    }

    public List<String> getMailBCc() {
        return mailBCc;
    }

    public String getMailBCcString() {
        return convertToString(mailBCc);
    }

    public String getMailCcString() {
        return convertToString(mailCc);
    }

    public String getMailToString() {
        return convertToString(mailTo);
    }

    public void setMailBCc(List<String> mailBCc) {
        this.mailBCc = mailBCc;
    }

    private String convertToString(List<String> list) {

        StringBuilder sb = new StringBuilder();

        if (list != null && ! list.isEmpty()) {
            list.forEach(sb::append);
        }

        return sb.toString();
    }

}
