package pl.pateman.gunwo.di;

public final class DIException extends RuntimeException
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
