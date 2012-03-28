package com.javaposse.android.zenwriter;

import java.util.Date;
import java.util.UUID;

public class Note {

    public String name = "";
    public String id;
    public String filename;
    public String content;
    public Date lastModified = new Date();

    
    public Note() {
        id = UUID.randomUUID().toString();
        filename = id + ".txt";
    }
    
    public Note(String id, String name, String filename, String content, Date lastModified) {
        this.id = id;
        this.name = name;
        this.filename = filename;
        this.content = content;
        this.lastModified = lastModified;
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
