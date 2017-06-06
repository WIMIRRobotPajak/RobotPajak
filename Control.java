package com.witp.spiders;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import java.io.IOException;
import java.util.UUID;

public class Control extends AppCompatActivity {

    Button btnForw, btnBack, btnDis, btnDance, btnLeft, btnRight;
    private Switch mySwitch;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //otrzymanie adresu urz¹dzenia bluetooth
        setContentView(R.layout.activity_control);

        //deklaracja wid¿etów
        btnForw = (Button)findViewById(R.id.button2);
        btnBack = (Button)findViewById(R.id.button3);
        btnDis = (Button)findViewById(R.id.button4);
        btnLeft = (Button)findViewById(R.id.left);
        btnRight = (Button)findViewById(R.id.right);
        btnDance = (Button)findViewById(R.id.dance);
        mySwitch = (Switch) findViewById(R.id.mySwitch);

        new ConnectBT().execute(); //wywo³anie klasy do po³¹czenia

        btnForw.setOnTouchListener( new View.OnTouchListener(){ //ruch do przodu
            public boolean onTouch( View btnForw , MotionEvent theMotion ) {
                switch ( theMotion.getAction() ) {
                    case MotionEvent.ACTION_DOWN: goForward(); break;
                    case MotionEvent.ACTION_UP: StopAction(); break;
                }
                return true;
            }});

        btnBack.setOnTouchListener( new View.OnTouchListener(){ //ruch do ty³u
            public boolean onTouch( View btnBack , MotionEvent theMotion ) {
                switch ( theMotion.getAction() ) {
                    case MotionEvent.ACTION_DOWN: goBack(); break;
                    case MotionEvent.ACTION_UP: StopAction(); break;
                }
                return true;
            }});

        btnLeft.setOnTouchListener( new View.OnTouchListener(){ //ruch w lewo
            public boolean onTouch( View btnLeft , MotionEvent theMotion ) {
                switch ( theMotion.getAction() ) {
                    case MotionEvent.ACTION_DOWN: turnLeft(); break;
                    case MotionEvent.ACTION_UP: StopAction(); break;
                }
                return true;
            }});

        btnRight.setOnTouchListener( new View.OnTouchListener(){ // ruch w prawo
            public boolean onTouch( View btnRight , MotionEvent theMotion ) {
                switch ( theMotion.getAction() ) {
                    case MotionEvent.ACTION_DOWN: turnRight(); break;
                    case MotionEvent.ACTION_UP: StopAction(); break;
                }
                return true;
            }});

        btnDance.setOnTouchListener( new View.OnTouchListener(){ //tañce hulañce
            public boolean onTouch( View btnDance , MotionEvent theMotion ) {
                switch ( theMotion.getAction() ) {
                    case MotionEvent.ACTION_DOWN: DanceAction(); break;
                    case MotionEvent.ACTION_UP: StopAction(); break;
                }
                return true;
            }
        });

        btnDis.setOnClickListener(new View.OnClickListener() //roz³¹czenie
        {
            @Override
            public void onClick(View v)
            {
                Disconnect();
            }
        });

        mySwitch.setChecked(true);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { //zmiana stanów
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    SwitchActionON(); //w³¹czony tryb manualny
                }else{
                    SwitchActionOFF(); // w³¹czony tryb automatyczny
                }
            }
        });
    }

    //___________________________________________________________________

    private void goForward() // ruch do przodu
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("F".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private void goBack() // ruch do ty³u
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("B".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private void turnLeft() //skrêt w lewo
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("L".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private void turnRight() // skrêt w prawo
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("R".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private void DanceAction() //tañce hulañce
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("D".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private void Disconnect() //roz³¹czenie bluetooth
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.close();
            }
            catch (IOException e)
            { msg("Error");}
        }
        finish(); //powrót do pierwszego layoutu
    }

    private void StopAction() //zatrzymanie
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("S".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private void SwitchActionON() //tryb manualny
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("N".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private void SwitchActionOFF() //tryb automatyczny
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("O".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    // funkcja do wywo³ywania Toastów
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>
    {
        private boolean ConnectSuccess = true; //czy urz¹dzenie jest po³¹czone

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(Control.this, "Connecting...", "Please wait!!!");  //pokazuje postêp w ³¹czeniu
        }

        @Override
        protected Void doInBackground(Void... devices) //³¹czenie w tle
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//osi¹ganie mobilnego urz¹dzenia bluetooth
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//³¹czy siê z urz¹dzeniem i sprawdza czy jest dostêpne
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//tworzy po³¹czenie RFCOMM
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//rozpoczyna po³¹czenie
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//gdy próba siê nie powiedzie - wyj¹tek
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //po wywo³aniu doInBackground, sprawdza czy wszystko dobrze idzie
        {
            super.onPostExecute(result);
            if (!ConnectSuccess)
            {
                msg("Connection Failed. Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}


