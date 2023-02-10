package ar.edu.iua.iw3.negocio.excepciones;

public class NoEncontradoException extends Exception {

	private static final long serialVersionUID = 5472509512704239504L;

	public NoEncontradoException() {
	
	}

	public NoEncontradoException(String message) {
		super(message);
		
	}

	public NoEncontradoException(Throwable cause) {
		super(cause);
		
	}

	public NoEncontradoException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public NoEncontradoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
