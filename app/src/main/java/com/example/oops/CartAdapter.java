package com.example.oops;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartItem> cartItems;

    public CartAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView priceTextView;
        public TextView quantityTextView;
        public TextView instructionsTextView;
        public ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            nameTextView = view.findViewById(R.id.cart_item_name);
            priceTextView = view.findViewById(R.id.cart_item_price);
            quantityTextView = view.findViewById(R.id.cart_item_quantity);
            instructionsTextView = view.findViewById(R.id.cart_item_instructions);
            imageView = view.findViewById(R.id.cart_item_image);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.nameTextView.setText(item.getName());
        holder.priceTextView.setText(item.getPrice());
        holder.quantityTextView.setText("Quantity: " + item.getQuantity());
        holder.instructionsTextView.setText("Instructions: " + (item.getInstructions().isEmpty() ? "None" : item.getInstructions()));

        int imageResId = item.getImageResId();
        if (imageResId != 0) {
            holder.imageView.setImageResource(imageResId);
        } else {
            holder.imageView.setImageDrawable(null);
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }
}