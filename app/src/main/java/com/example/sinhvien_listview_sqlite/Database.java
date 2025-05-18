package com.example.sinhvien_listview_sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "QuanLySinhVien.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_SINHVIEN = "SinhVien";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_HOTEN = "hoTen";
    private static final String COLUMN_MSSV = "mssv";
    private static final String COLUMN_AVATAR_PATH = "avatarPath";
    private static final String COLUMN_CHUYENNGANH = "chuyenNganh";
    private static final String COLUMN_NGAYSINH = "ngaySinh";
    private static final String COLUMN_SODIENTHOAI = "soDienthoai";

    private Context context; // Lưu context để sử dụng trong copyDrawableToInternalStorage

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context; // Lưu context
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SINHVIEN_TABLE = "CREATE TABLE " + TABLE_SINHVIEN + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_HOTEN + " TEXT NOT NULL,"
                + COLUMN_MSSV + " TEXT NOT NULL UNIQUE,"
                + COLUMN_AVATAR_PATH + " TEXT,"
                + COLUMN_CHUYENNGANH + " TEXT,"
                + COLUMN_NGAYSINH + " TEXT,"
                + COLUMN_SODIENTHOAI + " TEXT"
                + ")";
        db.execSQL(CREATE_SINHVIEN_TABLE);
        Log.i(TAG, "Bảng SinhVien đã được tạo.");

        addInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Nâng cấp database từ phiên bản " + oldVersion + " lên " + newVersion + ". Dữ liệu cũ sẽ bị xóa.");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SINHVIEN);
        onCreate(db);
    }

    private String copyDrawableToInternalStorage(int drawableResourceId, String outputFileName) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableResourceId);
        if (bitmap == null) {
            Log.e(TAG, "Không thể decode drawable resource ID: " + drawableResourceId);
            return null;
        }

        File internalStorageDir = context.getFilesDir();
        File imageFile = new File(internalStorageDir, outputFileName + ".png");

        try (OutputStream outputStream = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            Log.i(TAG, "Đã sao chép drawable " + outputFileName + " vào: " + imageFile.getAbsolutePath());
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "Lỗi khi sao chép drawable " + outputFileName + " vào bộ nhớ trong: ", e);
            return null;
        }
    }

    // Thêm dữ liệu mẫu vào database
    private void addInitialData(SQLiteDatabase db) {
        String nobitaAvatarPath = copyDrawableToInternalStorage(R.drawable.nobita, "Nobita");
        String doraemonAvatarPath = copyDrawableToInternalStorage(R.drawable.doraemon, "doraemon");
        String shizukaAvatarPath = copyDrawableToInternalStorage(R.drawable.shizuka, "shizuka");
        String suneoAvatarPath = copyDrawableToInternalStorage(R.drawable.suneo, "suneo");
        String jaianAvatarPath = copyDrawableToInternalStorage(R.drawable.jaian, "jaian");
        String dekisugiAvatarPath = copyDrawableToInternalStorage(R.drawable.dekisugi, "dekisugi");




        addInitialSinhVien(db, new SinhVien(0, "Nobita", "SV001", nobitaAvatarPath, "Điện tử", "05/10/2003", "0992283321"));
        addInitialSinhVien(db, new SinhVien(0, "Doraemon", "SV002", doraemonAvatarPath, "Viễn Thông", "12/06/2004", "0237756344"));
        addInitialSinhVien(db, new SinhVien(0, "Shizuka", "SV003", shizukaAvatarPath, "Máy tính - Hệ thống nhúng", "15/01/2003", "066832253"));
        addInitialSinhVien(db, new SinhVien(0, "Suneo", "SV004", suneoAvatarPath, "Viễn Thông", "02/07/2005", "053788323"));
        addInitialSinhVien(db, new SinhVien(0, "Jaian", "SV005", jaianAvatarPath, "Viễn Thông", "22/08/2003", "037428744"));
        addInitialSinhVien(db, new SinhVien(0, "Dekisugi", "SV006", dekisugiAvatarPath, "Điện tử", "12/10/2004", "0776592411"));

    }

    private void addInitialSinhVien(SQLiteDatabase db, SinhVien sinhVien) {
        if (db == null || !db.isOpen()) {
            Log.e(TAG, "Database chưa mở hoặc null khi cố gắng thêm sinh viên ban đầu.");
            return;
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_HOTEN, sinhVien.getHoTen());
        values.put(COLUMN_MSSV, sinhVien.getMssv());
        values.put(COLUMN_AVATAR_PATH, sinhVien.getAvatarPath());
        values.put(COLUMN_CHUYENNGANH, sinhVien.getChuyenNganh());
        values.put(COLUMN_NGAYSINH, sinhVien.getNgaySinh());
        values.put(COLUMN_SODIENTHOAI, sinhVien.getSodienthoai());

        try {
            long id = db.insertOrThrow(TABLE_SINHVIEN, null, values);
            Log.i(TAG, "Đã thêm sinh viên ban đầu: " + sinhVien.getHoTen() + " với ID: " + id + ", AvatarPath: " + sinhVien.getAvatarPath());
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi thêm sinh viên ban đầu: " + sinhVien.getHoTen(), e);
        }
    }


    public long addSinhVien(SinhVien sinhVien) {
        SQLiteDatabase db = null;
        long id = -1;
        try {
            db = this.getWritableDatabase();
            db.beginTransaction();

            ContentValues values = new ContentValues();
            values.put(COLUMN_HOTEN, sinhVien.getHoTen());
            values.put(COLUMN_MSSV, sinhVien.getMssv());
            values.put(COLUMN_AVATAR_PATH, sinhVien.getAvatarPath());
            values.put(COLUMN_CHUYENNGANH, sinhVien.getChuyenNganh());
            values.put(COLUMN_NGAYSINH, sinhVien.getNgaySinh());
            values.put(COLUMN_SODIENTHOAI, sinhVien.getSodienthoai());

            id = db.insertOrThrow(TABLE_SINHVIEN, null, values);
            db.setTransactionSuccessful();
            Log.i(TAG, "Đã thêm sinh viên: " + sinhVien.getHoTen() + " với ID: " + id);

        } catch (Exception e) {
            Log.e(TAG, "Lỗi Exception khi thêm sinh viên: " + sinhVien.getHoTen(), e);
            id = -1;
        } finally {
            if (db != null) {
                if (db.inTransaction()) {
                    db.endTransaction();
                }
                db.close();
            }
        }
        return id;
    }

    public ArrayList<SinhVien> getAllSinhViens() {
        ArrayList<SinhVien> sinhViens = new ArrayList<>();
        String SELECT_QUERY = "SELECT * FROM " + TABLE_SINHVIEN + " ORDER BY " + COLUMN_HOTEN + " ASC";
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery(SELECT_QUERY, null);
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                    String hoTen = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HOTEN));
                    String mssv = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MSSV));
                    String avatarPath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVATAR_PATH));
                    String chuyenNganh = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHUYENNGANH));
                    String ngaySinh = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NGAYSINH));
                    String soDienThoai = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SODIENTHOAI));
                    sinhViens.add(new SinhVien(id, hoTen, mssv, avatarPath, chuyenNganh, ngaySinh, soDienThoai));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi truy vấn tất cả sinh viên: ", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }
        Log.d(TAG, "Lấy được " + sinhViens.size() + " sinh viên.");
        return sinhViens;
    }

    public SinhVien getStudentById(int studentId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        SinhVien sinhVien = null;
        try {
            db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_SINHVIEN + " WHERE " + COLUMN_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});

            if (cursor.moveToFirst()) {
                String hoTen = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HOTEN));
                String mssv = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MSSV));
                String avatarPath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVATAR_PATH));
                String chuyenNganh = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHUYENNGANH));
                String ngaySinh = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NGAYSINH));
                String soDienThoai = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SODIENTHOAI));
                sinhVien = new SinhVien(studentId, hoTen, mssv, avatarPath, chuyenNganh, ngaySinh, soDienThoai);
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi lấy sinh viên theo ID: " + studentId, e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }
        return sinhVien;
    }

    public int updateSinhVien(SinhVien sinhVien) {
        SQLiteDatabase db = null;
        int rowsAffected = 0;
        try {
            db = this.getWritableDatabase();
            db.beginTransaction();

            ContentValues values = new ContentValues();
            values.put(COLUMN_HOTEN, sinhVien.getHoTen());
            values.put(COLUMN_MSSV, sinhVien.getMssv());
            values.put(COLUMN_AVATAR_PATH, sinhVien.getAvatarPath());
            values.put(COLUMN_CHUYENNGANH, sinhVien.getChuyenNganh());
            values.put(COLUMN_NGAYSINH, sinhVien.getNgaySinh());
            values.put(COLUMN_SODIENTHOAI, sinhVien.getSodienthoai());

            rowsAffected = db.update(TABLE_SINHVIEN, values, COLUMN_ID + " = ?",
                    new String[]{String.valueOf(sinhVien.getId())});
            db.setTransactionSuccessful();
            Log.i(TAG, "Cập nhật sinh viên ID " + sinhVien.getId() + ". Số dòng bị ảnh hưởng: " + rowsAffected);
        } catch (Exception e) {
            Log.e(TAG, "Lỗi Exception khi cập nhật sinh viên ID " + sinhVien.getId(), e);
            rowsAffected = 0;
        } finally {
            if (db != null) {
                if (db.inTransaction()) {
                    db.endTransaction();
                }
                db.close();
            }
        }
        return rowsAffected;
    }

    public void deleteSinhVien(int id) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.beginTransaction();
            int rowsDeleted = db.delete(TABLE_SINHVIEN, COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
            Log.i(TAG, "Xóa sinh viên ID " + id + ". Số dòng bị xóa: " + rowsDeleted);
        } catch (Exception e) {
            Log.e(TAG, "Lỗi Exception khi xóa sinh viên ID " + id, e);
        } finally {
            if (db != null) {
                if (db.inTransaction()) {
                    db.endTransaction();
                }
                db.close();
            }
        }
    }
}
