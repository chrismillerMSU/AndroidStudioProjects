package com.example.android.seekbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Button;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import android.os.Handler;
import java.io.InputStream;

import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    //public static java.util.concurrent.ExecutorService service;

    SeekBar verticalSeekBar;
    SeekBar simpleSeekBar;
    Boolean connected = false;

    private Thread transmit;
    private static boolean sendUpdate = false;

    private final String DEVICE_ADDRESS = "00:18:E5:04:EA:FF"; //MAC Address of Bluetooth Module
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    Thread thread;
    Boolean stopThread;
    byte buffer[];
    private InputStream inputStream;
    TextView textView;

    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    Button bluetooth_connect_btn;

    String command;
    String drive = "#0";
    String steer = "$0";
    long lastSent = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        bluetooth_connect_btn = (Button) findViewById(R.id.bluetooth_connect_btn);
        textView = (TextView) findViewById(R.id.receiveData);
        bluetooth_connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (BTinit()) {
                    Toast.makeText(getApplicationContext(), "BT Initialized", Toast.LENGTH_SHORT).show();
                    BTconnect();
                    //Toast.makeText(getApplicationContext(), "BT Initialized", Toast.LENGTH_SHORT).show();
                }

            }
        });
        // initiate  views
        simpleSeekBar = (SeekBar) findViewById(R.id.simpleSeekBar);
        verticalSeekBar = (SeekBar) findViewById(R.id.verticalSeekBar);
        verticalSeekBar.setOnSeekBarChangeListener(listener());
        // perform seek bar change listener event used for getting the progress value
        simpleSeekBar.setOnSeekBarChangeListener(listener());

        transmit = new Thread(listenTransmit());
        transmit.start();
    }

    public boolean BTinit() {
        boolean found = false;

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) //Checks if the device supports bluetooth
        {
            Toast.makeText(getApplicationContext(), "Device doesn't support bluetooth", Toast.LENGTH_SHORT).show();
        }
        if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            //Toast.makeText(getApplicationContext(), "Device connected", Toast.LENGTH_SHORT).show();
        }

        if (!bluetoothAdapter.isEnabled()) //Checks if bluetooth is enabled. If not, the program will ask permission from the user to enable it
        {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        if (bondedDevices.isEmpty()) //Checks for paired bluetooth devices
        {
            Toast.makeText(getApplicationContext(), "Please pair the device first", Toast.LENGTH_SHORT).show();
        } else {
            for (BluetoothDevice iterator : bondedDevices) {
                if (iterator.getAddress().equals(DEVICE_ADDRESS)) {
                    device = iterator;
                    found = true;
                    break;
                }
            }
        }

        return found;
    }

    public boolean BTconnect() {
        connected = true;

        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID); //Creates a socket to handle the outgoing connection
            socket.connect();
            //beginListenForData();

            /*Toast.makeText(getApplicationContext(),
                    "Connection to bluetooth device successful", Toast.LENGTH_LONG).show();*/
            bluetooth_connect_btn.setVisibility(View.GONE);
        } catch (IOException e) {
            e.printStackTrace();
            connected = false;
        }

        if (connected) {
            try {
                outputStream = socket.getOutputStream(); //gets the output stream of the socket
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return connected;
    }

    //Code from http://www.electronics-lab.com/get-sensor-data-arduino-smartphone-via-bluetooth/
    void beginListenForData(){
        final Handler handler = new Handler();
        stopThread = false;
        buffer = new byte[1024];
        /*Toast.makeText(getApplicationContext(),
                "Test", Toast.LENGTH_LONG).show(); //pass*/
        Thread thread  = new Thread(new Runnable()
        {



            public void run()
            {
                Toast.makeText(getApplicationContext(),
                        "Test1.5", Toast.LENGTH_LONG).show(); //failed
                Toast.makeText(getApplicationContext(),
                        "Test2", Toast.LENGTH_LONG).show(); //failed
                while(!Thread.currentThread().isInterrupted() && !stopThread)
                {
                    try
                    {
                        int byteCount = inputStream.available();
                        if(byteCount > 0)
                        {
                            byte[] rawBytes = new byte[byteCount];
                            inputStream.read(rawBytes);
                            final String string=new String(rawBytes,"UTF-8");
                            handler.post(new Runnable() {
                                public void run()
                                {
                                    textView.setText(string);
                                }
                            });

                        }
                    }
                    catch (IOException ex)
                    {
                        stopThread = true;
                    }
                }
            }
        });

        thread.start();
    }

    private Runnable listenTransmit() {
        return new Runnable() {
            public void run() {
                while (true) {
                    if (sendUpdate && connected) {
                        steer = "$" + (simpleSeekBar.getProgress() - 20);
                        drive = "#" + (verticalSeekBar.getProgress() - 20);
                        if (drive + "," + steer + "," != command) {
                            command = drive + "," + steer + ",";
                            lastSent = System.currentTimeMillis();

                            try {
                                outputStream.write(command.getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        sendUpdate = false;
                    }
//                    ..l
                }
            }

            ;
        };
    }


    private SeekBar.OnSeekBarChangeListener listener(){
        return new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //textView.setText(""+System.currentTimeMillis());
                //verticalChangedValue = progress - 20;
                displayMessage(verticalSeekBar.getProgress() - 20, (TextView) findViewById(R.id.verticalProgress), "Speed: ");
                displayMessage(simpleSeekBar.getProgress() - 20, (TextView) findViewById(R.id.progress), "Turn angle: ");
                if(System.currentTimeMillis() - lastSent > 200 || !fromUser || Math.abs(simpleSeekBar.getProgress()-20) == 20)
                    sendUpdate = true;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getApplicationContext(),
                        command, Toast.LENGTH_LONG).show();
                seekBar.setProgress(20);
            }
        };
    }



    @Override
    protected void onStart() {
        super.onStart();
    }


    private void displayMessage(int value, TextView progressTextView, String text) {
        //TextView progressTextView = (TextView) findViewById(R.id.progress);
        progressTextView.setText(text + (value));

    }


}