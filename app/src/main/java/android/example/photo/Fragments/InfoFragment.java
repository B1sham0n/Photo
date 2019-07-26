package android.example.photo.Fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.example.photo.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class InfoFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.info, null);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView tvInfoMail = view.findViewById(R.id.infoMail);
        LinearLayout ll = view.findViewById(R.id.layoutWithMail);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getContext().CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied mail", tvInfoMail.getText());
                Objects.requireNonNull(clipboard).setPrimaryClip(clip);
                Toast.makeText(getActivity(), "Copied mail", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
