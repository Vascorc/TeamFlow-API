package com.example.projetoindividual.ui.calendario;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

public class DiaComTarefaDecorator implements DayViewDecorator {

    private final CalendarDay day;
    private final int cor; // permite personalizar a cor

    public DiaComTarefaDecorator(CalendarDay day, int cor) {
        this.day = day;
        this.cor = cor;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return this.day.equals(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(8, cor));
    }
}
