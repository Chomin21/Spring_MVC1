package hello.servlet.web.frontcontroller.v5;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v3.ControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberFormControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberListControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberSaveControllerV3;
import hello.servlet.web.frontcontroller.v4.ControllerV4;
import hello.servlet.web.frontcontroller.v4.controller.MemberFormControllerV4;
import hello.servlet.web.frontcontroller.v4.controller.MemberListControllerV4;
import hello.servlet.web.frontcontroller.v4.controller.MemberSaveControllerV4;
import hello.servlet.web.frontcontroller.v5.adapter.ControllerV3HandlerAdapter;
import hello.servlet.web.frontcontroller.v5.adapter.ControllerV4HandlerAdapter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "frontControllerServletV5", urlPatterns = "/front-controller/v5/*")
public class FrontControllerServletV5 extends HttpServlet {
    private final Map<String, Object> handlerMappingMap = new HashMap<>(); //핸들러 매핑 정보
    private final List<MyHandlerAdapter> handlerAdapters = new ArrayList <>(); //핸들러 어댑터 목록

    public FrontControllerServletV5() {
        initHandlerMappingMap();
        initHandlerAdapters();
    }

    private void initHandlerMappingMap() {
        handlerMappingMap.put("/front-controller/v5/v3/members/new-form", new MemberFormControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members/save", new MemberSaveControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members", new MemberListControllerV3());
        handlerMappingMap.put("/front-controller/v5/v4/members/new-form", new MemberFormControllerV4());
        handlerMappingMap.put("/front-controller/v5/v4/members/save", new MemberSaveControllerV4());
        handlerMappingMap.put("/front-controller/v5/v4/members", new MemberListControllerV4());
    }

    private void initHandlerAdapters() {
        handlerAdapters.add(new ControllerV3HandlerAdapter());
        handlerAdapters.add(new ControllerV4HandlerAdapter());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //MemberFormControllerV3
        Object handler = getHandler(request); //요청 정보를 토대로 핸들러 찾아오기

        if(handler == null){ //반환 받은 컨트롤러가 없는 경우
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        MyHandlerAdapter adapter = getHandlerAdapter(handler); //핸들러 어댑터 찾아오기

        ModelView mv = adapter.handle(request, response, handler); //찾아온 어댑터로 handler호출

        String viewName = mv.getViewName(); //논리이름 new-form

        // "/WEB-INF/views/new-form.jsp"
        MyView view = viewResolver(viewName); //포워딩할 경로 넣어줌.

        view.render(mv.getModel(),request, response);
    }

    private MyHandlerAdapter getHandlerAdapter(Object handler) {
        //ControllerV3HandlerAdapter 반환
        for (MyHandlerAdapter adapter : handlerAdapters) {
            if(adapter.supports(handler)){
               return adapter;
            }
        }
        throw new IllegalArgumentException(("handler adapter를 찾을 수 없습니다. "+handler));
    }

    private Object getHandler(HttpServletRequest request) {
        String requestURI = request.getRequestURI(); //요청받은 경로를 가져옴.

        //다형성 위해 Object로 받환
        return handlerMappingMap.get(requestURI);

    }
    private static MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }
}
