package pl.pateman.wiredi.testcomponents;

import pl.pateman.wiredi.testcomponents.dto.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRegistry {
    User createUser(String firstName, String lastName);
    Collection<User> getAll();
    Optional<User> getByFirstName(String firstName);
}
