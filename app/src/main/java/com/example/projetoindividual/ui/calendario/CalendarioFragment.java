package com.example.projetoindividual.ui.calendario;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.projetoindividual.R;
import com.example.projetoindividual.database.FirebaseHelper;
import com.example.projetoindividual.model.Projeto;
import com.example.projetoindividual.model.Tarefa;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.List;

public class CalendarioFragment extends Fragment {

    private MaterialCalendarView calendar;
    private LinearLayout containerTarefas;
    private List<Projeto> listaProjetos = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calendario, container, false);

        calendar = root.findViewById(R.id.calendarView);
        containerTarefas = root.findViewById(R.id.containerTarefas);

        // Buscar projetos do Firebase
        FirebaseHelper.getAllProjectsForCurrentUser((projetos, error) -> {
            if (error != null) {
                // Aqui você pode mostrar uma mensagem de erro
                return;
            }

            listaProjetos = projetos;
            marcarDiasComTarefas();
        });

        // Listener para clicar nos dias
        calendar.setOnDateChangedListener((widget, date, selected) -> mostrarTarefasDoDia(date));

        return root;
    }

    private void mostrarTarefasDoDia(CalendarDay date) {
        containerTarefas.removeAllViews();

        for (Projeto projeto : listaProjetos) {
            if (projeto.tarefas == null) continue;

            for (Tarefa tarefa : projeto.tarefas) {
                String[] parts = tarefa.dataConclusao.split("-");
                int ano = Integer.parseInt(parts[0]);
                int mes = Integer.parseInt(parts[1]);
                int dia = Integer.parseInt(parts[2]);

                // Mes no CalendarDay é 1-based, então mantemos assim
                if (ano == date.getYear() && mes == date.getMonth() && dia == date.getDay()) {
                    // Inflar o layout do card da tarefa
                    View card = LayoutInflater.from(getContext())
                            .inflate(R.layout.item_tarefa, containerTarefas, false);

                    // Encontrar os TextViews dentro do card
                    TextView tituloTarefa = card.findViewById(R.id.textTituloTarefa);
                    TextView nomeProjeto = card.findViewById(R.id.textNomeProjeto);

                    // Definir os textos com os dados reais da tarefa e do projeto
                    tituloTarefa.setText(tarefa.titulo);
                    nomeProjeto.setText(projeto.nome);

                    // Adicionar o card ao container
                    containerTarefas.addView(card);
                }
            }
        }
    }

    private void marcarDiasComTarefas() {
        int corAzulHevy = getResources().getColor(R.color.hevy_blue);

        for (Projeto projeto : listaProjetos) {
            if (projeto.tarefas == null) continue;

            for (Tarefa tarefa : projeto.tarefas) {
                String[] parts = tarefa.dataConclusao.split("-");
                int ano = Integer.parseInt(parts[0]);
                int mes = Integer.parseInt(parts[1]);
                int dia = Integer.parseInt(parts[2]);

                CalendarDay day = CalendarDay.from(ano, mes, dia);
                calendar.addDecorator(new DiaComTarefaDecorator(day, corAzulHevy));
            }
        }
    }
}
