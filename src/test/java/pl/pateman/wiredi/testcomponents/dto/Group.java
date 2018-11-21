package pl.pateman.wiredi.testcomponents.dto;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class Group {
    private final String id;
    private final Set<User> userSet;

    public Group(String id) {
        this.id = id;
        userSet = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public Set<User> getUserSet() {
        return Collections.unmodifiableSet(userSet);
    }

    public void addUser(User user) {
        userSet.add(user);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(id, group.id) &&
                Objects.equals(userSet, group.userSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
