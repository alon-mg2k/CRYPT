package com.example.crypt.Fragments.Model;

public class User {
    private String idUser, Name, imgURL, status, Pass;

    public User(String idUser, String Name, String imgURL, String status, String Pass) {
        this.idUser = idUser;
        this.Name = Name;
        this.imgURL = imgURL;
        this.status = status;
        this.Pass = Pass;
    }

    public User() {
    }

    public String getPass() { return Pass; }

    public void setPass(String pass) { Pass = pass; }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}
