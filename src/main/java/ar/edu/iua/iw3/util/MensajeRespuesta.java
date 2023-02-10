package ar.edu.iua.iw3.util;

public class MensajeRespuesta {

	private int codigo;
	private String mensaje="OK";

	public MensajeRespuesta() {	}

	public MensajeRespuesta(int codigo, String mensaje) {
		super();
		this.codigo = codigo;
		this.mensaje = mensaje;
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	@Override
	public String toString() {
		return String.format("{\"codigo\":%s,\"mensaje\":\"%s\"}", getCodigo(), getMensaje());
	}
}
