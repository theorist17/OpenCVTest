package com.example.opencvtest;

public class DataNote
{String text;
    String comment;
    String date;
    int img;

    public DataNote(String text, String comment, String date, int img)
    {
        this.img = img;
        this.text = text;
        this.comment = comment;
        this.date = date;

    }

    public int getImg(){return img;}

    public String getText()
    {
        return text;
    }

    public String getComment()
    {
        return comment;
    }

    public String getDate()
    {
        return date;
    }
}
