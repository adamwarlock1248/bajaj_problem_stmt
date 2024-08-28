package main;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Random;

public class test {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar test.jar 240343020103 path/to/file/test.json");
            return;
        }

        String prnNumber = args[0].toLowerCase(); // Convert PRN to lowercase, no spaces assumed
        String filePath = args[1];

        try (InputStream is = new FileInputStream(filePath)) {
            JSONTokener tokener = new JSONTokener(is);
            JSONObject jsonObject = new JSONObject(tokener);

            String destinationValue = findDestinationValue(jsonObject);
            if (destinationValue == null) {
                System.out.println("No 'destination' key found in the JSON file.");
                return;
            }

            String randomString = generateRandomString(8);
            String combinedString = prnNumber + destinationValue + randomString;
            String md5Hash = generateMD5Hash(combinedString);

            System.out.println(md5Hash + ";" + randomString);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Function to find the first occurrence of the "destination" key
    private static String findDestinationValue(JSONObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);

            if (key.equals("destination")) {
                return value.toString();
            } else if (value instanceof JSONObject) {
                String found = findDestinationValue((JSONObject) value);
                if (found != null) return found;
            } else if (value instanceof org.json.JSONArray) {
                for (Object item : ((org.json.JSONArray) value)) {
                    if (item instanceof JSONObject) {
                        String found = findDestinationValue((JSONObject) item);
                        if (found != null) return found;
                    }
                }
            }
        }
        return null;
    }

    // Function to generate an 8-character random alphanumeric string
    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }
        return result.toString();
    }

    // Function to generate MD5 hash of a string
    private static String generateMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
