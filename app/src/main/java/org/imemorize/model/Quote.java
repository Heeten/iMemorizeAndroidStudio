package org.imemorize.model;

public class Quote {

    private int id;
    private String introText = "";
    private String quoteId = "";
    private String text = "";
    private String author = "";
    private String reference = "";
    private String language = "English";
    private String url = "";
    
    public Quote() {
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public String toString(){
        String shortString = "";
        //TODO - determine the lenth based on the width of the device?
        // or easir, use a text field auto truncate
        //if(this.text.length()>40){
        //    shortString = this.text.substring(0, 40);
        //}else{
            shortString = this.text;
        //}
        return shortString;
    }
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the quoteId
     */
    public String getQuoteId() {
        return quoteId;
    }

    /**
     * @param quoteId the id to set
     */
    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    /**
     * @return the introtext
     */
    public String getIntroText() {
        return introText;
    }

    /**
     * @param introText the text to set - if null then set it to ""
     */
    public void setIntroText(String introText) {
        String _introtext = "";
        if(introText!=null){
            if(!introText.equalsIgnoreCase("NULL")){
                _introtext = introText;
            }
        }
        this.introText = _introtext;
    }


    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        // if the author is null then return an empty string
        return author.equalsIgnoreCase("NULL")?"":author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the reference
     */
    public String getReference() {
        // if the reference is null then return an empty string
        return reference.equalsIgnoreCase("NULL")?"":reference;

    }

    /**
     * @param reference the reference to set
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * @return the reference
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language
     */
    public void setLanguage(String language) {
        this.language = language;
    }


    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     */
    public void setUrl(String url) {
        String _url = "";
        if(url!=null){
            if(!url.equalsIgnoreCase("NULL")){
                _url = url;
            }
        }
        this.url = _url;
    }

    /**
     * returns true if this quote came from the users quote set
     * @return
     */
    public boolean isUserQuote(){
       if(getQuoteId().indexOf(Consts.USER_QUOTE_PREFIX)>-1){
            return true;
        }else{
           return false;
       }
    }


}
