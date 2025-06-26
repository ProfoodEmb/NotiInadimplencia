package br.inad;

import br.inad.model.DadosInadimplencia;
import br.inad.util.HtmlInadimplenciaBuilder;
import br.inad.util.WebhookService;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

public class AcAgNotiInadimplencia implements ScheduledAction {

    @Override
    public void onTime(ScheduledActionContext ctx) {
        List<DadosInadimplencia> inadimplencias = new ArrayList<>();

        try {
            // Abertura de sessão com a Sankhya
            EntityFacade facade = EntityFacadeFactory.getDWFFacade();
            JdbcWrapper jdbc = facade.getJdbcWrapper();
            jdbc.openSession();

            // Consulta SQL
            String sql = "SELECT " +
                    "    MAT.nomeparc AS Matriz, " +
                    "    PAR.nomeparc AS NomeParceiro, " +
                    "    VEN.APELIDO AS Vendedor, " +
                    "    VEN.EMAIL AS EmailVendedor, " +
                    "    FIN.NUFIN AS CodigoTitulo, " +
                    "    FIN.dtvenc AS DataVencimento, " +
                    "    FIN.numnota AS NumeroNota, " +
                    "    TRUNC(SYSDATE) - TRUNC(FIN.dtvenc) AS DiasVencido, " +
                    "    FIN.VLRDESDOB AS Valor " +
                    "FROM TGFFIN FIN " +
                    "INNER JOIN TGFPAR PAR ON PAR.codparc = FIN.codparc " +
                    "INNER JOIN TGFPAR MAT ON PAR.codparcmatriz = MAT.codparc " +
                    "LEFT JOIN TGFVEN VEN ON FIN.CODVEND = VEN.CODVEND " +
                    "WHERE FIN.RECDESP = 1 " +
                    "AND FIN.PROVISAO = 'N' " +
                    "AND PAR.CLIENTE = 'S' " +
                    "AND FIN.CODNAT = 101001001 " +
                    "AND FIN.DTVENC < TRUNC(SYSDATE) " +
                    "AND FIN.DHBAIXA IS NULL";

            // Execução da query
            try (PreparedStatement ps = jdbc.getPreparedStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    DadosInadimplencia dado = new DadosInadimplencia();
                    dado.setMatriz(rs.getString("MATRIZ"));
                    dado.setNomeParceiro(rs.getString("NOMEPARCEIRO"));
                    dado.setVendedor(rs.getString("VENDEDOR"));
                    dado.setEmailVendedor(rs.getString("EMAILVENDEDOR"));
                    dado.setCodigoTitulo(rs.getInt("CODIGOTITULO"));

                    // Conversão segura de Timestamp para LocalDate
                    Timestamp ts = rs.getTimestamp("DATAVENCIMENTO");
                    dado.setDataVencimento(ts != null ? ts.toLocalDateTime().toLocalDate() : null);

                    dado.setNumeroNota(rs.getInt("NUMERONOTA"));
                    dado.setDiasVencido(rs.getInt("DIASVENCIDO"));
                    dado.setValor(rs.getBigDecimal("VALOR"));

                    inadimplencias.add(dado);
                }
            }

            if (!inadimplencias.isEmpty()) {
                enviarEmails(inadimplencias);
                WebhookService.post("Inadimplências processadas com sucesso. Total: " + inadimplencias.size());
            } else {
                WebhookService.post("Nenhuma inadimplência encontrada para envio.");
            }

        } catch (Exception e) {
            WebhookService.post("Erro ao processar inadimplências: " + e.getMessage());
        } finally {
            JapeSession.close(); // Libera recursos da sessão Jape
        }
    }

    private void enviarEmails(List<DadosInadimplencia> inadimplencias) throws Exception {
        Map<String, List<DadosInadimplencia>> inadimplenciasPorEmail = new HashMap<>();

        for (DadosInadimplencia dado : inadimplencias) {
            String email = Optional.ofNullable(dado.getEmailVendedor()).orElse("yanprofood@gmail.com");

            inadimplenciasPorEmail
                    .computeIfAbsent(email, k -> new ArrayList<>())
                    .add(dado);
        }

        for (Map.Entry<String, List<DadosInadimplencia>> entry : inadimplenciasPorEmail.entrySet()) {
            String email = entry.getKey();
            List<DadosInadimplencia> dados = entry.getValue();

            String html = HtmlInadimplenciaBuilder.gerarHtmlEmail(dados);

            JapeWrapper emailDAO = JapeFactory.dao("MSDFilaMensagem");

            emailDAO.create()
                    .set("STATUS", "Pendente")
                    .set("CODCON", BigDecimal.ZERO)
                    .set("TENTENVIO", BigDecimal.ONE)
                    .set("MENSAGEM", html.toCharArray())
                    .set("TIPOENVIO", "E")
                    .set("MAXTENTENVIO", new BigDecimal(3))
                    .set("ASSUNTO", "Relatório de Inadimplência")
                    .set("EMAIL", "yanprofood@gmail.com") // dinâmico após validação
                    .set("CODUSU", BigDecimal.ZERO)
                    .save();
        }
    }
}
