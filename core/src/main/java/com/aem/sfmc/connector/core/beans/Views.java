package com.aem.sfmc.connector.core.beans;

public class Views {

    private EmailHTML html;

    private EmailText text;

    private EmailSubject subjectline;

    private EmailPreHeader preheader;

    public EmailHTML getHtml() {
        return html;
    }

    public void setHtml(EmailHTML html) {
        this.html = html;
    }

    public EmailText getText() {
        return text;
    }

    public void setText(EmailText text) {
        this.text = text;
    }

    public EmailSubject getSubjectline() {
        return subjectline;
    }

    public void setSubjectline(EmailSubject subjectline) {
        this.subjectline = subjectline;
    }

    public EmailPreHeader getPreheader() {
        return preheader;
    }

    public void setPreheader(EmailPreHeader preheader) {
        this.preheader = preheader;
    }
}
