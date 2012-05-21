package org.motechproject.whp.controller;

import org.motechproject.security.domain.AuthenticatedUser;
import org.motechproject.whp.patient.repository.AllProviders;
import org.motechproject.whp.refdata.domain.WHPRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.motechproject.flash.FlashAttributeName.in;
import static org.motechproject.flash.FlashAttributeName.out;

@Controller
public class HomeController extends BaseController {

    private AllProviders allProviders;

    @Autowired
    public HomeController(AllProviders allProviders) {
        this.allProviders = allProviders;
    }

    @RequestMapping("/")
    public String homePage(HttpServletRequest request) {
        AuthenticatedUser user = loggedInUser(request);
        if (user.getRoles().hasRole(WHPRole.ADMIN.name())) {
            return "admin";
        } else {
            Object message = request.getAttribute(in("message"));
            if (null != message) {
                request.setAttribute(out("message"), message.toString());
            }
            return "redirect:/patients?provider=" + allProviders.get(user.getExternalId()).getProviderId();
        }
    }
}

