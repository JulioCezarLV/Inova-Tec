package com.vitalis.saude;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vitalis.saude.adapters.EstabelecimentoAdapter;
import com.vitalis.saude.models.Estabelecimento;
import com.vitalis.saude.models.Usuario;
import com.vitalis.saude.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewHospitais, recyclerViewPostos, recyclerViewFarmacias;
    private EditText searchInput;
    private TextView userEmailDisplay;
    private LinearLayout userInfoArea;
    private Button loginButton, btnAgendar;
    private DatabaseHelper dbHelper;
    private SharedPreferences prefs;

    private List<Estabelecimento> postosList = new ArrayList<>();
    private List<Estabelecimento> farmaciasList = new ArrayList<>();
    private List<Estabelecimento> hospitaisList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        prefs = getSharedPreferences("VitalisPrefs", MODE_PRIVATE);

        initViews();
        setupSearch();
        setupData();
        checkSession();
        setupClickListeners();
    }

    private void initViews() {
        recyclerViewHospitais = findViewById(R.id.recyclerHospitais);
        recyclerViewPostos = findViewById(R.id.recyclerPostos);
        recyclerViewFarmacias = findViewById(R.id.recyclerFarmacias);
        searchInput = findViewById(R.id.searchInput);
        userEmailDisplay = findViewById(R.id.userEmailDisplay);
        userInfoArea = findViewById(R.id.userInfoArea);
        loginButton = findViewById(R.id.loginButton);
        btnAgendar = findViewById(R.id.btnAgendar);

        // Setup RecyclerViews
        recyclerViewHospitais.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPostos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFarmacias.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupData() {
        // Dados dos Hospitais
        hospitaisList.add(new Estabelecimento(
                "Hospital Pró-Vida",
                "Rua Inês Pinzon, 611 - Centro Norte",
                Estabelecimento.TIPO_HOSPITAL
        ));
        hospitaisList.add(new Estabelecimento(
                "UPA 24 Horas",
                "Av. Prefeito Dedi B. Montagner, 425 - Centro",
                Estabelecimento.TIPO_HOSPITAL
        ));

        // Dados dos Postos
        postosList.add(new Estabelecimento("ESF São Cristóvão", "Rua João Paulo II, 123 - São Cristóvão", Estabelecimento.TIPO_POSTO));
        postosList.add(new Estabelecimento("ESF Centro", "Av. Presidente Kennedy, 456 - Centro", Estabelecimento.TIPO_POSTO));
        postosList.add(new Estabelecimento("ESF Jardim América", "Rua das Acácias, 789 - Jardim América", Estabelecimento.TIPO_POSTO));
        postosList.add(new Estabelecimento("ESF Santo Antônio", "Rua Santo Antônio, 321 - Santo Antônio", Estabelecimento.TIPO_POSTO));
        postosList.add(new Estabelecimento("ESF Primavera", "Rua Primavera, 654 - Primavera", Estabelecimento.TIPO_POSTO));

        // Dados das Farmácias
        farmaciasList.add(new Estabelecimento("Farmácia Nissei", "Av. Prefeito Dedi B. Montagner, 1000 - Centro", Estabelecimento.TIPO_FARMACIA));
        farmaciasList.add(new Estabelecimento("Drogaria São João", "Rua Inês Pinzon, 450 - Centro Norte", Estabelecimento.TIPO_FARMACIA));
        farmaciasList.add(new Estabelecimento("Farmácia Popular", "Rua XV de Novembro, 200 - Centro", Estabelecimento.TIPO_FARMACIA));
        farmaciasList.add(new Estabelecimento("Drogaria Brasil", "Av. Presidente Kennedy, 850 - Centro", Estabelecimento.TIPO_FARMACIA));
        farmaciasList.add(new Estabelecimento("Farmácia Vitalis", "Rua Paraná, 300 - Centro", Estabelecimento.TIPO_FARMACIA));

        updateRecyclerViews(hospitaisList, postosList, farmaciasList);
    }

    private void updateRecyclerViews(List<Estabelecimento> hospitais, List<Estabelecimento> postos, List<Estabelecimento> farmacias) {
        recyclerViewHospitais.setAdapter(new EstabelecimentoAdapter(hospitais));
        recyclerViewPostos.setAdapter(new EstabelecimentoAdapter(postos));
        recyclerViewFarmacias.setAdapter(new EstabelecimentoAdapter(farmacias));
    }

    private void setupSearch() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterItems(s.toString());
            }
        });
    }

    private void filterItems(String query) {
        String lowerQuery = query.toLowerCase().trim();

        List<Estabelecimento> filteredPostos = new ArrayList<>();
        List<Estabelecimento> filteredFarmacias = new ArrayList<>();
        List<Estabelecimento> filteredHospitais = new ArrayList<>();

        for (Estabelecimento e : postosList) {
            if (e.getNome().toLowerCase().contains(lowerQuery) || 
                e.getEndereco().toLowerCase().contains(lowerQuery)) {
                filteredPostos.add(e);
            }
        }

        for (Estabelecimento e : farmaciasList) {
            if (e.getNome().toLowerCase().contains(lowerQuery) || 
                e.getEndereco().toLowerCase().contains(lowerQuery)) {
                filteredFarmacias.add(e);
            }
        }

        for (Estabelecimento e : hospitaisList) {
            if (e.getNome().toLowerCase().contains(lowerQuery) || 
                e.getEndereco().toLowerCase().contains(lowerQuery)) {
                filteredHospitais.add(e);
            }
        }

        updateRecyclerViews(filteredHospitais, filteredPostos, filteredFarmacias);
    }

    private void checkSession() {
        long userId = prefs.getLong("userId", -1);
        if (userId != -1) {
            Usuario usuario = dbHelper.getUsuario(userId);
            if (usuario != null) {
                loginButton.setVisibility(View.GONE);
                userInfoArea.setVisibility(View.VISIBLE);
                userEmailDisplay.setText(usuario.getEmail());
            } else {
                clearSession();
            }
        } else {
            loginButton.setVisibility(View.VISIBLE);
            userInfoArea.setVisibility(View.GONE);
        }
    }

    private void clearSession() {
        prefs.edit().clear().apply();
        loginButton.setVisibility(View.VISIBLE);
        userInfoArea.setVisibility(View.GONE);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

        btnAgendar.setOnClickListener(v -> {
            long userId = prefs.getLong("userId", -1);
            if (userId == -1) {
                Toast.makeText(this, "Faça login para agendar uma consulta", Toast.LENGTH_LONG).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            } else {
                startActivity(new Intent(MainActivity.this, AgendamentoActivity.class));
            }
        });

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            clearSession();
            Toast.makeText(this, "Você saiu da sua conta", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkSession();
    }
}