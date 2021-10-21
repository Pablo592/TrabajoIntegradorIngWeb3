package ar.edu.iua.iw3.negocio.excepciones;

public class EncontradoException extends Exception {

	private static final long serialVersionUID = 5472509512704239504L;

	public EncontradoException() {
	
	}

	public EncontradoException(String message) {
		super(message);
		
	}

	public EncontradoException(Throwable cause) {
		super(cause);
		
	}

	public EncontradoException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public EncontradoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
