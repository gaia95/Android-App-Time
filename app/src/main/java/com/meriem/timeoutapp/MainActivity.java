package com.meriem.timeoutapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.meriem.timeoutapp.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import java.net.InetAddress;
import java.text.DateFormat;
import java.util.Date;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

   long offset = 0; // to be able to adjust time in case of network delay

    @Override
    // What the layout will contain
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        Toolbar toolbar = findViewById(R.id.toolbar);

        binding.fab.setOnClickListener(new View.OnClickListener() {

            private Date getSystemTime(){


                // creating Date object to be able to get and return time
                Date time = new Date();
                time.setTime(time.getTime() + offset);

                // TextView text1.setText("System time: " + time);

                return time;
            }

            private Date getNetworkTime(){

                NTPUDPClient timeClient = new NTPUDPClient();
                timeClient.setDefaultTimeout(2000);

                // Get network time from ntp server if possible, else fallback with system time

                try {

                    // Communicating with and getting information from server
                    InetAddress ntpServer = InetAddress.getByName("1.se.pool.ntp.org");
                    TimeInfo timeInfo = timeClient.getTime(ntpServer);
                    long ntpTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();

                    // Creating an object for saving gathered time info +
                    // printing out to terminal and returning the time
                    Date date = new Date(ntpTime);
                    System.out.println("Returning time from NTPServer: " + date);
                    return date;

                } catch (Exception e){

                    // msg to terminal and calling system time method to run instead
                    System.out.println("!NETWORK ERROR! Returning system time from device: ");
                    Date systemTime = getSystemTime();
                    e.printStackTrace(); // For easier debugging

                    return systemTime;

                }
            }


            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // Referring to text field where info will be displayed and method call
                        final TextView text1 = (TextView) findViewById(R.id.textview_first);
                        Date netTime = getNetworkTime();

                        // Controlling if network time was successfully collected or not
                        if(netTime != null) {

                            netTime.setTime(netTime.getTime() + offset);
                            //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                            DateFormat timeFormat = DateFormat.getTimeInstance();
                            String current = timeFormat.format(netTime);
                            text1.setText(current);

                        } else {

                            // network time fail, show system time
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Date systemTime = getSystemTime();
                                    DateFormat timeFormat = DateFormat.getTimeInstance();
                                    String currentTime = timeFormat.format(systemTime);
                                    text1.setText(currentTime);
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}