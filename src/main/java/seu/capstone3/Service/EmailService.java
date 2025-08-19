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

    public void sendAcceptedEmail(Player player, Club club) {
        mailMessage.setFrom("alimuaffag@gmail.com");
        mailMessage.setTo(player.getEmail());
        mailMessage.setSubject("Accepted Email");
        String emailBody =   "Dear " + player.getName() + ",\n\n"
                + "Congratulations \n"
                + "We are pleased to inform you that your joining request has been ACCEPTED.\n\n"
                + "Club: " + club.getName() + "\n\n"
                + "We look forward to seeing your contribution and wish you great success in your journey with us.\n\n"
                + "Best regards,\n"
                + club.getName() + " Management Team";
        mailMessage.setText(emailBody);
        mailSender.send(mailMessage);
    }

    public void sendRejectedEmail(Player player, Club club) {
        mailMessage.setFrom("alimuaffag@gmail.com");
        mailMessage.setTo(player.getEmail());
        mailMessage.setSubject("Rejected Email");
        String emailBody =   "Dear " + player.getName() + ",\n\n"
                + "Thank you for your interest in joining "
                + club.getName()+ " our club" + ".\n"
                + "After careful consideration, we regret to inform you that your joining request "
                + "has not been accepted this time.\n\n"
                + "We truly appreciate your effort and encourage you to apply again in the future.\n\n"
                + "Best regards,\n"
                + club.getName() + " Management Team";
        mailMessage.setText(emailBody);
        mailSender.send(mailMessage);
    }


}
