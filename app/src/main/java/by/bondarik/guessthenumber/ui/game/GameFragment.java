package by.bondarik.guessthenumber.ui.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import by.bondarik.guessthenumber.NumberGenerator;
import by.bondarik.guessthenumber.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameFragment extends Fragment {

    private TextView showHint;
    private TextView showAttemptsLeft;
    private TextView showPlayerName;
    private TextView showGuess;

    private EditText editNum;

    private Button btnGuess;
    private Button btnRestart;

    private int hiddenNumber;

    private int currentDifficulty;

    private int maxAttempts = 5;
    private int minNumber = 10;
    private int maxNumber = 99;

    private int currentAttempts;
    private String currentHint;

    private String currentPlayerName = "Super player";

    public GameFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GameFragment.
     */
    public static GameFragment newInstance() {
        return new GameFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void sendEndGameResult(boolean isGuessed) {
        StringBuilder endGameResultBuilder = new StringBuilder();

        endGameResultBuilder.append("Date: ");
        endGameResultBuilder.append(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));

        if (isGuessed) {
            endGameResultBuilder.append("\nAttempt number: ");
            endGameResultBuilder.append(maxAttempts - currentAttempts + 1);
        }

        endGameResultBuilder.append("\nNumber: ");
        endGameResultBuilder.append(hiddenNumber);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setPackage("com.miui.notes");

        sendIntent.putExtra(Intent.EXTRA_TEXT, endGameResultBuilder.toString());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);

        sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, endGameResultBuilder.toString());
        sendIntent.setType("text/plain");

        shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentDifficulty = savedInstanceState != null ? savedInstanceState.getInt("currentDifficulty") : 2;
        currentHint = savedInstanceState != null ? savedInstanceState.getString("currentHint") : getString(R.string.show_hint_label) + " " + currentDifficulty;

        showHint = view.findViewById(R.id.show_hint);
        showHint.setText(currentHint);

        currentAttempts = savedInstanceState != null ? savedInstanceState.getInt("currentAttempts") : maxAttempts;

        showAttemptsLeft = view.findViewById(R.id.show_attempts_left);
        showAttemptsLeft.append(" ");
        showAttemptsLeft.append(Integer.toString(currentAttempts));

        showGuess = view.findViewById(R.id.show_msg);
        registerForContextMenu(showGuess);

        editNum = view.findViewById(R.id.edit_num);
        btnGuess = view.findViewById(R.id.btn_guess);
        btnRestart = view.findViewById(R.id.btn_restart);

        hiddenNumber = savedInstanceState != null ? savedInstanceState.getInt("hiddenNumber") : NumberGenerator.generate(10, 99);

        editNum.setOnClickListener(v -> editNum.selectAll());

        btnGuess.setOnClickListener(v -> {
            editNum.selectAll();
            try {
                int inputNumber = Integer.parseInt(editNum.getText().toString());

                if (inputNumber == hiddenNumber) {
                    btnGuess.setText(R.string.btn_guessed_label);
                    btnGuess.setEnabled(false);

                    Toast tCongratulation = Toast.makeText(requireActivity(), R.string.t_congratulation_label, Toast.LENGTH_LONG);
                    tCongratulation.show();

                    sendEndGameResult(true);
                }
                else {
                    currentHint = inputNumber > hiddenNumber ? getString(R.string.show_hint_less_label) : getString(R.string.show_hint_greater_label);
                    showHint.setText(currentHint);

                    currentAttempts--;

                    showAttemptsLeft.setText(R.string.show_attempts_left_label);
                    showAttemptsLeft.append(" ");
                    showAttemptsLeft.append(Integer.toString(currentAttempts));

                    if (currentAttempts == 0) {
                        btnGuess.setText(R.string.btn_not_guessed_label);
                        btnGuess.setEnabled(false);

                        sendEndGameResult(false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnRestart.setOnClickListener(v -> {
            currentHint = getString(R.string.show_hint_label) + " " + currentDifficulty;
            showHint.setText(currentHint);

            showAttemptsLeft.setText((R.string.show_attempts_left_label));
            showAttemptsLeft.append(" ");
            showAttemptsLeft.append(Integer.toString(maxAttempts));
            currentAttempts = maxAttempts;

            btnGuess.setText(R.string.btn_guess_label);

            hiddenNumber = NumberGenerator.generate(minNumber, maxNumber);

            btnGuess.setEnabled(true);
        });
    }
}