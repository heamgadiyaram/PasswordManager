package program;
import org.json.simple.parser.ParseException;

public class UserManager{
    public static User loadUser(String username, String password) throws ParseException{
        User user = new User(username, password);

        if(user.loadUserData(password)){
            return user;
        }

        return null;
    }
}
    