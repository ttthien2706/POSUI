package com.smb_business_chain_management.func_settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smb_business_chain_management.R;
import com.smb_business_chain_management.func_settings.fragments.UserDetailFragment;
import com.smb_business_chain_management.models.User;

import java.util.List;

class UserItemRecyclerViewAdapter extends RecyclerView.Adapter<UserItemRecyclerViewAdapter.ViewHolder> {

    private final UserListActivity mParentActivity;
    private final List<User> mUserList;
    private final boolean mTwoPane;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            User mCurrentUser = (User) view.getTag();
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putInt(UserDetailFragment.ARG_ITEM_ID, mCurrentUser.getId());
                arguments.putParcelable(UserDetailFragment.ARG_CURRENT_USER, mCurrentUser);
                UserDetailFragment fragment = new UserDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.invalidateOptionsMenu();
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.user_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, UserDetailActivity.class);
                intent.putExtra(UserDetailFragment.ARG_ITEM_ID, mCurrentUser.getId());

                context.startActivity(intent);
            }
        }
    };

    UserItemRecyclerViewAdapter(UserListActivity parent,
                                List<User> myData,
                                boolean twoPane) {
        mUserList = myData;
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mIdView.setText(String.valueOf(mUserList.get(position).getId()));
        holder.mContentView.setText(mUserList.get(position).getName());

        holder.itemView.setTag(mUserList.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mIdView;
        final TextView mContentView;

        ViewHolder(View view) {
            super(view);
            mIdView = view.findViewById(R.id.id_text);
            mContentView = view.findViewById(R.id.content);
        }
    }
}
