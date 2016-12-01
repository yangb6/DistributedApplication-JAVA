package module;

import java.io.Serializable;

/**
 * Created by Bing on 10/15/16.
 */
public class Package implements Serializable {
    private static final long serialVersionUID = 5950169519310163575L;
    private boolean isToken = false;
//    private boolean isRequest;
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
    public Package(String filename, String id, String operation, String content) {
//        this.isRequest = true;
        this.filename = filename;
        this.id = id;
        this.operation = operation;
        this.content = content;
    }

    public Package(Package pack) {
        this.filename = pack.getFilename();
        this.id = pack.getId();
        this.operation = pack.getOperation();
        this.content = pack.getContent();
    }

    public Package(String filename) {
        this.isToken = true;
        this.filename = filename;
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

    public boolean isToken() {
        return isToken;
    }

    public boolean getIsToken() {
        return isToken;
    }

    public void setToken(boolean isToken) {
        this.isToken = isToken;
    }


    @Override
    public String toString() {
        if (isToken) return filename;
        else return id + "\t" + filename + "\t" + operation + "\t" + content;
    }
}
