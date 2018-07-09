package ipb.dam.apptrainer.db;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * This class is used to hide information about the system
 * You can use that, like a data base;
 */

public class DataBase extends AppCompatActivity {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    /**
     * {@code ourInstance } holds the only instance of this class
      */
    private static DataBase ourInstance = null;

    /**
     * Name of the database for this application
     */
    private static final String DATA_BASE_DEFAULT = "bunny_coach_shared_preferences";

    /**
     * Key for storing data of the current training status of the user.
     */
    private static final String KEY_TRAINING = "key_training";

    /**
     * Key for storing data of training received from the server.
     */
    private static final String KEY_DATA = "key_data";

    /**
     * Key that maps to a value in the database that will return the date of the last synchronization.
     */
    private static final String KEY_LAST_SYNC = "key_last_sync";

    /**
     * Key that maps to the last email that has been used to log.
     */
    private static final String KEY_LOGGED_EMAIL = "key_logged_email";

    /**
     * Format of the date stored in the database.
     */
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

    /**
     * @param context Context of the app.
     * @return Instance of the class
     */
    public static synchronized DataBase getInstance(Context context) {

        if (ourInstance == null) {
            ourInstance = new DataBase(context);
        }

        return ourInstance;
    }


    private DataBase(Context context){

        preferences = context.getSharedPreferences(DATA_BASE_DEFAULT, MODE_PRIVATE);
        String loggedEmail = preferences.getString(KEY_LOGGED_EMAIL, null);

        if(loggedEmail != null){
            preferences = context.getSharedPreferences(loggedEmail, MODE_PRIVATE);
        }

    }

    /**
     * Changes the database to access data from the given user
     * @param context App context. Cannot be {@code null}.
     * @param login Login of the user to get access do his database. If {@code null},
     *              change to the default database and clears the register of the last logged user.
     */
    public void changeUser(@NonNull Context context, @Nullable String login){

        if (login == null) {
            preferences = context.getSharedPreferences(DATA_BASE_DEFAULT, MODE_PRIVATE);
            preferences.edit().remove(KEY_LOGGED_EMAIL).commit();
        }

        else
            preferences = context.getSharedPreferences(login, MODE_PRIVATE);

    }

    /**
     * Saves the current date to the database.
     */
    public void updateSyncDate(){

        Date currentDate = Calendar.getInstance(Locale.US).getTime();
        editor = preferences.edit();
        editor.putString(KEY_LAST_SYNC, dateFormat.format(currentDate));
        editor.commit();

    }

    /**
     *
     * @return Date of the last sync in the format: dd/MM/yyyy, locale US or
     * {@code null} if there was no sync.
     */
    public @Nullable Date getLastSyncDate(){

        String storedDate = preferences.getString(KEY_LAST_SYNC, null);

        if (storedDate == null) {
            return null;
        }

        Date date = null;
        try {
            date = dateFormat.parse(storedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }


    /**
     * @param json This parameters is use to take all the Json received from server,
     *             in this function the jason is converted to a string and save at database;
     */
    public void setTrainigTrackerDB(JSONObject json){
        Log.i("TrainigTrackerDB", json.toString());
        editor = preferences.edit();
        editor.putString(KEY_TRAINING, json.toString());
        
        //save in data base
        editor.commit();
        try {
            Log.i("Trainig.bankBuild", getTrainigTrackerDBJ().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    /**
     *
     * @param json This parameters is use to take all the Json received from server,
     *             in this function the jason is converted to a string and save at database;
     *             If {@code null}, removes the json data from the database.
     */

    public void setDataDB (@Nullable JSONObject json){
        Log.i("DataDB", json == null ? "null" : json.toString());
        editor = preferences.edit();

        if(json == null)
            editor.remove(KEY_DATA);
        else
            editor.putString(KEY_DATA, json.toString());

        //save in data base
        editor.commit();
    }

    /**
     *
     * @return The Json saved in the setDataDB can be take with this fuction; Null value
     * means that the user is not logged (no data in the database).
     */
    public @Nullable JSONObject getDataDBJ() throws JSONException {

        String jsonString = preferences.getString(KEY_DATA, null);

        if (jsonString == null)
            return null;
        else
            return new JSONObject(jsonString);
    }

    /**
     *
     * @return The Json saved in the {@link #setTrainigTrackerDB(JSONObject)} can be take with this fuction;
     */

    public JSONObject getTrainigTrackerDBJ() throws JSONException {
        return new JSONObject(
                preferences.getString(KEY_TRAINING,
                        "{\"unused\":\"\",\"qtd_exercises_done\":0,\"0\":[],\"qtd_exercises\":0,\"1\":[],\"2\":[],\"3\":[],\"4\":[],\"5\":[],\"6\":[]}")
        );
    }

    /**
     * TODO check if this is needed
     * @return The Json saved in the setDataDB can be take in this fuction in format of string;
     */

    public String getDataDB(){

        return preferences.getString(KEY_DATA, null);
    }

    /**
     * TODO check if this is needed
     * @return The Json saved in the setTrainigTrackerDB can be take in this fuction in format of string;
     */

    public String getTrainigTrackerDB(){

        return preferences.getString(KEY_TRAINING, null);
    }


}
