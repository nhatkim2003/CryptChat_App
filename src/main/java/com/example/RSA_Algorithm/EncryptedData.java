package com.example.RSA_Algorithm;

import com.example.entity.User;

import java.io.File;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

public class EncryptedData implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<BigInteger> data;
    private String dataType; // "message" hoặc "file"
    private User receivedUser;
    private User caller;
    private List<BigInteger> encryptedAesKey;
    private File encryptedFile;
    private byte[] fileBytes;
    private String IPAddress;

    public EncryptedData(List<BigInteger> data, String dataType, User receivedUser) {
        this.data = data;
        this.dataType = dataType;
        this.receivedUser = receivedUser;
    }

    public EncryptedData(File encryptedFile, byte[] fileBytes, String dataType, User receivedUser, List<BigInteger> encryptedAesKey) {
        this.encryptedFile = encryptedFile;
        this.dataType = dataType;
        this.receivedUser = receivedUser;
        this.encryptedAesKey = encryptedAesKey;
        this.fileBytes = fileBytes;
    }

    public EncryptedData(User caller, User receivedUser, String dataType) {
        this.caller = caller;
        this.receivedUser = receivedUser;
        this.dataType = dataType;
    }

    public EncryptedData(String dataType, User receivedUser) {
        this.dataType = dataType;
        this.receivedUser = receivedUser;
    }

    public User getCaller() {
        return caller;
    }

    public void setCaller(User caller) {
        this.caller = caller;
    }

    public List<BigInteger> getData() {
        return data;
    }

    public void setData(List<BigInteger> data) {
        this.data = data;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public User getReceivedUser() {
        return receivedUser;
    }

    public void setReceivedUser(User receivedUser) {
        this.receivedUser = receivedUser;
    }

    public List<BigInteger> getEncryptedAesKey() {
        return encryptedAesKey;
    }

    public void setEncryptedAesKey(List<BigInteger> encryptedAesKey) {
        this.encryptedAesKey = encryptedAesKey;
    }

    public File getEncryptedFile() {
        return encryptedFile;
    }

    public void setEncryptedFile(File encryptedFile) {
        this.encryptedFile = encryptedFile;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }

    public String getIPAddress() {
        return IPAddress;
    }

    public void setIPAddress(String IPAddress) {
        this.IPAddress = IPAddress;
    }
}

