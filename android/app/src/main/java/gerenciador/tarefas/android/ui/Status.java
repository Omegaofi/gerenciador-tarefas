package gerenciador.tarefas.android.ui;

import android.graphics.drawable.GradientDrawable;
import android.widget.TextView;
import gerenciador.tarefas.android.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import modelDominio.Tarefa;

/** Rótulos e cores dos status, como Tema.rotuloStatus / Tema.badge / Tema.corProjeto do desktop. */
public final class Status {

    public static final String[] PROJETO = {"planejado", "em_andamento", "concluido", "cancelado"};
    public static final String[] TAREFA = {"pendente", "em_andamento", "concluida"};

    private static final SimpleDateFormat DATA = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
    private static final SimpleDateFormat DATA_HORA = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "BR"));

    private Status() {
    }

    public static String rotulo(String status) {
        if (status == null) {
            return "—";
        }
        switch (status) {
            case "planejado": return "Planejamento";
            case "em_andamento": return "Em andamento";
            case "concluido": return "Concluído";
            case "cancelado": return "Cancelado";
            case "pendente": return "Pendente";
            case "concluida": return "Concluída";
            case "atrasada": return "Atrasada";
            case "enviada": return "Aguardando revisão";
            case "em_revisao": return "Em revisão";
            case "aprovada": return "Aprovada";
            case "rejeitada": return "Rejeitada";
            case "recusado": return "Recusado";
            case "aceito": return "Aceito";
            case "criador": return "Criador";
            case "participante": return "Participante";
            default: return status;
        }
    }

    public static String[] rotulos(String[] status) {
        String[] r = new String[status.length];
        for (int i = 0; i < status.length; i++) {
            r[i] = rotulo(status[i]);
        }
        return r;
    }

    /** Pinta o selo de status como o Tema.badge do desktop. */
    public static void pintarBadge(TextView badge, String status) {
        int fundo;
        int texto;
        int borda;
        String s = status == null ? "" : status;
        switch (s) {
            case "em_andamento":
            case "enviada":
            case "em_revisao":
                fundo = R.color.preto; texto = android.R.color.white; borda = R.color.preto;
                break;
            case "concluido":
            case "concluida":
            case "aprovada":
            case "aceito":
                fundo = R.color.limao; texto = R.color.texto; borda = R.color.limao;
                break;
            case "atrasada":
            case "cancelado":
            case "rejeitada":
            case "recusado":
                fundo = android.R.color.white; texto = R.color.vermelho; borda = R.color.vermelho;
                break;
            default:
                fundo = android.R.color.white; texto = R.color.texto_claro; borda = R.color.borda;
        }
        GradientDrawable fundoBadge = new GradientDrawable();
        float d = badge.getResources().getDisplayMetrics().density;
        fundoBadge.setCornerRadius(6 * d);
        fundoBadge.setColor(badge.getContext().getColor(fundo));
        fundoBadge.setStroke(Math.max(1, (int) d), badge.getContext().getColor(borda));
        badge.setBackground(fundoBadge);
        badge.setTextColor(badge.getContext().getColor(texto));
        badge.setText(rotulo(status).toUpperCase(new Locale("pt", "BR")));
    }

    /** Cor do quadradinho do projeto (Tema.corProjeto). */
    public static int corProjeto(String status) {
        if (status == null) {
            return R.color.borda;
        }
        switch (status) {
            case "em_andamento": return R.color.limao;
            case "planejado": return R.color.laranja;
            case "concluido": return R.color.azul;
            default: return R.color.borda;
        }
    }

    /** Como TarefaTableModel.statusExibido: tarefa aberta com prazo vencido aparece como "atrasada". */
    public static String statusExibido(Tarefa tarefa) {
        String status = tarefa.getStatus();
        boolean aberta = "pendente".equals(status) || "em_andamento".equals(status);
        if (aberta && tarefa.getPrazo() != null && tarefa.getPrazo().before(hoje())) {
            return "atrasada";
        }
        return status;
    }

    /** Hoje à meia-noite. */
    public static Date hoje() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static String data(Date d) {
        return d == null ? "sem prazo" : DATA.format(d);
    }

    public static String dataHora(Date d) {
        return d == null ? "" : DATA_HORA.format(d);
    }

    public static boolean vazio(String s) {
        return s == null || s.trim().isEmpty();
    }
}
