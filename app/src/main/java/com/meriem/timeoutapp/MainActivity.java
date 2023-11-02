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
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;


   // private long systemTime = 0;
   long offset = 0; // to be able to adjust time in case of network delay

    @Override
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

            private void getSystemTime(){

                Date time = new Date();
                time.setTime(time.getTime() + offset);

                Calendar cal = Calendar.getInstance();
                cal.setTime(time);

                int hours = cal.get(Calendar.HOUR_OF_DAY);
                int minutes = cal.get(Calendar.MINUTE);
                int seconds = cal.get(Calendar.SECOND);
                // Field for the time
                final TextView textView = findViewById(R.id.textview_second);

                // Sending information to the text field display on app
                String currentTime = hours + ":" + minutes + ":" + seconds;
                textView.setTextSize(75);
                textView.setText(currentTime);

            }

            private Date getNetworkTime(){


                NTPUDPClient timeClient = new NTPUDPClient();
                timeClient.setDefaultTimeout(2000);

                // Get network time if possible, else continue with system time

                try {

                    InetAddress ntpServer = InetAddress.getByName("1.se.pool.ntp.org");
                    TimeInfo timeInfo = timeClient.getTime(ntpServer);
                    long ntpTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
                    Date date = new Date(ntpTime);
                    System.out.println("Returning time from NTPServer: " + date);
                    return date;

                } catch (Exception e){

                    // msg to terminal
                    System.out.println("!NETWORK ERROR! Returning system time from device: ");
                    getSystemTime();
                    e.printStackTrace();

                    return new Date();
                }
            }


            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        final TextView text1 = (TextView) findViewById(R.id.textview_first);
                        Date netTime = getNetworkTime();
                        netTime.setTime(netTime.getTime() + offset);

                                //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

                        DateFormat timeFormat = DateFormat.getTimeInstance();
                        String current = timeFormat.format(netTime);
                        text1.setText(current);
                    }
                }).start();
            }
        });
    }

    // ----------------------------######################----------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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