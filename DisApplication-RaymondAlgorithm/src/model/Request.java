package model;

/**
 * Created by Ice on 10/15/16.
 */
public class Request {
    private String filename;
    private String id;
    private String operation;
    private String content;

    /**
     * Constructor
     * @param filename
     * @param id
     * @param operation
     * @param content
     */
    public Request(String filename, String id, String operation, String content) {
        this.filename = filename;
        this.id = id;
        this.operation = operation;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getFilename() {
        return filename;
    }

    public String getId() {
        return id;
    }

    public String getOperation() {
        return operation;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
