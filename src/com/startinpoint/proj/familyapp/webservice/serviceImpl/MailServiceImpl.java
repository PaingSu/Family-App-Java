package com.startinpoint.proj.familyapp.webservice.serviceImpl;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyProfile;
import com.startinpoint.proj.familyapp.webservice.entity.enums.MailType;
import com.startinpoint.proj.familyapp.webservice.exception.FamilyAppWebserviceException;
import com.startinpoint.proj.familyapp.webservice.service.MailService;
import com.startinpoint.utils.DesEncrypter;
import com.sun.mail.smtp.SMTPMessage;
import com.sun.mail.util.MailSSLSocketFactory;

@Service("mailService")
public class MailServiceImpl implements MailService {

	@Value("${application.server_url}")
	private String serverUrl;

	@Override
	public void sendVerificationCodeToMail(String code, String recipient, MailType mailType)
			throws FamilyAppWebserviceException, MessagingException {
		Session mailSession = buildMailSession();
		Message message = null;
		if (mailType == MailType.FORGOT_PASSWORD) {
			message = prepareForgotPasswordMail(mailSession, code);
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
			Transport.send(message);
		} else if (mailType == MailType.EMAIL_VERIFICATION) {
			message = prepareVerificationMail(mailSession, code);
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
			Transport.send(message);
		}

	}

	/**
	 * Prepare Forgot Password Message
	 * 
	 * @param session
	 * @param code
	 * @return
	 * @throws MessagingException
	 */
	public Message prepareForgotPasswordMail(Session session, String code) throws MessagingException {
		SMTPMessage m = new SMTPMessage(session);
		MimeMultipart content = new MimeMultipart("related");
		String text = "You recently requested to reset your password for your account.Please use this code <b>" + code
				+ "<b> for resetting password";
		text += "<br>If you did not request a password reset, please ignore this email.<br>This password reset is only valid for the next  days.\nThanks.";

		// HTML part
		MimeBodyPart textPart = new MimeBodyPart();
		textPart.setContent(text, "text/html");
		content.addBodyPart(textPart);
		m.setContent(content);
		m.setSubject("Forgot password");
		return m;
	}

	/**
	 * Prepare Verification Mail Message
	 * 
	 * @param session
	 * @param code
	 * @return
	 * @throws MessagingException
	 */
	public Message prepareVerificationMail(Session session, String code) throws MessagingException {
		SMTPMessage m = new SMTPMessage(session);
		MimeMultipart content = new MimeMultipart("related");
		String url = code;
		String text = "<a href='" + url + "'>Activate Account</a>";

		// HTML part
		MimeBodyPart textPart = new MimeBodyPart();
		textPart.setContent(text, "text/html");
		content.addBodyPart(textPart);
		m.setContent(content);
		m.setSubject("Verification Email");
		return m;
	}

	/**
	 * Prepare Verification Mail Message
	 * 
	 * @param session
	 * @param code
	 * @return
	 * @throws MessagingException
	 */
	public Message prepareFamilyInviteMail(Session session, FamilyProfile family) throws MessagingException {

		SMTPMessage m = new SMTPMessage(session);
		MimeMultipart content = new MimeMultipart("related");
		String text = "This is the family invite mail come from FamilyApp <br>";
		text += "Family Name: " + family.getFamilyName() + "<br>";
		text += "Family Code: " + family.getFamilyCode();

		// HTML part
		MimeBodyPart textPart = new MimeBodyPart();
		textPart.setContent(text, "text/html");
		content.addBodyPart(textPart);
		m.setContent(content);
		m.setSubject("Family Invitation ");
		return m;
	}

	/**
	 * Build google mail session
	 * @return
	 */
	public Session buildMailSession() {
		try {
			String mail = "familyapp.sip@gmail.com";
			String codeword = "NZPJPCOcM+QE9KjbIpH8PQ==";
			String fromMail = "familyapp.sip@gmail.com";
			String host = "smtp.gmail.com";
			String port = "587";
			String LTSEnable = "true";
			DesEncrypter decoder = new DesEncrypter();

			Properties mailProps = new Properties();
			mailProps.put("mail.transport.protocol", "smtp");
			mailProps.put("mail.smtp.host", host);
			mailProps.put("mail.from", fromMail);
			mailProps.put("mail.smtp.starttls.enable", LTSEnable);
			mailProps.put("mail.smtp.port", port);
			mailProps.put("mail.smtp.auth", "true");

			// added to ensure ssl protocols is TLSv1.2
			mailProps.put("mail.smtp.ssl.protocols", "TLSv1.2");
			// newly added for certificate
			MailSSLSocketFactory sf = new MailSSLSocketFactory();
			sf.setTrustAllHosts(true);
			mailProps.put("mail.smtp.ssl.trust", "*");
			mailProps.put("mail.smtp.ssl.socketFactory", sf);

			final PasswordAuthentication usernamePassword = new PasswordAuthentication(mail,
					new String(decoder.decrypt(codeword)));

			Authenticator auth = new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return usernamePassword;
				}
			};

			Session session = Session.getInstance(mailProps, auth);
			session.setDebug(true);
			return session;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void sendFamilyInviteMail(FamilyProfile family, String recipients) throws FamilyAppWebserviceException {

		try {
			Session mailSession = buildMailSession();
			Message message = null;
			message = prepareFamilyInviteMail(mailSession, family);
			InternetAddress[] recipientList = InternetAddress.parse(recipients);
			message.setRecipients(Message.RecipientType.TO, recipientList);
			Transport.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}

}
