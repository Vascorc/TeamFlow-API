package com.example.projetoindividual.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.projetoindividual.R;
import com.example.projetoindividual.databinding.FragmentHomeBinding;
import com.example.projetoindividual.model.Projeto;
import com.example.projetoindividual.model.Tarefa;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private List<Projeto> listaProjetos;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // --- Criar dados mock ---
        listaProjetos = new ArrayList<>();

        List<Tarefa> tarefas1 = new ArrayList<>();
        tarefas1.add(new Tarefa("Planejar treino", "Definir metas semanais", "2025-10-01"));
        tarefas1.add(new Tarefa("Comprar equipamento", "Halters e colchonetes", "2025-10-03"));

        List<Tarefa> tarefas2 = new ArrayList<>();
        tarefas2.add(new Tarefa("Criar cardápio", "Plano nutricional semanal", "2025-10-05"));

        listaProjetos.add(new Projeto("Projeto Academia", tarefas1));
        listaProjetos.add(new Projeto("Projeto Nutrição", tarefas2));

        // --- Adicionar TextViews dinâmicos ---
        LinearLayout layoutProjetos = binding.containerProjetos;    //é equivalente a usar findViewById(R.id.containerProjetos),
                                                                    // mas é mais seguro e direto, evitando erros se o id mudar.

        for (Projeto projeto : listaProjetos) {
            // Inflar o card
            View card = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_projeto, layoutProjetos, false); //vai ligar o card que criei em R.layout.item_projeto ao layout dos projetos

            // Preencher os campos
            TextView nome = card.findViewById(R.id.textNomeProjeto);
            TextView status = card.findViewById(R.id.textStatusProjeto);
            nome.setText(projeto.nome);
            status.setText(projeto.getStatus());

            // Adicionar o card ao LinearLayout
            layoutProjetos.addView(card);
        }



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
