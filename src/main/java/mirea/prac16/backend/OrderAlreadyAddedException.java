package mirea.prac16.backend;

public class OrderAlreadyAddedException extends Exception {
    public OrderAlreadyAddedException(String message) {
        super(message);
    }
}

