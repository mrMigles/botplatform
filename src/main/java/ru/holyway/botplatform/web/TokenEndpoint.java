package ru.holyway.botplatform.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.holyway.botplatform.security.TokenStorage;
import ru.holyway.botplatform.web.entities.UserInfo;

import java.io.UnsupportedEncodingException;

/**
 * Created by voyo on 10/14/2017.
 */
@Controller
public class TokenEndpoint {

    @Autowired
    TokenStorage tokenStorage;

    @RequestMapping(value = "/token", method = RequestMethod.GET)
    public ResponseEntity<UserInfo> message(@RequestParam("id") String id, @RequestParam("code") String code) throws UnsupportedEncodingException {
        UserInfo userInfo = tokenStorage.getUserInfo(code);
        if (userInfo != null) {
            tokenStorage.clearCode(code);
            return new ResponseEntity<>(userInfo,
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

}
