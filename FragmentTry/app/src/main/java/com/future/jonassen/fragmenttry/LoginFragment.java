package com.future.jonassen.fragmenttry;


import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.os.Bundle;
import android.app.DialogFragment;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends DialogFragment implements View.OnClickListener {

    private Bundle bundle;
    private Button mbutton;
    private EditText etUsername;
    private EditText etPassword;
    private DataInteraction mDataInteraction;

    public void setmDataInteraction(DataInteraction v) {
        mDataInteraction = v;

    }

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();


        if (getDialog() == null)
            return;
        getDialog().getWindow().setWindowAnimations(R.style.dialog_animation_fade);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setTitle("Login Surface");
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mbutton = (Button) view.findViewById(R.id.btnclick1);
        mbutton.setOnClickListener(this);
        etPassword = (EditText) view.findViewById(R.id.etpassword1);
        etUsername = (EditText) view.findViewById(R.id.etusername1);
        return view;
    }

    public interface DataInteraction {
        void GetUserInfo(Bundle data);
    }


    @Override
    public void onClick(View v) {
        if (mDataInteraction != null) {
            String Username = etUsername.getText().toString();
            String Password = etPassword.getText().toString();
            if (bundle == null)
                bundle = new Bundle();
            bundle.putString("user",Username);
            bundle.putString("pass", Password);
            mDataInteraction.GetUserInfo(bundle);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(this);
            ft.commit();
        }
    }
}
