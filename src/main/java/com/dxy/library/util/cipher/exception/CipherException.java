package com.dxy.library.util.cipher.exception;

import com.dxy.library.exception.FormativeException;

/**
 * @author duanxinyuan
 * 2019/12/30 15:24
 */
public class CipherException extends FormativeException {

    public CipherException() {
        super();
    }

    public CipherException(String message) {
        super(message);
    }

    public CipherException(Throwable cause) {
        super(cause);
    }

    public CipherException(String format, Object... arguments) {
        super(format, arguments);
    }

}
