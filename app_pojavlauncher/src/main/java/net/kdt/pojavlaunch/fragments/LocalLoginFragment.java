package net.kdt.pojavlaunch.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.extra.ExtraConstants;
import net.kdt.pojavlaunch.extra.ExtraCore;
import net.kdt.pojavlaunch.value.LauncherAccount;
import net.kdt.pojavlaunch.value.AccountManager;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalLoginFragment extends Fragment {
    public static final String TAG = "LOCAL_LOGIN_FRAGMENT";

    private final Pattern mUsernameValidationPattern;
    private EditText mUsernameEditText;

    public LocalLoginFragment(){
        super(R.layout.fragment_local_login);
        mUsernameValidationPattern = Pattern.compile("^[a-zA-Z0-9_]*$");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mUsernameEditText = view.findViewById(R.id.login_edit_email);
        
        // Standard Login Button Listener
        view.findViewById(R.id.login_button).setOnClickListener(v -> {
            if(!checkEditText()) {
                Context context = v.getContext();
                Tools.dialog(context, context.getString(R.string.local_login_bad_username_title), context.getString(R.string.local_login_bad_username_text));
                return;
            }

            ExtraCore.setValue(ExtraConstants.MOJANG_LOGIN_TODO, new String[]{
                    mUsernameEditText.getText().toString(), "" });

            Tools.swapFragment(requireActivity(), MainMenuFragment.class, MainMenuFragment.TAG, null);
        });

        // Custom Offline Mode (Power Outage) Button Listener
        View btnOffline = view.findViewById(R.id.btn_offline_login);
        if (btnOffline != null) {
            btnOffline.setOnClickListener(v -> {
                String inputName = mUsernameEditText.getText().toString().trim();
                String offlineUsername = inputName.isEmpty() ? "OfflinePlayer" : inputName;
                
                // Deterministic local UUID based on offline username
                UUID offlineUuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + offlineUsername).getBytes(StandardCharsets.UTF_8));

                LauncherAccount offlineAccount = new LauncherAccount();
                offlineAccount.username = offlineUsername;
                offlineAccount.sub = offlineUuid.toString();
                offlineAccount.accessToken = "0";
                offlineAccount.isMicrosoft = false;

                // Register account in local manager
                AccountManager.getInstance().addAccount(offlineAccount);
                AccountManager.getInstance().setCurrentAccount(offlineAccount.username);

                // Pass value through ExtraCore and transition to Main Menu
                ExtraCore.setValue(ExtraConstants.MOJANG_LOGIN_TODO, new String[]{ offlineUsername, "" });
                Tools.swapFragment(requireActivity(), MainMenuFragment.class, MainMenuFragment.TAG, null);
            });
        }
    }

    /** @return Whether the mail (and password) text are eligible to make an auth request  */
    private boolean checkEditText(){
        String text = mUsernameEditText.getText().toString();

        Matcher matcher = mUsernameValidationPattern.matcher(text);
        return !(text.isEmpty()
                || text.length() < 3
                || text.length() > 16
                || !matcher.find()
                || new File(Tools.DIR_ACCOUNT_NEW + "/" + text + ".json").exists()
        );
    }
}                Context context = v.getContext();
                Tools.dialog(context, context.getString(R.string.local_login_bad_username_title), context.getString(R.string.local_login_bad_username_text));
                return;
            }

            ExtraCore.setValue(ExtraConstants.MOJANG_LOGIN_TODO, new String[]{
                    mUsernameEditText.getText().toString(), "" });

            Tools.swapFragment(requireActivity(), MainMenuFragment.class, MainMenuFragment.TAG, null);
        });
    }


    /** @return Whether the mail (and password) text are eligible to make an auth request  */
    private boolean checkEditText(){

        String text = mUsernameEditText.getText().toString();

        Matcher matcher = mUsernameValidationPattern.matcher(text);
        return !(text.isEmpty()
                || text.length() < 3
                || text.length() > 16
                || !matcher.find()
                || new File(Tools.DIR_ACCOUNT_NEW + "/" + text + ".json").exists()
        );
    }
}
