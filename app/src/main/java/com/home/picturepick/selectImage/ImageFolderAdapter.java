package com.home.picturepick.selectImage;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.home.picturepick.R;

import java.util.List;

/**
 * author : CYS
 * e-mail : 1584935420@qq.com
 * date : 2020/9/18 17:30
 * desc :
 * version : 1.0
 */
public class ImageFolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ImageFolder> FolderList;
    private boolean mHasCamera;

    public ImageFolderAdapter(List<ImageFolder> folderList, boolean mHasCamera) {
        this.FolderList = folderList;
        this.mHasCamera = mHasCamera;
    }

    public ImageFolderAdapter(List<ImageFolder> FolderList) {
        this.FolderList = FolderList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //其他图片
        View view = getItemView(parent);
        //view.setTag(holder);用Glide不要加tag
        return new FolderViewHolder(view);
    }

    /**
     * 添加一个相同的图片容器
     * 全部代码实现布局
     * 两种方法：
     * 1.像这种inflateb布局的不要把parent放进去会报错的。要写null。（两个参数）
     * 2.或者如果你想填进去那就再多加一个attachToRoot=false的参数在后面。（三个参数）
     * 具体理由可以查https://www.jianshu.com/p/dacb6dea891a
     * 但是上面的第一种方法虽然解决了奔溃的问题，但是却啥子项也不显示，直接只有容器无内容。
     * 所以只有第二种方法可用。
     * * @param parent
     *
     * @return
     */
    private View getItemView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.image_folder_item, parent, false);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        FolderViewHolder folderViewHolder = (FolderViewHolder) holder;
        if (position >= 0 && position < FolderList.size()) {
            ImageFolder imageFolder = FolderList.get(position);
            folderViewHolder.onBind(imageFolder, mHasCamera);
            View itemView = folderViewHolder.itemView;//一项的点击事件
            if (onItemClickListener != null) {
                itemView.setOnClickListener(view -> {
                    //重点注意！！！可变的itemview，这里用的是getLayoutPosition，和平常只有一个ViewHolder的区别在此，平常用getAdapterPosition
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, imageFolder, pos);

                });
                //长按
                itemView.setOnLongClickListener(view -> {
                    //备注同上
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onLongClick(holder.itemView, imageFolder, pos);
                    return true;
                });
            }
        }

    }


    @Override
    public int getItemCount() {
        return FolderList.size();
    }


    public void updateAll(List<ImageFolder> FolderList) {
        this.FolderList = FolderList;
        notifyDataSetChanged();
    }


    public void clear() {
        if (FolderList != null && !FolderList.isEmpty()) {
            FolderList.clear();
            notifyDataSetChanged();
        }
    }

    public void remove(int position) {
        if (FolderList != null && !FolderList.isEmpty()) {
            FolderList.remove(position);
            notifyItemRemoved(position);//用它可以实现一个动画，就是删除后部位过去的时候有个平移的动画效果，很nice;
        }
    }


    /**
     * 图片
     */
    public static class FolderViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;
        public TextView title, count;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.picture);
            title = itemView.findViewById(R.id.pic_title);
            count = itemView.findViewById(R.id.pic_count);
        }

        @SuppressLint("SetTextI18n")
        public void onBind(ImageFolder imageFolder, boolean mHasCamera) {
            Glide.with(img).load(imageFolder.getAlbumPath()).into(img);
            title.setText(imageFolder.getName());
            if (mHasCamera)
                //减一是因为有相机时候填入了一张空白图片在下标为0的位置。所以真实的图片会少一张
                count.setText(imageFolder.getImages().size() - 1 + "张");
            else
                count.setText(imageFolder.getImages().size() + "张");
        }
    }

    /**
     * 点击事件的回调接口
     */
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, ImageFolder imageFolder, int position);

        void onLongClick(View view, ImageFolder imageFolder, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
