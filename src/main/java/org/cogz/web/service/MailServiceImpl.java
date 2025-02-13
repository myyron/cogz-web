/*
 * Copyright 2025 Contractors of Ground Zero (CoGZ)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cogz.web.service;

import brevo.ApiClient;
import brevo.ApiException;
import brevo.Configuration;
import brevo.auth.ApiKeyAuth;
import brevoApi.TransactionalEmailsApi;
import brevoModel.SendSmtpEmail;
import brevoModel.SendSmtpEmailSender;
import brevoModel.SendSmtpEmailTo;
import jakarta.annotation.PostConstruct;
import org.cogz.web.enums.EGameType;
import org.cogz.web.enums.ERole;
import org.cogz.web.model.User;
import org.cogz.web.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author altrax
 */
@Service
public class MailServiceImpl implements IMailService {

    Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    private ApiClient apiClient;
    private List<SendSmtpEmailTo> staffEmails;

    @Value("${mail.api-key}")
    private String apiKey;

    @Value("${mail.enabled}")
    private boolean isMailEnabled;

    @Value("${cogz-web.link}")
    private String appLink;

    @PostConstruct
    private void postConstruct() {

        if (!isMailEnabled) {
            return;
        }

        apiClient = Configuration.getDefaultApiClient();
        ApiKeyAuth apiKeyAuth = (ApiKeyAuth) apiClient.getAuthentication("api-key");
        apiKeyAuth.setApiKey(apiKey);
        staffEmails = getStaffEmails();
    }

    private List<SendSmtpEmailTo> getStaffEmails() {
        List<SendSmtpEmailTo> result = new ArrayList<>();
        for (User user : userRepository.findAllByRoleInAndEnabled(List.of(ERole.ROLE_STAFF, ERole.ROLE_ADMIN), 1)) {
            result.add(new SendSmtpEmailTo().email(user.getEmail()));
        }
        return result;
    }

    @Override
    public void accountRegistrationGood(User user) {

        if (!isMailEnabled) {
            return;
        }

        TransactionalEmailsApi emailApi = new TransactionalEmailsApi(apiClient);
        SendSmtpEmail email = new SendSmtpEmail();
        email.setSender(new SendSmtpEmailSender().email("cogz.onlineportal@gmail.com").name("CoGZ Online Portal"));
        email.addToItem(new SendSmtpEmailTo().email(user.getEmail()));
        email.setSubject("Account Verified - GOOD");
        email.setHtmlContent("<h2>Account Verified - GOOD</h2>\n" +
                "<br>\n" +
                "<p>Hi " + user.getFirstname().substring(0, 1).toUpperCase() + user.getFirstname().substring(1).toLowerCase() + ",</p>\n" +
                "<p>Congratulations! Your account has been verified as 'GOOD' by our team. You are now allowed to join and register to our games. Please visit <a href=\"" + appLink + "\">CoGZ Online Portal</a> for our available game schedules.</p>\n" +
                "<p>Best Regards,<br>The CoGZ team</p>\n" +
                "<br>\n" +
                "<p>This is a system generated email, please do not reply to this message. For inquiries, contact a CoGZ Admin thru our <a href=\\\"http://www.facebook.com/TeamCoGZAirsoftPH\\\">Facebook page</a>.</p>\n" +
                "<hr>");

        try {
            emailApi.sendTransacEmail(email);
        } catch (ApiException e) {
            logger.error(null, e);
        }
    }

    @Override
    public void accountRegistrationBanned(User user) {

        if (!isMailEnabled) {
            return;
        }

        TransactionalEmailsApi emailApi = new TransactionalEmailsApi(apiClient);
        SendSmtpEmail email = new SendSmtpEmail();
        email.setSender(new SendSmtpEmailSender().email("cogz.onlineportal@gmail.com").name("CoGZ Online Portal"));
        email.addToItem(new SendSmtpEmailTo().email(user.getEmail()));
        email.setSubject("Account Verified - BANNED");
        email.setHtmlContent("<h2>Account Verified - BANNED</h2>\n" +
                "<br>\n" +
                "<p>Hi " + user.getFirstname().substring(0, 1).toUpperCase() + user.getFirstname().substring(1).toLowerCase() + ",</p>\n" +
                "<p>We are sorry to inform you, after much deliberation by our team, your account has been deemed as 'BANNED' from playing in our gamesite. Feel free to contact any CoGZ admin for the details of your violation.</p>\n" +
                "<p>Best Regards,<br>The CoGZ team</p>\n" +
                "<br>\n" +
                "<p>This is a system generated email, please do not reply to this message. For inquiries, contact a CoGZ Admin thru our <a href=\\\"http://www.facebook.com/TeamCoGZAirsoftPH\\\">Facebook page</a>.</p>\n" +
                "<hr>");

        try {
            emailApi.sendTransacEmail(email);
        } catch (ApiException e) {
            logger.error(null, e);
        }
    }

    @Override
    public void accountRegistration(User user) {

        if (!isMailEnabled) {
            return;
        }

        TransactionalEmailsApi emailApi = new TransactionalEmailsApi(apiClient);
        SendSmtpEmail email = new SendSmtpEmail();
        email.setSender(new SendSmtpEmailSender().email("cogz.onlineportal@gmail.com").name("CoGZ Online Portal"));
        email.setTo(staffEmails);
        email.setSubject("New Account Registration - [" + user.getUsername() + "]");
        email.setHtmlContent("<h2>New Account Registration</h2>\n" +
                "<br>\n" +
                "<p>Hi Admin,</p>\n" +
                "<p>A new user signup has been made, following are the details for your reference.</p>\n" +
                "<ul style=\"padding: 0;list-style-type:none;\">\n" +
                "    <li>Username: <b>" + user.getUsername() + "</b></li>\n" +
                "    <li>Fullname: <b>" + user.getLastname() + ", " + user.getFirstname() + "</b></li>\n" +
                "    <li>Email: <b>" + user.getEmail() + "</b></li>\n" +
                "    <li>Mobile Number: <b>" + user.getMobileNumber() + "</b></li>\n" +
                "    <li>Birthdate: <b>" + user.getBirthdate() + "</b></li>\n" +
                "</ul>\n" +
                "<p>You need to verify this new account using <a href=\"" + appLink + "\">CoGZ Online Portal</a></p>\n" +
                "<br>\n" +
                "<p>This is a system generated email, please do not reply to this message.</p>\n" +
                "<hr>");

        try {
            emailApi.sendTransacEmail(email);
        } catch (ApiException e) {
            logger.error(null, e);
        }
    }

    @Override
    public void accountModificationApproved(User user) {

        if (!isMailEnabled) {
            return;
        }

        TransactionalEmailsApi emailApi = new TransactionalEmailsApi(apiClient);
        SendSmtpEmail email = new SendSmtpEmail();
        email.setSender(new SendSmtpEmailSender().email("cogz.onlineportal@gmail.com").name("CoGZ Online Portal"));
        email.addToItem(new SendSmtpEmailTo().email(user.getEmail()));
        email.setSubject("Account Modification - APPROVED");
        email.setHtmlContent("<h2>Account Modification - APPROVED</h2>\n" +
                "<br>\n" +
                "<p>Hi " + user.getFirstname().substring(0, 1).toUpperCase() + user.getFirstname().substring(1).toLowerCase() + ",</p>\n" +
                "<p>Your account modification request has been 'APPROVED' by our team. Below is your username in case you have changed your firstname or lastname.</p>\n" +
                "<p>Username: <b>" + user.getUsername() + "</b></p>\n" +
                "<p>Best Regards,<br>The CoGZ team</p>\n" +
                "<br>\n" +
                "<p>This is a system generated email, please do not reply to this message. For inquiries, contact a CoGZ Admin thru our <a href=\\\"http://www.facebook.com/TeamCoGZAirsoftPH\\\">Facebook page</a>.</p>\n" +
                "<hr>");

        try {
            emailApi.sendTransacEmail(email);
        } catch (ApiException e) {
            logger.error(null, e);
        }
    }

    @Override
    public void accountModificationRejected(User user) {

        if (!isMailEnabled) {
            return;
        }

        TransactionalEmailsApi emailApi = new TransactionalEmailsApi(apiClient);
        SendSmtpEmail email = new SendSmtpEmail();
        email.setSender(new SendSmtpEmailSender().email("cogz.onlineportal@gmail.com").name("CoGZ Online Portal"));
        email.addToItem(new SendSmtpEmailTo().email(user.getEmail()));
        email.setSubject("Account Modification - REJECTED");
        email.setHtmlContent("<h2>Account Modification - REJECTED</h2>\n" +
                "<br>\n" +
                "<p>Hi " + user.getFirstname().substring(0, 1).toUpperCase() + user.getFirstname().substring(1).toLowerCase() + ",</p>\n" +
                "<p>Your account modification request has been 'REJECTED' by our team. Please ensure that all information you provided are correct and accurate with the submitted valid ID. You can still resubmit another modification request for approval.</p>\n" +
                "<p>Best Regards,<br>The CoGZ team</p>\n" +
                "<br>\n" +
                "<p>This is a system generated email, please do not reply to this message. For inquiries, contact a CoGZ Admin thru our <a href=\\\"http://www.facebook.com/TeamCoGZAirsoftPH\\\">Facebook page</a>.</p>\n" +
                "<hr>");

        try {
            emailApi.sendTransacEmail(email);
        } catch (ApiException e) {
            logger.error(null, e);
        }
    }

    @Override
    public void paymentVerification(User user, LocalDate gameSchedule, EGameType gameType) {

        if (!isMailEnabled) {
            return;
        }

        TransactionalEmailsApi emailApi = new TransactionalEmailsApi(apiClient);
        SendSmtpEmail email = new SendSmtpEmail();
        email.setSender(new SendSmtpEmailSender().email("cogz.onlineportal@gmail.com").name("CoGZ Online Portal"));
        email.setTo(staffEmails);
        email.setSubject("Payment Verification - [" + user.getUsername() + ", " + gameSchedule + "]");
        email.setHtmlContent("<h2>Payment Verification</h2>\n" +
                "<br>\n" +
                "<p>Hi Admin,</p>\n" +
                "<p>A game fee payment has been made, following are the details for your reference.</p>\n" +
                "<ul style=\"padding: 0;list-style-type:none;\">\n" +
                "    <li>Game Schedule: <b>" + gameSchedule + "</b></li>\n" +
                "    <li>Game Type: <b>" + gameType + "</b></li>\n" +
                "    <li><br></li>\n" +
                "    <li>Username: <b>" + user.getUsername() + "</b></li>\n" +
                "    <li>Fullname: <b>" + user.getLastname() + ", " + user.getFirstname() + "</b></li>\n" +
                "    <li>Email: <b>" + user.getEmail() + "</b></li>\n" +
                "    <li>Mobile Number: <b>" + user.getMobileNumber() + "</b></li>\n" +
                "    <li>Birthdate: <b>" + user.getBirthdate() + "</b></li>\n" +
                "</ul>\n" +
                "<p>You need to verify this payment using <a href=\"" + appLink + "\">CoGZ Online Portal</a></p>\n" +
                "<br>\n" +
                "<p>This is a system generated email, please do not reply to this message.</p>\n" +
                "<hr>");

        try {
            emailApi.sendTransacEmail(email);
        } catch (ApiException e) {
            logger.error(null, e);
        }
    }

    @Override
    public void paymentVerified(User user, LocalDate gameSchedule) {

        if (!isMailEnabled) {
            return;
        }

        TransactionalEmailsApi emailApi = new TransactionalEmailsApi(apiClient);
        SendSmtpEmail email = new SendSmtpEmail();
        email.setSender(new SendSmtpEmailSender().email("cogz.onlineportal@gmail.com").name("CoGZ Online Portal"));
        email.addToItem(new SendSmtpEmailTo().email(user.getEmail()));
        email.setSubject("Payment Verified - [" + gameSchedule + "]");
        email.setHtmlContent("<h2>Payment Verified</h2>\n" +
                "<br>\n" +
                "<p>Hi " + user.getFirstname().substring(0, 1).toUpperCase() + user.getFirstname().substring(1).toLowerCase() + ",</p>\n" +
                "<p>Congratulations! Your payment has been verified by our team. You are now confirmed and registered to join the game this coming " + gameSchedule + ".</p>\n" +
                "<p>Best Regards,<br>The CoGZ team</p>\n" +
                "<br>\n" +
                "<p>This is a system generated email, please do not reply to this message. For inquiries, contact a CoGZ Admin thru our <a href=\\\"http://www.facebook.com/TeamCoGZAirsoftPH\\\">Facebook page</a>.</p>\n" +
                "<hr>");

        try {
            emailApi.sendTransacEmail(email);
        } catch (ApiException e) {
            logger.error(null, e);
        }
    }
}
