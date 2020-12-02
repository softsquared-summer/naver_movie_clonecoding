package com.example.mydictionary;


public class Movie {

    private String title=""; //제목
    private String link="";//링크
    private String image="";//이미지
    private String subtitle="";//부제목
    private int pubDate=0;//출판년도
    private String director="";//감독
    private String actor="";//배우
    private String userRating="";//평점

    public Movie(String title, String link, String image, String subtitle, int pubDate, String director, String actor, String userRating) {
        this.title = title;
        this.link = link;
        this.image = image;
        this.subtitle = subtitle;
        this.pubDate = pubDate;
        this.director = director;
        this.actor = actor;
        this.userRating = userRating;
    }

    public int getPubDate() {
        return pubDate;
    }

    public void setPubDate(int pubDate) {
        this.pubDate = pubDate;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public void Movie(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", image='" + image + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", pubDate=" + pubDate +
                ", director='" + director + '\'' +
                ", actor='" + actor + '\'' +
                ", userRating='" + userRating + '\'' +
                '}';
    }



}
