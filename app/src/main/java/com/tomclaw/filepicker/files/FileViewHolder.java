package com.tomclaw.filepicker.files;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tomclaw.filepicker.R;

/**
 * Created by solkin on 18.06.2017.
 */
class FileViewHolder extends RecyclerView.ViewHolder {

    private Context context;
    private View view;
    private ImageView thumb;
    private TextView title;
    private TextView info;

    public FileViewHolder(final View view) {
        super(view);
        this.view = view;
        this.context = view.getContext();
        thumb = (ImageView) view.findViewById(R.id.docs_item_thumb);
        title = (TextView) view.findViewById(R.id.docs_item_title);
        info = (TextView) view.findViewById(R.id.docs_item_info);
    }

    public void bind(FileItem fileItem, OnItemClickListener listener) {
        thumb.setImageDrawable(context.getResources().getDrawable(fileItem.getIcon()));
        title.setText(fileItem.getTitle());
        info.setText(fileItem.getInfo());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}
