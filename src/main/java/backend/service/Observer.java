package backend.service;

import backend.model.User;

public interface Observer {
    void notify(User user, String message);
}