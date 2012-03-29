package com.javaposse.android.zenwriter;

import java.util.Date;
import java.util.UUID;

public class Note {

    private String name = "";
    public final String id;
    private String filename;
    private String content;
    private Date lastModified = new Date();
    private boolean isNew = true;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public boolean isNew() {
        return isNew;
    }

    public Note() {
        id = UUID.randomUUID().toString();
        filename = id + ".txt";
        isNew = true;
    }
    
    public Note(String id, String name, String filename, String content, Date lastModified, boolean isNew) {
        this.id = id;
        this.name = name;
        this.filename = filename;
        this.content = content;
        this.lastModified = lastModified;
        this.isNew = isNew;
    }
    
    @Override
    public String toString() {
        if("".equals(name)) {
            return id;
        }
        else {
            return name;
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if(o != null) {
            Note otherN = (Note) o;
            if(id != null && otherN.id != null) {
                return otherN.id.equals(id);
            }
            else {
                return id != otherN.id;
            }
        }
        else {
            return false;
        }
    }

    public static String getDefaultNameFromContent(String content, int maxChars) {
        StringBuilder sb = new StringBuilder();
        if(content != null && content.length() > 0) {
            String[] words = content.split("\\s+");
            int i = 0;
            while(sb.length() < maxChars && i < words.length) {
                sb.append(words[i++]).append(" ");
            }
            String name = sb.substring(0, Math.min(maxChars, sb.length()));
            return name;
        }
        else {
            return "";
        }
    }
}
