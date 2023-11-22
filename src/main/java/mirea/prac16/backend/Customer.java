package mirea.prac16.backend;

import java.io.Serializable;

public final class Customer implements Serializable {
    private String firstName;
    private String secondName;
    private Address address;
    private int age;
    private static Customer MATURE_UNKNOWN_CUSTOMER = new Customer("Unknown", "Unknown", Address.EMPTY_ADDRESS, 21);
    private static Customer NOT_MATURE_UNKNOWN_CUSTOMER = new Customer("Unknown", "Unknown", Address.EMPTY_ADDRESS, 17);
    public Customer(String firstName, String secondName, Address address, int age) {
        if (firstName == null || firstName.isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (secondName == null || secondName.isEmpty()) {
            throw new IllegalArgumentException("Second name cannot be empty");
        }

        if (age <= 0) {
            throw new IllegalArgumentException("Age cannot be less than or equal to zero");
        }
        this.firstName = firstName;
        this.secondName = secondName;
        this.address = address;
        this.age = age;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getSecondName() {
        return secondName;
    }
    public Address getAddress() {
        return address;
    }
    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        String form;
        int secondToLastDigit = (age / 10) % 10;
        if (secondToLastDigit == 1) {
            form = "лет";
        } else {
            switch (age % 10) {
                case 1 -> form = "год";
                case 2, 3, 4 -> form ="года";
                default -> form = "лет";
            };
        }

        return firstName + " " + secondName +  ", " + age + " " + form;
    }
}

