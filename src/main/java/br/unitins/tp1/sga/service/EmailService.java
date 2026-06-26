package br.unitins.tp1.sga.service;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Serviço de envio de e-mails transacionais.
 *
 * Em ambiente de dev/test, o Quarkus Mailer está configurado em modo "mock"
 * (quarkus.mailer.mock=true) — os e-mails não são enviados de fato, apenas
 * registrados e disponíveis na Dev UI (/q/dev -> Mailer). Para produção,
 * configurar host/port/username/password/start-tls no application.properties
 * (ex: SMTP do Gmail, SendGrid, Amazon SES, etc.) e remover a flag mock.
 */
@ApplicationScoped
public class EmailService {

    @Inject
    Mailer mailer;

    /**
     * Envia o código de recuperação de senha para o cliente.
     */
    public void enviarCodigoRecuperacaoSenha(String destinatario, String nomeCliente, String codigo) {
        String corpo = "Olá, " + nomeCliente + "!\n\n"
                + "Recebemos uma solicitação de redefinição de senha para sua conta na FP Cutelaria.\n\n"
                + "Seu código de verificação é: " + codigo + "\n\n"
                + "Este código é válido por 15 minutos. Se você não solicitou esta alteração, "
                + "ignore este e-mail — sua senha continuará a mesma.\n\n"
                + "Atenciosamente,\nEquipe FP Cutelaria";

        mailer.send(Mail.withText(destinatario, "Recuperação de senha — FP Cutelaria", corpo));
    }

}
