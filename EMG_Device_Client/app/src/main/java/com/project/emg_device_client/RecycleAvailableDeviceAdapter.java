package com.project.emg_device_client;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
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
    public BluetoothDevice connectedDevice;
    public BluetoothLeService bleService;

    public RecycleAvailableDeviceAdapter(Context context, ArrayList<AvailableDevice> arrDevices){

        bleService = null;
        connectedDevice = null;
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
        if (EmgDeviceService.MacAddress != null && connectedDevice != null) {
            if ((EmgDeviceService.MacAddress).equals(bleDevice.device.getAddress())) {
                holder._connectBtn.setSelected(true);
            holder._connectBtn.setText("DISCONNECT");
        }
            else holder._connectBtn.setText("CONNECT");
        }
        holder._connectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!holder._connectBtn.isSelected()) {
                    holder._connectBtn.setSelected(true);
                    connectedDevice = bleDevice.device;
                    EmgDeviceService.MacAddress = connectedDevice.getAddress();
                    holder._connectBtn.setText("DISCONNECT");
                }else{
                    holder._connectBtn.setSelected(false);
                    EmgDeviceService.MacAddress = null;
                    connectedDevice = null;
                    holder._connectBtn.setText("CONNECT");

                }

            }
        });
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
