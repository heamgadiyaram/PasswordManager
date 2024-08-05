package program;
import java.io.*;
import java.util.*;
import java.security.*;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class User {
    private String username;
    private String passwordHash;
    private String salt;
    private Hashtable<String, String> passwordList;

    private static final String USERS_FOLDER = "users/";

    public User(String username, String password){
        this.username = username;
        this.salt = loadSalt();
        if(salt == null){
            this.salt = generateSalt();
            saveSalt();
        }
        this.passwordHash = hash(password);
        this.passwordList = new Hashtable<>();

        Crypto.generateEncryptionKey();
    }

    private String loadSalt(){
        String fileName = USERS_FOLDER + username + ".json";

        try (FileReader reader = new FileReader(fileName)){
            JSONParser parser = new JSONParser();
            JSONObject userData = (JSONObject) parser.parse(reader);
            
            return (String) userData.get("salt");
        } 
        catch (FileNotFoundException e) {
            return null; // Salt not found, user exists
        } 
        catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String generateSalt(){
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    private void saveSalt() {
        // Save the salt to the user data file
        String fileName = USERS_FOLDER + username + ".json";

        JSONObject userData = new JSONObject();
        userData.put("salt", salt);

        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write(userData.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    public void addPassword(String service, String password){
        if(passwordList.containsKey(service)){ //check to see if a service already exists
            System.out.println("Service \"" + service + "\" is already exists.");
        }
        else{
            String encryptedPassword = Crypto.encrypt(password);//ecrypt password
            passwordList.put(service, encryptedPassword);//add service/password pair
            System.out.println("Password for service \"" + service + "\" added successfully.");

        }
        
    }

    public String getPassword(String service){
        String encryptedPassword = passwordList.get(service);
        return (encryptedPassword != null) ? Crypto.decrypt(encryptedPassword) : null;
    }

    public void deleteService(String service){
        if(passwordList.containsKey(service)){//check if service exists in the hashtable
            passwordList.remove(service);//delete the service
            System.out.println("Service \"" + service + "\" deleted successfully.");
        }
        else{
            System.out.println("Service \"" + service + "\" does not exist.");
        }
    }

    public boolean loadUserData(String password) throws ParseException{
        String fileName = USERS_FOLDER + username + ".json";

        try(FileReader reader = new FileReader(fileName)){
            JSONParser parser = new JSONParser();
            JSONObject userData = (JSONObject) parser.parse(reader);

            password = password.trim();

            String savedHash = (String) userData.get("passwordHash");

            if(savedHash != null && savedHash.equals(hash(password))){//If password exists in the user file and the password is correct
                JSONObject passwordListObject = (JSONObject) userData.get("passwordList");
                
                Map<String, String> passwordMap = new HashMap();

                if (passwordListObject != null) {
                    for (Object key : passwordListObject.keySet()) {
                        String service = (String) key;
                        String encryptedPassword = (String) passwordListObject.get(key);
                        passwordMap.put(service, encryptedPassword);
                    }
                }

                passwordList = new Hashtable<>(passwordMap);
                
                return true;
            }
            else if (savedHash != null && !savedHash.equals(hash(password))){//Password exists in file, but entered password doesn't match it
                System.out.println("Invalid password. Exiting.");
                System.exit(1);
            }
            else{
                saveUserData();
            }
        }
        catch(FileNotFoundException e){
            return false;
        }
        catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public void saveUserData(){
        String fileName = USERS_FOLDER + username + ".json";//filename to save the data to, corresponds to entered username

        JSONObject userData = new JSONObject();
        userData.put("salt", salt);
        userData.put("passwordHash", passwordHash);
        userData.put("passwordList", passwordList);//add generated salt, hashed password, and passwordList to JSONObject

        try(FileWriter writer = new FileWriter(fileName)){
            writer.write(userData.toJSONString());//Write data to the file
            System.out.println("User data saved successfully.");
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public String hash(String input){
        String saltedInput = input + salt;

        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(saltedInput.getBytes());

            StringBuilder hexString = new StringBuilder();
            for(byte b : hashedBytes){
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1){
                    hexString.append('0');//pad with 0 to keep hex format
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
            return null;
        }
    }
}


