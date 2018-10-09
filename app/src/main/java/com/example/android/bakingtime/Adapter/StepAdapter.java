package com.example.android.bakingtime.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.bakingtime.Data.Step;
import com.example.android.bakingtime.R;
import com.example.android.bakingtime.Ui.StepListFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder> {

    private ArrayList<Step> mSteps;
    private Context mContext;
    private StepListFragment.OnStepClickListener mCallbacks;
    private int mLastPosition;
    private boolean mTwoPane;

    public StepAdapter(ArrayList<Step> steps, StepListFragment.OnStepClickListener callbacks,
                       int position, boolean twoPane) {
        mSteps = steps;
        mCallbacks = callbacks;
        mLastPosition = position;
        mTwoPane = twoPane;
    }

    @Override
    public StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutId = R.layout.step_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(layoutId, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StepViewHolder holder, int position) {
        boolean markSelected = false;
        if (mTwoPane) {
            markSelected = position == mLastPosition;
        }
        holder.bind(mSteps.get(position).getId(),
                mSteps.get(position).getShortDescription(),
                markSelected);
    }

    @Override
    public int getItemCount() {
        return mSteps.size();
    }

    public class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.step_id_view)
        TextView posTextView;
        @BindView(R.id.step_description_view)
        TextView desTextView;
        @BindView(R.id.step_container)
        LinearLayout mContainer;

        public StepViewHolder(View stepView) {
            super(stepView);
            stepView.setOnClickListener(this);
            ButterKnife.bind(this, stepView);
        }

        public void bind(String id, String des, boolean markSelected) {
            posTextView.setText(id);
            desTextView.setText(des);
            mContainer.setSelected(markSelected);
        }

        @Override
        public void onClick(View view) {
            notifyItemChanged(mLastPosition);
            mLastPosition = getAdapterPosition();
            notifyItemChanged(mLastPosition);
            mCallbacks.onStepSelected(view, getLayoutPosition());
        }


    }
}
