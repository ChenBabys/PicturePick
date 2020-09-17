package com.home.picturepick.selectImage;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.home.picturepick.R;

import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Image> photoList;
    private static int TYPE_CAMERA = 0;//照相机图片
    private static int TYPE_IMAGE = 1;//其他图片
    private int mMaxAlbum = 9;//最大选择图片的数量

    public ImagesAdapter(List<Image> photoList) {
        this.photoList = photoList;
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
     *
     * @param parent
     * @return
     */
    private View getItemView(ViewGroup parent) {
        ImageView img = new ImageView(parent.getContext());
        RecyclerView.LayoutParams layoutParams =
                new RecyclerView.LayoutParams(SizeUtils.dp2px(100), SizeUtils.dp2px(100));
        int pad = SizeUtils.dp2px(10);
        layoutParams.setMargins(pad, pad / 2, pad, pad / 2);
        img.setLayoutParams(layoutParams);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return img;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        View itemView = null;
        if (holder instanceof CameraViewHolder) {
            CameraViewHolder holderCamera = (CameraViewHolder) holder;
            if (position >= mMaxAlbum) {
                holderCamera.add.setVisibility(View.GONE);
            } else {
                //add.setText(position + "/" + mMaxAlbum);//用个文本器记录总数和当前坐标
                holderCamera.onBindCamera();
                holderCamera.add.setVisibility(View.VISIBLE);
                itemView = holderCamera.itemView;
            }
        } else if (holder instanceof ImageViewHolder) {
            ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
            if (position >= 0 && position < photoList.size()) {
                Image image = photoList.get(position);
                imageViewHolder.onBind(image);
                itemView = imageViewHolder.itemView;
            }
        }

        if (onItemClickListener != null && null != itemView) {
            itemView.setOnClickListener(view -> {
                //重点注意！！！，这里用的是getLayoutPosition，和平常只有一个ViewHolder的区别在此，平常用getAdapterPosition
                int pos = holder.getLayoutPosition();
                onItemClickListener.onClick(holder.itemView, pos);

            });
            //长按
            itemView.setOnLongClickListener(view -> {
                //备注同上
                int pos = holder.getLayoutPosition();
                onItemClickListener.onLongClick(holder.itemView, pos);
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
        private ImageView add;

        public CameraViewHolder(@NonNull View itemView) {
            super(itemView);
            add = (ImageView) itemView;
        }

        public void onBindCamera() {
            add.setImageResource(R.drawable.ic_camera);
        }
    }

    /**
     * 其他图片
     */
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            img = (ImageView) itemView;
        }

        public void onBind(Image image) {
            //img.setImageURI(imgUri);
            //使用Glide加载图片就不要用view.setTag(Viewholder)了，去掉上面的tag
            //另外用glide也不会造成资源使用过多的问题。不会像上面setImageURI那样卡,也不会让那些太大的照片造成闪退
            Glide.with(img).load(image.getPath()).into(img);
        }
    }

    /**
     * 点击事件的回调接口
     */
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
