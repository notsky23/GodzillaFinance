package com.example.godzillafinance;

public class UserCreated {

    public String fullName, age, email;

    public UserCreated(){

    }

    public UserCreated(String fullName_val, String age_val, String email_val) {
        this.fullName = fullName_val;
        this.age = age_val;
        this.email = email_val;
    }
}
