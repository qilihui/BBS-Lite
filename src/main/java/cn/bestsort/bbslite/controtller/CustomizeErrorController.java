package cn.bestsort.bbslite.controtller;

import cn.bestsort.bbslite.enums.CustomizeErrorCodeEnum;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 根据错误信息返回相关的错误提示页面
 * @author bestsort
 * @date 19-9-13 上午10:02
 * @version 1.0
 */
@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class CustomizeErrorController implements ErrorController {
    @Override
    public String getErrorPath(){
        return "error";
    }
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView errorHtml(HttpServletRequest request, Model model) {
        HttpStatus status = getStatus(request);
        // 客户端出错提示信息
        if(status.is4xxClientError()){
            model.addAttribute("message",CustomizeErrorCodeEnum.USER_ERROR.getName());
        }
        // 服务端出错提示信息
        else if(status.is5xxServerError()){
            model.addAttribute("message", CustomizeErrorCodeEnum.SYS_ERROR.getName());
        }
        return new ModelAndView("error");
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        }
        catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
