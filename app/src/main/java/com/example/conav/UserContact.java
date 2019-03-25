package com.example.conav;

public class UserContact {
    String name, phoneNumber, address;

    public UserContact(){}
    public UserContact(String name, String phoneNumber, String address) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address=address;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName){
        name=newName;
    }
    public void setPhoneNumber(String addPhone){
        phoneNumber=addPhone;
    }
    public void setAddress(String addAdress){
        address=addAdress;
    }

}
