package school.faang.user_service.testing;

import java.util.HashMap;
import java.util.Map;

public class User2RepositoryImpl implements User2Repository {

    Map<String, User2> db = new HashMap<>();

    @Override
    public boolean save(User2 user2) {
        System.out.println("save method is called");
        if (!db.containsKey(user2.getId())) {
            db.put(user2.getId(), user2);
            return true;
        }
        return false;
    }

}
