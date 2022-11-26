package com.example.crypt.Fragments.Model;

public class FilesModel {
    private String FileName, FileType, FilePass, FileAuthor;

    public FilesModel(String fileName, String fileType, String filePass, String fileAuthor) {
        FileName = fileName;
        FileType = fileType;
        FilePass = filePass;
        FileAuthor = fileAuthor;
    }

    public FilesModel(){}

    public String getFilePass() {
        return FilePass;
    }

    public void setFilePass(String filePass) {
        FilePass = filePass;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFileType() {
        return FileType;
    }

    public void setFileType(String fileType) {
        FileType = fileType;
    }

    public String getFileAuthor() { return FileAuthor; }

    public void setFileAuthor(String fileAuthor) { FileAuthor = fileAuthor; }
}
