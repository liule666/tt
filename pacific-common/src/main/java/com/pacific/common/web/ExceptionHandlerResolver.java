package com.pacific.common.web;

import com.pacific.common.exception.ExceptionUtil;
import com.pacific.common.exception.PacificException;
import com.pacific.common.http.HttpUtils;
import com.pacific.common.web.result.AjaxResult;
import com.pacific.common.web.xuser.XUserSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Created by Administrator on 2016/1/27.
 */
public class ExceptionHandlerResolver extends SimpleMappingExceptionResolver {
    public static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerResolver.class);

    @Override
    public ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        logger.info(HttpUtils.requestMessage(request));
        if (ex != null) {
            logger.error("seq : {} , yike runtime exception, e : {}",RequestContext.getSeq(), ExceptionUtil.exToString(ex));

            AjaxResult ajaxResult = null;
            if (ex instanceof PacificException) {
                PacificException pacificException = (PacificException) ex;
                String code = pacificException.getCode();
                String message = pacificException.getMessage();
                ajaxResult = new AjaxResult(code, message, null);
            } else {
                ajaxResult = new AjaxResult(AjaxResult.STATUS_ERROR, ex.getMessage(), null);
            }
            try {
                PrintWriter printWriter = response.getWriter();
                printWriter.write(JsonCommonRender.getJsonResult(ajaxResult));
                printWriter.flush();
                printWriter.close();
            } catch (Exception e) {
                logger.error("resolveException eror , e : {}", ExceptionUtil.exToString(e));
            } finally {
                clearCache();
            }
        }
        return null;
    }

    public void clearCache() {
        try {
            XUserSessionManager.refreshRemoteCache();
        } catch (Exception e) {
            logger.error("refreshRemoteCache error !");
        }
        try {
            RequestContext.clear();
        } catch (Exception e) {
            logger.error("RequestContext clear error ! , e : {}",e);
        }
    }
}
