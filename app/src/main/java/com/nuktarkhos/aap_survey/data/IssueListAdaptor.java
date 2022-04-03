package com.nuktarkhos.aap_survey.data;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class IssueListAdaptor extends RecyclerView.Adapter<IssueViewHolder> {

    private ArrayList<Issue> issues;

    public IssueListAdaptor() {
        super();
        this.issues = new ArrayList<>();
    }

    @Override
    public IssueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return IssueViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(IssueViewHolder viewHolder, final int position) {
        Issue current = issues.get(position);
        viewHolder.bind(current);
    }

    @Override
    public int getItemCount(){
        if (issues == null) return 0;
        else return issues.size();
    }

    public void updateIssues(final List<Issue> issueArrayList) {
        this.issues.clear();
        this.issues = (ArrayList<Issue>) issueArrayList;
        notifyDataSetChanged();
    }
}
