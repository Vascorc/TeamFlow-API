package com.example.projetoindividual.model;

import java.util.List;

public class Projeto {
    public String nome;
    public List<Tarefa> tarefas;

    public Projeto(String nome, List<Tarefa> tarefas) {
        this.nome = nome;
        this.tarefas = tarefas;
    }



    // Retorna o status atual do projeto
    public String getStatus() {
        int total = tarefas.size();
        int concluidas = 0;
        for (int i = 0; i < tarefas.size(); i++) {
            if (tarefas.get(i).concluida) {
                concluidas++;
            }
        }

        if (concluidas == 0) return "Pendente";         // nenhuma tarefa concluida
        else if (concluidas < total) return "Em andamento"; // algumas concluidas
        else return "Concluído";                         // todas concluidas
    }

    // Alterar nome do projeto
    public void alterarNome(String novoNome) {
        this.nome = novoNome;
    }

    // Adicionar uma nova tarefa ao projeto
    public void adicionarTarefa(Tarefa tarefa) {
        this.tarefas.add(tarefa);
    }

}
