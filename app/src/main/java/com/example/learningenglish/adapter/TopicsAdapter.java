package com.example.learningenglish.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningenglish.R;
import com.example.learningenglish.model.Topic;

import java.util.List;

public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.TopicViewHolder> {

    private List<Topic> topics;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public TopicsAdapter(List<Topic> topics, Context context, OnItemClickListener onItemClickListener) {
        this.topics = topics;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_topic, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        Topic topic = topics.get(position);
        holder.txtTopicName.setText(topic.getName());
        holder.txtTopicDescription.setText(topic.getDescription());

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(topic));
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView txtTopicName;
        TextView txtTopicDescription;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTopicName = itemView.findViewById(R.id.txtTopicName);
            txtTopicDescription = itemView.findViewById(R.id.txtTopicDescription);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Topic topic);

    }
}