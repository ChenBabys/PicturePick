package com.home.picturepick.selectImage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.home.picturepick.BuildConfig;
import com.home.picturepick.R;
import com.home.picturepick.fragment.PreViewDialogFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * author : CYS
 * e-mail : 1584935420@qq.com
 * date : 2020/9/18 16:42
 * desc :
 * version : 1.0
 */
public class ImageSelectActivity extends AppCompatActivity implements View.OnClickListener, ImageFolderView.ImageFolderViewListener {
    private TextView titleBack, titleFinish, imageFolder, imagePreview;
    private ConstraintLayout cslTop;
    private RecyclerView rlvImages;
    private ImageFolderView ifvFolderView;
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
    private ImagesSelectAdapter imagesAdapter;
    private ImageFolderAdapter mImageFolderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);
        initView();
        initImages();
    }

    private void initView() {
        cslTop = this.findViewById(R.id.csl_top);
        titleBack = this.findViewById(R.id.title_back);
        titleFinish = this.findViewById(R.id.title_finish);
        imageFolder = this.findViewById(R.id.image_folder);
        imagePreview = this.findViewById(R.id.image_preview);
        rlvImages = this.findViewById(R.id.rlv_images);
        ifvFolderView = this.findViewById(R.id.ifv_images_folder);
        ifvFolderView.setListener(this);
        //为四个按钮添加点击事件
        titleBack.setOnClickListener(this);
        titleFinish.setOnClickListener(this);
        imageFolder.setOnClickListener(this);
        imagePreview.setOnClickListener(this);
    }

    private void initImages() {
        //设置状态栏的颜色(这个是第三方的工具，布兰柯基github找AndroidUtilCode)
        BarUtils.setStatusBarColor(ImageSelectActivity.this, ContextCompat.getColor(this, R.color.colorBlack));
        //设置了状态栏为黑色之后，状态栏的高度没了。所以要加上这一句保持高度。
        BarUtils.addMarginTopEqualStatusBarHeight(cslTop);
        //默认先加载的选中图片，这是为了同步选中的代码，注释掉吧
        //setupSelectedImages();
        //异步加载图片
        LoaderManager.getInstance(this).initLoader(0, null, mLoaderCallbacks);
    }

    /**
     * //默认先加载的选中图片
     * 这是为了同步选中的代码，其实大可不必。和使用的地方同步，这种选择而觉得多余了，界面内同步就行了，使用的地方不用同步
     * 注释掉吧
     */
//    private void setupSelectedImages() {
//        ArrayList<Image> selectImages = getIntent().getParcelableArrayListExtra("selected_images");
//        mSelectedImages.addAll(selectImages);
//
//        if (mSelectedImages.size() > 0 && mSelectedImages.size() <= MAX_SIZE) {
//            imagePreview.setClickable(true);
//            imagePreview.setText(String.format("预览(%d/9) ", mSelectedImages.size()));
//            imagePreview.setTextColor(ContextCompat.getColor(ImageActivity.this, R.color.colorAccent));
//        }
//    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.title_finish:
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(EXTRA_RESULT, (ArrayList<? extends Parcelable>) mSelectedImages);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.image_folder:
                if (ifvFolderView.isShowing()) {
                    ifvFolderView.hide();
                } else {
                    ifvFolderView.show();
                }
                break;
            case R.id.image_preview:
                //预览图片
                List<String> imageStrs = new ArrayList<>();
                PreViewDialogFragment fragment = new PreViewDialogFragment();
                for (Image image : mSelectedImages) {
                    String imageStr = image.getPath();
                    imageStrs.add(imageStr);
                }
                fragment.setImagePathList(imageStrs);
                fragment.show(getSupportFragmentManager(), "PreViewDialogFragment");
                break;
            default:
                break;

        }

    }


    /**
     * 设置图片的适配器
     *
     * @param images
     */
    private void addImagesToAdapter(ArrayList<Image> images) {
        mImages.clear();//这种方式也许可以让应用没那么多缓存数据吧
        mImages.addAll(images);
        if (imagesAdapter == null) {
            imagesAdapter = new ImagesSelectAdapter(mImages, mSelectedImages);
            imagesAdapter.setOnItemClickListener(new ImagesSelectAdapter.OnItemClickListener() {
                @Override
                public void onClick(View view, List<Image> photoList, int position) {
                    //ToastUtils.showShort(position);
                    //如果是第一个就打开相机
                    if (position == 0)
                        onCameraClick();
                    //预览图片
                    //  setAllPreView(photoList, position);


//                        //奇怪。父布局也可以让子布局的drawable选中的么
//                        view.setSelected(true);
                }

                @Override
                public void onLongClick(View view, Image image, int position) {

                }

                @Override
                public void onSelectImageCount(int count) {
                    if (count == 0) {
                        //预览按钮
                        imagePreview.setClickable(false);
                        imagePreview.setText("预览");
                        imagePreview.setTextColor(ContextCompat.getColor(ImageSelectActivity.this, R.color.colorAccentGray));
                    } else {
                        imagePreview.setClickable(true);
                        imagePreview.setText(String.format(getString(R.string.String_preview), count));
                        imagePreview.setTextColor(ContextCompat.getColor(ImageSelectActivity.this, R.color.colorAccent));
                    }
                }

                @Override
                public void onSelectImageList(List<Image> images) {
                    //选中的图片list会通过设置适配器传递给适配器，而后又通过回调回来给予这边的选中图片list.
                    //这是为了同步这个活动和适配器选中的图片，无论怎么选择减少或增加都会同步，
                    // 可以避免使用notifyDataSetChanged时候选中的被刷新掉等等作用。
                    //还有一个重要作用就是把适配器同步更新的选中的图片回传回来，然后准备给点完成的按钮传递给使用的地方。
                    mSelectedImages = images;
                }
            });
            rlvImages.setHasFixedSize(true);
            rlvImages.setNestedScrollingEnabled(false);
            rlvImages.setLayoutManager(new GridLayoutManager(ImageSelectActivity.this, 4));
            rlvImages.setAdapter(imagesAdapter);
        } else {
            imagesAdapter.updateAll(mImages);
        }

    }

    @Override
    public void onSelectFolder(ImageFolderView imageFolderView, ImageFolder imageFolders) {
        //如果选择了文件夹则把图片数据重复赋予图片适配器
        addImagesToAdapter(imageFolders.getImages());
        rlvImages.scrollToPosition(0);//回到顶部
        this.imageFolder.setText(imageFolders.getName());//把名字给点击的textview
    }

    @Override
    public void onDismiss() {

    }

    @Override
    public void onShow() {

    }


    /**
     * 在相册里打开预览文件太多了是个耗时操作。用异步吧
     */
    private void setAllPreView(List<Image> photoList, int position) {

    }

    /**
     * 添加文件夹的适配器
     */
    private void addImageFoldersToAdapter() {
        if (mImageFolderAdapter == null) {
            mImageFolderAdapter = new ImageFolderAdapter(mImageFolders);
            ifvFolderView.setAdapter(mImageFolderAdapter);
        } else {
            mImageFolderAdapter.updateAll(mImageFolders);
        }
    }

    /**
     * loader居然在api28之后不再推荐使用了（没过时），叼了，推荐使用ViewModels和LiveData结合了
     */
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
            return new CursorLoader(ImageSelectActivity.this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                    null, null, IMAGE_PROJECTION[2] + " DESC");//加上+ " DESC"，可以按照添加的时间从新到旧排序
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                //存储从本地加载完毕的全部图片
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
                        if (folderFile != null) {
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
                        } else {
                            ToastUtils.showShort("父文件夹空空如也");
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
            //设置文件夹,这里设置了，适配器不用再设置了
            ifvFolderView.setImageFolders(mImageFolders);
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
                Toast.makeText(ImageSelectActivity.this, "需要您的相机权限", Toast.LENGTH_SHORT).show();
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
                    mImageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", takePhotoImageFile);
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
            galleryAddPictures();//保存到相册
            //缩略图信息是储存在返回的intent中的Bundle中的，对应Bundle中的键为data，因此从Intent中取出 Bundle再根据data取出来Bitmap即可
            // Bundle extras = data.getExtras();
            // Bitmap bitmap = (Bitmap) extras.get("data");
//            BitmapFactory.decodeFile(this.getContentResolver().)
//            galleryAddPictures(mImageUri);
//            getSupportLoaderManager().restartLoader(0, null, mLoaderCallbacks);

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
            LogUtils.d(takePhotoImageFile.getAbsolutePath() + "\n" + takePhotoImageFile.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtils.d(e.getMessage());
        }
        //通知图库更新
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(takePhotoImageFile));
        sendBroadcast(mediaScanIntent);
    }

    /**
     * 增加了一些在活动摧毁时清空list的代码，不知道是否能做到应用久了缓存少点。
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSelectedImages != null && !mSelectedImages.isEmpty()) {
            mSelectedImages.clear();
        }

        if (mImages != null && !mImages.isEmpty()) {
            mImages.clear();
        }

        if (mImageFolders != null && !mImageFolders.isEmpty()) {
            mImageFolders.clear();
        }
    }
}