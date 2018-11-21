package pl.pateman.wiredi.testcomponents;

import pl.pateman.wiredi.testcomponents.dto.Group;
import pl.pateman.wiredi.testcomponents.dto.User;

import java.util.Collection;
import java.util.Optional;

public interface GroupRegistry {
    Group createGroup(String id);
    Collection<Group> getAll();
    Optional<Group> getById(String id);
    void addUserToGroup(String groupId, User user);
}
