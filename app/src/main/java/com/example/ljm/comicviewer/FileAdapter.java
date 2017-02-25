package com.example.ljm.comicviewer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * FileAdapter
 *
 * Created by ljm on 2017/02/25.
 */
public class FileAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    private final List<File> files;
    private OnItemClickListener onItemClickListener;
    private RecyclerView recyclerView;

    public FileAdapter(List<File> files) {
        this.files = files;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_file, parent, false);
        view.setOnClickListener(this);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        File file = files.get(position);
        FileViewHolder viewHolder = (FileViewHolder) holder;
        viewHolder.imgFile.setImageResource(file.isDirectory() ? R.drawable.img_folder : R.drawable.img_file);
        viewHolder.tvFile.setText(file.getName());
    }

    @Override
    public int getItemCount() {
        return files != null ? files.size() : 0;
    }

    @Override
    public void onClick(View view) {
        if (recyclerView == null) {
            return;
        }

        if (onItemClickListener != null) {
            int position = recyclerView.getChildAdapterPosition(view);
            File file = files.get(position);
            onItemClickListener.onItemClick(this, position, file);
        }
    }

    private class FileViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgFile;
        private final TextView tvFile;

        private FileViewHolder(View view) {
            super(view);

            imgFile = ((ImageView) view.findViewById(R.id.img_file));
            tvFile = ((TextView) view.findViewById(R.id.tv_file));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(RecyclerView.Adapter adapter, int position, Object item);
    }
}
