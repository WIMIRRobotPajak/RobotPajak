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
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //otrzymanie adresu urz�dzenia bluetooth
        setContentView(R.layout.activity_control);

        //deklaracja wid�et�w
        btnForw = (Button)findViewById(R.id.button2);
        btnBack = (Button)findViewById(R.id.button3);
        btnDis = (Button)findViewById(R.id.button4);
        btnLeft = (Button)findViewById(R.id.left);
        btnRight = (Button)findViewById(R.id.right);
        btnDance = (Button)findViewById(R.id.dance);
        mySwitch = (Switch) findViewById(R.id.mySwitch);

        new ConnectBT().execute(); //wywo�anie klasy do po��czenia

        btnForw.setOnTouchListener( new View.OnTouchListener(){ //ruch do przodu
            public boolean onTouch( View btnForw , MotionEvent theMotion ) {
                switch ( theMotion.getAction() ) {
                    case MotionEvent.ACTION_DOWN: goForward(); break;
                    case MotionEvent.ACTION_UP: StopAction(); break;
                }
                return true;
            }});

        btnBack.setOnTouchListener( new View.OnTouchListener(){ //ruch do ty�u
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

        btnDance.setOnTouchListener( new View.OnTouchListener(){ //ta�ce hula�ce
            public boolean onTouch( View btnDance , MotionEvent theMotion ) {
                switch ( theMotion.getAction() ) {
                    case MotionEvent.ACTION_DOWN: DanceAction(); break;
                    case MotionEvent.ACTION_UP: StopAction(); break;
                }
                return true;
            }
        });

        btnDis.setOnClickListener(new View.OnClickListener() //roz��czenie
        {
            @Override
            public void onClick(View v)
            {
                Disconnect();
            }
        });

        mySwitch.setChecked(true);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { //zmiana stan�w
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    SwitchActionON(); //w��czony tryb manualny
                }else{
                    SwitchActionOFF(); // w��czony tryb automatyczny
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

    private void goBack() // ruch do ty�u
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

    private void turnLeft() //skr�t w lewo
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

    private void turnRight() // skr�t w prawo
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

    private void DanceAction() //ta�ce hula�ce
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

    private void Disconnect() //roz��czenie bluetooth
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
        finish(); //powr�t do pierwszego layoutu
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

    // funkcja do wywo�ywania Toast�w
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>
    {
        private boolean ConnectSuccess = true; //czy urz�dzenie jest po��czone

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(Control.this, "Connecting...", "Please wait!!!");  //pokazuje post�p w ��czeniu
        }

        @Override
        protected Void doInBackground(Void... devices) //��czenie w tle
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//osi�ganie mobilnego urz�dzenia bluetooth
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//��czy si� z urz�dzeniem i sprawdza czy jest dost�pne
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//tworzy po��czenie RFCOMM
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//rozpoczyna po��czenie
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//gdy pr�ba si� nie powiedzie - wyj�tek
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //po wywo�aniu doInBackground, sprawdza czy wszystko dobrze idzie
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


