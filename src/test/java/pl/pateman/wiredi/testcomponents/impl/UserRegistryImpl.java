package pl.pateman.wiredi.testcomponents.impl;

import pl.pateman.wiredi.Wire;
import pl.pateman.wiredi.WireComponent;
import pl.pateman.wiredi.WireName;
import pl.pateman.wiredi.testcomponents.RandomStringGenerator;
import pl.pateman.wiredi.testcomponents.UserRegistry;
import pl.pateman.wiredi.testcomponents.dto.User;

import java.util.*;

@WireComponent
public class UserRegistryImpl implements UserRegistry {

    private final Set<User> userSet;

    @Wire(name = "lettersOnlyRandomStringGenerator")
    private RandomStringGenerator firstNameGenerator;

    private RandomStringGenerator lastNameGenerator;

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
    public User createRandomUser() {
        return createUser(firstNameGenerator.generate(), lastNameGenerator.generate());
    }

    @Override
    public Collection<User> getAll() {
        return Collections.unmodifiableSet(userSet);
    }

    @Override
    public Optional<User> getByFirstName(String firstName) {
        return userSet.stream().filter(u -> u.getFirstName().equals(firstName)).findAny();
    }

    @Wire
    private void setLastNameGenerator(@WireName("alphanumericRandomStringGenerator") RandomStringGenerator lastNameGenerator) {
        this.lastNameGenerator = lastNameGenerator;
    }
}
