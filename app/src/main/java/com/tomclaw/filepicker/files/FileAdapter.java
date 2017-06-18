package com.tomclaw.filepicker.files;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tomclaw.filepicker.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by solkin 18.06.2017.
 */
public class FileAdapter extends RecyclerView.Adapter<FileViewHolder> {

    private Context context;
    private OnItemClickListener clickListener;

    private List<FileItem> list;

    public FileAdapter(Context context) {
        this.context = context;
        list = Collections.emptyList();
    }

    public void setFileItems(List<FileItem> items) {
        list = Collections.unmodifiableList(items);
    }

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.file_item, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FileViewHolder holder, int position) {
        FileItem fileItem = list.get(position);
        holder.bind(fileItem, clickListener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
