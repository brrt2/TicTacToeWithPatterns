package gamemanagement;

import gamemanagement.moveManagement.Move;

public interface Subject {

    void register(Observer o);
    void notifyObservers();

}
