package com.mycity.place.exception;

public class TooManyPlacesException extends RuntimeException
{
   public TooManyPlacesException(String msg)
   {
	   super(msg);
   }
}
