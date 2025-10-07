package com.example.projetoindividual;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projetoindividual.database.FirebaseHelper;
import com.example.projetoindividual.model.Projeto;
import com.example.projetoindividual.model.Tarefa;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class AddProjetoActivity extends AppCompatActivity {

    private EditText editTituloProjeto;
    private LinearLayout containerTarefas, containerUsuarios;
    private MaterialButton btnAdicionarTarefa, btnAdicionarUsuario, btnSalvarProjeto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_projeto);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        // Ativar a seta de voltar na ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        editTituloProjeto = findViewById(R.id.editTituloProjeto);
        containerTarefas = findViewById(R.id.containerTarefas);
        containerUsuarios = findViewById(R.id.containerUsuarios);
        btnAdicionarTarefa = findViewById(R.id.btnAdicionarTarefa);
        btnAdicionarUsuario = findViewById(R.id.btnAdicionarUsuario);
        btnSalvarProjeto = findViewById(R.id.btnSalvarProjeto);

        adicionarTarefa();
        adicionarUsuario();

        btnAdicionarTarefa.setOnClickListener(v -> adicionarTarefa());
        btnAdicionarUsuario.setOnClickListener(v -> adicionarUsuario());
        btnSalvarProjeto.setOnClickListener(v -> salvarProjeto());
    }

    private void adicionarTarefa() {
        LinearLayout tarefaLayout = new LinearLayout(this);
        tarefaLayout.setOrientation(LinearLayout.HORIZONTAL);
        tarefaLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tarefaLayout.setPadding(0, 8, 0, 8);

        EditText editTarefaNome = new EditText(this);
        editTarefaNome.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        editTarefaNome.setHint("Nome da tarefa");
        editTarefaNome.setTextColor(getResources().getColor(android.R.color.white, null));
        editTarefaNome.setHintTextColor(getResources().getColor(android.R.color.white, null));
        editTarefaNome.getBackground().mutate().setTint(getResources().getColor(android.R.color.white, null));

        EditText editTarefaData = new EditText(this);
        editTarefaData.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        editTarefaData.setHint("Data (yyyy-MM-dd)");
        editTarefaData.setInputType(InputType.TYPE_CLASS_DATETIME);
        editTarefaData.setTextColor(getResources().getColor(android.R.color.white, null));
        editTarefaData.setHintTextColor(getResources().getColor(android.R.color.white, null));
        editTarefaData.getBackground().mutate().setTint(getResources().getColor(android.R.color.white, null));

        tarefaLayout.addView(editTarefaNome);
        tarefaLayout.addView(editTarefaData);
        containerTarefas.addView(tarefaLayout);
    }

    private void adicionarUsuario() {
        EditText editEmail = new EditText(this);
        editEmail.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        editEmail.setHint("Email do responsável");
        editEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        editEmail.setTextColor(getResources().getColor(android.R.color.white, null));
        editEmail.setHintTextColor(getResources().getColor(android.R.color.white, null));
        editEmail.getBackground().mutate().setTint(getResources().getColor(android.R.color.white, null));

        containerUsuarios.addView(editEmail);
    }

    private void salvarProjeto() {
        String titulo = editTituloProjeto.getText().toString().trim();
        if (titulo.isEmpty()) {
            editTituloProjeto.setError("Digite o título do projeto");
            return;
        }

        // Criar lista de tarefas
        List<Tarefa> listaTarefas = new ArrayList<>();
        for (int i = 0; i < containerTarefas.getChildCount(); i++) {
            LinearLayout layout = (LinearLayout) containerTarefas.getChildAt(i);
            EditText nomeTarefa = (EditText) layout.getChildAt(0);
            EditText dataTarefa = (EditText) layout.getChildAt(1);

            String nome = nomeTarefa.getText().toString().trim();
            String data = dataTarefa.getText().toString().trim();

            if (!nome.isEmpty() && !data.isEmpty()) {
                Tarefa tarefa = new Tarefa(nome, data, false);
                listaTarefas.add(tarefa);
            }
        }

        // 2️⃣ Criar lista de emails
        List<String> listaEmails = new ArrayList<>();
        for (int i = 0; i < containerUsuarios.getChildCount(); i++) {
            EditText editEmail = (EditText) containerUsuarios.getChildAt(i);
            String email = editEmail.getText().toString().trim();
            if (!email.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                listaEmails.add(email);
            }
        }

        String emailAtual = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (emailAtual != null && !listaEmails.contains(emailAtual)) {
            listaEmails.add(emailAtual);
        }

        // 3️⃣ Criar projeto
        Projeto projeto = new Projeto(titulo, "Por começar", new ArrayList<>(), listaEmails);

        // Usar FirebaseHelper.criarProjeto
        FirebaseHelper.criarProjeto(projeto, (documentRef, error) -> {
            if (documentRef != null) {
                String projetoId = documentRef.getId();

                // Adicionar cada tarefa usando adicionarTarefa
                for (Tarefa tarefa : listaTarefas) {
                    FirebaseHelper.adicionarTarefa(projetoId, tarefa, (success, errMsg) -> {
                        if (!success) {
                            Toast.makeText(this, "Erro ao salvar tarefa: " + errMsg, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                Toast.makeText(this, "Projeto salvo com sucesso!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Erro ao criar projeto: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // termina esta activity e volta à anterior
        return true;
    }

}
