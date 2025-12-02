package com.example.da1.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.da1.R;
import com.example.da1.models.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private List<Address> addressList;
    private OnAddressSelectedListener listener;
    private Address selectedAddress;

    public interface OnAddressSelectedListener {
        void onAddressSelected(Address address);
    }

    public AddressAdapter(List<Address> addressList) {
        this.addressList = addressList;
    }

    public void setOnAddressSelectedListener(OnAddressSelectedListener listener) {
        this.listener = listener;
    }

    public void setSelectedAddress(Address address) {
        this.selectedAddress = address;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addressList.get(position);
        boolean isSelected = selectedAddress != null && 
            address.getId() != null && 
            address.getId().equals(selectedAddress.getId());
        holder.bind(address, isSelected);
    }

    @Override
    public int getItemCount() {
        return addressList != null ? addressList.size() : 0;
    }

    class AddressViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView tvFullName;
        private TextView tvPhone;
        private TextView tvFullAddress;
        private TextView tvDefault;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvFullAddress = itemView.findViewById(R.id.tvFullAddress);
            tvDefault = itemView.findViewById(R.id.tvDefault);
        }

        public void bind(Address address, boolean isSelected) {
            tvFullName.setText(address.getFullName() != null ? address.getFullName() : "");
            tvPhone.setText(address.getPhone() != null ? address.getPhone() : "");
            tvFullAddress.setText(address.getFullAddress());
            
            if (address.isDefault()) {
                tvDefault.setVisibility(View.VISIBLE);
            } else {
                tvDefault.setVisibility(View.GONE);
            }

            // Highlight selected address
            if (isSelected) {
                cardView.setCardBackgroundColor(0xFFE3F2FD);
            } else {
                cardView.setCardBackgroundColor(0xFFFFFFFF);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddressSelected(address);
                }
            });
        }
    }
}

