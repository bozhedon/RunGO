package com.myrungo.rungo;

public class ChallengeItem {
    public String id;
    public String url;
    public String image;
    public static String customPic;
    public Long distance, hour, minutes;

    public ChallengeItem() {
    }

    public ChallengeItem(String customPic,String id, Long distance, Long hour, Long minutes, String url, String image) {
        super();
        this.customPic = customPic;
        this.id = id;
        this.distance = distance;
        this.image = image;
        this.hour = hour;
        this.minutes = minutes;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImge() {
        return image;
    }

    public void setImge(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getDistance(){
        return distance;
    }
    public void setDistance(Long distance){
        this.distance = distance;
    }
    public Long getHour(){
        return hour;
    }
    public void setHour(Long hour){
        this.hour = hour;
    }
    public Long getMinutes(){
        return minutes;
    }
    public void setMinutes(Long minutes){
        this.minutes = minutes;
    }
    public static String getCustomPic() {
        return customPic;
    }

    public void setCustomPic(String customPic) {
        this.customPic = customPic;
    }


}
