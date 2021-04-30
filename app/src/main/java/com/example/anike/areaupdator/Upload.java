package com.example.anike.areaupdator;

public class Upload {

    private String area;
    private String link;
    private String time;

    public Upload(){

    }

    public Upload(String area, String link, String time){
        this.area = area;
        this.link = link;
        this.time = time;
    }

    public String getArea(){
        return area;
    }

    public String getLink(){

        return link;
    }

    public String getTime(){
        return time;
    }

    public void setArea(String area){
        this.area = area;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
