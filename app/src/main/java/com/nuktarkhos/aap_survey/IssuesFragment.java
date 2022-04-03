package com.nuktarkhos.aap_survey;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nuktarkhos.aap_survey.data.Issue;
import com.nuktarkhos.aap_survey.data.IssueListAdaptor;
import com.nuktarkhos.aap_survey.data.IssueViewModel;
import com.nuktarkhos.aap_survey.data.AppDatabase;
import com.nuktarkhos.aap_survey.databinding.FragmentIssuesBinding;
import com.nuktarkhos.aap_survey.utility.Locative;

import java.io.File;
import java.util.List;

public class IssuesFragment extends Fragment {

    private FragmentIssuesBinding binding;
    private AppDatabase surveyDb;
    private MainActivity parent;

    private IssueListAdaptor issueListAdaptor;
    private IssueViewModel issueViewModel;

    private LocationManager locationManager;
    private int locStatus = -1;
    private Location currentLoc = null;
    private boolean locationEnabled = true;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentIssuesBinding.inflate(inflater, container, false);

        parent = (MainActivity)getActivity();

        issueListAdaptor = new IssueListAdaptor();
        binding.issueView.setAdapter(issueListAdaptor);
        binding.issueView.setLayoutManager(new LinearLayoutManager(getContext()));

        Observer<List<Issue>> issueListUpdateObserver = issues -> issueListAdaptor.updateIssues(issues);

        if (parent != null) {
            surveyDb = parent.getSurveyDb();

            if (parent.permitLocation())
                initializeLocationRequest();

            issueViewModel = new ViewModelProvider(this).get(IssueViewModel.class);

            issueViewModel.getAllIssues().observe(getViewLifecycleOwner(),
                    issueListUpdateObserver);
        }

        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                Issue issue = issueViewModel.getIssueForPosition(viewHolder.getAdapterPosition());

                if (issue != null){
                    if (issue.pictureURI != null){
                        File image = new File(issue.pictureURI);

                        if (image.exists()) image.delete();
                    }

                    surveyDb.issueDao().deleteIssue(issue);
                }
            }
        });

        touchHelper.attachToRecyclerView(binding.issueView);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (parent != null){
            binding.surveyPathname.setText(parent.getPathname());
            binding.surveyDate.setText(parent.getSurveyDate());
        }

        updateLocation();

        binding.addIssueButton.setOnClickListener(view1 -> {

            if (parent.getLocation() != null){
                NavHostFragment.findNavController(IssuesFragment.this)
                        .navigate(R.id.action_addIssue);
            } else {
                Toast.makeText(getContext(), R.string.waiting_for_loc, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initializeLocationRequest(){
        locationManager = (LocationManager) parent.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                currentLoc = location;
                updateLocation();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                locStatus = status;
            }

            public void onProviderEnabled(String provider) {
                locationEnabled = true;
            }

            public void onProviderDisabled(String provider) {
                locationEnabled = false;
            }
        };

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    3000, 3, locationListener);
        } catch (SecurityException e){
            Log.e("AAP.Issue", e.toString());
        }
    }

    private void updateLocation() {

        if (binding == null) return;

        if (!locationEnabled) {
            binding.latitudeText.setText(R.string.disabled_string);
            binding.longitudeText.setText(R.string.disabled_string);
            binding.gridText.setText(R.string.disabled_string);
        } else if (currentLoc == null) {
            binding.latitudeText.setText(R.string.hyphen);
            binding.longitudeText.setText(R.string.hyphen);
            binding.gridText.setText(R.string.hyphen);
        } else {
            String locs[] = Locative.gpsToString(currentLoc);
            binding.latitudeText.setText(locs[0]);
            binding.longitudeText.setText(locs[1]);

            String gridLoc = Locative.gpsToNatGridString(currentLoc);

            if (gridLoc != null)
                binding.gridText.setText(gridLoc);
            else
                binding.gridText.setText(R.string.na_string);

            parent.setLocation(currentLoc);
        }
    }


}