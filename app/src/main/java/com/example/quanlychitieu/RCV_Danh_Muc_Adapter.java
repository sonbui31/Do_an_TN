package com.example.quanlychitieu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RCV_Danh_Muc_Adapter extends RecyclerView.Adapter<RCV_Danh_Muc_Adapter.ViewHolder>{
    private ArrayList<Danh_Muc> list;

    public RCV_Danh_Muc_Adapter(ArrayList<Danh_Muc> list) {
        this.list = list;
    }

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String id, String Ten_Danh_Muc);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rcv_danh_muc, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Danh_Muc danh_muc = list.get(position);

        // Sử dụng Glide để tải ảnh từ URL và hiển thị lên ImageView
        Glide.with(holder.itemView.getContext())
                .load(danh_muc.getIcon_Danh_Muc())
                .into(holder.img_icon);

        holder.tv_ten_danh_muc.setText(danh_muc.getTen_Danh_Muc());



        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String id = danh_muc.getId_Danh_Muc();

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Xóa");
                builder.setMessage("Bạn có xóa phòng này không!");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteItem(id);

                    }
                });
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.create();
                builder.show();

                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(danh_muc.getId_Danh_Muc(), danh_muc.getTen_Danh_Muc());
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView img_icon;
        private TextView tv_ten_danh_muc;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_icon = itemView.findViewById(R.id.img_icon);
            tv_ten_danh_muc = itemView.findViewById(R.id.tv_ten_danh_muc);
        }
    }
    // Phương thức để xóa một item trong RecyclerView
    private void deleteItem(String id) {
        DatabaseReference referenceXoa = FirebaseDatabase.getInstance().getReference("Danh_Muc").child(id);
        referenceXoa.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // Xóa thành công, có thể thông báo hoặc làm gì đó tương ứng
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Xóa thất bại, có thể thông báo hoặc làm gì đó tương ứng
            }
        });
    }
}
