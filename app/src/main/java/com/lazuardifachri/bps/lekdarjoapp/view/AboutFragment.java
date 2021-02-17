package com.lazuardifachri.bps.lekdarjoapp.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lazuardifachri.bps.lekdarjoapp.databinding.FragmentAboutBinding;

public class AboutFragment extends Fragment {

    FragmentAboutBinding binding;

    public AboutFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentAboutBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.facebookIcon.setOnClickListener(v -> {
            Intent fb;
            try {
                getContext().getPackageManager().getPackageInfo("com.facebook.katana", 0);
                fb = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/100028641581615"));
            } catch (Exception e) {
                fb = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/statistik.sidoarjo"));
            }
            startActivity(fb);
        });

        binding.instagramIcon.setOnClickListener(v -> {
            Intent insta;
            try {
                getContext().getPackageManager().getPackageInfo("com.instagram.android", 0);
                insta = new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/bps.sidoarjo"));
            } catch (Exception e) {
                insta = new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/bps.sidoarjo"));
            }
            startActivity(insta);
        });

        binding.twiterIcon.setOnClickListener(v -> {
            Intent tweet;
            try {
                getContext().getPackageManager().getPackageInfo("com.twitter.android", 0);
                tweet = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=bpskabsidoarjo"));
            } catch (Exception e) {
                tweet = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/bpskabsidoarjo"));
            }
            startActivity(tweet);
        });

        binding.youtubeIcon.setOnClickListener(v -> {
            Intent tube = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/channel/UCmyDuyP2NjLxnTAh9_dcdBA"));
            tube.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(tube);
        });

        binding.phoneIcon.setOnClickListener(v -> {
            Intent call = new Intent(Intent.ACTION_DIAL);
            call.setData(Uri.parse("tel:0318946473"));
            startActivity(call);
        });

        binding.mailIcon.setOnClickListener(v -> {
            Intent mail = new Intent(Intent.ACTION_SENDTO);
            mail.setData(Uri.parse("mailto:bps3515@bps.go.id"));
            startActivity(mail);
        });
    }
}
