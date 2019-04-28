package pl.pateman.wiredi.exception;

public class DIException extends RuntimeException
{
   public DIException(String message)
   {
      super(message);
   }

   public DIException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public DIException(Throwable cause)
   {
      super(cause);
   }
}
