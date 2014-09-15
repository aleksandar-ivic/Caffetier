package com.example.caffetier.app;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AboutUsFragment extends Fragment {
	
	public AboutUsFragment(){}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_about_us, container, false);

        TextView titleTextView = (TextView) rootView.findViewById(R.id.aboutUsTitle);
        TextView infoTextView = (TextView) rootView.findViewById(R.id.info);
        infoTextView.setText(R.string.aboutUs);
        Button contactUsButton = (Button) rootView.findViewById(R.id.contactUs);
        contactUsButton.setText("Kontaktirajte nas!");
        contactUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "aleksandar.ivic.coa@gmail.com", null));
                startActivity(Intent.createChooser(intent, "Choose an Email client :"));
            }
        });
         
        return rootView;
    }
}
