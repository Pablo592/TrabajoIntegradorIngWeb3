package ar.edu.iua.iw3.negocio.excepciones;

public class NegocioException extends Exception {

	private static final long serialVersionUID = 5472509512704239504L;

	public NegocioException() {
	
	}

	public NegocioException(String message) {
		super(message);
		
	}

	public NegocioException(Throwable cause) {
		super(cause);
		
	}

	public NegocioException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public NegocioException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
