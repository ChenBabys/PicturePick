package com.home.picturepick.selectImage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.home.picturepick.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImageActivity extends AppCompatActivity {
    private TextView titleBack, titleFinish, imageMore, imagePreview;
    private RecyclerView rlvImages;
    private boolean mHasCamera = true;
    // 返回选择图片列表的EXTRA_KEY
    public static final String EXTRA_RESULT = "EXTRA_RESULT";
    public static final int MAX_SIZE = 9;
    private static final int PERMISSION_REQUEST_CODE = 88;
    private static final int TAKE_PHOTO = 99;
    private Uri mImageUri;
    private File takePhotoImageFile;
    //被选中图片的集合
    private List<Image> mSelectedImages = new ArrayList<>();
    private List<Image> mImages = new ArrayList<>();
    private List<ImageFolder> mImageFolders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        initView();
        initImages();
    }

    private void initView() {
        titleBack = this.findViewById(R.id.title_back);
        titleFinish = this.findViewById(R.id.title_finish);
        imageMore = this.findViewById(R.id.image_more);
        imagePreview = this.findViewById(R.id.image_preview);
        rlvImages = this.findViewById(R.id.rlv_images);
    }

    private void initImages() {
        LoaderManager.getInstance(this).initLoader(0, null, mLoaderCallbacks);
    }


    private void addImagesToAdapter(ArrayList<Image> images) {


    }

    private void addImageFoldersToAdapter() {


    }


    private final LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.MINI_THUMB_MAGIC,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};


        //创建一个CursorLoader，去异步加载相册的图片
        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
            return new CursorLoader(ImageActivity.this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                    null, null, IMAGE_PROJECTION[2] + "DESC");
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                ArrayList<Image> images = new ArrayList<>();
                //是否显示照相图片
                if (mHasCamera) {
                    images.add(new Image());
                }
                //添加一个全部图片的一个图片文件夹
                ImageFolder defaultFolder = new ImageFolder();
                defaultFolder.setName("全部照片");
                defaultFolder.setPath("");
                mImageFolders.add(defaultFolder);

                int count = data.getCount();
                if (count > 0) {
                    //如果有数据。则先把游标挪到开始
                    data.moveToFirst();
                    do {
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        int id = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
                        String thumbPath = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
                        String bucket = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[5]));

                        Image image = new Image();
                        image.setPath(path);
                        image.setName(name);
                        image.setId(id);
                        image.setDate(dateTime);
                        image.setThumbPath(thumbPath);
                        image.setFolderName(bucket);
                        images.add(image);

                        //如果是被选中的图片
                        if (mSelectedImages.size() > 0) {
                            for (Image i : mSelectedImages) {
                                if (i.getPath().equals(image.getPath())) {
                                    image.setSelect(true);
                                }
                            }
                        }

                        //设置图片分类的文件夹
                        File imageFile = new File(path);
                        File folderFile = imageFile.getParentFile();//获取父文件
                        ImageFolder folder = new ImageFolder();
                        folder.setName(folderFile.getName());
                        folder.setPath(folderFile.getAbsolutePath());
                        //ImageFolder复写了equal方法，equal方法比较的是文件夹的路径
                        if (!mImageFolders.contains(folder)) {
                            folder.getImages().add(image);
                            //默认相册封面
                            folder.setAlbumPath(image.getPath());
                            mImageFolders.add(folder);
                        } else {
                            ImageFolder imageFolder = mImageFolders.get(mImageFolders.indexOf(folder));
                            imageFolder.getImages().add(image);
                        }
                    } while (data.moveToNext());
                }

                addImagesToAdapter(images);
                //全部照片
                defaultFolder.getImages().addAll(images);
                if (mHasCamera) {
                    defaultFolder.setAlbumPath(images.size() > 1 ? images.get(1).getPath() : null);
                } else {
                    defaultFolder.setAlbumPath(images.size() > 0 ? images.get(0).getPath() : null);
                }
                if (mSelectedImages.size() > 0) {
                    List<Image> rs = new ArrayList<>();
                    for (Image img : mSelectedImages) {
                        File f = new File(img.getPath());
                        if (!f.exists()) {
                            rs.add(img);
                        }
                    }
                    mSelectedImages.removeAll(rs);
                }
            }
            ///TODO    mImageFolderView.setImageFolders(mImageFolders);
            //mImageFolderView.setImageFolders(mImageFolders);
            addImageFoldersToAdapter();
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        }
    };


    //    @Override
    public void onCameraClick() {
        int isPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (isPermission == PackageManager.PERMISSION_GRANTED) {
            takePhoto();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
                Toast.makeText(ImageActivity.this, "需要您的相机权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void takePhoto() {
        //intent打开相机
        Intent takePhtotIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //这句作用是如果没有相机则该应用不会闪退，要是不加这句则当系统没有相机应用的时候该应用会闪退
        if (takePhtotIntent.resolveActivity(getPackageManager()) != null) {
            takePhotoImageFile = createImageFile();
            if (takePhotoImageFile != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    ///7.0以上要通过FileProvider将File转化为Uri
                    mImageUri = FileProvider.getUriForFile(this, this.getPackageName() + ".fileprovider", takePhotoImageFile);
                } else {
                    //7.0以下则直接使用Uri的fromFile方法将File转化为Uri
                    mImageUri = Uri.fromFile(takePhotoImageFile);
                }
                //将用于输出的文件Uri传递给相机
                takePhtotIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                startActivityForResult(takePhtotIntent, TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == TAKE_PHOTO) {
            //缩略图信息是储存在返回的intent中的Bundle中的，对应Bundle中的键为data，因此从Intent中取出 Bundle再根据data取出来Bitmap即可
            // Bundle extras = data.getExtras();
            // Bitmap bitmap = (Bitmap) extras.get("data");
//            BitmapFactory.decodeFile(this.getContentResolver().)
//            galleryAddPictures(mImageUri);
//            getSupportLoaderManager().restartLoader(0, null, mLoaderCallbacks);
            galleryAddPictures();//保存到相册
            //下面的代码不过是打印一下看看。。。
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(mImageUri));
                Log.i("take photo", bitmap + "");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());//中国时区
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = null;
        try {
            imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }

    /**
     * 将拍好的照片添加到相册
     */
    private void galleryAddPictures() {
        //把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(this.getContentResolver(), takePhotoImageFile.getAbsolutePath(), takePhotoImageFile.getName(), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //通知图库更新
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(takePhotoImageFile));
        sendBroadcast(mediaScanIntent);
    }

}