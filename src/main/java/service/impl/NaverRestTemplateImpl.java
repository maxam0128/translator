package service.impl;

import lombok.extern.slf4j.Slf4j;
import request.Auth;
import request.naver.NaverRequestParameter;
import response.TranslateResponse;
import response.naver.NaverResponse;
import service.LanguageChecker;
import service.NaverRestTemplate;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

/**
 * Created by jojoldu@gmail.com on 2017. 5. 19.
 * Blog : http://jojoldu.tistory.com
 * Github : http://github.com/jojoldu
 */

@Slf4j
public class NaverRestTemplateImpl implements NaverRestTemplate {
    private static final String TRANSLATE_URL = "https://openapi.naver.com/v1/language/translate";

    @Override
    public Optional<TranslateResponse> requestTranslate(String requestBody, Auth auth) {
        Auth.Naver naverAuth = auth.getNaver();

        Response response = createWebTarget(TRANSLATE_URL)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header(Auth.Naver.HEADER_CLIENT_ID, naverAuth.getClientId())
                .header(Auth.Naver.HEADER_CLIENT_SECRET, naverAuth.getClientSecret())
                .header("charset", "UTF-8")
                .post(Entity.entity(requestBody, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);

        if(response.getStatus() != 200){
            log.error("Translate Request Exception : {}", response.readEntity(String.class));
            return Optional.empty();
        }

        return Optional.of(response.readEntity(NaverResponse.class));
    }

    @Override
    public String createRequestData(LanguageChecker languageChecker, String text) {
        String source = languageChecker.detect(text);
        return NaverRequestParameter.builder()
                .source(source)
                .target(languageChecker.exchange(source))
                .text(text)
                .build()
                .toUrlParameter();
    }
}
