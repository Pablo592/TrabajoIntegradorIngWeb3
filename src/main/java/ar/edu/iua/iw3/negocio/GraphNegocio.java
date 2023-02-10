package ar.edu.iua.iw3.negocio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import ar.edu.iua.iw3.modelo.Alarma;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import ar.edu.iua.iw3.util.ChangeStateMessage;
import ar.edu.iua.iw3.util.LabelValue;

@Service
public class GraphNegocio implements IGraphNegocio {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SimpMessagingTemplate wSock;


	@Override
	public void pushGraphDataCarga(double preset,float cargaAcumulada,String codigoExterno) {
		try {
			List<LabelValue> datosCarga = new ArrayList<LabelValue>();
			datosCarga.add(new LabelValue("Preset",preset));
			datosCarga.add(new LabelValue("Carga Acumulada",cargaAcumulada));

			wSock.convertAndSend("/iw3/data/"+codigoExterno,
					new ChangeStateMessage<List<LabelValue>>(ChangeStateMessage.TYPE_GRAPH_DATA, datosCarga));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void pushExistAlarma(Alarma alarma) {
		try {
			wSock.convertAndSend("/iw3/alarma",
					new ChangeStateMessage<Alarma>(ChangeStateMessage.TYPE_NOTIFICA, alarma));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
