
package com.hrms.model.dto.common;

import java.io.Serializable;

public class ResponseDTO<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int status;      
    private String message;  
    private T data;          

    public ResponseDTO() {
    }

    public ResponseDTO(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public int getStatus() { 
        return status; 
    }
    
    public void setStatus(int status) { 
        this.status = status; 
    }

    public String getMessage() { 
        return message; 
    }
    
    public void setMessage(String message) { 
        this.message = message; 
    }

    public T getData() { 
        return data; 
    }
    
    public void setData(T data) { 
        this.data = data; 
    }
}
