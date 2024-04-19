package ServerSide;

import java.io.Serializable;

public class Description implements Serializable {
    String itemType;
    String title;
    String author;
    int pages;
    String summary;

    public Description(String itemType, String title, String author, int pages, String summary) {
        this.itemType = itemType;
        this.title = title;
        this.author = author;
        this.pages = pages;
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "[" + itemType + ": " + title + ", " + author + ", " + pages + "]";
    }


    public String getItemType() {return this.itemType;}

    public String getTitle() {return this.title;}

    public String getAuthor() {return this.author;}

    public int getPages() {return this.pages;}

    public String getSummary() {return this.summary;}

}