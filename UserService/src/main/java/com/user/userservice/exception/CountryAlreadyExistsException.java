package com.user.userservice.exception;

public class CountryAlreadyExistsException extends Exception
{
    public CountryAlreadyExistsException(String message)
    {
        super(message);
    }
}
