package com.example.imagesearch;

public class ImageObject
{
    protected String title, desc, date, hdURL, img_filepath;
    protected long id;

    public ImageObject(String title, String desc, String date, String hdURL, String img_filepath, long id)
    {
        this.title = title;
        this.desc = desc;
        this.date = date;
        this.img_filepath = img_filepath;
        this.hdURL = hdURL;
        this.id = id;
    }

    public long getId()
    {
        return id;
    }
    public String getTitle() { return title; }
    public String getImg_filepath() { return img_filepath; }
    public String getDesc() { return desc; }
    public String getDate() { return date; }
    public String getHdURL() { return hdURL; }
}