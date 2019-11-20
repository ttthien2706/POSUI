package com.smb_business_chain_management.func_selling;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.smb_business_chain_management.R;
import com.smb_business_chain_management.models.Category;

import java.util.List;

class CategoryListRecyclerViewAdapter extends RecyclerView.Adapter<CategoryListRecyclerViewAdapter.CategoryListViewHolder> {

    private List<Category> mCategoryList;
    private static RecyclerViewClickListener mClickListener;

    CategoryListRecyclerViewAdapter(List<Category> categoryList, RecyclerViewClickListener itemListener) {
        mCategoryList = categoryList;
        mClickListener = itemListener;
    }

    class CategoryListViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener{
        Button button;
        CategoryListViewHolder(View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.categoryButton);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.categoryRecyclerViewListClickListener(v, getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View v) {
            mClickListener.categoryRecyclerViewListClickListener(v, this.getLayoutPosition());
        }
    }

    @NonNull
    @Override
    public CategoryListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View categoryButton = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_button_item, parent, false);
        return new CategoryListViewHolder(categoryButton);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryListViewHolder holder, int position) {
        holder.button.setText(mCategoryList.get(position).getCategoryName());
    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
    }
}
