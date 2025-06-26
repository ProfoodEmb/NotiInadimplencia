package br.inad.util;

import br.inad.model.DadosInadimplencia;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class HtmlInadimplenciaBuilder {

    public static String gerarHtmlEmail(List<DadosInadimplencia> dados) {
        StringBuilder sb = new StringBuilder();
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String emailVendedor = dados.get(0).getEmailVendedor(); // assume que todos são do mesmo vendedor

        sb.append("<!DOCTYPE html>");
        sb.append("<html lang=\"pt-BR\">");
        sb.append("<head>");
        sb.append("<meta charset=\"UTF-8\">");
        sb.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        sb.append("<title>Relatório de Inadimplência</title>");
        sb.append("</head>");
        sb.append("<body style=\"background-color:#f5f5f5;font-family:'Inter', Arial, sans-serif; margin:0; padding:0;\">");

        sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"padding:24px; font-family:Arial, sans-serif;\">");
        sb.append("<tr><td align=\"center\">");

        sb.append("<table width=\"800\" style=\"background-color:#fff; border-radius:12px; box-shadow:0 2px 6px rgba(0,0,0,0.1);\">");

        sb.append("<tr><td align=\"center\" style=\"padding: 20px 0;\">");
        sb.append("<img src=\"https://imgur.com/uyCBGU6.png\" alt=\"Logo\" style=\"max-width:200px;\">");
        sb.append("</td></tr>");

        sb.append("<tr><td style=\"padding: 0 20px 20px 20px; text-align: center;\">");
        sb.append("<h2 style=\"color:#333; margin-bottom:10px;\">Relatório de Inadimplência</h2>");
        sb.append("<p style=\"margin: 0; color:#555; font-size:14px;\">Enviado para: <strong>").append(emailVendedor).append("</strong></p>");
        sb.append("</td></tr>");

        sb.append("<tr><td style=\"padding: 0 20px 20px 20px;\">");
        sb.append("<table width=\"100%\" cellpadding=\"6\" cellspacing=\"0\" style=\"border-collapse: collapse; font-size:14px;\">");
        sb.append("<thead style=\"background-color:#A62B1F; color:#fff; text-align:left; border-radius:8px 8px 0 0;\">");
        sb.append("<tr>");
        sb.append("<th style=\"padding: 8px;\">Matriz</th>");
        sb.append("<th style=\"padding: 8px;\">Nome Parceiro</th>");
        sb.append("<th style=\"padding: 8px;\">Vendedor</th>");
        sb.append("<th style=\"padding: 8px;\">Email Vendedor</th>");
        sb.append("<th style=\"padding: 8px;\">Código Título</th>");
        sb.append("<th style=\"padding: 8px;\">Data Vencimento</th>");
        sb.append("<th style=\"padding: 8px;\">Número Nota</th>");
        sb.append("<th style=\"padding: 8px;\">Dias Vencido</th>");
        sb.append("<th style=\"padding: 8px; text-align:right;\">Valor</th>");
        sb.append("</tr>");
        sb.append("</thead>");

        sb.append("<tbody>");

        boolean zebra = false;
        for (DadosInadimplencia d : dados) {
            String bgColor = zebra ? "#f9f9f9" : "#ffffff";
            zebra = !zebra;

            sb.append("<tr style=\"background-color:").append(bgColor).append("; border-bottom:1px solid #eee;\">");
            sb.append("<td style=\"padding:8px;\">").append(d.getMatriz()).append("</td>");
            sb.append("<td style=\"padding:8px;\">").append(d.getNomeParceiro()).append("</td>");
            sb.append("<td style=\"padding:8px;\">").append(d.getVendedor()).append("</td>");
            sb.append("<td style=\"padding:8px;\">").append(d.getEmailVendedor()).append("</td>");
            sb.append("<td style=\"padding:8px;\">").append(d.getCodigoTitulo()).append("</td>");
            sb.append("<td style=\"padding:8px;\">")
                    .append(d.getDataVencimento() != null ? d.getDataVencimento().format(dtf) : "")
                    .append("</td>");
            sb.append("<td style=\"padding:8px;\">").append(d.getNumeroNota()).append("</td>");
            sb.append("<td style=\"padding:8px;\">").append(d.getDiasVencido()).append("</td>");
            sb.append("<td style=\"padding:8px; text-align:right;\"><strong>")
                    .append(nf.format(d.getValor()))
                    .append("</strong></td>");
            sb.append("</tr>");
        }

        sb.append("</tbody>");
        sb.append("</table>");
        sb.append("</td></tr>");

        // Rodapé visual como fechamento
        sb.append("<tr><td style=\"padding: 0 20px 20px 20px;\">");
        sb.append("<div style=\"margin-top:24px; height:16px; background-color:#A62B1F; border-radius:0 0 12px 12px;\"></div>");
        sb.append("</td></tr>");

        sb.append("</table>");
        sb.append("</td></tr>");
        sb.append("</table>");
        sb.append("</body>");
        sb.append("</html>");

        return sb.toString();
    }
}
