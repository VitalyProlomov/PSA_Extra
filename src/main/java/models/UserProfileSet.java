package models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class UserProfileSet {
    HashMap<String, UserProfile> idUserMap;

    public UserProfileSet() {
        this.idUserMap = new HashMap<>();
    }

    public HashMap<String, UserProfile> getIdUserMap() {
        return idUserMap;
    }

    public void setIdUserMap(HashMap<String, UserProfile> idUserMap) {
        this.idUserMap = new HashMap<>(idUserMap);
    }

    public void addUsers(HashMap<String, UserProfile> addendumUsers) {
        idUserMap.putAll(addendumUsers);
    }

    public void addUser(UserProfile userProfile) {
        if (idUserMap.containsKey(userProfile.getUserName())) {
            UserProfile updatedUser = idUserMap.get(userProfile.getUserName());

            for (String id : userProfile.getAllGamesIds()) {
                updatedUser.addGame(id, userProfile.getHashInGame(id));
            }
            idUserMap.put(userProfile.getUserName(), updatedUser);
        }
        else {
            idUserMap.put(userProfile.getUserName(), userProfile);
        }
    }
}
