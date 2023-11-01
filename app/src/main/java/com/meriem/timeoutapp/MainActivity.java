package com.meriem.timeoutapp;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.meriem.timeoutapp.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.net.InetAddress;
import java.util.Date;


import java.io.IOException;
import java.util.Date;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.SocketException;
import java.net.UnknownHostException;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    // Variabler som lagrar tid.
    private long systemTime = 0;
    private long offset = 0;

    // Returnerar skillnaden mellan system tid och nätverks tid
    protected long getTimeOffset(){
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        uppdateraTiden();

        binding.fab.setOnClickListener(new View.OnClickListener() {

            private Date getNetworkTime(){

                NTPUDPClient timeClient = new NTPUDPClient();
                timeClient.setDefaultTimeout(2000);
                TimeInfo timeInfo;

                try {

                    InetAddress inaddr = InetAddress.getByName("1.se.pool.ntp.org");
                    timeInfo = timeClient.getTime(inaddr);
                    long NTPTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
                    Date date = new Date(NTPTime);
                    System.out.println("getTime() returning NTPServer time: " + date);
                    return date;


                } catch (Exception e){

                    System.out.println("FEL från nätverk: " + e.toString());
                    System.out.println("getTime() returning System time");
                    return new Date();
                }
            }

            @Override
            public void onClick(View view) {


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            final Date d = getNetworkTime();
                            final TextView text1 = (TextView) findViewById(R.id.textview_first);
                            d.setTime(d.getTime() + offset);
                            text1.setText(d.getHours() + ":" + d.getMinutes() + " " + d.getSeconds());

                        } catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                }).start();
            }

        });
    }

    public void uppdateraNatverksTid() {


        /*
        try {
            client.open(123, InetAddress.getByName("time.google.com"));
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }  */

    }


    public void uppdateraTiden(){

        // Hämta fältet vi vill skriva till
        final TextView text1 = (TextView) findViewById(R.id.textview_first);
        text1.setText("00:00");
        text1.setTextSize(70);

        final Date datum = new Date();
        text1.setText(
                (datum.getHours() > 9 ? "" : "0") + datum.getHours() + ":" +
                        (datum.getMinutes() > 9 ? "" : "0") + datum.getMinutes() + " " +
                        (datum.getSeconds() > 9 ? "" : "0") + datum.getSeconds()
        );
    }




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