package errors;

/**
 * Created by kaspe on 2017-01-24.
 */

public class ApiErrorMessage
{
    private String domainActor;
    private String errorMessage;
    private String httpErrorCode;

    public ApiErrorMessage(String domainActor, String errorMessage, String httpErrorCode)
    {
        this.domainActor = domainActor;
        this.errorMessage = errorMessage;
        this.httpErrorCode = httpErrorCode;
    }

    public String getDomainActor()
    {
        return domainActor;
    }

    public void setDomainActor(String domainActor)
    {
        this.domainActor = domainActor;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getHttpErrorCode()
    {
        return httpErrorCode;
    }

    public void setHttpErrorCode(String httpErrorCode)
    {
        this.httpErrorCode = httpErrorCode;
    }

    @Override
    public String toString()
    {
        return "ApiErrorMessage{" +
                "domainActor='" + domainActor + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", httpErrorCode='" + httpErrorCode + '\'' +
                '}';
    }
}
