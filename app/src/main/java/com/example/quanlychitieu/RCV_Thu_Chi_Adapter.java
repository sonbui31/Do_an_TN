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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RCV_Thu_Chi_Adapter extends RecyclerView.Adapter<RCV_Thu_Chi_Adapter.ViewHolder>{
    private ArrayList<Thu_Chi> list;

    public RCV_Thu_Chi_Adapter(ArrayList<Thu_Chi> list) {
        this.list = list;
    }

    private RCV_Thu_Chi_Adapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String id, String Id_danh_muc);
    }

    public void setOnItemClickListener(RCV_Thu_Chi_Adapter.OnItemClickListener listener){
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rcv_thu_chi, parent,false);
        return new RCV_Thu_Chi_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Thu_Chi thu_chi = list.get(position);

        holder.tv_ghi_chu.setText(thu_chi.getGhi_chu());
        holder.tv_date.setText(thu_chi.getNgay());
        String loai_thu_chi = thu_chi.getLoai_thu_chi();
        String tt = thu_chi.getSo_tien();

        if (loai_thu_chi.equals("Chi phí"))
            holder.tv_so_tien.setText("- " + CurrencyFormatter.formatVND(Double.parseDouble(tt)));
        else if (loai_thu_chi.equals("Thu nhập")){
            holder.tv_so_tien.setText( CurrencyFormatter.formatVND(Double.parseDouble(tt)));

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(thu_chi.getId_thu_chi(), thu_chi.getId_danh_muc());

            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String id = thu_chi.getId_thu_chi();

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Xóa");
                builder.setMessage("Bạn có xóa hoá đơn này không!");
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


        String idDanhMuc = thu_chi.getId_danh_muc();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Danh_Muc").child(idDanhMuc);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Danh_Muc danh_muc = snapshot.getValue(Danh_Muc.class);
                // Sử dụng thư viện Glide để tải và hiển thị ảnh từ URL
                Glide.with(holder.itemView.getContext())
                        .load(danh_muc.getIcon_Danh_Muc())
                        .into(holder.img_icon);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView img_icon;
        TextView tv_ghi_chu, tv_so_tien, tv_date;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_icon = itemView.findViewById(R.id.img_icon);
            tv_ghi_chu = itemView.findViewById(R.id.tv_ghi_chu);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_so_tien = itemView.findViewById(R.id.tv_so_tien);

        }
    }

    private void deleteItem(String id) {
        DatabaseReference referenceXoa = FirebaseDatabase.getInstance().getReference("Thu_Chi").child(id);
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
