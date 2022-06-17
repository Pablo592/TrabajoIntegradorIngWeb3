package ar.edu.iua.iw3;

import ar.edu.iua.iw3.negocio.IGraphNegocio;
import ar.edu.iua.iw3.security.authtoken.IAuthTokenBusiness;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableScheduling
public class ScheduleEvents {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IAuthTokenBusiness atB;

    @Scheduled(fixedDelay = 24*60*60*1000)
    public void purgeTokens() {
        try {
            atB.purgeTokens();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }
}