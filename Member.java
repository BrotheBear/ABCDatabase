
import java.io.Serializable;

public class Member implements Serializable
{
    private String membershipNumber;
    private String forename;
    private String surname;
    private String nickname;
    private String email;
    private boolean mailList = false;
    private String dateIssued;
    private String issuer;
    private String paid;
    
    
    public Member(String membershipNumber,String forename)
    {
        this.membershipNumber = membershipNumber;
        this.forename = forename;        
    }
    public Member(String membershipNumber)
    {
        this.membershipNumber = membershipNumber;
    }
    public Member()
    {
        this.membershipNumber = "9999";
        this.forename = "TEMPORARY";
    }
    public String getMembershipNumber()
    {
        return membershipNumber;
    }
    public String getForename()
    {
        return forename;
    }
    public String getSurname()
    {
        return surname;
    }
    public String getNickname()
    {
        return nickname;
    }
    public String getEmail()
    {
        return email;
    }
    public boolean getMailList()
    {
        return mailList;
    }
    public String getDateIssued()
    {
        return dateIssued;
    }
    public String getIssuer()
    {
        return issuer;
    }
    public String getPaid()
    {
        return paid;
    }
    public void setMembershipNumber(String membershipNumber)
    {
        this.membershipNumber = membershipNumber;
    }
    public void setForename(String forename)
    {
        this.forename = forename;
    }
    public void setSurname(String surname)
    {
        this.surname = surname;
    }
    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }
    public void setEmail(String email)
    {
        this.email = email;
    }
    public void setMailList(boolean mailList)
    {
        this.mailList = mailList;
    }
    public void setDateIssued(String dateIssued)
    {
        this.dateIssued = dateIssued;
    }
    public void setIssuer(String issuer)
    {
        this.issuer = issuer;
    }
    public void setPaid(String paid)
    {
        this.paid = paid;
    }
}
