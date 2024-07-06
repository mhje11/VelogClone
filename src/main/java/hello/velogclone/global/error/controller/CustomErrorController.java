package hello.velogclone.global.error.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping(value = "/error")
    public String handleError(HttpServletRequest request) {
        // HTTP 상태 코드 받아오기
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        log.error("Handling error with status code: " + status);

        // 상태 코드에 따른 페이지 반환
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error/404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "error/500";
            }
        }
        return "error/error";
    }
}
