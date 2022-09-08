package eu.lindroos.taas.teams.service.service.controller.exception;

/**
 * Created by Andreas on 13.4.2019
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String msg) {
        super(msg);
    }
}
