package com.example.projetoindividual.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projetoindividual.R;
import com.example.projetoindividual.database.FirebaseHelper;
import com.example.projetoindividual.model.Projeto;
import com.example.projetoindividual.model.Tarefa;

public class ProjetoDetalheActivity extends AppCompatActivity {

    public static final String EXTRA_PROJETO = "extra_projeto";

    private Projeto projeto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projeto_detalhe);

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

        // Buscar projeto atualizado do Firebase (com tarefas e usuários)
        FirebaseHelper.getProjectById(projIntent.id, (proj, error) -> {
            if (proj != null) {
                projeto = proj;
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(projeto.nome);
                }
                renderizarTarefas();
                atualizarEstadoProjeto();
            } else {
                Toast.makeText(this, "Erro ao carregar projeto: " + error, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void renderizarTarefas() {
        LinearLayout containerTarefas = findViewById(R.id.containerTarefas);
        containerTarefas.removeAllViews();

        for (Tarefa tarefa : projeto.tarefas) {
            // Criar layout horizontal
            LinearLayout linha = new LinearLayout(this);
            linha.setOrientation(LinearLayout.HORIZONTAL);
            linha.setPadding(8, 8, 8, 8);
            linha.setWeightSum(3);

            // Checkbox
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

            // Quando o usuário marcar/desmarcar a tarefa
            checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                tarefa.concluida = isChecked;
                atualizarTarefa(tarefa);
            });

            // TextView título
            TextView txtTitulo = new TextView(this);
            txtTitulo.setText(tarefa.titulo);
            txtTitulo.setTextSize(18);
            txtTitulo.setTextColor(getResources().getColor(android.R.color.white, null));
            txtTitulo.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);

            // TextView data
            TextView txtData = new TextView(this);
            txtData.setText(tarefa.dataConclusao);
            txtData.setTextSize(16);
            txtData.setTextColor(getResources().getColor(R.color.hevy_blue, null));
            txtData.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);

            // LayoutParams
            LinearLayout.LayoutParams paramsCheckbox = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.26f);
            LinearLayout.LayoutParams paramsTitulo = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.7f);
            LinearLayout.LayoutParams paramsData = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);

            linha.addView(checkbox, paramsCheckbox);
            linha.addView(txtTitulo, paramsTitulo);
            linha.addView(txtData, paramsData);

            containerTarefas.addView(linha);
        }
    }

    /**
     * Atualiza o estado local do projeto no ActionBar
     */
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

    /**
     * Atualiza a tarefa no Firebase e também o estado do projeto
     */
    private void atualizarTarefa(Tarefa tarefa) {
        // Atualiza estado local do projeto
        atualizarEstadoProjeto();

        // Atualiza tarefa no Firebase
        FirebaseHelper.updateTask(tarefa, (success, error) -> {
            if (!success) {
                Toast.makeText(this, "Erro ao atualizar tarefa: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        // Atualiza projeto no Firebase
        FirebaseHelper.updateProject(projeto, (success, error) -> {
            if (!success) {
                Toast.makeText(this, "Erro ao atualizar projeto: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_projeto_detalhe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_excluir) {
            FirebaseHelper.removerProjeto(projeto.id, (success, error) -> {
                if (success) finish();
                else Toast.makeText(this, "Erro ao deletar projeto: " + error, Toast.LENGTH_SHORT).show();
            });
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
