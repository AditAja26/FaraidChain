package com.ems.estatemanagementsystem.pattern;

public interface Subject {
    void registerObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers(Object arg);
}
