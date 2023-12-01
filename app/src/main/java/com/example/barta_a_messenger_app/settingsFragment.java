package com.example.barta_a_messenger_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class settingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Find the logout button in the layout
        androidx.constraintlayout.utils.widget.MotionButton btnLogout = view.findViewById(R.id.btnLogout);

        // Set an OnClickListener to the logout button
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the logout function when the button is clicked
                logout();
            }
        });

        return view;
    }

    // Function to handle logout
    private void logout() {
        // Sign out the user from Firebase
        FirebaseAuth.getInstance().signOut();

        // TODO: Navigate to the login screen or perform other relevant actions
        // For example, you might start a new LoginActivity:
         Intent intent = new Intent(getActivity(), LoginPageActivity.class);
         startActivity(intent);
        // getActivity().finish(); // Optional: finish the current activity
    }
}
