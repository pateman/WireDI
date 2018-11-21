package pl.pateman.wiredi.testcomponents.impl;

import pl.pateman.wiredi.WireComponent;
import pl.pateman.wiredi.testcomponents.UserRegistry;
import pl.pateman.wiredi.testcomponents.dto.User;

import java.util.*;

@WireComponent
public class UserRegistryImpl implements UserRegistry {

    private final Set<User> userSet;

    private UserRegistryImpl() {
        userSet = new HashSet<>();
    }

    @Override
    public User createUser(String firstName, String lastName) {
        User newUser = new User(firstName, lastName);
        userSet.add(newUser);
        return newUser;
    }

    @Override
    public Collection<User> getAll() {
        return Collections.unmodifiableSet(userSet);
    }

    @Override
    public Optional<User> getByFirstName(String firstName) {
        return userSet.stream().filter(u -> u.getFirstName().equals(firstName)).findAny();
    }
}
