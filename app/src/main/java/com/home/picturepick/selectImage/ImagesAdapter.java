package com.home.picturepick.selectImage;


import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.home.picturepick.R;

import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Image> photoList;
    private List<Image> mSelectedImages;
    private static int TYPE_CAMERA = 0;//照相机图片
    private static int TYPE_IMAGE = 1;//其他图片
    private int mMaxAlbum = 9;//最大选择图片的数量
    private Image image;//单张图片资源

    public ImagesAdapter(List<Image> photoList, List<Image> mSelectedImages) {
        this.photoList = photoList;
        this.mSelectedImages = mSelectedImages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CAMERA) {
            View view = getItemView(parent);
            CameraViewHolder holderCamera = new CameraViewHolder(view);
            //view.setTag(holderAdd);用Glide不要加tag
            return holderCamera;
        } else {
            //其他图片
            View view = getItemView(parent);
            ImageViewHolder holder = new ImageViewHolder(view);
            //view.setTag(holder);用Glide不要加tag
            return holder;
        }
    }

    /**
     * 添加一个相同的图片容器
     * 全部代码实现布局
     *
     * @param parent
     * @return
     */
    private View getItemView(ViewGroup parent) {
        //父布局
        ConstraintLayout constrain = new ConstraintLayout(parent.getContext());
        RecyclerView.LayoutParams layoutParams =
                new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(100));
        int pad = SizeUtils.dp2px(1);
        layoutParams.setMargins(pad, pad, pad, pad);
        constrain.setLayoutParams(layoutParams);
        //图片
        ImageView img = new ImageView(parent.getContext());
        ConstraintLayout.LayoutParams imgParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT);
        img.setLayoutParams(imgParams);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //选中的图片显示的select
        ImageView selectImg = new ImageView(parent.getContext());
        ConstraintLayout.LayoutParams selectParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        selectParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        selectParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        selectParams.setMargins(0, 0, SizeUtils.dp2px(5), SizeUtils.dp2px(5));
        selectImg.setLayoutParams(selectParams);
        selectImg.setBackground(ContextCompat.getDrawable(parent.getContext(), R.drawable.image_select__bg));
        //覆盖图片的mask,选中时候变色
        View mask = new View(parent.getContext());
        ConstraintLayout.LayoutParams maskParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT);
        mask.setLayoutParams(maskParams);
        mask.setBackgroundColor(ContextCompat.getColor(parent.getContext(), R.color.lightblack));
        mask.setVisibility(View.GONE);//默认隐藏
        //添加到父布局
        constrain.addView(img);
        constrain.addView(selectImg);
        constrain.addView(mask);
        return constrain;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        View itemView = null;
        if (holder instanceof CameraViewHolder) {
            CameraViewHolder holderCamera = (CameraViewHolder) holder;
            // holderCamera.mCamera.setText(position + "/" + mMaxAlbum);//用个文本器记录总数和当前坐标
            holderCamera.onBindCamera();
            itemView = holderCamera.itemView;
            //如果是相机按钮,则隐藏选中与否的按钮
            ((ConstraintLayout) holder.itemView).getChildAt(1).setVisibility(View.GONE);
        } else if (holder instanceof ImageViewHolder) {
            ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
            if (position >= 0 && position < photoList.size()) {
                image = photoList.get(position);
                imageViewHolder.onBind(image, mSelectedImages);
                itemView = imageViewHolder.itemView;
            }
        }

        if (onItemClickListener != null && null != itemView) {
            itemView.setOnClickListener(view -> {
                //重点注意！！！可变的itemview，这里用的是getLayoutPosition，和平常只有一个ViewHolder的区别在此，平常用getAdapterPosition
                int pos = holder.getLayoutPosition();
                onItemClickListener.onClick(holder.itemView, image, pos);

            });
            //长按
            itemView.setOnLongClickListener(view -> {
                //备注同上
                int pos = holder.getLayoutPosition();
                onItemClickListener.onLongClick(holder.itemView, image, pos);
                return true;
            });
        }
    }

    /**
     * 如果position是第一个就给相机图片
     * getItemCount中的长度一定要加一，否则还是达不到意想效果。
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_CAMERA : TYPE_IMAGE;//就这一个判断就可以了。
    }

    /**
     * 因为photoList中是不包含相机图片，
     * 加号完全是由适配器这边更具type添加的，所以适配器的所有项的总数一定photoList的长度加一。
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return photoList.size() + 1;//加一代表最后一个添加图片按钮
    }


    public void updateAll(List<Image> photoList) {
        this.photoList = photoList;
        notifyDataSetChanged();
    }


    public void clear() {
        if (photoList != null && !photoList.isEmpty()) {
            photoList.clear();
            notifyDataSetChanged();
        }
    }

    public void remove(int position) {
        if (photoList != null && !photoList.isEmpty()) {
            photoList.remove(position);
            //因为notifyItemRemoved这种只刷新删除哪项的有删除动画，所有加了个if语句，美滋滋
            if (position >= mMaxAlbum - 1) {
                notifyDataSetChanged();//用它和下面这种方式不会出现下标溢出，其他的就会溢出
            } else {
                notifyItemRemoved(position);//用它可以实现一个动画，就是删除后部位过去的时候有个平移的动画效果，很nice;
            }
        }
    }


    /**
     * 加号图标
     */
    public static class CameraViewHolder extends RecyclerView.ViewHolder {
        public ImageView mCamera;

        public CameraViewHolder(@NonNull View itemView) {
            super(itemView);
            mCamera = (ImageView) ((ConstraintLayout) itemView).getChildAt(0);
        }

        public void onBindCamera() {
            mCamera.setImageResource(R.drawable.ic_camera);
            mCamera.setBackgroundColor(ContextCompat.getColor(mCamera.getContext(), R.color.colorBlack));
        }
    }

    /**
     * 其他图片
     */
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView img, selectImg;
        public View mask;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            // img = (ImageView) itemView;
            //按照他添加到ConstraintLayout中的顺序来，顺序拿出。切记和上面的add顺序一致
            img = (ImageView) ((ConstraintLayout) itemView).getChildAt(0);
            selectImg = (ImageView) ((ConstraintLayout) itemView).getChildAt(1);
            mask = (View) ((ConstraintLayout) itemView).getChildAt(2);
        }

        public void onBind(Image image, List<Image> mSelectedImages) {
            //img.setImageURI(imgUri);
            //使用Glide加载图片就不要用view.setTag(Viewholder)了，去掉上面的tag
            //另外用glide也不会造成资源使用过多的问题。不会像上面setImageURI那样卡,也不会让那些太大的照片造成闪退
            Glide.with(img).load(image.getPath()).into(img);
            selectImg.setOnClickListener(v -> {
                if (image.isSelect()) {
                    image.setSelect(false);
                    mSelectedImages.remove(image);
                    selectImg.setSelected(false);
                    //是这玩意,一个背景色
                    mask.setVisibility(image.isSelect() ? View.VISIBLE : View.GONE);
                } else {
                    if (mSelectedImages.size() < ImageActivity.MAX_SIZE) {
                        image.setSelect(true);
                        mSelectedImages.add(image);
                        selectImg.setSelected(true);
                        //是这玩意,一个背景色
                        mask.setVisibility(image.isSelect() ? View.VISIBLE : View.GONE);
                    } else {
                        ToastUtils.showShort("最多只能选9张图片");
                    }
                }
                //回调选中的图片和选中的图片数目
                if (onItemClickListener != null) {
                    onItemClickListener.onSelectImageCount(mSelectedImages.size());
                    onItemClickListener.onSelectImageList(mSelectedImages);
                }
            });
            //用来记录没有触发点击时候，默认之前的选中与否的显示的，
            // 比如你选择图片时候，突然换到了微信去看消息，回来的时候回onResume自动刷新界面(数据是保存的,但界面是刷新的)，
            // 这时候这两句代码就能帮你选中之前选的图片。而不是又得重新选择
            selectImg.setSelected(image.isSelect());
            mask.setVisibility(image.isSelect() ? View.VISIBLE : View.GONE);


        }
    }

    /**
     * 点击事件的回调接口(使用了静态的接口)
     */
    private static OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onClick(View view, Image image, int position);

        void onLongClick(View view, Image image, int position);

        void onSelectImageCount(int count);

        void onSelectImageList(List<Image> images);
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        ImagesAdapter.onItemClickListener = onItemClickListener;
    }

}
