package com.nuktarkhos.aap_survey.data;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.nuktarkhos.aap_survey.R;
import com.nuktarkhos.aap_survey.utility.IssueType;

public class IssueViewHolder extends RecyclerView.ViewHolder {

    private TextView timeTextView;
    private TextView latTextView;
    private TextView lngTextView;
    private TextView issueTextView;
    private ImageView issueImage;

    private Issue issue;
    private Context context;

    private IssueViewHolder(View view) {
        super(view);

        timeTextView = (TextView) view.findViewById(R.id.issueTimeValue);
        latTextView = (TextView) view.findViewById(R.id.issueLatValue);
        lngTextView = (TextView) view.findViewById(R.id.issueLngValue);
        issueTextView = (TextView) view.findViewById(R.id.issueTypeValue);
        issueImage = (ImageView) view.findViewById(R.id.imageView);

        context = view.getContext();

        view.setOnClickListener(view1 -> {
            Bundle bundle = new Bundle();
            bundle.putInt("issueId", issue.id);

            NavHostFragment.findNavController(FragmentManager.findFragment(view))
                    .navigate(R.id.action_updateIssue, bundle);
        });

        //view.setOn
    }

    public void bind(Issue newIssue) {
        issue = newIssue;

        timeTextView.setText(issue.time);
        latTextView.setText(issue.latitude);
        lngTextView.setText(issue.longitude);
        issueTextView.setText(IssueType.toResourceId(issue.type));

        if (issue.thumbnail != null){
            Drawable issueDrawable = new BitmapDrawable(context.getResources(),
                    BitmapFactory.decodeByteArray(issue.thumbnail, 0, issue.thumbnail.length));

            issueImage.setImageDrawable(issueDrawable);
        }
    }

    static IssueViewHolder create(ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);
        return new IssueViewHolder(view);
    }
}
