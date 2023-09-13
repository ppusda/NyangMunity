package cat.community.NyangMunity.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class NyangmunityException extends RuntimeException{
    public final Map<String, String> validation = new HashMap<>();

    public NyangmunityException(String message) {
        super(message);
    }

    public NyangmunityException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract int getStatusCode();

    public void addValidation(String filedName, String message){
        validation.put(filedName, message);
    }
}
