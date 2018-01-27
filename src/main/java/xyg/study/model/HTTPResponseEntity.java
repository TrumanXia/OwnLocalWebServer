package xyg.study.model;

public class HTTPResponseEntity
{

    private String contentType;
    private String firstLineOfResponse;
    private String responseHeader;
    private String resourceName;

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getFirstLineOfResponse() {
        return firstLineOfResponse;
    }

    public void setFirstLineOfResponse(String firstLineOfResponse) {
        this.firstLineOfResponse = firstLineOfResponse;
    }

    public String getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(String responseHeader) {
        this.responseHeader = responseHeader;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
}
