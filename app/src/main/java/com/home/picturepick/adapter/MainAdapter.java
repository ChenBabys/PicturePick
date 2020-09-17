package com.home.picturepick.adapter;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.home.picturepick.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Uri> photoList;
    private static int TYPE_ADD = 0;//加号图片
    private static int TYPE_COMMON = 1;//其他图片
    private int mMaxAlbum = 9;//最大选择图片的数量

    public MainAdapter(List<Uri> photoList) {
        this.photoList = photoList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ADD) {
            View view = getItemView(parent);
            AddViewHolder holderAdd = new AddViewHolder(view);
            //view.setTag(holderAdd);用Glide不要加tag
            return holderAdd;
        } else {
            //其他图片
            View view = getItemView(parent);
            MainViewHolder holder = new MainViewHolder(view);
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
        if (holder instanceof AddViewHolder) {
            AddViewHolder holderAdd = (AddViewHolder) holder;
            if (position >= mMaxAlbum) {
                holderAdd.add.setVisibility(View.GONE);
            } else {
                //add.setText(position + "/" + mMaxAlbum);//用个文本器记录总数和当前坐标
                holderAdd.onBindAdd();
                holderAdd.add.setVisibility(View.VISIBLE);
                itemView = holderAdd.itemView;
            }
        } else if (holder instanceof MainViewHolder) {
            MainViewHolder mainViewHolder = (MainViewHolder) holder;
            if (position >= 0 && position < photoList.size()) {
                Uri uri = photoList.get(position);
                mainViewHolder.onBind(uri);
                itemView = mainViewHolder.itemView;
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
     * 如果position是最后一个就给加号
     * getItemCount中的长度一定要加一，否则还是达不到意想效果。
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return position == photoList.size() ? TYPE_ADD : TYPE_COMMON;//就这一个判断就可以了。
    }

    /**
     * 因为photoList中是不包含加号的，
     * 加号完全是由适配器这边更具type添加的，所以适配器的所有项的总数一定photoList的长度加一。
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return photoList.size() + 1;//加一代表最后一个添加图片按钮
    }


    public void updateAll(List<Uri> photoList) {
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
    public static class AddViewHolder extends RecyclerView.ViewHolder {
        private ImageView add;

        public AddViewHolder(@NonNull View itemView) {
            super(itemView);
            add = (ImageView) itemView;
        }

        public void onBindAdd() {
            add.setImageResource(R.drawable.ic_add);
        }
    }

    /**
     * 其他图片
     */
    public static class MainViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);
            img = (ImageView) itemView;
        }

        public void onBind(Uri imgUri) {
            //img.setImageURI(imgUri);
            //使用Glide加载图片就不要用view.setTag(Viewholder)了，去掉上面的tag
            //另外用glide也不会造成资源使用过多的问题。不会像上面setImageURI那样卡,也不会让那些太大的照片造成闪退
            Glide.with(img).load(imgUri).into(img);
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
