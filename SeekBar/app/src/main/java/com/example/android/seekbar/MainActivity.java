package com.example.android.seekbar;

import android.content.Context;
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

import java.io.FileReader;
import java.io.FileWriter;
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
    InputStream inputStream;
    TextView textView;
    float battery= 0;

    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    Button bluetooth_connect_btn;

    String command;
    String drive = "#0";
    String steer = "$0";
    long lastSent = System.currentTimeMillis();
    volatile boolean stopWorker;
    int readBufferPosition;
    byte[] readBuffer;
    Thread listenerThread;
//    java.io.File dir = android.os.Environment.getExternalStorageDirectory();
//    java.io.File file = new java.io.File(dir.getAbsolutePath() + "/carControllerData.file");

    long lastRunTime = 0, runningUpTime = 0;
    double lastBattery = 0, runningBattery = 0;
    long prevRunTime = 0;
    double prevBatt = 0;
    long lastSave = 0;

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

//        readFile();

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


        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID); //Creates a socket to handle the outgoing connection
            socket.connect();
            //beginListenForData();

            /*Toast.makeText(getApplicationContext(),
                    "Connection to bluetooth device successful", Toast.LENGTH_LONG).show();*/
            bluetooth_connect_btn.setVisibility(View.GONE);
            //connected = true;
        } catch (IOException e) {
            e.printStackTrace();
            connected = false;
            return connected;
        }


            try {
                outputStream = socket.getOutputStream(); //gets the output stream of the socket
                inputStream = socket.getInputStream();
                connected = true;
            } catch (IOException e) {
                e.printStackTrace();
            }


        return connected;
    }

    //Code from http://www.electronics-lab.com/get-sensor-data-arduino-smartphone-via-bluetooth/
    //https://stackoverflow.com/questions/13450406/how-to-receive-serial-data-using-android-bluetooth
    void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        listenerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = inputStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            inputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        public void run() {
                                            textView.setText(data);
                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        listenerThread.start();
    }


    private Runnable listenTransmit() {

        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        return new Runnable() {
            public void run() {
                final long START = System.currentTimeMillis();
                while (!Thread.currentThread().isInterrupted() && !stopWorker) { //while(true)
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
                                //bluetooth_connect_btn.setVisibility(View.VISIBLE);
                            }
                        }
                        sendUpdate = false;

                    }
                    if (connected) {
                        try {
                            if (inputStream.available() > 0) {
                                try {
                                    Thread.sleep(250);
                                } catch (InterruptedException e) {
                                    //ignore
                                }
//                            final int bytesAvailable = inputStream.available();
//                            byte[] packetBytes = new byte[bytesAvailable];
//                            inputStream.read(packetBytes);
                                final int BUFFER_SIZE = 256;
                                byte[] dataReceived = new byte[BUFFER_SIZE];
                                int index = 0;
                                while (inputStream.available() > 0) {
                                    int numReceived = inputStream.read(dataReceived, index, inputStream.available());
                                    index += numReceived;
                                }
                                StringBuilder sb = new StringBuilder();
                                for (byte b : dataReceived) {
                                    sb.append((char) b); //String.format("%02X ", b));

                                }
                                String dataFinal = sb.toString();
                                String dataPoints[] = dataFinal.split(",");
                                try{
                                    int temp = Integer.parseInt(dataPoints[0]); // time vector
                                    double temp2 = Double.parseDouble(dataPoints[1]); // battery drained
                                    runningUpTime = (temp > runningUpTime) ? temp : runningUpTime;
                                    runningBattery = (temp2 > runningBattery) ? temp2 : runningBattery;
                                } catch(NumberFormatException e1) {

                                }
                                dataFinal = "Battery: ";
                                double percent = 100 - (runningBattery + lastBattery)/38.0;
                                dataFinal += String.format("%.3f%%", percent);
                                //double remainingTime = percent / ((100-percent)/(runningUpTime+lastRunTime));
                                //double remainingTime = (3800 - (runningBattery + lastBattery)) / ((runningBattery - prevBatt)/(runningUpTime-prevRunTime));
                                //dataFinal += "%\nTime Left: ";
                                //dataFinal += String.format("%d:%d:%d", (int) Math.floor(remainingTime/3600), (int) (Math.floor(remainingTime/60)%60), (int) (Math.floor(remainingTime)%60));
                                //final String data = new String(dataReceived , "UTF-8");
                                final String data = dataFinal;
//                                prevBatt = runningBattery;
//                                prevRunTime = runningUpTime;
                                //battery += Float.parseFloat(data);
                                handler.post(new Runnable() {
                                    public void run() {
                                        textView.setText(data);
                                        //textView.setText(("Battery: " + battery));
                                    }
                                });
//                                if(System.currentTimeMillis() - lastSave > 15000) {
//                                    lastSave = System.currentTimeMillis();
//                                    writeFile();
//                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                            bluetooth_connect_btn.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            //handler.post(new Runnable() {
//                            public void run() {
                                    textView.setText(e.getStackTrace()[0].toString().split(":")[1]);
//                                }
//                            });
                        }


                        //End
                        //delete end if bracket
                    }
                }

            }
        };

    }

    ;


    private SeekBar.OnSeekBarChangeListener listener() {
        return new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //textView.setText(""+System.currentTimeMillis());
                //verticalChangedValue = progress - 20;
                displayMessage(verticalSeekBar.getProgress() - 20, (TextView) findViewById(R.id.verticalProgress), "Speed: ");
                displayMessage(simpleSeekBar.getProgress() - 20, (TextView) findViewById(R.id.progress), "Turn angle: ");
                if (System.currentTimeMillis() - lastSent > 200 || !fromUser || Math.abs(simpleSeekBar.getProgress() - 20) == 0)
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
    /*
    private void readFile(){
        try{
            if (!dir.exists()){
                dir.mkdirs();
            }
            if (!file.exists()){
                file.createNewFile();
            }
            java.io.BufferedReader br = new java.io.BufferedReader(new FileReader(
                file));
            String in;
            while ((in=br.readLine()) != null){
                String[] input = in.split(",");
                lastRunTime = Integer.parseInt(input[0]);
                lastBattery = Integer.parseInt(input[1]);
            }
            br.close();

        }catch(Exception e){
        }
    }
    private void writeFile(){
        try{
            if (!dir.exists()){
                dir.mkdirs();
            }
            if (!file.exists()){
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file);
            String data=String.format("%d,%.5f", runningUpTime+lastRunTime, runningBattery+lastBattery);
            fw.write(data);
            fw.flush();
            fw.close();

        }catch(Exception e){
            textView.setText(file.getAbsolutePath());
        }
    }
*/
    private void displayMessage(int value, TextView progressTextView, String text) {
        //TextView progressTextView = (TextView) findViewById(R.id.progress);
        progressTextView.setText(text + (value));

    }


}
