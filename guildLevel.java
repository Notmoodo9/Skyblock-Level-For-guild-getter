import com.sun.source.tree.CatchTree;
import org.json.*;
import java.util.*;
import java.net.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class guildLevel
{
    public static Scanner sc = new Scanner(System.in);
    public static String jsonDATA = "";
    public static String guildInfo = "";
    public static HashMap<String,Integer> plrs = new HashMap<>();
    public static ArrayList<String> plruuid = new ArrayList<String>();
    public static int totalplr;
    public static int plrsDataGotten = 0;
    public static HashMap<String,Double> finalplr = new HashMap<>();
    public static String apiKey;
    public static String uuid;

    public static void main(String[] args) throws Exception
    {
        int purgeLVL = 0;
        System.out.println("What is your api key?");
        apiKey = sc.next();
        System.out.println("What is the players uuid?");
        uuid = sc.next();
        boolean guildDataGotten = false;
        while(!guildDataGotten){
            guildDataGotten = guildInfos();
        }
        playerLevel();
        dataWithUser();
        playerDataSort();

        System.out.println("Level to purge");
        purgeLVL = sc.nextInt();
        getShitters(purgeLVL);
    }

    public static void getShitters(int purge) throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter("underLevel"+purge+".txt"));
        writer.write("People to purge:");
        writer.newLine();
        for (Map.Entry<String, Double> entry : finalplr.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();
            if(value < purge + 0.0){
                uuid2user(key);
                JSONObject UUid = new JSONObject(jsonDATA);
                String uuidd = UUid.getString("uuid");
                System.out.println(uuidd);
                boolean ironman = getData("https://api.hypixel.net/skyblock/profiles?key="+apiKey+"&player="+uuidd);
                while(ironman){
                    ironman = getData("https://api.hypixel.net/skyblock/profiles?key=\"apiKey\"&player=\""+uuidd);
                }
                System.out.println(jsonDATA);
                String ironmans = "";
                JSONObject profileDATA = new JSONObject(jsonDATA);
                try {
                    JSONArray profiles = profileDATA.getJSONArray("profiles");
                    for (int i = 0; i < profiles.length(); i++) {
                        JSONObject profile = profiles.getJSONObject(i);
                        String gamemode = profile.getJSONObject("members").getJSONObject(uuidd).getString("game_mode");
                        if (gamemode == "ironman") {
                            Integer str = profile.getJSONObject("members").getJSONObject(plruuid.get(i)).getJSONObject("leveling").getInt("experience");
                            ironmans = ironmans + " " + "Ironman: " + (str / 100) + "lvl";
                        }
                    }
                }catch (Exception e){
                    System.out.println(e);
                    ironmans = "";
                }
                String line = key +": "+ value + ironmans;
                writer.write(line);
                writer.newLine();
            }
            // add newline separator
        }
        writer.close();
    }
    public static boolean guildInfos(){
        try{
            URL hypixel = new URL("https://api.hypixel.net/guild?key="+apiKey+"&player="+uuid); // gets the api url that it connects to
            URLConnection ez = hypixel.openConnection();  // connects to the api

            ez.setRequestProperty("User-Agent", "<Mozilla/5.0 (X11; CrOS x86_64 15117.111.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36>");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    ez.getInputStream())); // dont fully know

            String inputLine; // makes a data storage type called inputline

            while ((inputLine = in.readLine()) != null)
                guildInfo = inputLine;
            in.close(); // not sure

            JSONObject json = new JSONObject(guildInfo);
            JSONArray members = json.getJSONObject("guild").getJSONArray("members");

            for (int i = 0; i < members.length(); i++) {
                JSONObject member = members.getJSONObject(i);
                plruuid.add(member.getString("uuid"));
                System.out.println(plruuid.get(i));
            }
            totalplr = plruuid.size();
            return true;
        }catch(Exception e){
            System.out.println("Error");
            return false;
        }
    }


    public static void playerLevel(){
        intit();
        for(int i = 0; i < plruuid.size(); i++){
            boolean failed = getData("https://api.hypixel.net/skyblock/profiles?key="+apiKey+"&uuid=" + plruuid.get(i));
            while(failed) {
                try {
                    Thread.sleep(1); // Sleep for 2 seconds
                } catch (InterruptedException e) {
                    // Handle exception
                }
                failed = getData("https://api.hypixel.net/skyblock/profiles?key="+apiKey+"&uuid=" + plruuid.get(i));
            }

            JSONObject json = new JSONObject(jsonDATA);
            JSONArray profiles = json.getJSONArray("profiles");

            for(int index = 0; index < profiles.length(); index++){
                try {
                    JSONObject level = profiles.getJSONObject(index);
                    Integer str = level.getJSONObject("members").getJSONObject(plruuid.get(i)).getJSONObject("leveling").getInt("experience");
                    if(str > plrs.get(plruuid.get(i))){
                        plrs.put(plruuid.get(i),str);
                    }
                    System.out.println(plruuid.get(i) + " e " + str + " ");
                } catch (Exception e){
                }
            }

            try {
                Thread.sleep(1000); // Sleep for 2 seconds
            } catch (InterruptedException e) {
                // Handle exception
            }
            plrsDataGotten++;
            System.out.println(plrsDataGotten + "/" + totalplr);
        }
    }
    public static boolean getData(String apiurl){
        jsonDATA = "";
        try {
            URL hypixel = new URL(apiurl); // gets the api url that it connects to
            URLConnection ez = hypixel.openConnection();  // connects to the api

            ez.setRequestProperty("User-Agent", "<Mozilla/5.0 (X11; CrOS x86_64 15117.111.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36>");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    ez.getInputStream())); // dont fully know

            String inputLine; // makes a data storage type called inputline

            while ((inputLine = in.readLine()) != null)
                jsonDATA = inputLine;
            in.close(); // not sure
            return false;
        }catch (Exception e){
            System.out.println(e);
            return true;
        }
    }

    public static void intit(){
        for(int i = 0; i < plruuid.size(); i++){
            plrs.put(plruuid.get(i),0);
        }
    }

    public static void playerDataSort() throws Exception{
        Map<String, Double> sortedMap = finalplr.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));

        // print the sorted hashmap
        BufferedWriter writer = new BufferedWriter(new FileWriter("guildData.txt"));

        // write each entry to file with a newline separator
        for (Map.Entry<String, Double> entry : sortedMap.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();
            String line = key +": "+ value;
            writer.write(line);
            writer.newLine(); // add newline separator
        }

        // close file
        writer.close();
        System.out.println(sortedMap);
        finalplr.clear();
        finalplr.putAll(sortedMap);
    }

    public static void dataWithUser(){
        for(int i = 0; i < plruuid.size(); i++){
            boolean works = uuid2user(plruuid.get(i));
            while(!works){
                works = uuid2user(plruuid.get(i));
                try {
                    Thread.sleep(1000); // Sleep for 2 seconds
                } catch (InterruptedException e) {
                    // Handle exception
                }
            }
            JSONObject json = new JSONObject(jsonDATA);
            String name = json.getString("username");
            System.out.println(name);
            finalplr.put(name, (plrs.get(plruuid.get(i)) + 0.0) / 100.0);

            try {
                Thread.sleep(1); // Sleep for 2 seconds
            } catch (InterruptedException e) {
                // Handle exception
            }
        }
    }
    public static boolean uuid2user(String uuid){
        try {
            URL url = new URL("https://api.ashcon.app/mojang/v2/user/"+uuid);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Java client");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Scanner scanner = new Scanner(conn.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                // The response string contains the data returned by the API
                String apiData = response.toString();
                jsonDATA = apiData;
                System.out.println(apiData);
            } else {
                System.out.println("Failed to fetch data from API");
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
