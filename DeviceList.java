package com.witp.spiders;

        import android.content.Intent;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.ListView;
        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
        import android.widget.TextView;
        import android.widget.Toast;
        import java.util.ArrayList;
        import java.util.Set;

public class DeviceList extends AppCompatActivity
{
    //wid¿ety
    Button btnPaired;
    ListView devicelist;
    //bluetooth
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        //wywo³anie wid¿etów
        btnPaired = (Button)findViewById(R.id.button);
        devicelist = (ListView)findViewById(R.id.listView);

        //jeœli urz¹dzenie ma bluetooth
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if(myBluetooth == null) //jeœli nie ma bluetooth
        {
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
            finish();
        }
        else if(!myBluetooth.isEnabled()) //jeœli jest dostêp do bluetooth
        {
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }

        btnPaired.setOnClickListener(new View.OnClickListener() { //dzia³anie przycisku wywo³uj¹cego sparowane urz¹dzenia
            @Override
            public void onClick(View v)
            {
                pairedDevicesList();
            }
        });
    }

    private void pairedDevicesList() //wyœwitlenie sparowanych urz¹dzeñ
    {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();
        if (pairedDevices.size()>0)
        {
            for(BluetoothDevice bt : pairedDevices)
            {
                list.add(bt.getName() + "\n" + bt.getAddress()); //wyœwietla nazwy i adresy urz¹dzeñ
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }
        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        devicelist.setAdapter(adapter);
        devicelist.setOnItemClickListener(myListClickListener); //metoda wywo³ywana gdy urz¹dzenie z listy zostaje wybrane

    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            // pobiera adres MAC urz¹dzenia
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // przejœcie do sterowania
            Intent i = new Intent(DeviceList.this, Control.class);
            i.putExtra(EXTRA_ADDRESS, address);
            startActivity(i);
        }
    };

}
