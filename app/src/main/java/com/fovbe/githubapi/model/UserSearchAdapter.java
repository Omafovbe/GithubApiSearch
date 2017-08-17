package com.fovbe.githubapi.model;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fovbe.githubapi.R;
import com.fovbe.githubapi.UserDetails;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;



/**
 * Created by OWNER1 on 7/12/2017.
 */

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.ViewHolder> {
    private List<Users> qUsers;
    private Context context;

    public UserSearchAdapter(Context context, List<Users> qUsers){
        this.context = context;
        this.qUsers = qUsers;
    }

    @Override
    public UserSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserSearchAdapter.ViewHolder viewHolder, int i ){

        viewHolder.qUsername.setText(qUsers.get(i).getLogin());
        Picasso.with(context).load(qUsers.get(i).getAvatarUrl())
                .transform(new CropCircleTransformation())
                .placeholder(R.drawable.githubs)
                .into(viewHolder.qImage);

    }

    @Override
    public int getItemCount() {
        return qUsers.size();
    }

    public void clear(){
        qUsers.clear();
        notifyDataSetChanged();
    }

    public void addNew(){
        //qUsers.addA(newUsers);
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView qImage;
        private TextView qUsername;

        public ViewHolder(View view){
            super(view);

            qImage = (ImageView)view.findViewById(R.id.dispImg);
            qUsername = (TextView)view.findViewById(R.id.dispName);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        Intent nextPage = new Intent(context, UserDetails.class);
                        nextPage.putExtra("username", qUsers.get(pos).getLogin());
                        nextPage.putExtra("avatar", qUsers.get(pos).getAvatarUrl());
                        nextPage.putExtra("url", qUsers.get(pos).getHtmlUrl());
                        nextPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(nextPage);
                    }
                }
            });
        }
    }
}
