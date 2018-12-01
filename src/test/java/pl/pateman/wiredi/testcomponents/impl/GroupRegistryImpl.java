package pl.pateman.wiredi.testcomponents.impl;

import pl.pateman.wiredi.annotation.Wire;
import pl.pateman.wiredi.annotation.WireComponent;
import pl.pateman.wiredi.testcomponents.GroupRegistry;
import pl.pateman.wiredi.testcomponents.UserRegistry;
import pl.pateman.wiredi.testcomponents.dto.Group;
import pl.pateman.wiredi.testcomponents.dto.User;

import java.util.*;

@WireComponent(name = "groupRegistry")
public class GroupRegistryImpl implements GroupRegistry {

    private final Set<Group> groupSet;
    private UserRegistry userRegistry;

    @Wire
    private GroupRegistryImpl(UserRegistry userRegistry) {
        groupSet = new HashSet<>();
        this.userRegistry = userRegistry;
    }

    @Override
    public Group createGroup(String id) {
        Group group = new Group(id);
        groupSet.add(group);
        return group;
    }

    @Override
    public Collection<Group> getAll() {
        return Collections.unmodifiableSet(groupSet);
    }

    @Override
    public Optional<Group> getById(String id) {
        return groupSet.stream().filter(g -> g.getId().equals(id)).findFirst();
    }

    @Override
    public void addUserToGroup(String groupId, String userFirstName) {
        Optional<User> user = userRegistry.getByFirstName(userFirstName);
        user.ifPresent(u -> getById(groupId).ifPresent(g -> g.addUser(u)));
    }
}
