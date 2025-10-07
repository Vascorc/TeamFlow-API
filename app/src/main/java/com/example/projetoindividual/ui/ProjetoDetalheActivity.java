package com.example.projetoindividual.ui;

import android.os.Bundle;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.android.material.appbar.MaterialToolbar;
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

        // Pega a toolbar do layout e define como ActionBar
        MaterialToolbar toolbar = findViewById(R.id.toolbarProjeto);
        setSupportActionBar(toolbar);

        // Habilita seta de voltar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white, null));
            toolbar.setSubtitleTextColor(getResources().getColor(android.R.color.white, null));
        }



        // Pega os elementos do layout
        containerConteudo = findViewById(R.id.containerConteudo);
        btnTarefas = findViewById(R.id.btnTarefas);
        btnResponsaveis = findViewById(R.id.btnResponsaveis);

        // Recebe o projeto passado na intent
        Projeto projIntent = (Projeto) getIntent().getSerializableExtra(EXTRA_PROJETO);
        if (projIntent == null) {
            finish();
            return;
        }

        // Busca o projeto no Firebase
        FirebaseHelper.getProjectById(projIntent.id, (proj, error) -> {
            if (proj != null) {
                projeto = proj;

                // Atualiza título e subtítulo da ActionBar
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(projeto.nome);

                    SpannableString subtitle = new SpannableString("Estado: " + projeto.getEstado());
                    subtitle.setSpan(
                            new android.text.style.RelativeSizeSpan(0.7f),
                            0,
                            subtitle.length(),
                            android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                    getSupportActionBar().setSubtitle(subtitle);
                }


                // Mostra as tarefas por padrão
                mostrarTarefas();
            } else {
                Toast.makeText(this, "Erro ao carregar projeto: " + error, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Configura os botões de alternar entre tarefas e responsáveis
        btnTarefas.setOnClickListener(v -> mostrarTarefas());
        btnResponsaveis.setOnClickListener(v -> mostrarResponsaveis());
    }


    private void mostrarTarefas() {
        containerConteudo.removeAllViews();

        for (Tarefa tarefa : projeto.tarefas) {
            View itemView = getLayoutInflater().inflate(R.layout.detalhe_tarefa, containerConteudo, false);

            CheckBox checkbox = itemView.findViewById(R.id.checkboxTarefa);
            TextView txtTitulo = itemView.findViewById(R.id.txtTituloTarefa);
            TextView txtData = itemView.findViewById(R.id.txtDataConclusao);
            TextView btnApagar = itemView.findViewById(R.id.btnApagarTarefa);

            checkbox.setChecked(tarefa.concluida);
            txtTitulo.setText(tarefa.titulo);
            txtData.setText("Concluir até: " + tarefa.dataConclusao);

            checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                tarefa.concluida = isChecked;
                atualizarTarefa(tarefa);
            });

            btnApagar.setOnClickListener(v -> {
                FirebaseHelper.removerTarefa(projeto.id, tarefa.id, tarefa, (success, error) -> {
                    if (success) {
                        Toast.makeText(this, "Tarefa removida", Toast.LENGTH_SHORT).show();
                        projeto.tarefas.remove(tarefa);
                        mostrarTarefas(); // atualiza a lista
                    } else {
                        Toast.makeText(this, "Erro ao remover tarefa: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            });

            containerConteudo.addView(itemView);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_proj_detalhe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            // Confirma que o projeto existe
            if (projeto != null) {
                FirebaseHelper.removerProjeto(projeto.id, (success, error) -> {
                    if (success) {
                        Toast.makeText(this, "Projeto removido com sucesso!", Toast.LENGTH_SHORT).show();
                        finish(); // volta à lista de projetos
                    } else {
                        Toast.makeText(this, "Erro ao remover projeto: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
