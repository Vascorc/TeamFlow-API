package com.example.projetoindividual.model;

public class Tarefa {
    public String titulo;
    public String descricao;
    public String dataConclusao;
    public boolean concluida;

    public Tarefa(String titulo, String descricao, String dataConclusao) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataConclusao = dataConclusao;
        this.concluida = false;
    }

    // Alterna o status de concluída
    public void marcarConcluida(boolean status) {
        this.concluida = status;
    }

    // Alterar título da tarefa
    public void alterarTitulo(String novoTitulo) {
        this.titulo = novoTitulo;
    }

    // Alterar descrição da tarefa
    public void alterarDescricao(String novaDescricao) {
        this.descricao = novaDescricao;
    }

    // Alterar data de conclusão
    public void alterarDataConclusao(String novaData) {
        this.dataConclusao = novaData;
    }
}
