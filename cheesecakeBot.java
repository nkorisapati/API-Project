//https://webchat.freenode.net

import com.google.gson.*;
import org.jibble.pircbot.PircBot;
import org.json.*;
import java.io.*;
import java.net.*;
import java.text.DecimalFormat;
    //cheesecakeBot class class with the main-
    public class cheesecakeBot extends PircBot {
        public static void main(String[] args) throws Exception {
            cheesecakeBotMain cheesecake = new cheesecakeBotMain();         // Now start our bot up.
            cheesecake.setVerbose(true);                 // Enable debugging output.
            cheesecake.connect("irc.freenode.net");        // Connect to the IRC server.
            cheesecake.joinChannel("#freeSpace");         // Join the #pircbot channel.and send a message
            cheesecake.sendMessage("#freeSpace", "Hello! Welcome to freeSpace channel. My name is " +
                    "cheesecakeBot and I will be assisting you today! " +
                    "Please enter the keyword 'weather' or 'synonym' to get the weather of a city in the United States " +
                    "or to get the synonyms of any word! If your hungry, you can ask the bot to serve you cheesecake, by using the " +
                    "keyword 'cheesecake'!");
        }
    }
    class cheesecakeBotMain extends PircBot {
        public cheesecakeBotMain()
        {
            this.setName("cheesecakeBot");
        }   //constructor with attribute of name
        public void onMessage(String channel, String sender, String login, String hostname, String message) {
            //if hi or hello.. return message
            if ((message.contains("Hello"))||(message.contains("Hi"))||(message.contains("hello"))||(message.contains("hi"))) {           //when the user enters something in the chat box if it contains the word cheesecake.. reply wiht this
                sendMessage(channel, "Hello "+sender);
            }
            if (message.contains("cheesecake")) {           //when the user enters something in the chat box if it contains the word cheesecake.. reply wiht this
                sendMessage(channel, "The cheesecake will be sold shortly! Enjoy :)");
            }
            if (message.contains("weather")) {//when the user enters something in the chat box if it contains the word weather.. reply wiht this
                StringBuilder city = new StringBuilder();    //store the city string
                String[] separate = message.split(" ");    //then separat it wherever there is a space
                int len = separate.length;   //store the length

                //local variable temp.. used after to store url fxn return
                String temperature;
                //if the length is greater than 2 then store all the words in the phrase except for word "weather"
                if (len >= 2) {
                    for (int i = 0; i < len; i++) {
                        if (!(separate[i].equals("weather"))) {
                            city.append(separate[i]).append(" ");
                        }
                    }
                    temperature = callWeatherURL(city.toString()); //then call the fxn to get the values
                    sendMessage(channel, "Hey " + sender + "! " + temperature);     //and send the result to chat box
                } else {
                    sendMessage(channel, "The message did not contain a location. Please try again");   //if city could not be found then error message
                }
            }

            //if the user enters the word synonym in the chat then get the word being asked of synonym
            if (message.contains("synonym")) {
                //  local variables
                String word;
                String[] separate = message.split(" ");
                int len = separate.length;
                String synon;
                // as long as the user enters only 2 words then.. continue to get the word entered
                if (len == 2) {
                    if (separate[0].equals("synonym")) {
                        word = separate[1];
                    }
                    else{
                        word = separate[0];
                    }
                    //and then call the url fxn
                    synon = callSynonymURL(word);
                    sendMessage(channel, "Hey " + sender + "! " + synon);
                }
                else {
                    //if no word found then error message
                    sendMessage(channel, "The message did not contain a word or there are no synonyms for your entry. Please try again");
                }
                }
            //if message says bye.. return message
            if ((message.contains("Bye"))||(message.contains("Goodbye"))||(message.contains("bye"))||(message.contains("goodbye"))) {           //when the user enters something in the chat box if it contains the word cheesecake.. reply wiht this
                sendMessage(channel, "Bye "+sender+"! Hope to see you soon!");
            }
            }
        //calling the fxn to open url and get the temp details
        static String callWeatherURL (String cityName) {
            //local variables
            StringBuilder stringFromURL = new StringBuilder();
            String line;
            //try opeing theURL and connect with HttpURL
            try {
                URL weatherURL = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + cityName +"&appid=3f01dd8e1a3a11d5a104e6c56bb45be6");
                HttpURLConnection connection = (HttpURLConnection) weatherURL.openConnection();
                connection.setRequestMethod("GET");
                //bufferedReader allows for each line in website returned to be read in json
                BufferedReader oneLine = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                //so,., store the json lines into stringbuilder
                while ((line = oneLine.readLine()) != null) {
                    stringFromURL.append(line);
                }
                oneLine.close();
                //then send it to be parsed
                return parseIntoReadableWeather(stringFromURL.toString());
            }
            //if something goes wrong and try catch is not able to execute completely throw error
            catch (Exception e) {
                return "Error found! Exception " + e + " thrown";
            }
        }
        //calling the fxn to open url and get synonym of word
        static String callSynonymURL (String word) {
            //local variables
            StringBuilder wordsFromURL = new StringBuilder();
            String line;
            //open the url and form a httpURL connection
            try {
                URL synonymURL = new URL("https://www.dictionaryapi.com/api/v3/references/thesaurus/json/"+word+"?key=d4b0c7f8-b84c-49ae-83e4-f7dcc55f57e6");
                HttpURLConnection connection = (HttpURLConnection) synonymURL.openConnection();
                connection.setRequestMethod("GET");
                //bufferedReader allows for each line in website returned to be read in json
                BufferedReader oneLine = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                //so,., store the json lines into stringbuilder
                while((line=oneLine.readLine() )!= null)
                {
                    wordsFromURL.append(line);
                }
                oneLine.close();
                //then send it to be parsed
                return parseIntoSynonymURL(wordsFromURL.toString());
            }
            //if something goes wrong and try catch is not able to execute completely throw error
            catch (Exception e)
            {
                return "Error found! Exception " +e+ " thrown";
            }
        }
        //parse the json string casted to string to get only the details we want
        static String parseIntoReadableWeather (String originalString)
        {
            //create a json object so sting can be parsed
            JsonObject objectOfWeather = new JsonParser().parse(originalString).getAsJsonObject();
            //4 different types of objects so that their children can be called
            JsonObject  coord = objectOfWeather.getAsJsonObject("coord");
            JsonObject  main = objectOfWeather.getAsJsonObject("main");
            JsonObject  wind = objectOfWeather.getAsJsonObject("wind");
            JsonObject  clouds = objectOfWeather.getAsJsonObject("clouds");

            //get the respective values using the respective object and cast it to wanted type
            String cityName = objectOfWeather.get("name").getAsString();
            double longitude = coord.get("lon").getAsDouble();
            double latitude = coord.get("lat").getAsDouble();
            double windSpd = wind.get("speed").getAsDouble();
            double cloudy = clouds.get("all").getAsDouble();
            double temp = main.get("temp").getAsDouble();
            temp = (temp * 1.8) - 459.67;   //convert to F
            double feelsLike = main.get("feels_like").getAsDouble();
            feelsLike = (feelsLike * 1.8) - 459.67;//convert to F
            double humid = main.get("humidity").getAsDouble();
            double minTemp = main.get("temp_min").getAsDouble();
            minTemp = (minTemp * 1.8) - 459.67;//convert to F
            double maxTemp = main.get("temp_max").getAsDouble();
            maxTemp = (maxTemp * 1.8) - 459.67;//convert to F

            //then format the output to have 1 decimal point and print the rest of the details
            DecimalFormat uF = new DecimalFormat("####0.0");
            return (cityName+" at longitude: "+longitude+ " and latitude: " + latitude+
                    " has a current temperature of "+uF.format(temp) + "˚F which feels like: "+ uF.format(feelsLike)+
                    ". The minimum temperature is: " +uF.format(minTemp)+"˚F and the maximum temperature is: "+uF.format(maxTemp)+
                    "˚F. The humidity is: "+humid + ". The wind speed is: "+ windSpd
                    + ". The cloudiness is: "+cloudy);
        }
        //parse the json string casted to string for synoyms
        static String parseIntoSynonymURL(String jsonString)
        {
            //store 3 synoyms in the array to be printed
            String[] synArr = new String [3];

            //starting with the array of JSON.. use the object to get the index of the object
            JSONArray objectWord = new JSONArray(jsonString);
            JSONObject objectWordIndex = objectWord.getJSONObject(0);
            //then using the object.. use it to get the array of def and then get the object of it
            JSONArray def = objectWordIndex.getJSONArray("def");
            JSONObject defIndex = def.getJSONObject(0);
            //then using the object.. use it to get the array of sseq and then get the object of it
            JSONArray sseq = defIndex.getJSONArray("sseq");
            JSONArray sseqIndex = sseq.getJSONArray(0);
            //then get the array of the rest of the array.. without the braces
            JSONArray extraBrace = sseqIndex.getJSONArray(0);
            JSONObject anotherNext = extraBrace.getJSONObject(1);
            //then close in on the synonym list and then iterate
            JSONArray synList = anotherNext.getJSONArray("syn_list");

            //iterate thru the JSON array and cast it to an object of the type wd and store in regular array
            for (int i =0; i<3; i++ )
            {
                synArr[i]= ((synList.getJSONArray(0)).getJSONObject(i)).getString("wd");
            }
            //then print the synonyms
            return ("The synonyms are: "+ synArr[0]+ ", " + synArr[1]+ ", and " +synArr[2]);
        }

    }





