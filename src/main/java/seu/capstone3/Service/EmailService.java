package seu.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import seu.capstone3.Model.Club;
import seu.capstone3.Model.Player;
import seu.capstone3.Model.RecruitmentOpportunity;
import seu.capstone3.Model.RequestJoining;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SimpleMailMessage mailMessage = new SimpleMailMessage();

    public void sendTextEmail(String to, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("alimuaffag@gmail.com"); // لازم نفس البريد اللي بالـ properties
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }

    public void sendAcceptedEmail(Player player, Club club, RecruitmentOpportunity recruitmentOpportunity) {
        mailMessage.setFrom("alimuaffag@gmail.com");
        mailMessage.setTo(player.getEmail());
        mailMessage.setSubject("Accepted Email");
        String emailBody = "Dear " + player.getName() + ", you have been accepted by " + club.getName();
        mailMessage.setText(emailBody);
        mailSender.send(mailMessage);
    }


}
