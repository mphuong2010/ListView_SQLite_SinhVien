package com.example.sinhvien_listview_sqlite;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<SinhVien> {

    private static final String TAG = "ListAdapter";
    Activity context;
    int idLayoutResource;
    ArrayList<SinhVien> sinhVienList;

    public ListAdapter(Activity context, int idLayoutResource, ArrayList<SinhVien> sinhVienList) {
        super(context, idLayoutResource, sinhVienList);
        this.context = context;
        this.idLayoutResource = idLayoutResource;
        this.sinhVienList = sinhVienList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(idLayoutResource, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.imgAvatar = convertView.findViewById(R.id.imgAvatar);
            viewHolder.tvHoTen = convertView.findViewById(R.id.tvHoTen);
            viewHolder.tvMssv = convertView.findViewById(R.id.tvMssv);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (sinhVienList != null && position < sinhVienList.size()) {
            SinhVien sinhVien = sinhVienList.get(position);

            if (sinhVien != null) {
                // Hiển thị thông tin văn bản
                viewHolder.tvHoTen.setText(sinhVien.getHoTen() != null ? sinhVien.getHoTen() : "N/A");
                viewHolder.tvMssv.setText(sinhVien.getMssv() != null ? sinhVien.getMssv() : "N/A");


                String avatarPath = sinhVien.getAvatarPath();
                if (avatarPath != null && !avatarPath.isEmpty()) {
                    File imgFile = new File(avatarPath);
                    if (imgFile.exists() && imgFile.isFile()) {
                        try {
                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            if (myBitmap != null) {
                                viewHolder.imgAvatar.setImageBitmap(myBitmap);
                            } else {
                                Log.e(TAG, "Không thể giải mã ảnh từ: " + avatarPath + " cho sinh viên: " + sinhVien.getHoTen());
                                viewHolder.imgAvatar.setImageResource(R.mipmap.ic_launcher_round);
                            }
                        } catch (OutOfMemoryError e) {
                            Log.e(TAG, "OutOfMemoryError khi tải ảnh cho ListView: " + avatarPath + " cho sinh viên: " + sinhVien.getHoTen(), e);
                            viewHolder.imgAvatar.setImageResource(R.mipmap.ic_launcher_round);
                        } catch (Exception e) {
                            Log.e(TAG, "Lỗi không xác định khi tải ảnh cho ListView: " + avatarPath + " cho sinh viên: " + sinhVien.getHoTen(), e);
                            viewHolder.imgAvatar.setImageResource(R.mipmap.ic_launcher_round);
                        }
                    } else {
                        Log.w(TAG, "File ảnh không tồn tại hoặc không phải file: " + avatarPath + " cho sinh viên: " + sinhVien.getHoTen());
                        viewHolder.imgAvatar.setImageResource(R.mipmap.ic_launcher_round);
                    }
                } else {
                    Log.w(TAG, "Đường dẫn ảnh null hoặc rỗng cho sinh viên: " + sinhVien.getHoTen());
                    viewHolder.imgAvatar.setImageResource(R.mipmap.ic_launcher_round);
                }
            } else {
                Log.w(TAG, "Đối tượng SinhVien tại vị trí " + position + " là null.");
                viewHolder.tvHoTen.setText("N/A");
                viewHolder.tvMssv.setText("N/A");
                viewHolder.imgAvatar.setImageResource(R.mipmap.ic_launcher_round);
            }
        } else {
            Log.e(TAG, "Danh sách sinh viên rỗng hoặc vị trí không hợp lệ: " + position);
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView imgAvatar;
        TextView tvHoTen;
        TextView tvMssv;
    }

    public void updateData(ArrayList<SinhVien> newSinhVienList) {

        super.clear();

        if (newSinhVienList != null) {
            super.addAll(newSinhVienList);
        }
        Log.d(TAG, "updateData: Dữ liệu đã được cập nhật, số lượng mới: " + (newSinhVienList != null ? newSinhVienList.size() : 0));
    }
}
