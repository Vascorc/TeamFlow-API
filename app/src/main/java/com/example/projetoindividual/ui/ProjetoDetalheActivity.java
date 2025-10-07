package com.example.projetoindividual.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projetoindividual.R;
import com.example.projetoindividual.database.FirebaseHelper;
import com.example.projetoindividual.model.Projeto;
import com.example.projetoindividual.model.Tarefa;
import com.google.android.material.button.MaterialButton;

public class ProjetoDetalheActivity extends AppCompatActivity {

    public static final String EXTRA_PROJETO = "extra_projeto";
    private Projeto projeto;

    private LinearLayout containerConteudo;
    private MaterialButton btnTarefas, btnResponsaveis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projeto_detalhe);

        containerConteudo = findViewById(R.id.containerConteudo);
        btnTarefas = findViewById(R.id.btnTarefas);
        btnResponsaveis = findViewById(R.id.btnResponsaveis);

        // Habilitar seta de voltar no ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Projeto projIntent = (Projeto) getIntent().getSerializableExtra(EXTRA_PROJETO);
        if (projIntent == null) {
            finish();
            return;
        }

        FirebaseHelper.getProjectById(projIntent.id, (proj, error) -> {
            if (proj != null) {
                projeto = proj;
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(projeto.nome);
                    getSupportActionBar().setSubtitle("Estado: " + projeto.getEstado());
                }
                mostrarTarefas(); // Mostrar tarefas por padrão
            } else {
                Toast.makeText(this, "Erro ao carregar projeto: " + error, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnTarefas.setOnClickListener(v -> mostrarTarefas());
        btnResponsaveis.setOnClickListener(v -> mostrarResponsaveis());
    }

    private void mostrarTarefas() {
        containerConteudo.removeAllViews();

        for (Tarefa tarefa : projeto.tarefas) {
            LinearLayout linha = new LinearLayout(this);
            linha.setOrientation(LinearLayout.HORIZONTAL);
            linha.setPadding(8, 8, 8, 8);
            linha.setWeightSum(3);

            CheckBox checkbox = new CheckBox(this);
            checkbox.setChecked(tarefa.concluida);
            checkbox.setScaleX(1.1f);
            checkbox.setScaleY(1.1f);

            int[][] states = {
                    new int[]{android.R.attr.state_checked},
                    new int[]{-android.R.attr.state_checked}
            };
            int[] colors = {
                    getResources().getColor(R.color.hevy_blue, null),
                    getResources().getColor(android.R.color.white, null)
            };
            checkbox.setButtonTintList(new android.content.res.ColorStateList(states, colors));


            checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                tarefa.concluida = isChecked;
                atualizarTarefa(tarefa);
            });

            TextView txtTitulo = new TextView(this);
            txtTitulo.setText(tarefa.titulo);
            txtTitulo.setTextSize(18);
            txtTitulo.setTextColor(getResources().getColor(android.R.color.white, null));
            txtTitulo.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);

            TextView txtData = new TextView(this);
            txtData.setText(tarefa.dataConclusao);
            txtData.setTextSize(16);
            txtData.setTextColor(getResources().getColor(R.color.hevy_blue, null));
            txtData.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);

            LinearLayout.LayoutParams paramsCheckbox = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.26f);
            LinearLayout.LayoutParams paramsTitulo = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.7f);
            LinearLayout.LayoutParams paramsData = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);

            linha.addView(checkbox, paramsCheckbox);
            linha.addView(txtTitulo, paramsTitulo);
            linha.addView(txtData, paramsData);

            containerConteudo.addView(linha);
        }
    }

    private void mostrarResponsaveis() {
        containerConteudo.removeAllViews();

        for (String email : projeto.users) {
            TextView txtUser = new TextView(this);
            txtUser.setText(email);
            txtUser.setTextSize(18);
            txtUser.setTextColor(getResources().getColor(android.R.color.white, null));
            txtUser.setPadding(8, 12, 8, 12);
            txtUser.setGravity(Gravity.START);

            containerConteudo.addView(txtUser);
        }
    }

    private void atualizarTarefa(Tarefa tarefa) {
        FirebaseHelper.updateTask(tarefa, (success, error) -> {
            if (!success) Toast.makeText(this, "Erro ao atualizar tarefa: " + error, Toast.LENGTH_SHORT).show();
        });

        atualizarEstadoProjeto();
        FirebaseHelper.updateProject(projeto, (success, error) -> {
            if (!success) Toast.makeText(this, "Erro ao atualizar projeto: " + error, Toast.LENGTH_SHORT).show();
        });
    }

    private void atualizarEstadoProjeto() {
        boolean todasConcluidas = true;
        boolean algumaConcluida = false;

        for (Tarefa t : projeto.tarefas) {
            if (t.concluida) algumaConcluida = true;
            else todasConcluidas = false;
        }

        if (todasConcluidas) projeto.estado = "Concluído";
        else if (algumaConcluida) projeto.estado = "Em andamento";
        else projeto.estado = "Por começar";

        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle("Estado: " + projeto.getEstado());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
