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
import java.util.Set;
import java.util.UUID;

import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    SeekBar verticalSeekBar;
    SeekBar simpleSeekBar;
    Boolean connected = false;

    int progressChangedValue = 0;
    int verticalChangedValue = 0;

    private final String DEVICE_ADDRESS = "00:18:E5:04:EA:FF"; //MAC Address of Bluetooth Module
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    Button bluetooth_connect_btn;

    String command;
    String drive;
    String steer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetooth_connect_btn = (Button) findViewById(R.id.bluetooth_connect_btn);
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
        verticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                verticalChangedValue = progress - 100;
                displayMessage(verticalChangedValue, (TextView) findViewById(R.id.verticalProgress), "Speed: ");
                if (connected) {
                    drive = "#" + Integer.toString(verticalChangedValue);

                    if (drive + steer != command) {
                        command = drive + steer;
                        try {
                            outputStream.write(command.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(100);
            }

        });
        // perform seek bar change listener event used for getting the progress value
        simpleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress - 100;
               /* Toast.makeText(MainActivity.this, "Seek bar progress is :" + progressChangedValue,
                        Toast.LENGTH_SHORT).show();*/
                displayMessage(progressChangedValue, (TextView) findViewById(R.id.progress), "Turn angle: ");
                if (connected) {
                    steer = "$" + Integer.toString(progressChangedValue);

                    if (drive + steer != command) {
                        command = drive + steer;

                        try {
                            outputStream.write(command.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(MainActivity.this, "Seek bar progress is :" + progressChangedValue,
                //Toast.LENGTH_SHORT).show();
                seekBar.setProgress(100);

            }
        });

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

            Toast.makeText(getApplicationContext(),
                    "Connection to bluetooth device successful", Toast.LENGTH_LONG).show();
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

    @Override
    protected void onStart() {
        super.onStart();
    }


    private void displayMessage(int value, TextView progressTextView, String text) {
        //TextView progressTextView = (TextView) findViewById(R.id.progress);
        progressTextView.setText(text + (value));

    }


}