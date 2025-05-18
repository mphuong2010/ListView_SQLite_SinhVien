package com.example.sinhvien_listview_sqlite;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ThemSinhVien extends AppCompatActivity {

    private static final String TAG = "AddEditSinhVienActivity";

    private ImageView imgAvatar;
    private TextView tvChooseAvatar;
    private EditText edtHoTen, edtMssv, edtChuyenNganh, edtNgaySinh, edtsodienthoai;
    private Button btnLuu;

    private String currentAvatarPath = null;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    private Database databaseHelper;
    private boolean isEditMode = false;
    private int studentIdToEdit = -1;
    private SinhVien currentEditingStudent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_sinh_vien);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        imgAvatar = findViewById(R.id.imgAvatar);
        tvChooseAvatar = findViewById(R.id.tvChooseAvatar);
        edtHoTen = findViewById(R.id.edtHoTen);
        edtMssv = findViewById(R.id.edtMssv);
        edtChuyenNganh = findViewById(R.id.edtChuyenNganh);
        edtNgaySinh = findViewById(R.id.edtNgaySinh);
        edtsodienthoai = findViewById(R.id.edtSoDienThoai);
        btnLuu = findViewById(R.id.btnLuu);

        databaseHelper = new Database(this);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            try {

                                currentAvatarPath = saveImageToInternalStorage(imageUri);

                                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                                imgAvatar.setImageBitmap(selectedImage);
                                if (imageStream != null) imageStream.close();

                                imgAvatar.setVisibility(View.VISIBLE);
                                tvChooseAvatar.setText("Đổi ảnh");
                                Log.i(TAG, "Ảnh đã được chọn và hiển thị. Đường dẫn mới: " + currentAvatarPath);

                            } catch (IOException e) {
                                Log.e(TAG, "Lỗi khi xử lý hoặc lưu ảnh mới: " + e.getMessage(), e);
                                Toast.makeText(this, "Không thể tải hoặc lưu ảnh mới", Toast.LENGTH_SHORT).show();
                                if (isEditMode && currentEditingStudent != null) {
                                    currentAvatarPath = currentEditingStudent.getAvatarPath();
                                } else {
                                    currentAvatarPath = null;
                                }
                            }
                        }
                    }
                }
        );

        View.OnClickListener chooseImageListener = v -> openImageChooser();
        tvChooseAvatar.setOnClickListener(chooseImageListener);
        imgAvatar.setOnClickListener(chooseImageListener);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("STUDENT_ID_TO_EDIT")) {
            studentIdToEdit = intent.getIntExtra("STUDENT_ID_TO_EDIT", -1);
            if (studentIdToEdit != -1) {
                isEditMode = true;
                if (actionBar != null) actionBar.setTitle("Sửa Thông Tin Sinh Viên");
                btnLuu.setText("Cập nhật");
                loadStudentDataForEditing(studentIdToEdit);
            } else {
                isEditMode = false;
                if (actionBar != null) actionBar.setTitle("Thêm Sinh Viên Mới");
                btnLuu.setText("Lưu");
            }
        } else {
            isEditMode = false;
            if (actionBar != null) actionBar.setTitle("Thêm Sinh Viên Mới");
            btnLuu.setText("Lưu");
        }

        btnLuu.setOnClickListener(v -> saveOrUpdateStudent());
    }

    private void loadStudentDataForEditing(int studentId) {
        Log.d(TAG, "Đang tải dữ liệu để sửa cho sinh viên ID: " + studentId);
        currentEditingStudent = databaseHelper.getStudentById(studentId);
        if (currentEditingStudent != null) {
            edtHoTen.setText(currentEditingStudent.getHoTen());
            edtMssv.setText(currentEditingStudent.getMssv());
            edtChuyenNganh.setText(currentEditingStudent.getChuyenNganh());
            edtNgaySinh.setText(currentEditingStudent.getNgaySinh());
            edtsodienthoai.setText(currentEditingStudent.getSodienthoai());

            currentAvatarPath = currentEditingStudent.getAvatarPath();

            if (currentAvatarPath != null && !currentAvatarPath.isEmpty()) {
                File imgFile = new File(currentAvatarPath);
                if (imgFile.exists() && imgFile.isFile()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imgAvatar.setImageBitmap(myBitmap);
                    imgAvatar.setVisibility(View.VISIBLE);
                    tvChooseAvatar.setText("Đổi ảnh");
                } else {
                    Log.w(TAG, "File ảnh không tồn tại để sửa: " + currentAvatarPath);
                    imgAvatar.setImageResource(R.mipmap.ic_launcher_round);
                    imgAvatar.setVisibility(View.VISIBLE);
                    tvChooseAvatar.setText("Chọn ảnh");
                }
            } else {
                imgAvatar.setImageResource(R.mipmap.ic_launcher_round);
                imgAvatar.setVisibility(View.VISIBLE);
                tvChooseAvatar.setText("Chọn ảnh");
            }
            Log.i(TAG, "Đã tải dữ liệu để sửa cho sinh viên: " + currentEditingStudent.getHoTen());
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin sinh viên để sửa.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Không tìm thấy sinh viên với ID: " + studentId + " để sửa.");
            finish(); // Đóng activity nếu không tìm thấy sinh viên
        }
    }

    private void saveOrUpdateStudent() {
        String hoTen = edtHoTen.getText().toString().trim();
        String mssv = edtMssv.getText().toString().trim();
        String chuyenNganh = edtChuyenNganh.getText().toString().trim();
        String ngaySinh = edtNgaySinh.getText().toString().trim();
        String sodienthoai = edtsodienthoai.getText().toString().trim();

        if (TextUtils.isEmpty(hoTen) || TextUtils.isEmpty(mssv)) {
            Toast.makeText(this, "Vui lòng nhập Họ tên và MSSV.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEditMode && currentEditingStudent != null) {
            currentEditingStudent.setHoTen(hoTen);
            currentEditingStudent.setMssv(mssv);
            currentEditingStudent.setChuyenNganh(chuyenNganh);
            currentEditingStudent.setNgaySinh(ngaySinh);
            currentEditingStudent.setSodienthoai(sodienthoai);
            currentEditingStudent.setAvatarPath(currentAvatarPath);

            int rowsAffected = databaseHelper.updateSinhVien(currentEditingStudent);
            if (rowsAffected > 0) {
                Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Lỗi khi cập nhật thông tin. MSSV có thể bị trùng hoặc không có thay đổi.", Toast.LENGTH_LONG).show();
            }
        } else {
            SinhVien sv = new SinhVien(0, hoTen, mssv, currentAvatarPath, chuyenNganh, ngaySinh, sodienthoai);
            long newRowId = databaseHelper.addSinhVien(sv);
            if (newRowId != -1) {
                Toast.makeText(this, "Đã thêm sinh viên thành công!", Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Lỗi khi thêm sinh viên. MSSV có thể đã tồn tại.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private String saveImageToInternalStorage(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        if (inputStream == null) {
            throw new IOException("Không thể mở InputStream cho Uri: " + uri);
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Calendar.getInstance().getTime());
        String fileName = "AVATAR_" + timeStamp + ".jpg";
        File internalStorageDir = getFilesDir();
        File imageFile = new File(internalStorageDir, fileName);

        try (OutputStream outputStream = new FileOutputStream(imageFile)) {
            byte[] buffer = new byte[1024 * 4];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            inputStream.close();
        }
        Log.i(TAG, "Ảnh đã được lưu vào bộ nhớ trong tại: " + imageFile.getAbsolutePath());
        return imageFile.getAbsolutePath();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
