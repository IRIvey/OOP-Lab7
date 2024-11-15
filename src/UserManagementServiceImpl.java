import java.util.Map;

public class UserManagementServiceImpl implements UserManagementService {
    private static volatile UserManagementServiceImpl instance;
    private final Map<String, MutableUser> userMap;
    private final UserFileManager fileManager;

    private UserManagementServiceImpl() {
        this.fileManager = new UserFileManager();
        this.userMap = fileManager.loadUsers();
    }

    public static UserManagementServiceImpl getInstance() {
        if (instance == null) {
            synchronized (UserManagementServiceImpl.class) {
                if (instance == null) {
                    instance = new UserManagementServiceImpl();
                }
            }
        }
        return instance;
    }

    public ReadOnlyUser authenticateUser(String username, String password) {
        MutableUser user = userMap.get(username);
        return (user != null && user.getPassword().equals(password)) ? user : null;
    }

    public ReadOnlyUser getUser(String username) {
        return userMap.get(username);
    }

    public void addUser(String username, String email, String password, int userType) {
        int userId = userMap.size() + 1;
        MutableUser user = UserFactory.createUser(userId, username, email, password, userType);
        userMap.put(username, user);
        fileManager.saveUsers(userMap);
    }

    public void updateUser(String username, String email, String password, int userType) {
        MutableUser user = userMap.get(username);
        if (user != null) {
            user.setEmail(email);
            user.setPassword(password);
            user.setUserType(userType);
            fileManager.saveUsers(userMap);
        }
    }
}
