package com.example.quanlychitieu;

import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RCV_Icon_Adapter extends RecyclerView.Adapter<RCV_Icon_Adapter.IconViewHolder> {

    private List<Icon_Model> iconList;
    private Uri selectedIconUri;

    // Khai báo một interface để lắng nghe sự kiện click trên item
    public interface OnItemClickListener {
        void onItemClick(Icon_Model icon);
    }

    // Khai báo biến để lưu trữ listener
    private OnItemClickListener listener;

    // Phương thức để thiết lập listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public RCV_Icon_Adapter(List<Icon_Model> iconList) {
        this.iconList = iconList;
    }

    @NonNull
    @Override
    public IconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rcv_icon_danh_muc, parent, false);
        return new IconViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IconViewHolder holder, int position) {
        Icon_Model iconModel = iconList.get(position);
        holder.img_icon.setImageResource(iconModel.getIcon());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gọi phương thức onItemClick của listener và truyền icon được click
                if (listener != null) {
                    listener.onItemClick(iconModel);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return iconList.size();
    }

    static class IconViewHolder extends RecyclerView.ViewHolder {
        ImageView img_icon;

        IconViewHolder(@NonNull View itemView) {
            super(itemView);
            img_icon = itemView.findViewById(R.id.img_icon);

        }
    }
}
