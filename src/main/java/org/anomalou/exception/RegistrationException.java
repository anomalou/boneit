package org.anomalou.exception;

public class RegistrationException extends Exception{
    public RegistrationException(){
        super("Registration object not found");
    }
}
