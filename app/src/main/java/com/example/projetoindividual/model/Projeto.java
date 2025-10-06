// Conteúdo para: com/example/projetoindividual/model/Projeto.java
package com.example.projetoindividual.model;

import java.io.Serializable;
import java.util.List;

public class Projeto implements Serializable {

    //1. Adicionar o campo 'id'
    public String id;
    public List<String> users; // IDs dos usuários que trabalham no projeto

    public String nome;
    public List<Tarefa> tarefas;
    public String estado;

    public Projeto() {}//pode sre preciso

    public Projeto(String id, String nome, String estado, List<Tarefa> tarefas, List<String> users) {
        this.id = id;
        this.nome = nome;
        this.estado = estado;
        this.tarefas = tarefas;
        this.users = users;
    }

    public Projeto( String nome, String estado, List<Tarefa> tarefas, List<String> users) {
        this.nome = nome;
        this.estado = estado;
        this.tarefas = tarefas;
        this.users = users;
    }

    // 2. Criar um novo construtor que inclui o 'id'
    public Projeto(String id, String nome, List<Tarefa> tarefas) {
        this.id = id;
        this.nome = nome;
        this.tarefas = tarefas;
        this.estado = calcularEstado();
    }

    // 3. Manter (ou criar) um construtor sem 'id' para quando se cria um projeto novo
    //    O ID será gerado pela base de dados.
    public Projeto(String nome, List<Tarefa> tarefas) {
        this.nome = nome;
        this.tarefas = tarefas;
        this.estado = calcularEstado();
    }

    private String calcularEstado() {
        int total = tarefas.size();
        int concluidas = 0;

        for (Tarefa tarefa : tarefas) {
            if (tarefa.concluida) {
                concluidas++;
            }
        }

        if (concluidas == 0) return "Por começar";        // nenhuma concluída
        else if (concluidas < total) return "Em andamento"; // algumas concluídas
        else return "Concluído";                          // todas concluídas
    }


    // 4. Renomear o método 'getStatus()' para ser um getter padrão (boa prática)
    public String getEstado() {
        return estado;
    }

    public String getId() {
        return id;
    }
}