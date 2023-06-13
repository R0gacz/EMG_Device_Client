package com.project.emg_device_client;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecycleAvailableDeviceAdapter extends RecyclerView.Adapter<RecycleAvailableDeviceAdapter.ViewHolder> {

    Context _context;
    ArrayList<AvailableDevice> _arrDevices;

    public RecycleAvailableDeviceAdapter(Context context, ArrayList<AvailableDevice> arrDevices){
        _context = context;
        _arrDevices = arrDevices;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(_context).inflate(R.layout.available_device_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AvailableDevice bleDevice = _arrDevices.get(position);
        holder._txtDeviceName.setText(bleDevice.name);
        holder._txtMacAddress.setText(bleDevice.macAddress);
        holder._txtRssiValue.setText(Integer.toString(bleDevice.rssi));
        holder._connectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                holder._connectBtn.setText("Connecting");
                ConnectToDevice(bleDevice, holder);
            }
        });
    }


    private void ConnectToDevice(AvailableDevice bleDevice, ViewHolder holder){

            BluetoothLeService service = new BluetoothLeService();
            service.initialize();
            Boolean isConnected =  service.connect(bleDevice.macAddress);
        if (isConnected){
            holder._connectBtn.setText("CONNECTED");
        }
        else {
//            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("Connection attempt failed");
//            builder.setPositiveButton(android.R.string.ok, null);
//            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            });
//            builder.show();
            holder._connectBtn.setText("DisCONNECT");
        }
    }

    @Override
    public int getItemCount() {
        return _arrDevices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView _txtDeviceName, _txtMacAddress, _txtRssiValue;
        Button _connectBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            _txtDeviceName = itemView.findViewById(R.id.deviceName);
            _txtMacAddress = itemView.findViewById(R.id.macAddress);
            _txtRssiValue = itemView.findViewById(R.id.rssiValue);
            _connectBtn = (Button) itemView.findViewById(R.id.connectBtn);
        }





    }
}
